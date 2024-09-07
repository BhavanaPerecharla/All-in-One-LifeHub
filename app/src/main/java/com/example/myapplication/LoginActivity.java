package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    FloatingActionButton f1, f2, f3, f4;
    Button b1;
    String query = "";
    private TextView timeTextView, dateTextView;
    private ImageButton ib, b2;
    private final Handler handler = new Handler();

    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimeAndDate();
            handler.postDelayed(this, 1000); // 1000 milliseconds = 1 second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        f1 = findViewById(R.id.fab);
        f2 = findViewById(R.id.fab2);
        f3 = findViewById(R.id.fab3);
        f4 = findViewById(R.id.fab4);
        b1 = findViewById(R.id.button9);
        timeTextView = findViewById(R.id.timeTextView);
        dateTextView = findViewById(R.id.dateTextView);
        ib = findViewById(R.id.imageButton);
        b2 = findViewById(R.id.imageButton2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Intent intent = getIntent();
        String identifier = intent.getStringExtra("USERNAME");

        if (identifier != null) {
            // Handle the identifier (e.g., display user info or use it for other purposes)
            Toast.makeText(LoginActivity.this, "Logged in as: " + identifier, Toast.LENGTH_SHORT).show();
        } else {
            // Handle cases where identifier is not passed
            Toast.makeText(LoginActivity.this, "No user information available.", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(toolbar);
        registerForContextMenu(timeTextView);
        registerForContextMenu(dateTextView);
        updateTimeAndDate();

        // Button and FloatingActionButton listeners
        b2.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, Contacts.class)));

        f3.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ToDoList.class)));

        f4.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, List.class)));

        f1.setOnClickListener(v -> {
            if (f2.getVisibility() == View.VISIBLE) {
                f2.setVisibility(View.GONE);
                f3.setVisibility(View.GONE);
                f4.setVisibility(View.GONE);
            } else {
                f2.setVisibility(View.VISIBLE);
                f3.setVisibility(View.VISIBLE);
                f4.setVisibility(View.VISIBLE);
            }
        });

        // Search query functionality
        final EditText searchEditText = findViewById(R.id.searchEditText);
        f2.setOnClickListener(v -> {
            if (searchEditText.getVisibility() == View.VISIBLE) {
                searchEditText.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.INVISIBLE);
            } else {
                searchEditText.setVisibility(View.VISIBLE);
                b1.setVisibility(View.VISIBLE);
            }
        });

        b1.setOnClickListener(v -> {
            query = searchEditText.getText().toString();
            if (query.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
            } else {
                // Open Google Chrome for search using an explicit intent
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
                startActivity(in);
                searchEditText.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.INVISIBLE);
            }
        });

        ib.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, call_dial.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdatingTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdatingTime();
    }

    private void startUpdatingTime() {
        handler.post(updateTimeRunnable);
    }

    private void stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable);
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());

        timeTextView.setText(timeFormat.format(calendar.getTime()));
        dateTextView.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_profile) {
            SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String identifier = preferences.getString("identifier", null); // Replace "identifier" with the actual key used for storing user data

            if (identifier != null) {
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", identifier);
                startActivity(intent);
                Toast.makeText(this, "Showing the Profile of Yours", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.menu_timetable) {
            startActivity(new Intent(LoginActivity.this, Timetable.class));
            Toast.makeText(this, "Show Timetable Image", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (itemId == R.id.menu_timetable) {
            startActivity(new Intent(LoginActivity.this, Timetable.class));
            Toast.makeText(this, "Show Timetable Image", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_calculator) {
            startActivity(new Intent(LoginActivity.this, Calculator.class));
            Toast.makeText(this, "Open Calculator", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_email) {
            startActivity(new Intent(LoginActivity.this, Mail.class));
            Toast.makeText(this, "Open Email", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.menu_logout) {
            performLogout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void performLogout() {
        SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    // Context menu for time and date views
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.timeTextView) {
            getMenuInflater().inflate(R.menu.change_time, menu);
        } else if (v.getId() == R.id.dateTextView) {
            getMenuInflater().inflate(R.menu.change_time, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.changeTime) {
            showTimePickerDialog();
            return true;
        } else if (item.getItemId() == R.id.changeDate) {
            showDatePickerDialog();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%s %02d, %d", getMonthName(month1), dayOfMonth, year1);
            dateTextView.setText(selectedDate);
            Toast.makeText(LoginActivity.this, "Date changed to: " + selectedDate, Toast.LENGTH_SHORT).show();
        }, year, month, day).show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            timeTextView.setText(selectedTime);
            Toast.makeText(LoginActivity.this, "Time changed to: " + selectedTime, Toast.LENGTH_SHORT).show();
        }, hour, minute, true).show();
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }
}
