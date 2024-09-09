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
    private ArrayList<String> draftMails = new ArrayList<>(); // Store draft mails

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

        // Check if all fields are filled
        if (recipient.isEmpty() || subject.isEmpty() || messageBody.isEmpty()) {
            Toast.makeText(Mail.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate the email address
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(recipient).matches()) {
            Toast.makeText(Mail.this, "Invalid email address", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Mail.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Save sent mail to history
    private void saveSentMailToHistory(String recipient, String subject, String messageBody) {
        String sentMail = "To: " + recipient + "\nSubject: " + subject + "\nMessage: " + messageBody;
        sentMailsHistory.add(sentMail);
    }

    // Save draft method
    private void saveDraft() {
        String recipient = editTextRecipient.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String messageBody = editTextMessageBody.getText().toString();

        if (!recipient.isEmpty() || !subject.isEmpty() || !messageBody.isEmpty()) {
            String draft = "To: " + recipient + "\nSubject: " + subject + "\nMessage: " + messageBody;
            draftMails.add(draft);
            Toast.makeText(Mail.this, "Draft saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Mail.this, "Cannot save empty draft.", Toast.LENGTH_SHORT).show();
        }
    }

    // Load draft method (could be enhanced to load specific drafts)
    private void loadDraft(int index) {
        if (index < draftMails.size()) {
            String[] parts = draftMails.get(index).split("\n");
            editTextRecipient.setText(parts[0].replace("To: ", ""));
            editTextSubject.setText(parts[1].replace("Subject: ", ""));
            editTextMessageBody.setText(parts[2].replace("Message: ", ""));
        }
    }

    // Method to view sent mails
    private void viewSentMails() {
        Intent intent = new Intent(Mail.this, SentMailsHistoryActivity.class);
        intent.putStringArrayListExtra("sentMails", sentMailsHistory);
        startActivity(intent);
    }
}
