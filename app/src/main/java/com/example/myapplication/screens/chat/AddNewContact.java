package com.example.myapplication.screens.chat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.datastructures.chatty.R;

import java.util.Objects;


public class AddNewContact extends AppCompatActivity {
    private EditText displayNameEditor;
    private EditText phoneNumberEditor;
    float v = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_phone_contact);
        Objects.requireNonNull(getSupportActionBar()).hide(); //to hide action bar

        displayNameEditor = findViewById(R.id.add_phone_contact_display_name);
        phoneNumberEditor = findViewById(R.id.add_phone_contact_number);
        final Spinner phoneTypeSpinner = findViewById(R.id.add_phone_contact_type);
        String[] phoneTypeArr = {"Mobile", "Home", "Work"};
        ArrayAdapter<String> phoneTypeSpinnerAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, phoneTypeArr);
        phoneTypeSpinner.setAdapter(phoneTypeSpinnerAdaptor);
        Button savePhoneContactButton = findViewById(R.id.add_phone_contact_save_button);

        savePhoneContactButton.setOnClickListener(view -> {
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

        displayNameEditor.setTranslationY(800);
        phoneNumberEditor.setTranslationY(800);
        phoneTypeSpinner.setTranslationY(800);
        savePhoneContactButton.setTranslationY(800);

        displayNameEditor.setAlpha(v);
        phoneNumberEditor.setAlpha(v);
        phoneTypeSpinner.setAlpha(v);
        savePhoneContactButton.setAlpha(v);

        displayNameEditor.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        phoneNumberEditor.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        phoneTypeSpinner.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        savePhoneContactButton.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

    }

    private long getRawContactId()
    {
        // Insert an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri);
    }
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