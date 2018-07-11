package com.github.carlhmitchell.contactablespicker.listViewHelpers;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.carlhmitchell.contactablespicker.R;
import com.github.carlhmitchell.contactablespicker.Storage.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //private static final String DEBUG_TAG = "ContactsListAdapter";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Contact> mContacts; // Cached copy of Contacts
    private List<ListItem> mList;

    public ContactsListAdapter() {
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        populateList();
        notifyDataSetChanged();
    }

    // Unused method. Could be useful for enhancements later.
    /*
    public List<Contact> getContacts() {
        return mContacts;
    }
    */

    public List<ListItem> getList() {
        return mList;
    }

    private void populateList() {
        ArrayList<ListItem> tempList = new ArrayList<>();
        for (Contact contact : mContacts) {
            NameHeader nameHeader = new NameHeader();
            nameHeader.setContactName(contact.getContactName());
            nameHeader.setId(contact.getId());
            tempList.add(nameHeader);
            for (String phoneNumber : contact.getPhoneNumbers()) {
                ContentItem phoneNumberItem = new ContentItem();
                phoneNumberItem.setData(phoneNumber);
                tempList.add(phoneNumberItem);
            }
            for (String email : contact.getEmailAddresses()) {
                ContentItem emailAddressItem = new ContentItem();
                emailAddressItem.setData(email);
                tempList.add(emailAddressItem);
            }
        }
        mList = tempList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            return new VHHeader(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
            return new VHItem(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHHeader) {
            NameHeader currentItem = (NameHeader) mList.get(position);
            VHHeader VHheader = (VHHeader) holder;
            VHheader.txtTitle.setText(currentItem.getContactName());
        } else if (holder instanceof VHItem) {
            ContentItem currentItem = (ContentItem) mList.get(position);
            VHItem VHitem = (VHItem) holder;
            VHitem.txtData.setText(currentItem.getData());
        }
    }

    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {

        return mList.get(position) instanceof NameHeader;

    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0; // can't return null for the empty list, so return 0.
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        final TextView txtTitle;

        VHHeader(View itemView) {
            super(itemView);
            this.txtTitle = itemView.findViewById(R.id.text_header);
        }
    }

    class VHItem extends RecyclerView.ViewHolder {
        final TextView txtData;

        VHItem(View itemView) {
            super(itemView);
            this.txtData = itemView.findViewById(R.id.text_data);
        }
    }
}
