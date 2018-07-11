package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

/**
 * Class to create a connection to the Room database, or create the database if none exists yet.
 */
@Database(entities = {Contact.class}, version = 2, exportSchema = false)
@TypeConverters({DBTypeConverters.class})
public abstract class ContactsDatabase extends RoomDatabase {
    private static final String DEBUG_TAG = "ContactsDatabase";

    private static ContactsDatabase INSTANCE;

    public static ContactsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ContactsDatabase.class) {
                if (INSTANCE == null) {
                    // Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                                    ContactsDatabase.class,
                                                    "contacts_database")
                                   .build();
                    Log.i(DEBUG_TAG, "Database created.");
                }
            }
        }
        return INSTANCE;
    }

    public abstract ContactDAO contactDAO();
}
