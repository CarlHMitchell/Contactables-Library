package com.github.carlhmitchell.contactablespicker.Storage;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInitializer {
    public static void populateAsync(final ContactsDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final ContactsDatabase db) {
        populateWithTestData(db);
    }

    private static void populateWithTestData(ContactsDatabase db) {
        List<String> phonesTest = new ArrayList<>();
        phonesTest.add("7605937583");
        List<String> emailTest = new ArrayList<>();
        emailTest.add("peregrinebf@gmail.com");
        Contact contact = new Contact("Test", phonesTest, emailTest);

        try {
            db.contactDAO().insert(contact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final ContactsDatabase mDb;

        PopulateDbAsync(ContactsDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }
    }
}
