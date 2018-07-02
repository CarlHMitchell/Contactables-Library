package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.carlhmitchell.contactablespicker.utils.AppExecutor;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ContactRepository {
    private static final String DEBUG_TAG = "ContactRepository";

    private ContactDAO mContactDAO;
    public static LiveData<List<Contact>> mAllContactsLD;

    public ContactRepository(Context context) {
        ContactsDatabase db = ContactsDatabase.getDatabase(context.getApplicationContext());
        mContactDAO = db.contactDAO();
        getAllContactsLD();
    }



    private static class GetAllContactsLDAsyncTask extends AsyncTask<Void, Void, LiveData<List<Contact>>> {
        private ContactDAO mAsyncTaskDao;

        GetAllContactsLDAsyncTask(ContactDAO dao) {
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



    public void getAllContactsLD() {
        try {
            mAllContactsLD = new GetAllContactsLDAsyncTask(mContactDAO).execute().get();
        } catch (InterruptedException e) {
            Log.e(DEBUG_TAG, "Error, got Interrupted Exception:\n" + e);
        } catch (ExecutionException e) {
            Log.e(DEBUG_TAG, "Error, got Execution exception:\n" + e);
        }
    }

    public int getCount() {
        return mContactDAO.getCount();
    }

    public void insert(Contact contact) {
        new insertAsyncTask(mContactDAO).execute(contact);
    }

    public void delete(Contact contact) {
        new deleteAsyncTask(mContactDAO).execute(contact);
    }

    private static class insertAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mAsyncTaskDao;

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

    private static class deleteAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDAO mAsyncTaskDao;

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