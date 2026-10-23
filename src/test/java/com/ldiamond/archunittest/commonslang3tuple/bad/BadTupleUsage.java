package com.ldiamond.archunittest.commonslang3tuple.bad;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class BadTupleUsage {
    Pair<String, Integer> createPair() {
        return new ImmutablePair<>("Jane Doe", 42);
    }
}
