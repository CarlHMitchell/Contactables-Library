package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Database(entities = {Contact.class}, version = 2, exportSchema = false)
@TypeConverters({DBTypeConverters.class})
public abstract class ContactsDatabase extends RoomDatabase {
    private static ContactsDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    // If you want to keep the data through app restarts,
                    // comment out the following line.
                    //new PopulateTestDbAsync(INSTANCE).execute();
                }
            };

    public static ContactsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ContactsDatabase.class) {
                if (INSTANCE == null) {
                    // Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                    ContactsDatabase.class,
                                                    "contacts_database")
                                   .allowMainThreadQueries()
                                   .addCallback(sRoomDatabaseCallback)
                                   .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ContactsDatabase getTestInMemoryDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ContactsDatabase.class) {
                if (INSTANCE == null) {
                    // Create database. Allows creation in main thread. TESTING ONLY
                    INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                                                            ContactsDatabase.class)
                                   .allowMainThreadQueries()
                                   .addCallback(sRoomDatabaseCallback)
                                   .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ContactDAO contactDAO();



    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final ContactDAO mDao;

        PopulateDbAsync(ContactsDatabase db) {
            mDao = db.contactDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }

    private static class PopulateTestDbAsync extends AsyncTask<Void, Void, Void> {
        private final ContactDAO mDao;

        PopulateTestDbAsync(ContactsDatabase db) {
            mDao = db.contactDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();
            List<String> phonesTest = new ArrayList<>();
            phonesTest.add("7605937583");
            List<String> emailTest = new ArrayList<>();
            emailTest.add("peregrinebf@gmail.com");
            Contact contact = new Contact("PopulateTestDbAsync Name", phonesTest, emailTest);
            mDao.insert(contact);
            return null;
        }
    }

}
