package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Mail extends AppCompatActivity {
    private Button buttonSendMail, buttonViewHistory;
    private EditText editTextRecipient, editTextSubject, editTextMessageBody;
    private static ArrayList<String> sentMailsHistory = new ArrayList<>(); // Store sent mails history

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        // Initialize views
        buttonSendMail = findViewById(R.id.buttonSendMail);
        buttonViewHistory = findViewById(R.id.buttonViewHistory);
        editTextRecipient = findViewById(R.id.editTextRecipient);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextMessageBody = findViewById(R.id.editTextMessageBody);

        // Set button click listeners
        buttonSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

        buttonViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSentMails();
            }
        });
    }

    // Method to send email
    protected void sendEmail() {
        Log.i("Send email", "");

        // Get text from EditText widgets
        String recipient = editTextRecipient.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String messageBody = editTextMessageBody.getText().toString();

        if (recipient.isEmpty() || subject.isEmpty() || messageBody.isEmpty()) {
            Toast.makeText(Mail.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the TO field with the recipient email address
        String[] TO = {recipient};

        // Create the email intent
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, messageBody);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            saveSentMailToHistory(recipient, subject, messageBody);
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Mail.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Save sent mail to history
    private void saveSentMailToHistory(String recipient, String subject, String messageBody) {
        String sentMail = "To: " + recipient + "\nSubject: " + subject + "\nMessage: " + messageBody;
        sentMailsHistory.add(sentMail);
    }

    // Method to view sent mails
    private void viewSentMails() {
        Intent intent = new Intent(Mail.this, SentMailsHistoryActivity.class);
        intent.putStringArrayListExtra("sentMails", sentMailsHistory);
        startActivity(intent);
    }
}
