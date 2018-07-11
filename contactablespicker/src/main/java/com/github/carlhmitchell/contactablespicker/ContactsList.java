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
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ContactsListAdapter;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ContactsListViewModel;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ContentItem;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ListItem;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.NameHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static android.provider.ContactsContract.CommonDataKinds.Email;
import static android.provider.ContactsContract.CommonDataKinds.Identity;
import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static com.github.carlhmitchell.contactablespicker.utils.AppConstants.CONTACT_PICKER_RESULT;

public class ContactsList extends AppCompatActivity {
    private static final String DEBUG_TAG = "ContactsList";

    private ContactsListAdapter mAdapter;
    private ContactsListViewModel mContactsListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // Setup the Toolbar to show the application name.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The Floating Action Button launches the Android contact picker.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.contacts_list_recycler_view);

        // Set to true to improve performance if changes in content do not change the layout size of
        // the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mContactsListViewModel = ViewModelProviders.of(this).get(ContactsListViewModel.class);

        // Get a new Adapter.
        mAdapter = new ContactsListAdapter();

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

        // Specify which Adapter the RecyclerView should use.
        mRecyclerView.setAdapter(mAdapter);


        // Make mRecyclerView swipe to the left and right.
        // Implement delete on swipe.
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
                Log.d(DEBUG_TAG, "AdapterPosition: " + position);
                final List<ListItem> list = mAdapter.getList();
                final ListItem item = list.get(position);
                Log.d(DEBUG_TAG, "Item: " + item.toString());

                /* The database stores Contacts, not ListItems. If the user swipes on a phone number
                 *    or email address the contact ID is in that item's header. This finds the
                 *    appropriate header.
                 */
                ListItem currentHeader = new ListItem();
                for (int i = 0; i <= position; i++) {
                    ListItem tempItem = list.get(i);
                    if (tempItem instanceof NameHeader) {
                        currentHeader = tempItem;
                        Log.d("ListIteration", "NameHeader ID: " + tempItem.getId());
                    } else {
                        Log.d("ListIteration", "ID: " + tempItem.getId());
                    }
                }

                final long id = currentHeader.getId();

                /*
                 * If the swiped item is a Header (contact name), delete the entire Contact.
                 * If the swiped item is a ContentItem (phone number or email address)
                 *     delete only that item and update the Contact in the database.
                 */
                Log.d(DEBUG_TAG, "id: " + id);
                if (item instanceof NameHeader) {
                    try {
                        Contact contact = mContactsListViewModel.getContactById(id);
                        mContactsListViewModel.delete(contact);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if (item instanceof ContentItem) {
                    Log.d(DEBUG_TAG, "Content item found: " + item.getData());
                    String data = item.getData();
                    try {
                        Contact contact = mContactsListViewModel.getContactById(id);
                        List<String> phoneNumbers = contact.getPhoneNumbers();
                        ArrayList<String> numbers = new ArrayList<>(phoneNumbers);
                        List<String> emailAddresses = contact.getEmailAddresses();
                        ArrayList<String> emails = new ArrayList<>(emailAddresses);
                        try {
                            numbers.remove(data);
                            emails.remove(data);
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "Error removing item: " + e);
                            e.printStackTrace();
                        }
                        contact.setPhoneNumbers(numbers);
                        contact.setEmailAddresses(emails);

                        mContactsListViewModel.insert(contact);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    /**
     * Start the Android contact picker to get the URI of the user selected contact.
     */
    private void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * Get the URI of the Contact the user selected, and parse that Contact to store it in the
     * Database.
     *
     * @param requestCode Request code sent to the Contact Picker.
     * @param resultCode  Result code from the Contact Picker. If this is anything other than
     *                    RESULT_OK there won't be a URI to parse.
     * @param data        URI of the contact the user selected.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String contactID = "";
        String contactName = "";
        String number;
        ArrayList<String> numbers = new ArrayList<>();
        String email;
        ArrayList<String> emailAddresses = new ArrayList<>();
        switch (resultCode) {
            case RESULT_OK: {
                try {
                    Uri uri = data.getData();
                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(Objects.requireNonNull(uri, "Error, null URI from contact picker"),
                                             null, null, null, null);
                    if (Objects.requireNonNull(cursor, "Contact URI cursor null!").moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(Identity.DISPLAY_NAME);
                        contactName = cursor.getString(nameIndex);
                        // Example: Show the name.
                        Toast.makeText(this, "Name: " + contactName, Toast.LENGTH_SHORT).show();
                        Log.i(DEBUG_TAG, "User selected contact " + contactName);
                        contactID =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        Log.i(DEBUG_TAG, "Contact ID: " + contactID);

                        // Get all phone numbers
                        try {
                            Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                                     Phone.CONTACT_ID + " = " + contactID,
                                                     null, null);
                            while (Objects.requireNonNull(phones, "Phones cursor null!").moveToNext()) {
                                number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                                Log.i(DEBUG_TAG, "Got phone number " + number);

                                numbers.add(number);
                            }
                            phones.close();
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "Failed to get phone numbers.");
                            e.printStackTrace();
                        }


                        // Get all email addresses
                        try {
                            Cursor emails = cr.query(Email.CONTENT_URI, null,
                                                     Email.CONTACT_ID + " = " + contactID,
                                                     null, null);
                            while (Objects.requireNonNull(emails, "Emails cursor null!").moveToNext()) {
                                email = emails.getString(emails.getColumnIndex(Email.ADDRESS));
                                Log.i(DEBUG_TAG, "Got email address: " + email);
                                // Like with phone numbers, for some uses someone might want to be able
                                //    to filter by type.
                                emailAddresses.add(email);
                            }
                            emails.close();
                        } catch (Exception e) {
                            Log.e(DEBUG_TAG, "Failed to get email addresses");
                            e.printStackTrace();
                        }
                    }
                    cursor.close();

                    Contact contact = new Contact(Integer.parseInt(contactID), contactName, numbers, emailAddresses);
                    mContactsListViewModel.insert(contact);
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Failed to get contact name: \n" + e);
                    e.printStackTrace();
                }
            }
            break;
            case RESULT_CANCELED: {
                Toast.makeText(this, "Selection cancelled.", Toast.LENGTH_SHORT).show();
            }
            break;
            default: {
                Toast.makeText(this, "Invalid result code.", Toast.LENGTH_SHORT).show();
                Log.e(DEBUG_TAG, "Invalid result code.");
            }
            break;
        }
        mAdapter.notifyDataSetChanged();
    }

}
