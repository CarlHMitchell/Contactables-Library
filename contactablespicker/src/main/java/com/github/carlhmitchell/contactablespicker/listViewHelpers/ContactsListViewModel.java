package com.github.carlhmitchell.contactablespicker.listViewHelpers;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * ViewModel to keep a reference to the Contact repository and an up-to-date list of all Contacts.
 */
public class ContactsListViewModel extends AndroidViewModel {
    private static final String DEBUG_TAG = "ContactsListViewModel";

    private final ContactRepository repository;
    /*
     *  Using LiveData and caching what getAllContactsLD() returns has several benefits:
     *  - We can put an Observer on the data (instead of polling for changes) and only update the
     *      UI when the data actually changes.
     *  - The Repository is completely separated from the UI through the ViewModel.
     */
    private final LiveData<List<Contact>> contactsList;

    /**
     * Initializes a repository and retrieves the contacts list from it.
     *
     * @param application The Application to which this ViewModel belongs.
     */
    public ContactsListViewModel(@NonNull Application application) {
        super(application);

        repository = new ContactRepository(application);
        Log.i(DEBUG_TAG, "Retrieving data from database");
        contactsList = ContactRepository.mAllContacts;
    }

    /**
     * Gets the LiveData copy of the contacts list.
     * @return LiveData instance of the contacts list.
     */
    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    /**
     * Gets a contact by its contact ID.
     * @param id  Long number which is the internal unique ID of the Contact.
     * @return Contact with the given ID.
     * @throws InterruptedException The retrieval occurs asynchronously. If it's interrupted this is thrown.
     * @throws ExecutionException The retrieval occurs asynchronously. If its thread crashes this is thrown.
     */
    public Contact getContactById(long id) throws InterruptedException, ExecutionException {
        return repository.getById(id);
    }

    /**
     * Inserts a new Contact (or replaces an existing Contact) into the local database.
     * @param contact Contact to insert.
     */
    public void insert(Contact contact) {
        repository.insert(contact);
    }

    /**
     * Deletes a Contact from the local database.
     * @param contact Contact to delete.
     */
    public void delete(Contact contact) {
        repository.delete(contact);
    }
}
