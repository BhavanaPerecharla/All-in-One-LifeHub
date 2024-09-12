package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return name + ": " + phoneNumber;
    }
}

public class Contacts extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final String CONTACTS_KEY = "contacts";
    private List<Contact> contacts = new ArrayList<>();
    private ArrayAdapter<Contact> adapter;
    private SharedPreferences sharedPreferences;
    private Contact contactToDelete;

    private final ActivityResultLauncher<Intent> addContactResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String phoneNumber = result.getData().getStringExtra("EXTRA_PHONE_NUMBER");
                    String contactName = result.getData().getStringExtra("EXTRA_CONTACT_NAME");
                    if (phoneNumber != null && contactName != null) {
                        Contact newContact = new Contact(contactName, phoneNumber);
                        contacts.add(newContact);
                        saveContacts();
                        adapter.notifyDataSetChanged();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        sharedPreferences = getSharedPreferences("MyContacts", MODE_PRIVATE);
        loadContacts();

        ListView contactListView = findViewById(R.id.contactListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
        contactListView.setAdapter(adapter);

        Button addContactButton = findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(v -> openAddContactDialog());

        ImageButton dialPadButton = findViewById(R.id.imageButton3);
        dialPadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Contacts.this, call_dial.class);
            addContactResultLauncher.launch(intent);
        });

        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            Contact contact = contacts.get(position);
            makePhoneCall(contact.getPhoneNumber());
        });

        registerForContextMenu(contactListView);
    }

    private void saveContacts() {
        String jsonContacts = new Gson().toJson(contacts);
        sharedPreferences.edit().putString(CONTACTS_KEY, jsonContacts).apply();
    }

    private void loadContacts() {
        String jsonContacts = sharedPreferences.getString(CONTACTS_KEY, null);
        if (jsonContacts != null) {
            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
            contacts = new Gson().fromJson(jsonContacts, type);
        }
    }

    private void openAddContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String phoneNumber = phoneEditText.getText().toString();
            if (isValidPhoneNumber(phoneNumber)) {
                Contact newContact = new Contact(name, phoneNumber);
                contacts.add(newContact);
                saveContacts();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(Contacts.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d+"); // Check for digits only
    }

    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, no action needed as calling is handled in makePhoneCall
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) return super.onContextItemSelected(item);

        int position = info.position;
        Contact selectedContact = contacts.get(position);

        if (item.getItemId() == R.id.edit) {
            // Handle edit contact
            openEditContactDialog(selectedContact);
            return true;
        } else if (item.getItemId() == R.id.delete) {
            contactToDelete = selectedContact;
            showDeleteConfirmationDialog();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void openEditContactDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_contact, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.editNameEditText);
        final EditText phoneEditText = dialogView.findViewById(R.id.editPhoneEditText);

        // Set the current values in the EditText fields
        nameEditText.setText(contact.getName());
        phoneEditText.setText(contact.getPhoneNumber());

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = nameEditText.getText().toString().trim();
            String newPhoneNumber = phoneEditText.getText().toString().trim();
            if (isValidPhoneNumber(newPhoneNumber)) {
                // Update the contact
                contact.setName(newName);
                contact.setPhoneNumber(newPhoneNumber);
                saveContacts();
                adapter.notifyDataSetChanged(); // Refresh the ListView
            } else {
                Toast.makeText(Contacts.this, "Invalid phone number. Please enter a 10-digit number.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            contacts.remove(contactToDelete);
            saveContacts();
            adapter.notifyDataSetChanged();
            contactToDelete = null; // Reset after deletion
        });
        builder.setNegativeButton("No", (dialog, which) -> contactToDelete = null); // Reset if canceled
        builder.create().show();
    }
}
