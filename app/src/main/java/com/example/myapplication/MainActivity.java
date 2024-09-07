package com.example.myapplication;
import android.content.SharedPreferences;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "new_user_credentials.db"; // New database name
    private static final int DATABASE_VERSION = 11; // Incremented version

    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_BIRTHDAY = "birthday";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_BIO = "bio";
    private static final String COLUMN_PROFILE_PHOTO = "profile_photo"; // For storing image as BLOB

    // SQL statement to create the users table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_BIRTHDAY + " TEXT, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_AGE + " INTEGER, " +
                    COLUMN_STATE + " TEXT, " +
                    COLUMN_COUNTRY + " TEXT, " +
                    COLUMN_BIO + " TEXT, " +
                    COLUMN_PROFILE_PHOTO + " BLOB" + // BLOB for storing image data
                    ");";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }



    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }


    public boolean isValidUser(String username, String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + "=? AND " +
                COLUMN_EMAIL + "=? AND " +
                COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
    public boolean isValid(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE (" +
                COLUMN_USERNAME + "=? OR " +
                COLUMN_EMAIL + "=? OR " +
                COLUMN_PHONE + "=?) AND " +
                COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{identifier, identifier, identifier, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}


public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        Button btnSignUpPage = findViewById(R.id.btnSignUpPage);
        btnSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }


    private void login() {
        String identifier = etUsername.getText().toString().trim(); // Assuming this EditText can hold username, email, or phone number
        String password = etPassword.getText().toString().trim();

        if (databaseHelper.isValid(identifier, password)) {
            SharedPreferences preferences = getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("identifier", identifier); // Save identifier
            editor.apply();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("USERNAME", identifier);
            startActivity(i);
        } else {
            // Failed login
            Toast.makeText(MainActivity.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }




}