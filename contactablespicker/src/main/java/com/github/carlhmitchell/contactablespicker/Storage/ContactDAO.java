package com.github.carlhmitchell.contactablespicker.Storage;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContactDAO {

    /**
     * Gets a list of all contacts.
     *
     * @return All contacts in the database.
     */
    @Query("SELECT * FROM contacts_table")
    List<Contact> getAll();

    /**
     * Gets a List of all Contacts from the local database as a LiveData object, for use with
     * Observers. This allows a dynamically updating lifecycle-aware UI.
     *
     * @return A List of all Contacts in the database as a LiveData object.
     */
    @Query("SELECT * FROM contacts_table")
    LiveData<List<Contact>> getAllLD();

    /**
     * Gets a Contact from the database by its ID.
     * @param id ID of the Contact to retrieve.
     * @return Contact with the given ID.
     */
    @Query("SELECT * FROM contacts_table WHERE id LIKE (:id)")
    Contact getById(long id);

    /**
     * Inserts a Contact into the database, replacing it if a contact with the same ID is already
     *     present.
     * @param contact Contact to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    /**
     * Remove a Contact from the database.
     * @param contact Contact to remove.
     */
    @Delete
    void delete(Contact contact);

    // Some unused methods. Possibly needed for future enhancements or testing.

    /*
    @Query("DELETE FROM contacts_table")
    void deleteAll();
    */

    /*
    @Query("SELECT COUNT (*) FROM contacts_table")
    int getCount();
    */

    /*
    @Query("SELECT * FROM contacts_table WHERE id IN(:ids)")
    List<Contact> loadAllByIds(long[] ids);
    */

    /*
    @Query("SELECT * FROM contacts_table WHERE id IN(:ids)")
    LiveData<List<Contact>> loadAllByIdsLD(long[] ids);
    */


    /*
    @Query("SELECT * FROM contacts_table WHERE contact_name LIKE :displayName LIMIT 1")
    Contact findByName(String displayName);
    */

    /*
    @Query("SELECT * FROM contacts_table WHERE contact_name LIKE :displayName LIMIT 1")
    LiveData<Contact> findByNameLD(String displayName);
    */
}
