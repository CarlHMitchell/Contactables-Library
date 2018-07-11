package com.github.carlhmitchell.contactablespicker.listViewHelpers;

public class ListItem {
    //private static final String DEBUG_TAG = "ListItem";

    private String data;
    private long id;

    public ListItem() {
    }

    public String getData() {
        return data;
    }

    @SuppressWarnings("unused")
    public void setData(String data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}