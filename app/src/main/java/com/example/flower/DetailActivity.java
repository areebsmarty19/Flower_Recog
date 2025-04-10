package com.example.flower;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class DetailActivity extends AppCompatActivity {
    TextView detailDesc, detailTitle;
    ImageView detailImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailDesc = findViewById(R.id.detailDesc);
        detailTitle = findViewById(R.id.detailTitle);
        detailImage = findViewById(R.id.detailImage);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getInt("Desc"));
            detailImage.setImageResource(bundle.getInt("Image"));
            detailTitle.setText(bundle.getString("Title"));
        }
    }

    // Assuming FlowerMatcher class is updated to handle extended features
    static class FlowerMatcher {
        private FlowerRepository repository;

        public FlowerMatcher(FlowerRepository repository) {
            this.repository = repository;
        }

        public String matchFlower(float[] features) {
            float bestScore = Float.MAX_VALUE;
            String bestMatch = null;
            float threshold = 0.1f; // Matching threshold

            return bestMatch;
        }

        private float calculateDistance(float[] f1, float[] f2) {
            float sum = 0;
            for (int i = 0; i < f1.length; i++) {
                sum += Math.pow(f1[i] - f2[i], 2);
            }
            return (float)Math.sqrt(sum);
        }
    }
}
