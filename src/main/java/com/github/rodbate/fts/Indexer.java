package com.github.rodbate.fts;


import org.apache.lucene.document.*;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class Indexer {

    private final String storePath;
    private final String srcFilesPath;
    private final FileFilter fileFilter;
    private final IndexWriter indexWriter;


    public Indexer(String storePath, String srcFilesPath, FileFilter fileFilter) throws IOException {
        Objects.requireNonNull(storePath);
        Objects.requireNonNull(srcFilesPath);
        this.storePath = storePath;
        this.srcFilesPath = srcFilesPath;
        this.fileFilter = fileFilter;

        FSDirectory dir = FSDirectory.open(Paths.get(this.storePath));
        indexWriter = new IndexWriter(dir, new IndexWriterConfig().setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND));
    }


    public int index() {
        File dataDir = new File(srcFilesPath);
        if (!dataDir.exists()) {
            return 0;
        }
        Optional.ofNullable(dataDir.listFiles())
                .ifPresent(
                        files -> Stream.of(files)
                                .filter(f -> f.exists() && f.isFile() && (fileFilter == null || fileFilter.accept(f)))
                                .forEach(this::indexFile)
                );
        return indexWriter.numDocs();
    }


    public void indexFile(File f) {
        Document doc = new Document();
        try {
            doc.add(new Field("contents", new FileReader(f), TextField.TYPE_NOT_STORED));
            doc.add(new Field("filename", f.getName(), StringField.TYPE_STORED));
            doc.add(new Field("fullpath", f.getCanonicalPath(), StringField.TYPE_STORED));
            indexWriter.addDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        indexWriter.close();
    }


    public static void main(String[] args) throws IOException {
        Indexer indexer = new Indexer("E:\\fts\\index", "E:\\fts\\files", file -> file.getName().endsWith(".txt"));
        int index = indexer.index();
        System.out.println("Doc Num : " + index);
        //close
        indexer.close();
    }

}
