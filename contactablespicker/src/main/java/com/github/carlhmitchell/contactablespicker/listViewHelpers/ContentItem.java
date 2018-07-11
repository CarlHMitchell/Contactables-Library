package com.github.carlhmitchell.contactablespicker.listViewHelpers;

/**
 * ContentItems contain non-header data for the displayed list of contacts.
 * This can be a phone number or an email address.
 */
public class ContentItem extends ListItem {
    //private static final String DEBUG_TAG = "ContentItem";

    private String data;

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        this.data = data;
    }
}