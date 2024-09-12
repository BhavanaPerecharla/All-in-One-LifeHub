package com.example.myapplication;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

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
            tvUsername.setText(R.string.no_user_info);
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
                tvUsername.setText(getString(R.string.username_label, cursor.getString(cursor.getColumnIndexOrThrow("username"))));
                tvFullName.setText(getString(R.string.full_name_label, cursor.getString(cursor.getColumnIndexOrThrow("full_name"))));
                tvEmail.setText(getString(R.string.email_label, cursor.getString(cursor.getColumnIndexOrThrow("email"))));
                tvPhone.setText(getString(R.string.phone_label, cursor.getString(cursor.getColumnIndexOrThrow("phone"))));
                tvBirthday.setText(getString(R.string.birthday_label, cursor.getString(cursor.getColumnIndexOrThrow("birthday"))));
                tvGender.setText(getString(R.string.gender_label, cursor.getString(cursor.getColumnIndexOrThrow("gender"))));
                tvBio.setText(getString(R.string.bio_label, cursor.getString(cursor.getColumnIndexOrThrow("bio"))));
                tvAddress.setText(getString(R.string.address_label, cursor.getString(cursor.getColumnIndexOrThrow("address"))));
                tvState.setText(getString(R.string.state_label, cursor.getString(cursor.getColumnIndexOrThrow("state"))));
                tvCountry.setText(getString(R.string.country_label, cursor.getString(cursor.getColumnIndexOrThrow("country"))));
                tvAge.setText(getString(R.string.age_label, cursor.getInt(cursor.getColumnIndexOrThrow("age")))); // Assuming age is an integer

                // Load profile image if available
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_photo"));
                if (imageBytes != null) {
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                }
            } else {
                // Handle the case where no data is returned
                tvUsername.setText(R.string.no_user_details);
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error retrieving user details", e);
            // Handle any errors that may have occurred
            tvUsername.setText(R.string.error_retrieving_user_details);
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

