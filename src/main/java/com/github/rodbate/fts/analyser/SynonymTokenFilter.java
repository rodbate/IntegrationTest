package com.github.rodbate.fts.analyser;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by rodbate on 2017/11/27.
 */
public final class SynonymTokenFilter extends TokenFilter {

    public final static String TYPE_NAME = "SYNONYM";
    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);
    private final TypeAttribute typeAttribute = addAttribute(TypeAttribute.class);
    private final SynonymEngine synonymEngine;
    private final LinkedList<String> synonymItems = new LinkedList<>();

    //current attribute state
    private AttributeSource.State currentState;


    public SynonymTokenFilter(TokenStream input, SynonymEngine engine) {
        super(input);
        this.synonymEngine = engine;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (synonymItems.size() > 0){
            String item = synonymItems.pop();
            restoreState(currentState);
            termAttribute.setEmpty().append(item);
            positionIncrementAttribute.setPositionIncrement(0);
            typeAttribute.setType(TYPE_NAME);
            return true;
        }

        //get next token
        if (!input.incrementToken()) {
            return false;
        }

        String term = new String(termAttribute.buffer());
        List<String> synonyms = synonymEngine.getSynonyms(term.trim());
        if (saveSynonymItems(synonyms)) {
            currentState = captureState();
        }
        return true;
    }


    private boolean saveSynonymItems(List<String> items) {
        return !(items == null || items.size() == 0) && synonymItems.addAll(items);
    }
}
