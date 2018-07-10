package com.github.carlhmitchell.contactablespicker.listViewHelpers;

public class ContentItem extends ListItem {

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