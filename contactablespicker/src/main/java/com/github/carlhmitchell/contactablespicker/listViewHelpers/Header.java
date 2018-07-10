package com.github.carlhmitchell.contactablespicker.listViewHelpers;

public class Header extends ListItem {
    private String contactName;

    public Header() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}