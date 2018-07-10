package com.github.carlhmitchell.contactablespicker.listViewHelpers;

import android.arch.lifecycle.ViewModelProviders;

import com.github.carlhmitchell.contactablespicker.ContactsList;
import com.github.carlhmitchell.contactablespicker.ContactsListViewModel;
import com.github.carlhmitchell.contactablespicker.Storage.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsListGenerator {
    List<ListItem> mList;
    List<Contact> mContacts;

    public ContactsListGenerator(List<Contact> contacts) {
        mContacts = contacts;
    }

    public ArrayList<ListItem> getList() {
        ArrayList<ListItem> arrayList = new ArrayList<>();
        for (Contact contact: mContacts) {
            Header header = new Header();
            header.setContactName(contact.getContactName());
            arrayList.add(header);
            for (String phoneNumber : contact.getPhoneNumbers()) {
                ContentItem item = new ContentItem();
                item.setData(phoneNumber);
                arrayList.add(item);
            }
            for (String email : contact.getEmailAddresses()) {
                ContentItem item = new ContentItem();
                item.setData(email);
                arrayList.add(item);
            }
        }
        return arrayList;
    }
}
