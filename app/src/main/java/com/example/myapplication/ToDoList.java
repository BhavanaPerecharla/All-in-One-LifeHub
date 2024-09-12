package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ToDoList extends AppCompatActivity {
    private EditText taskEditText, descriptionEditText, dueDateEditText, dueTimeEditText;

    private ArrayAdapter<String> taskAdapter;
    private Set<String> taskSet;
    private ArrayList<TaskItem> taskItems;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, timeFormat;
    private static final String PREFS_NAME = "ToDoPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        taskEditText = findViewById(R.id.taskEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateEditText = findViewById(R.id.dueDateEditText);
        dueTimeEditText = findViewById(R.id.dueTimeEditText);

        Button addButton = findViewById(R.id.addButton);
        ListView taskListView = findViewById(R.id.taskListView);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        taskSet = sharedPreferences.getStringSet("tasks", new HashSet<>());
        taskItems = new ArrayList<>();

        for (String task : taskSet) {
            // Split the concatenated task string using the delimiter
            String[] taskParts = task.split("\\|");
            if (taskParts.length == 4) {
                String taskName = taskParts[0];
                String description = taskParts[1];
                String dueDate = taskParts[2];
                String dueTime = taskParts[3];
                taskItems.add(new TaskItem(taskName, description, dueDate, dueTime));
            }
        }
        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(taskSet));
        taskListView.setAdapter(taskAdapter);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        addButton.setOnClickListener(v -> {
            String taskName = taskEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String dueDate = dueDateEditText.getText().toString().trim();
            String dueTime = dueTimeEditText.getText().toString().trim();

            if (!taskName.isEmpty() && !dueDate.isEmpty() && !dueTime.isEmpty()) {
                // Concatenate task details with a delimiter
                String taskString = taskName + "|" + description + "|" + dueDate + "|" + dueTime;
                TaskItem newTask = new TaskItem(taskName, description, dueDate, dueTime);
                taskItems.add(newTask);
                taskSet.add(taskString); // Save task string in the set
                taskAdapter.add(newTask.toString());
                saveTasksToPrefs();
                clearInputFields();
            } else {
                Toast.makeText(ToDoList.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            TaskItem taskItem = taskItems.get(position);
            taskItem.toggleCompletion();
            updateTaskList();
        });

        taskListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteTaskDialog(position);
            return true;
        });

        dueDateEditText.setOnClickListener(v -> showDatePicker());

        dueTimeEditText.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String dueDate = dateFormat.format(calendar.getTime());
            dueDateEditText.setText(dueDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            String dueTime = timeFormat.format(calendar.getTime());
            dueTimeEditText.setText(dueTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    private void showDeleteTaskDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskSet.remove(new ArrayList<>(taskSet).get(position));
                    taskItems.remove(position);
                    saveTasksToPrefs();
                    updateTaskList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveTasksToPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("tasks", taskSet);
        editor.apply();
    }

    private void updateTaskList() {
        taskAdapter.clear();
        for (TaskItem taskItem : taskItems) {
            taskAdapter.add(taskItem.toString());
        }
        saveTasksToPrefs();
    }

    private void clearInputFields() {
        taskEditText.setText("");
        descriptionEditText.setText("");
        dueDateEditText.setText("");
        dueTimeEditText.setText("");
    }

    private static class TaskItem {
        private final String taskName;
        private final String description;
        private final String dueDate;
        private final String dueTime;
        private boolean isCompleted;

        TaskItem(String taskName, String description, String dueDate, String dueTime) {
            this.taskName = taskName;
            this.description = description;
            this.dueDate = dueDate;
            this.dueTime = dueTime;
            this.isCompleted = false;
        }

        void toggleCompletion() {
            isCompleted = !isCompleted;
        }

        @NonNull
        @Override
        public String toString() {
            String completionMark = isCompleted ? "[Completed] " : "";
            return completionMark + taskName + " - Due: " + dueDate + " " + dueTime + "\nDescription: " + description;
        }
    }
}
