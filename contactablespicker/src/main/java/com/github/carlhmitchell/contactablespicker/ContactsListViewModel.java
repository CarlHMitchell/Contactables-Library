package com.github.carlhmitchell.contactablespicker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.Storage.ContactRepository;
import com.github.carlhmitchell.contactablespicker.Storage.ContactsDatabase;

import java.util.List;


/*
 * ViewModel to keep a reference to the Contact repository and an up-to-date list of all Contacts.
 */
public class ContactsListViewModel extends AndroidViewModel {

    private ContactRepository repository;
    /*
     *  Using LiveData and caching what getAllContactsLD() returns has several benefits:
     *  - We can put an Observer on the data (instead of polling for changes) and only update the
     *      UI when the data actually changes.
     *  - The Repository is completely separated from the UI through the ViewModel.
     */
    private LiveData<List<Contact>> contactsList;

    public ContactsListViewModel(@NonNull Application application) {
        super(application);

        repository = new ContactRepository(application);
        Log.i("ContactsList View Model", "Retreiving data from database");
        contactsList = repository.mAllContactsLD;
    }

    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    public void insert(Contact contact) {
        repository.insert(contact);
    }

    public void delete(Contact contact) {
        repository.delete(contact);
    }
}
