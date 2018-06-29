package com.github.carlhmitchell.contactablespicker;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.provider.ContactsContract.CommonDataKinds.*;
import static com.github.carlhmitchell.contactablespicker.utils.AppConstants.CONTACT_PICKER_RESULT;

public class ContactsList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.contacts_list_recycler_view);

        // Set to true to improve performance if changes in content do not change the layout size of
        // the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        // sepcify an adapter
        mAdapter = new ContactsListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker();
            }
        });
    }

    private void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //TODO: Move this into its own class.
        switch (resultCode){
            case RESULT_OK: {
                //TODO: shrink this into small try/catch blocks instead of one big one.
                try {
                    Uri uri = data.getData();
                    // Todo: use CursorLoaders instead of Cursors & ContentResolvers directly.
                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(uri, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(Identity.DISPLAY_NAME);
                        String contactName = cursor.getString(nameIndex);
                        // Example: Show the name.
                        Toast.makeText(this, "Name: " + contactName, Toast.LENGTH_SHORT).show();
                        Log.i("ContactsList", "User selected contact " + contactName);
                        String contactID =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        Log.i("ContactsList", "Contact ID: " + contactID);

                        // Get all phone numbers
                        Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                                 Phone.CONTACT_ID + " = " + contactID,
                                                 null, null);
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                            //Log.i("ContactsList", "Got phone number " + number);
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
                            String email = emails.getString(emails.getColumnIndex(Email.ADDRESS));
                            int type = emails.getInt(emails.getColumnIndex(Email.TYPE));
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
