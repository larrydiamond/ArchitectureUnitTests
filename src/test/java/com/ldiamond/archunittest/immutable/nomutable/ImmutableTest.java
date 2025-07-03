package com.ldiamond.archunittest.immutable.nomutable;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.Value;

public class ImmutableTest {
    public static final String dateFormat = "dd MMM yyyy hh:mm:ss a";
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    public static final List<String> list = List.of("a", "b", "c");
    public static final String[] array = new String[]{"a", "b", "c"};
    public static final Map<String, String> map = Map.of("a", "1", "b", "2", "c", "3");
    public static final InternalClass internalClass = new InternalClass("name", 42);
    public static final ImmutableInternalClass immutableInternalClass = new ImmutableInternalClass("immutablePublicStaticField");
}

@Value
class InternalClass {
    String name;
    int value;
}

class ImmutableInternalClass {
    public static final String immutablePublicStaticField = "immutablePublicStaticField";
    private final String immutableField;

    public ImmutableInternalClass(String immutablePublicStaticField) {
        this.immutableField = immutablePublicStaticField;
    }

    public String getImmutableField() {
        return immutableField;
    }
}