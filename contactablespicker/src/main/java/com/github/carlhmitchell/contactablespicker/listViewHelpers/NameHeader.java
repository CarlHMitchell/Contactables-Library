package com.github.carlhmitchell.contactablespicker.listViewHelpers;

/**
 * NameHeader contains the Contact's name for display as a header in the list of contacts.
 */
public class NameHeader extends ListItem {
    //private static final String DEBUG_TAG = "NameHeader";

    private String contactName;

    NameHeader() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}