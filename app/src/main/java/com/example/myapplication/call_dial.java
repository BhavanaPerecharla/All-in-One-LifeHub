package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.provider.ContactsContract;

public class call_dial extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;
    private EditText phoneNumberEditText;
    private Button clearBtn, saveBtn, callBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_dial);

        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        clearBtn = findViewById(R.id.clearBtn);
        callBtn = findViewById(R.id.callBtn);
        saveBtn = findViewById(R.id.saveBtn);

        // Clear button functionality
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberEditText.setText("");
            }
        });

        // Call button functionality
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    if (ContextCompat.checkSelfPermission(call_dial.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted, make the call
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    } else {
                        // Request CALL_PHONE permission
                        ActivityCompat.requestPermissions(call_dial.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                    }
                }
            }
        });

        // Save button functionality
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString();
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                startActivity(intent);
            }
        });
    }

    // Handle the result of permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            } else {
                // Permission denied, handle it (e.g., show a message)
                Toast.makeText(this, "Permission Denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to handle button inputs for phone number
    public void inputNumber(View v) {
        Button btn = (Button) v;
        String digit = btn.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        phoneNumberEditText.setText(phoneNumber + digit);
    }
}
