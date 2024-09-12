package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;

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
        clearBtn.setOnClickListener(v -> phoneNumberEditText.setText(""));

        // Call button functionality
        callBtn.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            if (!phoneNumber.isEmpty()) {
                if (ContextCompat.checkSelfPermission(call_dial.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, make the call
                    makePhoneCall(phoneNumber);
                } else {
                    // Request CALL_PHONE permission
                    ActivityCompat.requestPermissions(call_dial.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
                }
            } else {
                Toast.makeText(call_dial.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            }
        });
        saveBtn.setOnClickListener(v -> startActivity(new Intent(call_dial.this, Contacts.class)));

    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                String phoneNumber = phoneNumberEditText.getText().toString().trim();
                makePhoneCall(phoneNumber);
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
