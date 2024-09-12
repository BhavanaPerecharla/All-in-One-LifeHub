package com.example.myapplication;
import android.app.DatePickerDialog;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {
    private Spinner spinnerGender;
    private ImageView profileImage;
    private EditText etUsername, etFullName, etEmail, etPhone, etBirthday, etBio, etAddress, etState, etCountry, etAge;
    private Uri imageUri;
    private DatabaseHelper databaseHelper;
    private String username;

    // Remove unnecessary class fields
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        spinnerGender = findViewById(R.id.spinnerGender);
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
        Button btnSaveChanges = findViewById(R.id.btnSaveChanges); // Local variable

        // Set up the gender spinner locally
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("USERNAME");

        if (username != null) {
            loadProfileData(username, adapter); // Pass adapter to loadProfileData
        }

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                    }
                }
        );

        profileImage.setOnClickListener(v -> openFileChooser());

        btnSaveChanges.setOnClickListener(view -> validateAndSaveProfile());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void loadProfileData(String username, ArrayAdapter<CharSequence> adapter) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        try (Cursor cursor = db.query("users", null, "username = ?", new String[]{username}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                etUsername.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                etFullName.setText(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
                etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                etPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
                etBirthday.setText(cursor.getString(cursor.getColumnIndexOrThrow("birthday")));

                // Set gender in spinner
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                spinnerGender.setSelection(adapter.getPosition(gender)); // Use adapter here

                etBio.setText(cursor.getString(cursor.getColumnIndexOrThrow("bio")));
                etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                etState.setText(cursor.getString(cursor.getColumnIndexOrThrow("state")));
                etCountry.setText(cursor.getString(cursor.getColumnIndexOrThrow("country")));
                etAge.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));

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
        }

        etBirthday.setOnClickListener(v -> openDatePicker());
    }
    // Open DatePicker for birthday selection
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfileActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date as a string (e.g., YYYY-MM-DD)
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etBirthday.setText(selectedDate);

                    // Automatically calculate age based on the selected date
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

        // If the current date is before the birthday this year, subtract one year from the age
        if (today.get(Calendar.MONTH) < month ||
                (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }
        return age;
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

        // Handle profile photo saving
        if (imageUri != null) {
            byte[] imageBytes = getImageBytes(imageUri);
            if (imageBytes != null) {
                values.put("profile_photo", imageBytes);
            }
        }

        long result = db.update("users", values, "username = ?", new String[]{username});
        if (result == -1) {
            Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert image URI to byte array
    private byte[] getImageBytes(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e("EditProfileActivity", "Error reading image bytes", e);
            return null;
        }
    }
}
