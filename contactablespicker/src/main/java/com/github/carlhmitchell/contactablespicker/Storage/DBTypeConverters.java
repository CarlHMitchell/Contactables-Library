package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.persistence.room.TypeConverter;
import java.util.Arrays;
import java.util.List;

public class DBTypeConverters {
    @TypeConverter
    public static String listToString(List<String> value) {
        return value.toString();
    }

    @TypeConverter
    public static List<String> stringToList(String values) {
        String stripped = values.substring(1, values.length() - 1);
        // List<>.toString() adds spaces between the entries in the list.
        // Thus both the commas and the spaces need to be stripped when converting the string
        //   back to a list.
        String[] array = stripped.split(", ");
        return Arrays.asList(array);
    }
}
