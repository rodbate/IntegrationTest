package com.github.rodbate.fts.analyser;

import java.util.*;

/**
 *
 * Created by rodbate on 2017/11/27.
 */
@FunctionalInterface
public interface SynonymEngine {


    SynonymEngine DEFAULT = new SynonymEngine() {

        private final Map<String, List<String>> cup = new HashMap<>();
        {
            cup.put("high", Arrays.asList("up", "upper"));
            cup.put("quick", Arrays.asList("fast", "speedy"));
            cup.put("jumps", Arrays.asList("leaps", "hops"));
            cup.put("over", Arrays.asList("above"));
            cup.put("lazy", Arrays.asList("apathetic", "sluggish"));
            cup.put("dog", Arrays.asList("canine", "pooch"));
        }

        @Override
        public List<String> getSynonyms(String item) {
            return cup.get(item);
        }
    };


    List<String> getSynonyms(String item);
}
