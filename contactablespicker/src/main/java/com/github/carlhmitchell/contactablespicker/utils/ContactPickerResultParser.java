package com.github.carlhmitchell.contactablespicker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Identity;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;

import java.util.ArrayList;
import java.util.Objects;

public class ContactPickerResultParser {
    private static final String DEBUG_TAG = "ContactResultParser";

    public ContactPickerResultParser() {
    }

    public Contact parseContactUri(Uri uri, Context context) {
        String contactID = "";
        String contactName = "";
        String number;
        ArrayList<String> numbers = new ArrayList<>();
        String email;
        ArrayList<String> emailAddresses = new ArrayList<>();
        Contact contact = null;
        try {
            ContextWrapper wrapper = new ContextWrapper(context);
            ContentResolver cr = wrapper.getContentResolver();
            Cursor cursor = cr.query(Objects.requireNonNull(uri, "Error, null URI from contact picker"),
                                     null, null, null, null);
            if (Objects.requireNonNull(cursor, "Contact URI cursor null!").moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(Identity.DISPLAY_NAME);
                contactName = cursor.getString(nameIndex);
                // Example: Show the name.
                Log.i(DEBUG_TAG, "User selected contact " + contactName);
                contactID =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Log.i(DEBUG_TAG, "Contact ID: " + contactID);

                // Get all phone numbers
                try {
                    Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                             Phone.CONTACT_ID + " = " + contactID,
                                             null, null);
                    while (Objects.requireNonNull(phones, "Phones cursor null!").moveToNext()) {
                        number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                        Log.i(DEBUG_TAG, "Got phone number " + number);

                        numbers.add(number);
                    }
                    phones.close();
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Failed to get phone numbers.");
                    e.printStackTrace();
                }

                // Get all email addresses
                try {
                    Cursor emails = cr.query(Email.CONTENT_URI, null,
                                             Email.CONTACT_ID + " = " + contactID,
                                             null, null);
                    while (Objects.requireNonNull(emails, "Emails cursor null!").moveToNext()) {
                        email = emails.getString(emails.getColumnIndex(Email.ADDRESS));
                        Log.i(DEBUG_TAG, "Got email address: " + email);

                        emailAddresses.add(email);
                    }
                    emails.close();
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Failed to get email addresses");
                    e.printStackTrace();
                }
            }
            cursor.close();

            contact = new Contact(Integer.parseInt(contactID), contactName, numbers, emailAddresses);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to get contact information: \n" + e);
            e.printStackTrace();
        }
        return contact;
    }
}
