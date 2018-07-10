package com.github.carlhmitchell.contactablespicker;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.carlhmitchell.contactablespicker.Storage.Contact;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ContentItem;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.Header;
import com.github.carlhmitchell.contactablespicker.listViewHelpers.ListItem;

import java.util.ArrayList;
import java.util.List;

public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Contact> mContacts; // Cached copy of Contacts
    private List<ListItem> mList;

    public ContactsListAdapter(List<Contact> contacts) {
        setContacts(contacts);
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        mList = getList();
        notifyDataSetChanged();
    }

    public List<Contact> getContacts() {
        return mContacts;
    }

    public ArrayList<ListItem> getList() {
        ArrayList<ListItem> arrayList = new ArrayList<>();
        for (Contact contact: mContacts) {
            Header header = new Header();
            header.setContactName(contact.getContactName());
            arrayList.add(header);
            for (String phoneNumber : contact.getPhoneNumbers()) {
                ContentItem item = new ContentItem();
                item.setData(phoneNumber);
                arrayList.add(item);
            }
            for (String email : contact.getEmailAddresses()) {
                ContentItem item = new ContentItem();
                item.setData(email);
                arrayList.add(item);
            }
        }
        return arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            return new VHHeader(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
            return new VHItem(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof VHHeader) {
            // VHHeader VHheader = (VHHeader)holder;
            Header currentItem = (Header) mList.get(position);
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

        return mList.get(position) instanceof Header;

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
        TextView txtTitle;

        public VHHeader(View itemView) {
            super(itemView);
            this.txtTitle = itemView.findViewById(R.id.text_header);
        }
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView txtData;

        public VHItem(View itemView) {
            super(itemView);
            this.txtData = itemView.findViewById(R.id.text_data);
        }
    }
}
