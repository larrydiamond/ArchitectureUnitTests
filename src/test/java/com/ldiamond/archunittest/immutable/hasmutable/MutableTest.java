package com.ldiamond.archunittest.immutable.hasmutable;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.AllArgsConstructor;

public class MutableTest {
    public static String dateFormat = "dd MMM yyyy hh:mm:ss a";
    public static int someInt = 42;
    public static double someDouble = 3.14;
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    public static List<String> list = List.of("a", "b", "c");
    public static String[] array = new String[]{"a", "b", "c"};
    public static Map<String, String> map = Map.of("a", "1", "b", "2", "c", "3");
    public static final MutableInternalClass mutableInternalClass = new MutableInternalClass();
    public static final MutableAnnotatedClass mutableAnnotatedClass = new MutableAnnotatedClass("mutableAnnotatedClass");
    public static final ArrayList<String> arrayListsAreMutable = new ArrayList<>();
    public static final HashMap<String,String> hashMapsAreMutable = new HashMap<>();
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
}

class MutableInternalClass {
    public static String mutablePublicStaticField = "mutablePublicStaticField";
} 

class MutableAnnotatedClass {
    private String mutableField;

    public MutableAnnotatedClass(String mutablePublicStaticField) {
        this.mutableField = mutablePublicStaticField;
    }

    public String getMutableField() {
        return mutableField;
    }

    public void setMutableField(String mutableField) {
        this.mutableField = mutableField;
    }
}