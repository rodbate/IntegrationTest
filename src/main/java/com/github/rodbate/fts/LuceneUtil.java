package com.github.rodbate.fts;


import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;


public final class LuceneUtil {

    private LuceneUtil() {}


    public static IndexWriter getIndexWriter(String indexDir) throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(indexDir));
        return new IndexWriter(directory, new IndexWriterConfig());
    }

    public static IndexWriter getIndexWriter(String indexDir, IndexWriterConfig config) throws IOException {
        Objects.requireNonNull(config);
        FSDirectory directory = FSDirectory.open(Paths.get(indexDir));
        return new IndexWriter(directory, config);
    }

    public static IndexSearcher getIndexSearch(String indexDir) throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(indexDir));
        return new IndexSearcher(DirectoryReader.open(directory));
    }


    public static FieldTypeBuilder newFieldTypeBuilder() {
        return new FieldTypeBuilder();
    }


    public static class FieldTypeBuilder {

        private final FieldType fieldType = new FieldType();

        private FieldTypeBuilder() {}

        public FieldTypeBuilder setIndexOptions(IndexOptions indexOptions){
            fieldType.setIndexOptions(indexOptions);
            return this;
        }

        public FieldTypeBuilder setDocValueType(DocValuesType valueType){
            fieldType.setDocValuesType(valueType);
            return this;
        }

        public FieldTypeBuilder setStored(boolean stored) {
            fieldType.setStored(stored);
            return this;
        }

        public FieldTypeBuilder setTokenized(boolean tokenized) {
            fieldType.setTokenized(tokenized);
            return this;
        }

        public FieldTypeBuilder setOmitNorms(boolean omitNorms) {
            fieldType.setOmitNorms(omitNorms);
            return this;
        }

        public FieldTypeBuilder setStoreTermVectors(boolean storeTermVectors){
            fieldType.setStoreTermVectors(storeTermVectors);
            return this;
        }

        public FieldType build() {
            fieldType.freeze();
            return fieldType;
        }

    }

}
