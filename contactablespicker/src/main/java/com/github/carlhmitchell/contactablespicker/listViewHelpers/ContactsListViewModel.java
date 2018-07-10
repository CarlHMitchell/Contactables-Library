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


/*
 * ViewModel to keep a reference to the Contact repository and an up-to-date list of all Contacts.
 */
public class ContactsListViewModel extends AndroidViewModel {

    private final ContactRepository repository;
    /*
     *  Using LiveData and caching what getAllContactsLD() returns has several benefits:
     *  - We can put an Observer on the data (instead of polling for changes) and only update the
     *      UI when the data actually changes.
     *  - The Repository is completely separated from the UI through the ViewModel.
     */
    private final LiveData<List<Contact>> contactsList;

    public ContactsListViewModel(@NonNull Application application) {
        super(application);

        repository = new ContactRepository(application);
        Log.i("ContactsList View Model", "Retrieving data from database");
        contactsList = ContactRepository.mAllContactsLD;
    }

    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    public Contact getContactById(long id) throws InterruptedException, ExecutionException{
        return repository.getById(id);
    }

    public void insert(Contact contact) {
        repository.insert(contact);
    }

    public void delete(Contact contact) {
        repository.delete(contact);
    }
}
