package com.github.rodbate.fts.analyser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 *
 * Created by rodbate on 2017/11/27.
 */
public final class SynonymAnalyzer extends Analyzer {

    public SynonymAnalyzer() {
        super(Analyzer.PER_FIELD_REUSE_STRATEGY);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer src = new StandardTokenizer();
        TokenFilter tof = new LowerCaseFilter(src);
        tof = new SynonymTokenFilter(tof, SynonymEngine.DEFAULT);
        return new TokenStreamComponents(src, tof);
    }

    @Override
    public int getPositionIncrementGap(String fieldName) {
        return 2;
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return super.normalize(fieldName, in);
    }
}
