package com.github.rodbate.fts;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {


    static void search(String indexDir, String query) throws IOException, ParseException {

        FSDirectory dir = FSDirectory.open(Paths.get(indexDir));
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));

        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query1 = parser.parse(query);

        //query1 = new TermQuery(new Term("fullpath", "*"));
        //query1 = new FuzzyQuery(new Term("fullpath", "*fts*"));
        query1 = new WildcardQuery(new Term("fullpath", "*fts*"));
        long start = System.currentTimeMillis();
        TopDocs rs = searcher.search(query1, 10);
        long end = System.currentTimeMillis();
        System.out.println(String.format("Found %d documents in %dms matched for query : [%s]", rs.totalHits, end  - start, query));

        for (ScoreDoc scoreDoc : rs.scoreDocs) {
            //System.out.println("score doc : " + scoreDoc);
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullpath"));
        }
        dir.close();
    }


    public static void main(String[] args) throws IOException, ParseException {
        search("E:\\fts\\index", "mozilla1.1.txt");
    }
}
