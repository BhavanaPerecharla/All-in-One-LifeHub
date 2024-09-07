package com.example.myapplication;

import android.graphics.BitmapFactory;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.net.Uri;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.DatePickerDialog;
import java.util.Calendar;


public class EditProfileActivity extends AppCompatActivity {
    private Spinner spinnerGender;
    private ImageView profileImage;
    private EditText etUsername, etFullName, etEmail, etPhone, etBirthday, etBio, etAddress, etState, etCountry, etAge;
    private Button btnSaveChanges;
    private DatabaseHelper databaseHelper;
    private String username;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        spinnerGender = findViewById(R.id.spinnerGender);

        // Initialize views
        profileImage = findViewById(R.id.editProfileImage);
        etUsername = findViewById(R.id.etUsername);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        etBio = findViewById(R.id.etBio);
        etAddress = findViewById(R.id.etAddress);
        etState = findViewById(R.id.etState);
        etCountry = findViewById(R.id.etCountry);
        etAge = findViewById(R.id.etAge);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Set up the spinner for gender selection
        adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Get the username from intent
        username = getIntent().getStringExtra("USERNAME");

        // Load user profile data
        if (username != null) {
            loadProfileData(username);
        }

        // Set up profile photo picker
        profileImage.setOnClickListener(v -> openFileChooser());

        // Set up save changes button with validation
        btnSaveChanges.setOnClickListener(view -> validateAndSaveProfile());
    }

    // Open DatePicker for birthday
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etBirthday.setText(selectedDate);

                    // Automatically calculate age based on selected date
                    int calculatedAge = calculateAge(selectedYear, selectedMonth, selectedDay);
                    etAge.setText(String.valueOf(calculatedAge));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Calculate age based on birthday
    private int calculateAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) < month || (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }
        return age;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    // Load profile data from the database for the given username
    private void loadProfileData(String username) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("users", null, "username = ?", new String[]{username}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                etUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("username"))); // Only display username, not for editing
                etFullName.setText(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                etBirthday.setText(cursor.getString(cursor.getColumnIndexOrThrow("birthday")));

                // Set gender in spinner
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                spinnerGender.setSelection(adapter.getPosition(gender));

                etBio.setText(cursor.getString(cursor.getColumnIndexOrThrow("bio")));
                etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                etState.setText(cursor.getString(cursor.getColumnIndexOrThrow("state")));
                etCountry.setText(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                etAge.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));

                // Load profile image if available
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("profile_photo"));
                if (imageBytes != null) {
                    profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                }
            } else {
                Toast.makeText(this, "No data found for username: " + username, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("EditProfileActivity", "Error loading profile data", e);
            Toast.makeText(this, "Error loading profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        // Set up birthday to open a DatePickerDialog
        etBirthday.setOnClickListener(v -> openDatePicker());
    }

    // Validate and save the profile changes to the database
    private void validateAndSaveProfile() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String bio = etBio.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String age = etAge.getText().toString().trim();

        // Validate fields
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email must be a Gmail address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (birthday.isEmpty()) {
            Toast.makeText(this, "Please enter your birthday", Toast.LENGTH_SHORT).show();
            return;
        }

        if (age.isEmpty() || Integer.parseInt(age) < 0) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with saving the profile
        saveProfileChanges(fullName, email, phone, birthday, gender, bio, address, state, country, age);
    }

    // Save the profile changes to the database
    private void saveProfileChanges(String fullName, String email, String phone, String birthday, String gender, String bio, String address, String state, String country, String age) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("full_name", fullName);
        values.put("email", email);
        values.put("phone", phone);
        values.put("birthday", birthday);
        values.put("gender", gender);
        values.put("bio", bio);
        values.put("address", address);
        values.put("state", state);
        values.put("country", country);
        values.put("age", Integer.parseInt(age));

        // Handle profile photo if available
        if (imageUri != null) {
            byte[] imageBytes = getImageBytes(imageUri);
            if (imageBytes != null) {
                values.put("profile_photo", imageBytes);
            }
        }

        // Update the database with new values
        int rowsAffected = db.update("users", values, "username=?", new String[]{username});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert image URI to byte array
    private byte[] getImageBytes(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


