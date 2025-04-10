package com.example.flower.domain;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "";
    // Database Details
    private static final String DATABASE_NAME = "flowers_db";
    private static final int DATABASE_VERSION = 1;

    // 🌸 Flowers Table
    public static final String TABLE_FLOWERS = "flowers";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FEATURES = "features";

    // 👤 Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_LOGGED_IN = "logged_in";
    private static final String COLUMN_USERNAME = "username";

    private String userEmail; // Stores the email of the logged-in user
    private boolean isLoggedIn;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 🌸 Create Flowers Table
        String CREATE_FLOWERS_TABLE = "CREATE TABLE " + TABLE_FLOWERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_FEATURES + " TEXT)";
        db.execSQL(CREATE_FLOWERS_TABLE);
        insertInitialFlowers(db);

        // 👤 Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_LOGGED_IN + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // 🌸 Insert Initial Flowers
    private void insertInitialFlowers(SQLiteDatabase db) {
        String[] flowers = {"Rose", "Sunflower", "Lily", "Hibiscus", "Tulip", "Orchid", "Daisy", "Marigold", "Jasmine",
                "Daffodil", "Lavender", "Lotus", "Poppy", "Carnation", "Peony", "Chrysanthemum", "Azalea", "Begonia", "Petunia", "Zinnia"};

        for (String flower : flowers) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, flower);
            db.insert(TABLE_FLOWERS, null, values);
        }
    }

    // 🌸 Check if Flower Exists
    public boolean isFlowerInDatabase(String flowerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FLOWERS + " WHERE " + COLUMN_NAME + "=?", new String[]{flowerName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 👤 Check Email & Password (Login)
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // 👤 Set User Logged In
    public void setUserLoggedIn(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LOGGED_IN, isLoggedIn ? 1 : 0);
        db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        userEmail = isLoggedIn ? email : null; // Store logged-in user's email
    }

    // 👤 Get Password by Email
    public String getPasswordByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        if (cursor.moveToFirst()) {
            String password = cursor.getString(0);
            cursor.close();
            return password;
        }
        cursor.close();
        return null;
    }

    // 👤 Register New User
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1; // Returns true if insert is successful
    }

    // 👤 Check if User is Logged In
    public boolean isLoggedIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_LOGGED_IN + " = 1", null);

        boolean isLogged = cursor.getCount() > 0; // If any user has logged_in = 1, return true
        cursor.close();
        return isLogged;
    }


    // 👤 Check if Email Exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    

    public void setUserLoggedIn(boolean b, String email, boolean isLoggedIn) {
        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_LOGGED_IN, isLoggedIn ? 1 : 0);

            // Update the login status only for the user with the provided email
            db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        }

    public Boolean insertdata(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_LOGGED_IN, 0); // Default to not logged in

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public String getUsernameByIfLoggIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_LOGGED_IN + " = 1", null);
        if (cursor.moveToFirst()) {
            String email = cursor.getString(0);
            cursor.close();
            return email;
        }
        cursor.close();
        return null;
    }

    public String getEmailByIfLoggIn() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_LOGGED_IN + " = 1", null);
        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            cursor.close();
            return username;
        }
        cursor.close();
        return null;
    }

    public List<String> getAllFlowerNames() {
            List<String> flowerNames = new ArrayList<> ();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_FLOWERS, null);

            if (cursor.moveToFirst()) {
                do {
                    flowerNames.add(cursor.getString(0)); // 0 is the index of the "name" column
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close(); // Optional: close the database connection
            return flowerNames;

    }
}
