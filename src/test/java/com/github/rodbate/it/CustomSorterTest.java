package com.github.rodbate.it;

import com.github.rodbate.fts.FieldExt;
import com.github.rodbate.fts.LuceneUtil;
import com.github.rodbate.scala.oo.Number;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 *
 *
 * Created by rodbate on 2017/12/13.
 */
public class CustomSorterTest {

    private final String xySeparator = "#";
    private IndexWriter indexWriter;

    @Before
    public void setUp() throws IOException {
        Directory dir = new RAMDirectory();
        indexWriter = new IndexWriter(dir, new IndexWriterConfig().setInfoStream(System.err));
    }

    @After
    public void tearDown() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    @Test
    public void testCustomSorter() throws IOException {
        //indexing
        Document doc = new Document();
        doc.add(new FieldExt("point", "a", LuceneUtil.newFieldTypeBuilder()
                .setStored(true)
                .build()));
        doc.add(new FieldExt("location", new BytesRef(3 + xySeparator + 4), LuceneUtil.newFieldTypeBuilder()
                .setStored(true).setDocValueType(DocValuesType.SORTED).build()));
        indexWriter.addDocument(doc);

        doc = new Document();
        doc.add(new FieldExt("point", "b", LuceneUtil.newFieldTypeBuilder()
                .setStored(true)
                .build()));
        doc.add(new FieldExt("location", new BytesRef(1 + xySeparator + 1), LuceneUtil.newFieldTypeBuilder()
                .setStored(true).setDocValueType(DocValuesType.SORTED).build()));
        indexWriter.addDocument(doc);

        doc = new Document();
        doc.add(new FieldExt("point", "c", LuceneUtil.newFieldTypeBuilder()
                .setStored(true)
                .build()));
        doc.add(new FieldExt("location", new BytesRef(5 + xySeparator + 6), LuceneUtil.newFieldTypeBuilder()
                .setStored(true).setDocValueType(DocValuesType.SORTED).build()));
        indexWriter.addDocument(doc);

        indexWriter.commit();

        //search
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexWriter));
        Query query = new MatchAllDocsQuery();
        TopFieldDocs topFieldDocs = searcher.search(query, 2, new Sort(new SortField("location", new LocationFieldComparatorSource(xySeparator), false)));

        for (ScoreDoc sd : topFieldDocs.scoreDocs){
            System.out.println("score doc : " + sd);
            System.out.println("document : " + searcher.doc(sd.doc));
        }

    }




    //field comparator factory
    static class LocationFieldComparatorSource extends FieldComparatorSource {

        private final String xySeparator;

        public LocationFieldComparatorSource(String xySeparator) {
            this.xySeparator = xySeparator;
        }

        @Override
        public FieldComparator<?> newComparator(String fieldname, int numHits, int sortPos, boolean reversed) {
            return new LocationFieldComparator(fieldname, numHits, xySeparator);
        }

    }

    //custom location field comparator
    static class LocationFieldComparator extends FieldComparator<String> implements LeafFieldComparator {

        private final String[] values;
        private final String xySeparator;
        private final String field;
        private String topValue;
        private String bottomValue;

        private SortedDocValues currentDocValues;

        public LocationFieldComparator(String field, int numHits, String xySeparator) {
            this.field = field;
            this.values = new String[numHits];
            this.xySeparator = xySeparator;
        }

        @Override
        public int compare(int slot1, int slot2) {
            return Double.compare(getDistance(values[slot1]), getDistance(values[slot2]));
        }

        private double getDistance(String value) {
            double dis = 0;
            if (value != null) {
                String[] split = value.split(xySeparator);
                if (split.length == 2) {
                    try {
                        double x = Double.valueOf(split[0]);
                        double y = Double.valueOf(split[1]);
                        dis = Math.sqrt(x * x + y * y);
                    } catch (NumberFormatException ignore) {}
                }
            }
            return dis;
        }

        @Override
        public void setTopValue(String value) {
            this.topValue = value;
        }

        @Override
        public String value(int slot) {
            return values[slot];
        }

        @Override
        public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
            currentDocValues = DocValues.getSorted(context.reader(), field);
            return this;
        }

        @Override
        public void setBottom(int slot) throws IOException {
            this.bottomValue = value(slot);
        }

        @Override
        public int compareBottom(int doc) throws IOException {
            return Double.compare(getDistance(bottomValue), getDistance(getValueByDoc(doc)));
        }

        @Override
        public int compareTop(int doc) throws IOException {
            return Double.compare(getDistance(topValue), getDistance(getValueByDoc(doc)));
        }

        @Override
        public void copy(int slot, int doc) throws IOException {
            values[slot] = getValueByDoc(doc);
        }

        @Override
        public void setScorer(Scorer scorer) throws IOException {

        }

        private String getValueByDoc(int docId) throws IOException {
            if (currentDocValues.advanceExact(docId)) {
                return currentDocValues.binaryValue().utf8ToString();
            }
            return "";
        }
    }


}
