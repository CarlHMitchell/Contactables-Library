package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.persistence.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

class DBTypeConverters {
    //private static final String DEBUG_TAG = "DBTypeConverters";

    /**
     * Convert a List of Strings into a String.
     * EG: ["Foo", "Bar", "Baz"]
     *
     * @param value List of Strings to convert.
     * @return Single String representing the List.
     */
    @TypeConverter
    public static String listToString(List<String> value) {
        return value.toString();
    }

    /**
     * Parses a String containing a List of Strings back into a List of Strings.
     *     The input string should have its values contained in double quotes, separated by a comma
     *         and a space, and the entire set enclosed in square brackets.
     * @param values String containing a list of strings.
     * @return List of extracted Strings.
     */
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
