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

/**
 * RecyclerView Adapter for the ContactsList.
 */
public class ContactsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //private static final String DEBUG_TAG = "ContactsListAdapter";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTENT = 1;
    private List<Contact> mContacts; // Cached copy of Contacts
    private List<ListItem> mList;    // List of items to display in the RecyclerView


    public ContactsListAdapter() {
    }

    /**
     * Updates the displayed contactablespicker_list of contact information.
     * Also updates the cached copy of the Contacts contactablespicker_list.
     *
     * @param contacts New contactablespicker_list of Contacts to display.
     */
    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        populateList();
        notifyDataSetChanged();
    }

    /**
     * Gets the contactablespicker_list of visible contacts in the RecyclerView.
     * @return This adapter's contactablespicker_list of displayed contacts.
     */
    public List<ListItem> getList() {
        return mList;
    }

    /**
     * Converts the List of Contacts into a contactablespicker_list of displayed contacts.
     *     Displayed contacts are ListItems, either NameHeaders (contact names) or ContentItems
     *     (phone numbers and/or email addresses).
     */
    private void populateList() {
        ArrayList<ListItem> tempList = new ArrayList<>();
        for (Contact contact : mContacts) {
            NameHeader nameHeader = new NameHeader();
            nameHeader.setContactName(contact.getContactName());
            nameHeader.setId(contact.getId());
            tempList.add(nameHeader);
            for (String phoneNumber : contact.getPhoneNumbers()) {
                if (!phoneNumber.equals("")) {
                    ContentItem phoneNumberItem = new ContentItem();
                    phoneNumberItem.setData(phoneNumber);
                    tempList.add(phoneNumberItem);
                }
            }
            for (String email : contact.getEmailAddresses()) {
                if (!email.equals("")) {
                    ContentItem emailAddressItem = new ContentItem();
                    emailAddressItem.setData(email);
                    tempList.add(emailAddressItem);
                }
            }
            if (tempList.get(tempList.size() - 1) instanceof NameHeader) {
                tempList.remove(tempList.size() - 1);
            }
        }
        mList = tempList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactablespicker_header, parent, false);
            return new VHHeader(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactablespicker_list, parent, false);
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

    /**
     * Determines the type of a ListItem at a given position in the contactablespicker_list.
     * @param position Position in a List of ListItems of the ListItem in question
     * @return Constant representing the type (Header or Content) of the ListItem
     */
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof NameHeader) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;
    }

    /**
     * Gets the number of items in the contactablespicker_list of displayed contacts.
     * @return The size of the contactablespicker_list of displayed contacts, or 0 if the contactablespicker_list is null.
     */
    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0; // can't return null for the empty contactablespicker_list, so return 0.
        }
    }

    /**
     * ViewHolder for Header (Contact name) items.
     */
    class VHHeader extends RecyclerView.ViewHolder {
        final TextView txtTitle;

        VHHeader(View itemView) {
            super(itemView);
            this.txtTitle = itemView.findViewById(R.id.contactablespicker_text_header);
        }
    }

    /**
     * ViewHolder for content items (displayed phone numbers/email addresses).
     */
    class VHItem extends RecyclerView.ViewHolder {
        final TextView txtData;

        VHItem(View itemView) {
            super(itemView);
            this.txtData = itemView.findViewById(R.id.contactablespicker_text_data);
        }
    }
}
