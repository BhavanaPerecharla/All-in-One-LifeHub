package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class List extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private EditText editText;
    private final ArrayList<String> values = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialize views
        editText = findViewById(R.id.text_edit);
        ImageView addItem = findViewById(R.id.add_text);
        ListView itemList = findViewById(R.id.listview);
        Button backButton = findViewById(R.id.b15);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Load saved items from SharedPreferences
        Set<String> savedItems = sharedPreferences.getStringSet("items", new HashSet<>());
        values.addAll(savedItems);

        // Initialize ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
        itemList.setAdapter(adapter);

        // Set listeners
        addItem.setOnClickListener(this);
        itemList.setOnItemLongClickListener(this);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(List.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View view) {
        String newItem = editText.getText().toString();

        if (!newItem.isEmpty()) {
            if (values.contains(newItem)) {
                Toast.makeText(this, "Item Already Exists", Toast.LENGTH_LONG).show();
            } else {
                values.add(newItem);
                adapter.notifyDataSetChanged();

                // Save the updated list to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet("items", new HashSet<>(values));
                editor.apply();

                editText.setText(""); // Clear input
            }
        } else {
            Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
        builder.setMessage("Do you want to delete?")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    values.remove(position);
                    adapter.notifyDataSetChanged();

                    // Save the updated list to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("items", new HashSet<>(values));
                    editor.apply();

                    Toast.makeText(List.this, "Item Deleted", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();

        return true;
    }
}
