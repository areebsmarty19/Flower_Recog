package com.example.flower;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class favitems extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favitems);
    }

    public class flowerresult extends AppCompatActivity {
        private FlowerRepository repository;
        private DetailActivity.FlowerMatcher matcher;
        private TextView textViewResult;
        private ImageView imageView;

        private final ActivityResultLauncher<Intent> imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Uri imageUri = result.getData().getData();
                                handleSelectedImage(imageUri);
                            }
                        });

        private final ActivityResultLauncher<Intent> cameraLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                                handleCapturedImage(photo);
                            }
                        });

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            textViewResult = findViewById(R.id.flower_info_textview);
            imageView = findViewById(R.id.imageView);
            repository = new FlowerRepository(this);
            matcher = new DetailActivity.FlowerMatcher (repository);

            checkPermissions();
            insertFlowerData();

            // Single button for photo selection
            Button btnSelectPhoto = findViewById(R.id.button);
            btnSelectPhoto.setOnClickListener(v -> showPhotoSelectionDialog());
        }

        private void showPhotoSelectionDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Photo Source");
            String[] options = {"Take Photo", "Choose from Gallery"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    openCamera();
                } else {
                    selectImage();
                }
            });
            builder.show();
        }

        private void checkPermissions() {
            String[] permissions = {android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 101);
                }
            }
        }

        private void selectImage() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        }

        private void openCamera() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        }

        private void handleSelectedImage(Uri imageUri) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                processImage(bitmap);
            } catch (IOException e) {
                textViewResult.setText("Error: " + e.getMessage());
            }
        }

        private void handleCapturedImage(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            processImage(bitmap);
        }

        private void processImage(Bitmap bitmap) {
            // Segment the image to focus on flower
            Bitmap segmentedBitmap = segmentFlower(bitmap);

            if (segmentedBitmap == null || !isFlowerPresent(segmentedBitmap)) {
                textViewResult.setText("No flower detected. Please upload a clear flower photo.");
                return;
            }

            // Adjust brightness and contrast
            Bitmap adjustedBitmap = adjustBrightnessContrast(segmentedBitmap);

            // Extract CNN-like features
            float[] features = extractCNNFeatures(adjustedBitmap);

            // Match with database
            String result = matcher.matchFlower(features);
            if (result == null) {
                textViewResult.setText("Flower not matched. Please upload a clear flower photo.");
            } else {
                textViewResult.setText("Detected Flower: " + result);
            }
        }

        private Bitmap segmentFlower(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap segmented = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Simple color-based segmentation (focus on vibrant colors)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // Keep vibrant colors, make background gray
                    if (isVibrantColor(r, g, b)) {
                        segmented.setPixel(x, y, pixel);
                    } else {
                        segmented.setPixel(x, y, 0xFF808080); // Gray
                    }
                }
            }
            return segmented;
        }

        private boolean isVibrantColor(int r, int g, int b) {
            // Check if color is vibrant (not too dark or too light)
            int brightness = (r + g + b) / 3;
            int saturation = Math.max(Math.max(r, g), b) - Math.min(Math.min(r, g), b);
            return brightness > 50 && brightness < 200 && saturation > 100;
        }

        private Bitmap adjustBrightnessContrast(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Bitmap adjusted = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            float brightness = 1.2f; // Increase brightness by 20%
            float contrast = 1.3f;   // Increase contrast by 30%

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    // Apply brightness and contrast
                    r = clamp((int)((contrast * (r - 128) + 128) * brightness));
                    g = clamp((int)((contrast * (g - 128) + 128) * brightness));
                    b = clamp((int)((contrast * (b - 128) + 128) * brightness));

                    adjusted.setPixel(x, y, (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }
            return adjusted;
        }

        private int clamp(int value) {
            return Math.max(0, Math.min(255, value));
        }

        private boolean isFlowerPresent(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int threshold = width * height / 5; // 20% of image must be detailed

            int vibrantCount = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    int r = (pixel >> 16) & 0xFF;
                    int g = (pixel >> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    if (isVibrantColor(r, g, b)) vibrantCount++;
                }
            }
            return vibrantCount > threshold;
        }

        private float[] extractCNNFeatures(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int size = width * height;

            // Simple CNN-like feature extraction
            float[] features = new float[6]; // RGB averages + edge features

            long sumRed = 0, sumGreen = 0, sumBlue = 0;
            int edgeCount = 0;
            int horizontalEdges = 0;
            int verticalEdges = 0;

            // Calculate color averages and edges
            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    sumRed += (pixel >> 16) & 0xFF;
                    sumGreen += (pixel >> 8) & 0xFF;
                    sumBlue += pixel & 0xFF;

                    // Edge detection
                    int center = pixel & 0xFF;
                    int right = bitmap.getPixel(x + 1, y) & 0xFF;
                    int bottom = bitmap.getPixel(x, y + 1) & 0xFF;

                    if (Math.abs(center - right) > 30) horizontalEdges++;
                    if (Math.abs(center - bottom) > 30) verticalEdges++;
                }
            }

            edgeCount = horizontalEdges + verticalEdges;

            features[0] = sumRed / (float)size / 255f;     // Normalized Red
            features[1] = sumGreen / (float)size / 255f;   // Normalized Green
            features[2] = sumBlue / (float)size / 255f;    // Normalized Blue
            features[3] = edgeCount / (float)size;         // Edge density
            features[4] = horizontalEdges / (float)size;   // Horizontal edge density
            features[5] = verticalEdges / (float)size;     // Vertical edge density

            return features;
        }

        private void insertFlowerData() {
            // Extended features: [R, G, B, edge_density, h_edges, v_edges]
            repository.insertFlower("Rose", new float[]{0.8f, 0.2f, 0.2f, 0.3f, 0.15f, 0.15f});
            repository.insertFlower("Tulip", new float[]{0.8f, 0.3f, 0.6f, 0.25f, 0.1f, 0.15f});
            repository.insertFlower("Sunflower", new float[]{0.9f, 0.7f, 0.3f, 0.4f, 0.2f, 0.2f});
            repository.insertFlower("Daisy", new float[]{0.9f, 0.9f, 0.9f, 0.2f, 0.1f, 0.1f});
            // Add other flowers with extended features...
        }
    }
}
