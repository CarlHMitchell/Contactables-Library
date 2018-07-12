package com.github.carlhmitchell.contactablespicker;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.github.carlhmitchell.contactablespicker.utils.ContactPickerResultParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.github.carlhmitchell.contactablespicker.utils.AppConstants.CONTACT_PICKER_RESULT;
import static com.github.carlhmitchell.contactablespicker.utils.AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS;

public class ContactsList extends AppCompatActivity {
    private static final String DEBUG_TAG = "ContactsList";

    private ContactsListAdapter mAdapter;
    private ContactsListViewModel mContactsListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        // Setup the Toolbar to show the application name.
        Toolbar toolbar = findViewById(R.id.contactablespicker_toolbar);
        setSupportActionBar(toolbar);

        // The Floating Action Button launches the Android contact picker.
        FloatingActionButton fab = findViewById(R.id.contactablespicker_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLaunchContactPicker();
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.contactablespicker_contacts_list_recycler_view);

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                                              new String[]{Manifest.permission.READ_CONTACTS},
                                              MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doLaunchContactPicker();
                } else {
                    //Permission denied.
                    Snackbar.make(findViewById(R.id.contactablespicker_contacts_list_coordinator_layout),
                                  R.string.contactablespicker_permission_denied_error,
                                  LENGTH_LONG).show();
                }
            }
        }
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
        switch (resultCode) {
            case RESULT_OK: {
                try {
                    Uri uri = data.getData();
                    ContactPickerResultParser parser = new ContactPickerResultParser();
                    mContactsListViewModel.insert(parser.parseContactUri(uri, this));
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
