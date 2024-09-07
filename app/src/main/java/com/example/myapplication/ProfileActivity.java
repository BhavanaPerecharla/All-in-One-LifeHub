package com.example.myapplication;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvFullName, tvEmail, tvPhone, tvBirthday, tvGender, tvBio, tvAddress, tvState, tvCountry, tvAge;
    private ImageView profileImage;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvGender = findViewById(R.id.tvGender);
        tvBio = findViewById(R.id.tvBio);
        tvAddress = findViewById(R.id.tvAddress);
        tvState = findViewById(R.id.tvState);
        tvCountry = findViewById(R.id.tvCountry);
        tvAge = findViewById(R.id.tvAge);
        profileImage = findViewById(R.id.ivProfilePhoto);

        String identifier = getIntent().getStringExtra("USERNAME");

        // If identifier is not null, fetch and display user details
        if (identifier != null) {
            displayProfile(identifier);

        } else {

            tvUsername.setText("No user information available.");
        }

        setupEditProfileButton(identifier);
    }

    private void displayProfile(String identifier) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Determine the type of identifier and query accordingly
            if (identifier.contains("@")) {
                // Assuming it's an email
                cursor = db.query("users", null, "email = ?", new String[]{identifier}, null, null, null);
            } else if (identifier.matches("\\d+")) {
                // Assuming it's a phone number
                cursor = db.query("users", null, "phone = ?", new String[]{identifier}, null, null, null);
            } else {
                // Assuming it's a username
                cursor = db.query("users", null, "username = ?", new String[]{identifier}, null, null, null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                // Retrieve and display user details with labels
                tvUsername.setText("USERNAME: " + cursor.getString(cursor.getColumnIndexOrThrow("username")));
                tvFullName.setText("FULL NAME: " + cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                tvEmail.setText("EMAIL: " + cursor.getString(cursor.getColumnIndexOrThrow("email")));
                tvPhone.setText("PHONE: " + cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                tvBirthday.setText("BIRTHDAY: " + cursor.getString(cursor.getColumnIndexOrThrow("birthday")));
                tvGender.setText("GENDER: " + cursor.getString(cursor.getColumnIndexOrThrow("gender")));
                tvBio.setText("BIO: " + cursor.getString(cursor.getColumnIndexOrThrow("bio")));
                tvAddress.setText("ADDRESS: " + cursor.getString(cursor.getColumnIndexOrThrow("address")));
                tvState.setText("STATE: " + cursor.getString(cursor.getColumnIndexOrThrow("state")));
                tvCountry.setText("COUNTRY: " + cursor.getString(cursor.getColumnIndexOrThrow("country")));
                tvAge.setText("AGE: " + cursor.getInt(cursor.getColumnIndexOrThrow("age"))); // Assuming age is an integer

                // Load profile image if available
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_photo"));
                if (imageBytes != null) {
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                }
            } else {
                // Handle the case where no data is returned
                tvUsername.setText("No user details found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any errors that may have occurred
            tvUsername.setText("Error retrieving user details.");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void setupEditProfileButton(String username) {
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }
}
