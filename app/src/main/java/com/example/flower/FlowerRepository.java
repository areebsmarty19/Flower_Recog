package com.example.flower;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.flower.domain.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class FlowerRepository {
    private final DatabaseHelper dbHelper;

    public FlowerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertFlower(String name, float[] features) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_FEATURES, convertFloatArrayToString(features));
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }

    public List<DataClass.FlowerData> getAllFlowers() {
        List<DataClass.FlowerData> flowers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null); // ✅ Fixed SQL query

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String featuresString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FEATURES));
                float[] features = convertStringToFloatArray(featuresString);
                flowers.add(new DataClass.FlowerData(name, features));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return flowers;
    }

    private String convertFloatArrayToString(float[] features) {
        StringBuilder builder = new StringBuilder();
        for (float feature : features) {
            builder.append(feature).append(",");
        }
        return builder.toString();
    }

    private float[] convertStringToFloatArray(String featuresString) {
        String[] parts = featuresString.split(",");
        float[] features = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                features[i] = Float.parseFloat(parts[i]);
            } catch (NumberFormatException e) {
                features[i] = 0.0f; // Default value in case of parsing error
            }
        }
        return features;
    }
}

