package com.github.carlhmitchell.contactablespicker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.utils.AppExecutor;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Email;
import static android.provider.ContactsContract.CommonDataKinds.Identity;
import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static com.github.carlhmitchell.contactablespicker.utils.AppConstants.CONTACT_PICKER_RESULT;

public class ContactsList extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private ContactsListAdapter mAdapter;
    public ContactsListViewModel mContactsListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker();
            }
        });

        mRecyclerView = findViewById(R.id.contacts_list_recycler_view);

        // Set to true to improve performance if changes in content do not change the layout size of
        // the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get a new or existing ViewModel from the ViewModelProvider
        mContactsListViewModel = ViewModelProviders.of(this).get(ContactsListViewModel.class);

        // Add an observer on the LiveData returned by getContactsList()
        // The onChanged()) method fires when the observed data changes and the activity is in the
        // foreground
        mContactsListViewModel.getContactsList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts) {
                // update the cached copy of the contacts in the adapter.
                mAdapter = new ContactsListAdapter(contacts);
            }
        });

        // specify an adapter
        mRecyclerView.setAdapter(mAdapter);


        // make mRecyclerView swipe to the left and right.
        // implement delete on swipe.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // MUST implement onMove & onSwiped, as ItemTouchHelper.SimpleCallback is abstract.
            // Nothing needs to happen onMove
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            // Should delete the contact in the list.
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //get item position
                final int position = viewHolder.getAdapterPosition();
                final List<Contact> contacts = mAdapter.getContacts();

                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mContactsListViewModel.delete(contacts.get(position));
                    }
                });

            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Is this needed?

        // Add an observer on the LiveData returned by getContactsList()
        // The onChanged()) method fires when the observed data changes and the activity is in the
        // foreground
        mContactsListViewModel.getContactsList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> contacts) {
                // update the cached copy of the contacts in the adapter.
                mAdapter.setContacts(contacts);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String contactID = "";
        String contactName = "";
        String number;
        ArrayList<String> numbers = new ArrayList<>();
        String email;
        ArrayList<String> emailAddresses = new ArrayList<>();
        //TODO: Move this into its own class.
        switch (resultCode) {
            case RESULT_OK: {
                //TODO: shrink this into small try/catch blocks instead of one big one.
                try {
                    Uri uri = data.getData();
                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(uri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(Identity.DISPLAY_NAME);
                        contactName = cursor.getString(nameIndex);
                        // Example: Show the name.
                        Toast.makeText(this, "Name: " + contactName, Toast.LENGTH_SHORT).show();
                        Log.i("ContactsList", "User selected contact " + contactName);
                        contactID =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        Log.i("ContactsList", "Contact ID: " + contactID);

                        // Get all phone numbers
                        Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                                 Phone.CONTACT_ID + " = " + contactID,
                                                 null, null);
                        while (phones.moveToNext()) {
                            number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                            //Log.i("ContactsList", "Got phone number " + number);

                            // Could put this under Mobile number only. Leaving for all just in case.
                            numbers.add(number);

                            int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                            switch (type) {
                                case Phone.TYPE_HOME:
                                    Log.i("ContactsList", "Got Home number " + number);
                                    break;
                                case Phone.TYPE_MOBILE:
                                    Log.i("ContactsList", "Got Mobile number " + number);
                                    break;
                                case Phone.TYPE_WORK:
                                    Log.i("ContactsList", "Got Work number " + number);
                                    break;
                                default:
                                    Log.i("ContactsList", "Got other type of number " + number);
                                    break;
                            }
                        }
                        phones.close();

                        // Get all email addresses
                        Cursor emails = cr.query(Email.CONTENT_URI, null,
                                                 Email.CONTACT_ID + " = " + contactID,
                                                 null, null);
                        while (emails.moveToNext()) {
                            email = emails.getString(emails.getColumnIndex(Email.ADDRESS));
                            int type = emails.getInt(emails.getColumnIndex(Email.TYPE));

                            emailAddresses.add(email);

                            switch (type) {
                                case Email.TYPE_HOME:
                                    Log.i("ContactsList", "Got Home email " + email);
                                    break;
                                case Email.TYPE_WORK:
                                    Log.i("ContactsList", "Got Work email " + email);
                                    break;
                                default:
                                    Log.i("ContactsList", "Got other type of email " + email);
                                    break;
                            }
                        }
                        emails.close();

                    }
                    cursor.close();


                    Contact contact = new Contact(Integer.parseInt(contactID), contactName, numbers, emailAddresses);
                    mContactsListViewModel.insert(contact);
                } catch (Exception e) {
                    Log.e("ContactsList", "Failed to get contact name: \n" + e);
                }
            }
            break;
            case RESULT_CANCELED: {
                Toast.makeText(this, "Selection cancelled.", Toast.LENGTH_SHORT).show();
            }
            break;
            default: {
                Toast.makeText(this, "Invalid result code.", Toast.LENGTH_SHORT).show();
                Log.e("ContactsList", "Invalid result code.");
            }
            break;
        }
    }

}
