package com.example.myapplication;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etUsernameOrEmail, etDateOfBirth;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        databaseHelper = new DatabaseHelper(this);

        // Set up the DatePicker for the Date of Birth field using lambda
        etDateOfBirth.setOnClickListener(v -> showDatePicker());

        // Reset password button - this can be converted to a local variable
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(view -> resetPassword());
    }

    private void showDatePicker() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog using lambda
        DatePickerDialog datePickerDialog = new DatePickerDialog(ForgotPasswordActivity.this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Format the date and set it to the EditText
                    String dateOfBirth = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etDateOfBirth.setText(dateOfBirth);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void resetPassword() {
        String identifier = etUsernameOrEmail.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();

        if (identifier.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(ForgotPasswordActivity.this, "Please enter both your username/email and date of birth.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user exists with the matching identifier and date of birth
        if (databaseHelper.userExistsWithDOB(identifier, dateOfBirth)) {
            // Ask user to enter new password
            showNewPasswordDialog(identifier);
        } else {
            Toast.makeText(ForgotPasswordActivity.this, "User not found or date of birth does not match. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNewPasswordDialog(final String identifier) {
        // Create a dialog to prompt for the new password
        final EditText newPasswordInput = new EditText(ForgotPasswordActivity.this);
        newPasswordInput.setHint("Enter new password");

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setView(newPasswordInput)
                .setPositiveButton("Update", (DialogInterface dialog, int which) -> {
                    String newPassword = newPasswordInput.getText().toString().trim();
                    if (!newPassword.isEmpty()) {
                        databaseHelper.updatePassword(identifier, newPassword);
                        Toast.makeText(ForgotPasswordActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after success
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
