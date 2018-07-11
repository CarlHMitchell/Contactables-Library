package com.github.carlhmitchell.contactablespicker.listViewHelpers;

/**
 * Superclass for the Header (contact name) and ContentItem (contact phone number/email address)
 * display classes.
 * Each ListItem has an ID which should be set to the same value as the Contact ID it is displaying.
 */
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