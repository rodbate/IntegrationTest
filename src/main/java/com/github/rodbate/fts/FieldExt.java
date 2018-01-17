package com.github.rodbate.fts;


import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.util.BytesRef;

public final class FieldExt extends Field {

    public FieldExt(String name, long value, IndexableFieldType type) {
        super(name, type);
        this.fieldsData = value;
    }

    public FieldExt(String name, double value, IndexableFieldType type) {
        super(name, type);
        this.fieldsData = value;
    }

    public FieldExt(String name, String value, IndexableFieldType type) {
        super(name, type);
        this.fieldsData = value;
    }

    public FieldExt(String name, BytesRef value, IndexableFieldType type) {
        super(name, type);
        this.fieldsData = value;
    }
}
