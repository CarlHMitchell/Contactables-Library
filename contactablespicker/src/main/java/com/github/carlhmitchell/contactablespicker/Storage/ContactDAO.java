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
// --Commented out by Inspection START (2018-07-10 20:56):
//    @Query("SELECT * FROM contacts_table")
//    List<Contact> getAll();
// --Commented out by Inspection STOP (2018-07-10 20:56)

    @Query("SELECT * FROM contacts_table")
    LiveData<List<Contact>> getAllLD();

    @Query("SELECT * FROM contacts_table WHERE id LIKE (:id)")
    Contact getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

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
