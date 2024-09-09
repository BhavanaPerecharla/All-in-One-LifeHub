package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import java.util.ArrayList;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class SentMailsHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSentMails;
    private SentMailsAdapter adapter;
    private Button buttonBack, buttonClearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_mails_history);

        // Initialize views
        recyclerViewSentMails = findViewById(R.id.recyclerViewSentMails);
        buttonBack = findViewById(R.id.buttonBack);
        buttonClearAll = findViewById(R.id.buttonClearAll);

        // Get sent mails from intent
        ArrayList<String> sentMails = getIntent().getStringArrayListExtra("sentMails");

        // Set up RecyclerView
        recyclerViewSentMails.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SentMailsAdapter(sentMails);
        recyclerViewSentMails.setAdapter(adapter);

        // Handle back button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity
            }
        });

        // Handle clear all button
        buttonClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentMails.clear(); // Clear the list
                adapter.notifyDataSetChanged();
                Toast.makeText(SentMailsHistoryActivity.this, "All sent mails deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
class SentMailsAdapter extends RecyclerView.Adapter<SentMailsAdapter.ViewHolder> {

    private ArrayList<String> sentMails;

    public SentMailsAdapter(ArrayList<String> sentMails) {
        this.sentMails = sentMails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_sent_mail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMail.setText(sentMails.get(position));

        // Handle delete button click
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) { // Check if position is valid
                    sentMails.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, sentMails.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sentMails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMail;
        public Button buttonDelete;

        public ViewHolder(View view) {
            super(view);
            textViewMail = view.findViewById(R.id.textViewMail);
            buttonDelete = view.findViewById(R.id.buttonDelete);
        }
    }
}
