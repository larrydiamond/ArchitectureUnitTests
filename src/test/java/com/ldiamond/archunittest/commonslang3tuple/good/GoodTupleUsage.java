package com.ldiamond.archunittest.commonslang3tuple.good;

public class GoodTupleUsage {
    NameAndAge createNameAndAge() {
        return new NameAndAge("Jane Doe", 42);
    }

    record NameAndAge(String name, int age) {
    }
}
