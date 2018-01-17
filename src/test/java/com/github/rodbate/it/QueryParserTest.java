package com.github.rodbate.it;

import com.github.rodbate.fts.FieldExt;
import com.github.rodbate.fts.LuceneUtil;
import com.github.rodbate.fts.analyser.SynonymAnalyzer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.*;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.payloads.TypeAsPayloadTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;


import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 *
 * Created by rodbate on 2017/11/21.
 */
public class QueryParserTest {


    QueryParser queryParser = new QueryParser("subject", new StandardAnalyzer());


    @Test
    public void testTermQuery() throws ParseException {
        //TermQuery ...
        Query query = queryParser.parse("kobe");
        assertEquals("subject:kobe", query.toString());
        assertEquals("name:james", queryParser.parse("name:james").toString());
    }


    @Test
    public void testRangeQuery() throws ParseException {
        //TermRangeQuery inclusive
        Query termRange = queryParser.parse("title:[a TO m]");
        assertTrue(termRange instanceof TermRangeQuery);
        assertEquals("title:[a TO m]", termRange.toString());

        //TermRangeQuery exclusive
        Query exclusiveTermRange = queryParser.parse("title:{a TO \"m a b\"}");
        assertTrue(exclusiveTermRange instanceof TermRangeQuery);
        assertEquals("title:{a TO m a b}", exclusiveTermRange.toString());

        //numeric range query
        Query numericRangeQuery = queryParser.parse("count:[10 TO 100]");
        System.out.println(numericRangeQuery.getClass().getName());


        //prefix and wildcard
        assertNotEquals("title:PGH*", queryParser.parse("title:PGH*").toString());
        assertEquals("title:pgh*", queryParser.parse("title:PGH*").toString());

        //assertEquals("title:*pgh*", queryParser.parse("title:*PGH*").toString());
        queryParser.setAllowLeadingWildcard(true);
        assertEquals("title:*pgh*", queryParser.parse("title:*PGH*").toString());
    }


    @Test
    public void testBooleanQuery() throws ParseException {
        assertEquals("-title:c +name:d", queryParser.parse("-title:c AND name:D").toString());
    }


    @Test
    public void testPhraseQuery() throws ParseException {
        //phrase query enclosed with double quotes
        Query query = queryParser.parse("\"this is phrase query*\"");
        assertTrue(query instanceof PhraseQuery);
        assertEquals("subject:\"? ? phrase query\"", query.toString());

        queryParser.setPhraseSlop(3);
        query = queryParser.parse("\"this is phrase query*\"");
        assertEquals("subject:\"? ? phrase query\"~3", query.toString());
    }


    @Test
    public void testFuzzyQuery() throws ParseException {
        assertEquals("subject:jzaa~1", queryParser.parse("jzaa~1").toString());

        assertTrue(queryParser.parse("*:*") instanceof MatchAllDocsQuery);
        assertTrue(queryParser.parse("abc^2") instanceof BoostQuery);

        System.out.println(RamUsageEstimator.shallowSizeOfInstance(QueryParserTest.class));
    }

    @Test
    public void testSynonymFilter() throws ParseException {
        queryParser = new QueryParser("f", new SynonymAnalyzer());
        System.out.println(queryParser.parse("\"fox jumps\"").toString("f"));
        assertTrue(queryParser.parse("\"fox jumps\"") instanceof MultiPhraseQuery);
        assertTrue(queryParser.parse("\"fox jumpsa\"") instanceof PhraseQuery);
    }

    @Test
    public void testSynonymFilterSearch() throws IOException {
        Directory dir = new RAMDirectory();
        Analyzer analyzer = new SynonymAnalyzer();
        IndexWriter indexWriter = new IndexWriter(dir, new IndexWriterConfig(analyzer));

        //add docs and commit
        Document doc = new Document();
        //doc.add(new TextField("content", "the fox jumps quick", Field.Store.YES));
        //doc.add(new TextField("content", "content append info", Field.Store.YES));
        doc.add(new FieldExt("content", "the fox jumps quick", LuceneUtil.newFieldTypeBuilder()
                                                                                .setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS)
                                                                                .setStored(true)
                                                                                .setTokenized(true)
                                                                                .setStoreTermVectors(true).build()));
        indexWriter.addDocument(doc);
        doc = new Document();
        //doc.add(new TextField("content", "the fox run quick", Field.Store.YES));
        //indexWriter.addDocument(doc);
        indexWriter.commit();

        //search docs
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexWriter));
        TermQuery query = new TermQuery(new Term("content", "jumps"));
        TopDocs topDocs = searcher.search(query, 1);
        assertTrue(topDocs.scoreDocs.length == 1);
        assertEquals("the fox jumps quick", searcher.doc(topDocs.scoreDocs[0].doc).get("content"));

        Fields termVectors = searcher.getIndexReader().getTermVectors(topDocs.scoreDocs[0].doc);
        System.out.println("TermVectors : " + termVectors);
        if (termVectors != null) {
            for (String f : termVectors) {
                Terms terms = termVectors.terms(f);
                if (terms != null) {
                    System.out.println(terms.getStats());
                    TermsEnum iterator = terms.iterator();
                    BytesRef term;
                    while ((term = iterator.next()) != null) {
                        System.out.println(term.utf8ToString());
                    }
                }
            }
        }

        query = new TermQuery(new Term("content", "fast"));
        topDocs = searcher.search(query, 1);
        assertTrue(topDocs.scoreDocs.length == 1);
        assertEquals("the fox jumps quick", searcher.doc(topDocs.scoreDocs[0].doc).get("content"));

        PhraseQuery phraseQuery = new PhraseQuery.Builder()
                .add(new Term("content", "fast"))
                .add(new Term("content", "content"))
                //
                .setSlop(analyzer.getPositionIncrementGap(null))
                .build();
        topDocs = searcher.search(phraseQuery, 1);
        assertTrue(topDocs.totalHits == 1);
        assertEquals("the fox jumps quick", searcher.doc(topDocs.scoreDocs[0].doc).get("content"));
        System.out.println(Arrays.asList(searcher.doc(topDocs.scoreDocs[0].doc).getFields("content")));




        //close resources
        indexWriter.close();
        dir.close();
    }



    @Test
    public void testTypeVariable() {
        Vector<String> x = new Vector<>();
        Vector<Integer> y = new Vector<>();
        boolean b = x.getClass() == y.getClass();
        System.out.println("x=" + x.getClass() + ",y=" + y.getClass());
    }


    static class Outer {
        int i = 100;
        static void classMethod() {
            final int l = 200;
            class LocalInStaticContext {
                //int k = i; // Compile-time error
                int m = l; // OK
            }
        }
        void foo() {
            class Local { // A local class
                int j = i;
            }
        }
    }



    @Test
    public void testFullWidthChar() {
        int min = 0xFF00;
        int max = 0xFFEF;
        int cnt = 0;
        for (int i = min; i <= max; i++) {
            ++cnt;
            System.out.println((char)i);
        }
        System.out.println("CNT : " + cnt);
    }












    @Test
    public void simpleAnalysis() throws IOException {

        String[] samples = {
                "The quick brown fox jumped over the lazy dog",
                "XY&Z Corporation - xyz@example.com",
                "I will email you at xyz@example.com",
                "我可是老大分厘卡圣诞节发；啊曌quick brown fox jumped"
        };

        Analyzer analyzers[] = {
                new StandardAnalyzer(),
                new SimpleAnalyzer(),
                new WhitespaceAnalyzer(),
                new StopAnalyzer(),
                new CustomAnalyzer(),
                new SmartChineseAnalyzer(true)
        };

        for (String text : samples){
            System.out.println("source text : " + text);
            for (Analyzer analyzer : analyzers) {
                System.out.println("Analyser : " + analyzer.getClass().getName());

                TokenStream tokenStream = analyzer.tokenStream("content", text);
                tokenStream.reset();
                //add TokenAttribute
                TermToBytesRefAttribute termToBytesRefAttribute = tokenStream.getAttribute(TermToBytesRefAttribute.class);
                PositionIncrementAttribute positionIncrementAttribute = tokenStream.getAttribute(PositionIncrementAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                TypeAttribute typeAttribute = tokenStream.getAttribute(TypeAttribute.class);
                BoostAttribute boostAttribute = tokenStream.addAttribute(BoostAttribute.class);
                PayloadAttribute payloadAttribute = tokenStream.addAttribute(PayloadAttribute.class);

                int position = 0;
                while (tokenStream.incrementToken()) {
                    BytesRef payload;
                    System.out.println(String.format("[%s -> Offset : [%d,%d),  Position : %d,  Type : %s,  Boost : %.2f,  Payload : %s]  ",
                            termToBytesRefAttribute.getBytesRef().utf8ToString(), offsetAttribute.startOffset(), offsetAttribute.endOffset()
                            , position += positionIncrementAttribute.getPositionIncrement(), typeAttribute.type(), boostAttribute.getBoost(),
                            (payload = payloadAttribute.getPayload()) != null ? payload.utf8ToString() : null));
                }
                tokenStream.close();
                System.out.println("\n");
            }
            System.out.println("-------------------- END ------------------");
        }
    }





    static class CustomAnalyzer extends Analyzer {

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            WhitespaceTokenizer src = new WhitespaceTokenizer();
            TokenFilter tof = new TypeAsPayloadTokenFilter(src);
            tof = new LowerCaseFilter(tof);
            tof = new StopFilter(tof, StopFilter.makeStopSet("quick", "the"));
            tof = new UpperCaseFilter(tof);
            return new TokenStreamComponents(src, tof);
        }

    }



    @Test
    public void testProxyProtectedMethod() throws Exception {
        AtomicInteger cnt = new AtomicInteger();
        MethodBeforeAdvice advice = (method, args, target) -> {
            cnt.incrementAndGet();
        };
        ProxyFactory proxyFactory = new ProxyFactory(new Class[]{ Bean.class });
        proxyFactory.setTarget(new MyBean());
        proxyFactory.addAdvice(advice);
        proxyFactory.addAdvice((MethodInterceptor) invocation -> {
            System.out.println("Entering method '" + invocation.getMethod().getName() + "' of class [" +
                    invocation.getThis().getClass().getName() + "]");
            Object ret = invocation.proceed();
            System.out.println("Exiting method '" + invocation.getMethod().getName() + "' of class [" +
                    invocation.getThis().getClass().getName() + "]");
            return ret;
        });
        proxyFactory.setProxyTargetClass(false);

        Bean proxy = (Bean) proxyFactory.getProxy();
        assertEquals(4, proxy.add(1, 3));
        assertEquals(1, cnt.get());
    }

    interface Bean {
        int add(int a, int b);
    }

    public static class MyBean implements Bean {
        static int initCnt = 0;
        public MyBean() {
            System.out.println(">>>>>>>>>>>>>>  MyBean() " + ++initCnt);
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int add(int x, int y) {
            return x + y;
        }
    }


    @Test
    public void classV() throws Throwable {
        /*ClassValueItem classValueItem = new ClassValueItem();
        for (int i = 0; i < 10; i++) {
            classValueItem.targetClass.get(QueryParserTest.class);
            classValueItem.targetClass.get(Object.class);
            classValueItem.targetClass.get(String.class);
        }*/


        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle constructor = lookup.findConstructor(ClassValueItem.class, MethodType.methodType(void.class)).asType(MethodType.methodType(ClassValueItem.class));
        ClassValueItem classValueItem = ClassValueItem.class.cast(constructor.invoke());
        classValueItem.targetClass.get(Object.class);

        int codePoint = 0x10437;
        char[] chars = Character.toChars(codePoint);
        System.out.println(Integer.toHexString((int) chars[0]) + "/" + Integer.toHexString((int) chars[1]));
        System.out.println(new String(chars));

        int remainPoint = codePoint - 0x010000;
        //high surrogate
        assertEquals(0xD800 + (remainPoint >>> 10), chars[0]);

        //low surrogate
        assertEquals(0xD800 + 0x400 + (remainPoint & 0x3FF), chars[1]);



        //1 4 5 9 10 14 18 30
        int arr[] = {1, 4, 5, 9, 10, 14, 18, 30};
        System.out.println(bs(arr, 0));


    }







    private int bs(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            if (key > arr[mid]) {
                low = mid + 1;
            } else if (key < arr[mid]) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }





    static class ClassValueItem {
        public ClassValueItem(){
            System.out.println("ClassValueItem.<init> invoked .. ");
        }
        final ClassValue<Class<?>> targetClass = new ClassValue<Class<?>>() {
            final Map<String, AtomicInteger> cnt = new HashMap<>();
            @Override
            protected Class<?> computeValue(Class<?> type) {
                int cnt = this.cnt.computeIfAbsent(type.getName(), name -> new AtomicInteger(0)).incrementAndGet();
                System.out.println(type.getName() + " -> " + cnt);
                return String.class;
            }
        };
    }
}
