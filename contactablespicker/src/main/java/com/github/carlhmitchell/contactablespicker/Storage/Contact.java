package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Contacts are represented in the local database as a name, a contactablespicker_list of phone numbers, and a contactablespicker_list of
 * email addresses.
 * This class allows manipulation of Contacts and their storage in the database using
 * Room annotations.
 */
@Entity(tableName = "contacts_table")
public class Contact {
    //private static final String DEBUG_TAG = "Contact";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "contact_name")
    private final String contactName;

    @ColumnInfo(name = "phone_numbers")
    private List<String> phoneNumbers;

    @ColumnInfo(name = "email_addresses")
    private List<String> emailAddresses;

    @Ignore
    public Contact(long id, @NonNull String contactName, List<String> phoneNumbers, List<String> emailAddresses) {
        this.id = id;
        this.contactName = contactName;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
    }

    Contact(@NonNull String contactName, List<String> phoneNumbers, List<String> emailAddresses) {
        this.contactName = contactName;
        this.phoneNumbers = phoneNumbers;
        this.emailAddresses = emailAddresses;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getContactName() {
        return contactName;
    }

    // Unused method. Possibly needed for future enhancements.
    /*
    public void setContactName(@NonNull String contactName) {
        this.contactName = contactName;
    }
    */

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    /**
     * Converts a Contact to a parseable String. Used for logging and testing.
     * @return String representation of the Contact.
     */
    public String toString() {
        //Ensure there's data in both phoneNumbers and emailAddresses. If not, set to empty lists.
        if ((phoneNumbers != null) && (emailAddresses != null)) {
            return "" + id + "," + contactName + "," + phoneNumbers.toString() + "," + emailAddresses.toString();
        } else if (emailAddresses != null) {
            return "" + id + "," + contactName + "," + "[\"\"]" + "," + emailAddresses.toString();
        } else if (phoneNumbers != null) {
            return "" + id + "," + contactName + "," + phoneNumbers.toString() + "," + "[\"\"]";
        } else {
            return "" + id + "," + contactName + "," + "[\"\"]" + "," + "[\"\"]";
        }
    }

    // Unused method. Possibly needed for future enhancements.
    /*
    public Contact getContact() {
        return this;
    }
    */
}