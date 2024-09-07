package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText etNewUsername, etNewPassword, etFullName, etEmail, etPhone, etAddress, etAge, etState, etCountry, etBio;
    private TextView tvBirthday,tvProfilePhotoPrompt;
    private Spinner spGender;
    private Button btnSignUp;
    private ImageView ivProfilePhoto;
    private DatabaseHelper databaseHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI elements
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        tvBirthday = findViewById(R.id.tvBirthday);
        spGender = findViewById(R.id.spGender);
        etAddress = findViewById(R.id.etAddress);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        etAge = findViewById(R.id.etAge);
        etState = findViewById(R.id.etState);
        etCountry = findViewById(R.id.etCountry);
        etBio = findViewById(R.id.etBio);
        btnSignUp = findViewById(R.id.btnSignUp);


        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);

        // Set up date picker for birthday
        tvBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                        tvBirthday.setText(selectedDate);
                        int age = calculateAge(year1, month1, dayOfMonth);
                        etAge.setText(String.valueOf(age));
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Set up profile photo picker
        ivProfilePhoto.setOnClickListener(v -> openFileChooser());

        // Set up sign up button
        btnSignUp.setOnClickListener(view -> registerUser());
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
            ivProfilePhoto.setImageURI(imageUri);
        }
    }

    private void registerUser() {
        String username = etNewUsername.getText().toString().trim();
        String password = etNewPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthday = tvBirthday.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();
        String address = etAddress.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        if (imageUri == null) {
            Toast.makeText(this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (birthday.isEmpty()) {
            Toast.makeText(this, "Please enter your birthday", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age.isEmpty()) {
            Toast.makeText(this, "Please enter your age", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(this, "Email must be a Gmail address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the username, email, or phone already exists
        if (databaseHelper.isValidUser(username, email, phone)) {
            Toast.makeText(this, "Username, email, or phone already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the new user
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("full_name", fullName);
        values.put("email", email);
        values.put("phone", phone);
        values.put("birthday", birthday);
        values.put("gender", gender);
        values.put("address", address);
        values.put("age", age);
        values.put("state", state);
        values.put("country", country);
        values.put("bio", bio);

        // Handle profile photo if available
        if (imageUri != null) {
            byte[] imageBytes = getImageBytes(imageUri);
            if (imageBytes != null) {
                values.put("profile_photo", imageBytes);
            }
        }

        long newRowId = db.insert("users", null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

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

    private int calculateAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) + 1 < month || (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }
        return age;
    }

}
