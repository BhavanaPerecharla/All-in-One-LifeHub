package com.example.myapplication;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Mail extends AppCompatActivity {


    private EditText editTextRecipient, editTextSubject, editTextMessageBody;
    private static final ArrayList<String> sentMailsHistory = new ArrayList<>(); // Store sent mails history


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        // Initialize views
        Button buttonSendMail = findViewById(R.id.buttonSendMail);
        Button buttonViewHistory = findViewById(R.id.buttonViewHistory);
        editTextRecipient = findViewById(R.id.editTextRecipient);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextMessageBody = findViewById(R.id.editTextMessageBody);

        // Set button click listeners
        buttonSendMail.setOnClickListener(v -> sendEmail());
        buttonViewHistory.setOnClickListener(v -> viewSentMails());
    }

    // Method to send email
    private void sendEmail() {
        Log.i("Send email", "");

        // Get text from EditText widgets
        String recipient = editTextRecipient.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String messageBody = editTextMessageBody.getText().toString();

        // Check if all fields are filled
        if (recipient.isEmpty() || subject.isEmpty() || messageBody.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate the email address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(recipient).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the email intent using mailto scheme
        String mailto = "mailto:" + Uri.encode(recipient) +
                "?subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(messageBody);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        // Start the email client
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            saveSentMailToHistory(recipient, subject, messageBody);
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Save sent mail to history
    private void saveSentMailToHistory(String recipient, String subject, String messageBody) {
        String sentMail = "To: " + recipient + "\nSubject: " + subject + "\nMessage: " + messageBody;
        sentMailsHistory.add(sentMail);
    }

    // Method to view sent mails
    private void viewSentMails() {
        Intent intent = new Intent(this, SentMailsHistoryActivity.class);
        intent.putStringArrayListExtra("sentMails", sentMailsHistory);
        startActivity(intent);
    }
}
