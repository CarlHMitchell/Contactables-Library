package com.github.carlhmitchell.contactablespicker;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactViewHolder> {

    private List<Contact> mContacts; // Cached copy of Contacts

    private Context mContext;

    ContactsListAdapter(Context context) {
        mContext = context;
    }

    public ContactsListAdapter(Context context, List<Contact> contactList) {
        mContacts = contactList;
        mContext = context;
    }

    /*
     *   Inflates the layout for each list item
     */
    @Override
    @NonNull
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, parent, false);
        return new ContactViewHolder(itemView);
    }

    /*
     *  Configures layouts for the list item (sets text to TextViews)
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        if (mContacts != null) {
            Contact current = mContacts.get(position);
            int contactID = current.getId();
            String contactName = current.getContactName();
            holder.contactNameView.setText(contactName);
            List<String> contactPhones = current.getPhoneNumbers();
        } else {
            // Covers the case of data not being ready yet.
            holder.contactNameView.setText(R.string.no_contacts_to_display);
        }
    }


    /*
    When data changes this method updates the list of Contacts and notifies the adapter to use
    the new values in it.
    */
    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mContacts has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mContacts != null) {
            return mContacts.size();
        } else {
            return 0;
        }
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

    // Inner class for creating ViewHolders
    class ContactViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the contact name, phone number, and email views
        TextView contactNameView;
        TextView phoneNumberView;
        TextView emailAddressView;
        View view;


        public ContactViewHolder(View itemView) {
            super(itemView);

            // Define click listener for the ViewHolder's view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ContactsListAdapter", "Element " + getAdapterPosition() + " clicked.");
                }
            });
            contactNameView = itemView.findViewById(R.id.recyclerContactName);
            phoneNumberView = itemView.findViewById(R.id.recyclerPhonesList);
            emailAddressView = itemView.findViewById(R.id.recyclerEmailsList);

            view = itemView;
        }

        // These getters probably won't be needed.

        public TextView getContactNameView() {
            return contactNameView;
        }

        public TextView getPhoneNumberView() {
            return phoneNumberView;
        }

        public TextView getEmailAddressView() {
            return emailAddressView;
        }

    }
}
