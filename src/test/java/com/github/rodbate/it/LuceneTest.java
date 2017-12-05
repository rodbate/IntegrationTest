package com.github.rodbate.it;




import com.github.rodbate.fts.FieldExt;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


import static com.github.rodbate.fts.LuceneUtil.*;
import static org.junit.Assert.*;



public class LuceneTest {

    private final String INDEX_DIR = "E:\\fts\\index";


    @Test
    public void testAdd() throws IOException {
        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND).setInfoStream(System.out));
        for (int i = 1; i <= 10; i++) {
            Document doc = new Document();
            doc.add(new FieldExt("id", i, newFieldTypeBuilder()
                    .setIndexOptions(IndexOptions.DOCS)
                    .setStored(true)
                    .setTokenized(false)
                    .setDocValueType(DocValuesType.NUMERIC)
                    .build()));

            doc.add(new Field("name", "NAME->" + i, newFieldTypeBuilder()
                    .setIndexOptions(IndexOptions.DOCS)
                    .setStored(true)
                    .setTokenized(false)
                    .build()));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }



    private void addDoc(Document doc, boolean close) throws IOException {
        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND).setInfoStream(System.out));
        indexWriter.addDocument(doc);
        if (close) {
            indexWriter.close();
        }
    }

    @Test
    public void add1() throws IOException {
        Document doc = new Document();
        doc.add(new FieldExt("id", 20, newFieldTypeBuilder()
                .setIndexOptions(IndexOptions.DOCS)
                .setStored(true)
                .setTokenized(false)
                .setDocValueType(DocValuesType.NUMERIC)
                .build()));

        doc.add(new Field("name", "the quick brown fox jumped over the lazy dog", newFieldTypeBuilder()
                .setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
                .setStored(true)
                .setTokenized(true)
                .build()));
        addDoc(doc, true);
    }


    @Test
    public void testPhraseQuery() throws IOException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        PhraseQuery query = new PhraseQuery.Builder()
                .setSlop(8)
                .add(new Term("name", "lazy"))
                .add(new Term("name", "jumped"))
                .add(new Term("name", "quick"))
                .build();
        /*long totalHits = searcher.search(query, 1).totalHits;
        System.out.println("totalHits -> " + totalHits);
        assertTrue(totalHits > 0);*/

        searcher.search(new PrefixQuery(new Term("name", "NAME")), new Collector() {
            @Override
            public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
                System.out.println("LeafContext => " + context);
                return new LeafCollector() {
                    private final AtomicInteger cnt = new AtomicInteger();
                    @Override
                    public void setScorer(Scorer scorer) throws IOException {
                        System.out.println("Scorer => " + scorer);
                    }

                    @Override
                    public void collect(int doc) throws IOException {
                        System.out.println("Doc => " + doc);
                        System.out.println(context.reader().document(doc));
                        if (cnt.incrementAndGet() == 5) {
                            throw new CollectionTerminatedException();
                        }
                    }
                };
            }

            @Override
            public boolean needsScores() {
                return true;
            }
        });
    }



    @Test
    public void testSearch() throws IOException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("id", "1")), 1);
        assertEquals(1, topDocs.totalHits);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = searcher.doc(scoreDoc.doc);
        assertEquals("NAME->1", doc.get("name"));
    }

    @Test
    public void testSearchRange() throws IOException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        //TermRangeQuery query = new TermRangeQuery("id", new BytesRef("1"), new BytesRef("5"), true, true);
        TopDocs topDocs = searcher.search(NumericDocValuesField.newSlowRangeQuery("id", 4, 5), 10);
        /*assertEquals(5, topDocs.totalHits);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = searcher.doc(scoreDoc.doc);
        assertEquals("NAME->1", doc.get("name"));*/
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            System.out.println(searcher.doc(scoreDoc.doc));
            /*System.out.println("------------------------------");
            Explanation explain = searcher.explain(query, scoreDoc.doc);
            System.out.println(explain);*/
        }
    }

    @Test
    public void testPrefixSearch() throws IOException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        PrefixQuery query = new PrefixQuery(new Term("name", "NAME"));
        TopDocs topDocs = searcher.search(query, 10);
        assertEquals(10, topDocs.totalHits);
    }

    @Test
    public void testBooleanQuery() throws IOException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        PrefixQuery prefixQuery = new PrefixQuery(new Term("name", "NAME"));
        TermQuery termQuery = new TermQuery(new Term("name", "NAME->5"));
        Query rangeQuery = NumericDocValuesField.newSlowRangeQuery("id", 1, 3);
        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(termQuery, BooleanClause.Occur.SHOULD)
                .add(rangeQuery, BooleanClause.Occur.SHOULD)
                .build();
        TopDocs topDocs = searcher.search(booleanQuery, 10);
        assertEquals(4, topDocs.totalHits);
    }

    @Test
    public void testDelete() throws IOException, InterruptedException {
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("id", "5")), 1);
        assertEquals(0, topDocs.totalHits);

        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND));
        long sequenceNum = indexWriter.deleteDocuments(new Term("id", "5"));
        System.out.println("sequence number : " + sequenceNum);
        indexWriter.commit();
        indexWriter.close();

        //Thread.sleep(3000);
        //searcher = getIndexSearch(INDEX_DIR);
        topDocs = searcher.search(new TermQuery(new Term("id", "9")), 1);
        assertEquals(3, topDocs.totalHits);
    }

    @Test
    public void testUpdate() throws IOException {
        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND));
        Document upDoc = new Document();
        upDoc.add(new Field("name", "UPDATE-NAME-9", newFieldTypeBuilder()
                .setIndexOptions(IndexOptions.DOCS)
                .setStored(true)
                .setTokenized(false)
                .build()));
        indexWriter.updateDocument(new Term("id", "9"), upDoc);
        indexWriter.close();
        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("name", "UPDATE-NAME-9")), 1);
        assertEquals(3, topDocs.totalHits);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = searcher.doc(scoreDoc.doc);
        assertEquals("UPDATE-NAME-9", doc.get("name"));
    }


    @Test
    public void updateDocValues() throws IOException {
        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig()
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
                .setInfoStream(System.out));
        indexWriter.updateDocValues(new Term("id", "1"), new Field("name", new BytesRef("UPDATE-NAME-1"), newFieldTypeBuilder()
                //.setIndexOptions(IndexOptions.DOCS)
                .setStored(true)
                .setTokenized(false)
                .setDocValueType(DocValuesType.BINARY)
                .build()));
        indexWriter.commit();
        indexWriter.close();

        IndexSearcher searcher = getIndexSearch(INDEX_DIR);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("id", "1")), 1);
        assertEquals(1, topDocs.totalHits);
        ScoreDoc scoreDoc = topDocs.scoreDocs[0];
        Document doc = searcher.doc(scoreDoc.doc);
        assertEquals("UPDATE-NAME-1", doc.getBinaryValue("name").utf8ToString());

    }



    @Test
    public void deleteAll() throws IOException {
        IndexWriter indexWriter = getIndexWriter(INDEX_DIR, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND).setInfoStream(System.out));
        indexWriter.deleteAll();
        indexWriter.commit();
        assertEquals(0, indexWriter.numDocs());
    }







    @Test
    public void test() throws IOException, InterruptedException {
        int d = 20;
        int v = 2;
        int vPower = 31 - Integer.numberOfLeadingZeros(v);
        //power of 2
        v = 1 << vPower;
        //assertEquals((d / v + ((d % v == 0) ? 0 : 1)), (d >>> vPower) + 1);
        //assertEquals((d + v - 1) / v, (d >>> vPower) + 1);
        assertEquals(Integer.MIN_VALUE + 1, add(Integer.MAX_VALUE, 2));
        assertEquals(-1, minus(4, 5));
        assertEquals(0, divide(4, 5));
        assertEquals(1, divide(5, 4));
        assertEquals(-1, divide(5, -4));
        assertEquals(-1, divide(-5, 4));
        assertEquals(1, divide(-5, -4));


    }

    private boolean isPowerOf2(int d){
        return false;
    }

    private int add(int a, int b) {
        int s = a;
        while (b != 0) {
            s = a ^ b;
            b = (a & b) << 1;
            a = s;
        }
        return s;
    }

    private int negative(int a) {
        return add(~a, 1);
    }

    private int minus(int a, int b) {
        return add(a, negative(b));
    }
    private int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException();
        }
        boolean negative = false;
        if (a < 0) {
            a = negative(a);
            negative = true;
        }
        if (b < 0) {
            b = negative(b);
            negative = !negative;
        }
        int cnt = 0;
        while (a >= b) {
            a = minus(a, b);
            cnt ++;
        }
        return negative ? negative(cnt) : cnt;
    }

    private int mod(int a, int b) {
        return 0;
    }
}
