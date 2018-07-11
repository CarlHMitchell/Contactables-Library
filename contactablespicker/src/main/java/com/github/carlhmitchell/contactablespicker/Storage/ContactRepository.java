package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ContactRepository {
    private static final String DEBUG_TAG = "ContactRepository";

    private final ContactDAO mContactDAO;
    public static LiveData<List<Contact>> mAllContacts;

    /**
     * Initializes the database connection and gets a cached copy of the Contacts List as LiveData
     *
     * @param context Context from which to get the Application Context to open the database with.
     */
    public ContactRepository(Context context) {
        ContactsDatabase db = ContactsDatabase.getDatabase(context.getApplicationContext());
        mContactDAO = db.contactDAO();
        getAllContacts();
    }

    /**
     * Gets a Contact from the local database by its ID.
     *
     * @param id ID of the Contact to retrieve.
     * @return Contact found from the database.
     * @throws InterruptedException The retrieval occurs asynchronously. If it's interrupted this is thrown.
     * @throws ExecutionException   The retrieval occurs asynchronously. If its thread crashes this is thrown.
     */
    public Contact getById(long id) throws InterruptedException, ExecutionException {
        return new GetContactByIdAsyncTask(mContactDAO).execute(id).get();
    }

    private void getAllContacts() {
        try {
            mAllContacts = new GetAllContactsAsyncTask(mContactDAO).execute().get();
        } catch (InterruptedException e) {
            Log.e(DEBUG_TAG, "Error, got Interrupted Exception:\n" + e);
        } catch (ExecutionException e) {
            Log.e(DEBUG_TAG, "Error, got Execution exception:\n" + e);
        }
    }

    /**
     * Add a Contact to the database, or replace an existing Contact with the same ID.
     *
     * @param contact Contact to add or replace.
     */
    public void insert(Contact contact) {
        new insertAsyncTask(mContactDAO).execute(contact);
    }

    /**
     * Remove a Contact from the database.
     *
     * @param contact Contact to remove.
     */
    public void delete(Contact contact) {
        new deleteAsyncTask(mContactDAO).execute(contact);
    }

    /**
     * AsyncTask to get the Contacts List as LiveData
     */
    private static class GetAllContactsAsyncTask extends AsyncTask<Void, Void, LiveData<List<Contact>>> {
        private final ContactDAO mAsyncTaskDao;

        GetAllContactsAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected LiveData<List<Contact>> doInBackground(Void... voids) {
            LiveData<List<Contact>> tempList = mAsyncTaskDao.getAllLD();
            Log.d(DEBUG_TAG, "Got all contacts as LiveData");
            return tempList;
        }

        @Override
        protected void onPostExecute(LiveData<List<Contact>> list) {
        }
    }

    /**
     * AsyncTask to get a Contact by its ID.
     */
    private static class GetContactByIdAsyncTask extends AsyncTask<Long, Void, Contact> {
        private final ContactDAO mAsyncTaskDao;

        GetContactByIdAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Contact doInBackground(final Long... params) {
            return mAsyncTaskDao.getById(params[0]);
        }

        @Override
        protected void onPostExecute(Contact contact) {

        }
    }

    /**
     * AsyncTask to add or replace Contacts in the database.
     */
    private static class insertAsyncTask extends AsyncTask<Contact, Void, Void> {
        private final ContactDAO mAsyncTaskDao;

        insertAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mAsyncTaskDao.insert(params[0]);
            Log.d(DEBUG_TAG, "Inserted contact: " + params[0].toString());
            return null;
        }
    }

    /**
     * AsyncTask to remove Contacts from the database.
     */
    private static class deleteAsyncTask extends AsyncTask<Contact, Void, Void> {
        private final ContactDAO mAsyncTaskDao;

        deleteAsyncTask(ContactDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            mAsyncTaskDao.delete(params[0]);
            Log.d("ContactRepository", "Deleted contact: " + params[0].toString());
            return null;
        }
    }
}