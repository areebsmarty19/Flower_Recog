package com.example.flower;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class favitems extends AppCompatActivity {
    private ImageView imageView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.favitems);

        // Reference all 5 ImageViews and TextViews
        ImageView[] imageViews = {
                findViewById (R.id.recognitionImageView1),
                findViewById (R.id.recognitionImageView2),
                findViewById (R.id.recognitionImageView3),
                findViewById (R.id.recognitionImageView4),
                findViewById (R.id.recognitionImageView5)
        };

        TextView[] textViews = {
                findViewById (R.id.recognitionResultTextView1),
                findViewById (R.id.recognitionResultTextView2),
                findViewById (R.id.recognitionResultTextView3),
                findViewById (R.id.recognitionResultTextView4),
                findViewById (R.id.recognitionResultTextView5)
        };

        SharedPreferences prefs = getSharedPreferences ("flowerResults", MODE_PRIVATE);

        // Load and display all 5 image-text pairs
        for (int i = 0; i < 5; i++) {
            String base64 = prefs.getString ("image_" + i, "");
            String result = prefs.getString ("result_" + i, "");

            if (!base64.isEmpty ()) {
                Bitmap bitmap = decodeBase64ToImage (base64);
                imageViews[i].setImageBitmap (bitmap);
            }

            textViews[i].setText (result.isEmpty () ? "No result" : result);
        }
    }

    private Bitmap decodeBase64ToImage(String base64String) {
        byte[] decodedString = Base64.decode (base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray (decodedString, 0, decodedString.length);
    }
}