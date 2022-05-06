package com.datastructures.chatty.screens.chat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;


public class AddNewContact extends AppCompatActivity {
    private EditText displayNameEditor;
    private EditText phoneNumberEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_contact);
        setTitle("dev2qa.com - Android Add Phone Contact Example.");
        displayNameEditor = (EditText)findViewById(R.id.add_phone_contact_display_name);
        phoneNumberEditor = (EditText)findViewById(R.id.add_phone_contact_number);
        // Initialize phone type dropdown spinner.
        final Spinner phoneTypeSpinner = (Spinner)findViewById(R.id.add_phone_contact_type);
        String[] phoneTypeArr = {"Mobile", "Home", "Work"};
        ArrayAdapter<String> phoneTypeSpinnerAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phoneTypeArr);
        phoneTypeSpinner.setAdapter(phoneTypeSpinnerAdaptor);
        // Click this button to save user input phone contact info.
        Button savePhoneContactButton = (Button)findViewById(R.id.add_phone_contact_save_button);
        savePhoneContactButton.setOnClickListener(view -> {
            // Get android phone contact content provider uri.
            //Uri addContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            // Below uri can avoid java.lang.UnsupportedOperationException: URI: content://com.android.contacts/data/phones error.
            Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
            // Add an empty contact and get the generated id.
            long rowContactId = getRawContactId();
            // Add contact name data.
            String displayName = displayNameEditor.getText().toString();
            insertContactDisplayName(addContactsUri, rowContactId, displayName);
            // Add contact phone data.
            String phoneNumber = phoneNumberEditor.getText().toString();
            String phoneTypeStr = (String)phoneTypeSpinner.getSelectedItem();
            insertContactPhoneNumber(addContactsUri, rowContactId, phoneNumber, phoneTypeStr);
            Toast.makeText(getApplicationContext(),"Added Successfully" , Toast.LENGTH_LONG).show();
            finish();
        });
    }
    // This method will only insert an empty data to RawContacts.CONTENT_URI
    // The purpose is to get a system generated raw contact id.
    private long getRawContactId()
    {
        // Insert an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri);
    }
    // Insert newly created contact display name.
    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // Put contact display name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        getContentResolver().insert(addContactsUri, contentValues);
    }
    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber, String phoneTypeStr)
    {
        // Create a ContentValues object.
        ContentValues contentValues = new ContentValues();
        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // Each contact must have a mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        // Calculate phone type by user selection.
        int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        if("mobile".equalsIgnoreCase(phoneTypeStr))
        {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        }else if("work".equalsIgnoreCase(phoneTypeStr))
        {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        }
        // Put phone type value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType);
        // Insert new contact data into phone contact list.
        getContentResolver().insert(addContactsUri, contentValues);
    }
    // ListPhoneContactsActivity use this method to start this activity.
    public static void start(Context context)
    {
        Intent intent = new Intent(context, AddNewContact.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}