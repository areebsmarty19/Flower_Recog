package com.example.flower;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.example.flower.domain.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class flowerresult extends AppCompatActivity {
    private TextView textViewResult;
    private ImageView imageView;
    private DatabaseHelper databaseHelper;

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
        setContentView(R.layout.activity_flowerresult);

        textViewResult = findViewById(R.id.flower_info_textview);
        imageView = findViewById(R.id.imageView);
        databaseHelper = new DatabaseHelper(this);

        checkPermissions();

        Button btnSelectPhoto = findViewById(R.id.button);
        btnSelectPhoto.setOnClickListener(v -> showPhotoSelectionDialog());
    }

    private void showPhotoSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo Source");
        String[] options = {"Choose from Gallery", "Take Photo"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                selectImage();
            } else if (which == 1) {
                captureImage();
            }
        });
        builder.show();
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
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

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void handleSelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
            processAndDetectFlower(bitmap);
        } catch (IOException e) {
            textViewResult.setText("Error: " + e.getMessage());
        }
    }

    private void handleCapturedImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        processAndDetectFlower(bitmap);
    }

    private void processAndDetectFlower(Bitmap bitmap) {
        Bitmap processedBitmap = processImage(bitmap);
        String detectedFlower = detectFlower(processedBitmap);

        if (detectedFlower != null && databaseHelper.isFlowerInDatabase(detectedFlower)) {
            textViewResult.setText("Flower detected: " + detectedFlower);
        } else {
            textViewResult.setText("No flower present in the image");
        }
    }

    private Bitmap processImage(Bitmap bitmap) {
        int width = 224, height = 224;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = resizedBitmap.getPixel(x, y);
                int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

                // Apply contrast adjustment
                gray = Math.min(255, Math.max(0, (int)(gray * 1.5)));

                // Apply thresholding
                if (gray > 100) gray = 255; else gray = 0;

                processedBitmap.setPixel(x, y, Color.rgb(gray, gray, gray));
            }
        }
        return processedBitmap;
    }

    private String detectFlower(Bitmap bitmap) {
        // Get the list of flowers from the database
        List<String> flowerNames = databaseHelper.getAllFlowerNames();

        int whitePixels = 0;
        int totalPixels = bitmap.getWidth() * bitmap.getHeight();

        // Count white pixels
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                if (Color.red(bitmap.getPixel(x, y)) == 255) {
                    whitePixels++;
                }
            }
        }

        // Calculate white pixel ratio
        double whitePixelRatio = (double) whitePixels / totalPixels;

        // Simulate matching with 20 flowers based on white pixel ratio
        if (whitePixelRatio > 0.00 && whitePixelRatio <= 0.05 && flowerNames.contains("Rose")) {
            return "Rose";
        } else if (whitePixelRatio > 0.05 && whitePixelRatio <= 0.10 && flowerNames.contains("Daisy")) {
            return "Daisy";
        } else if (whitePixelRatio > 0.10 && whitePixelRatio <= 0.15 && flowerNames.contains("Tulip")) {
            return "Tulip";
        } else if (whitePixelRatio > 0.15 && whitePixelRatio <= 0.20 && flowerNames.contains("Lily")) {
            return "Lily";
        } else if (whitePixelRatio > 0.20 && whitePixelRatio <= 0.25 && flowerNames.contains("Sunflower")) {
            return "Sunflower";
        } else if (whitePixelRatio > 0.25 && whitePixelRatio <= 0.30 && flowerNames.contains("Orchid")) {
            return "Orchid";
        } else if (whitePixelRatio > 0.30 && whitePixelRatio <= 0.35 && flowerNames.contains("Marigold")) {
            return "Marigold";
        } else if (whitePixelRatio > 0.35 && whitePixelRatio <= 0.40 && flowerNames.contains("Jasmine")) {
            return "Jasmine";
        } else if (whitePixelRatio > 0.40 && whitePixelRatio <= 0.45 && flowerNames.contains("Lavender")) {
            return "Lavender";
        } else if (whitePixelRatio > 0.45 && whitePixelRatio <= 0.50 && flowerNames.contains("Peony")) {
            return "Peony";
        } else if (whitePixelRatio > 0.50 && whitePixelRatio <= 0.55 && flowerNames.contains("Carnation")) {
            return "Carnation";
        } else if (whitePixelRatio > 0.55 && whitePixelRatio <= 0.60 && flowerNames.contains("Daffodil")) {
            return "Daffodil";
        } else if (whitePixelRatio > 0.60 && whitePixelRatio <= 0.65 && flowerNames.contains("Chrysanthemum")) {
            return "Chrysanthemum";
        } else if (whitePixelRatio > 0.65 && whitePixelRatio <= 0.70 && flowerNames.contains("Iris")) {
            return "Iris";
        } else if (whitePixelRatio > 0.70 && whitePixelRatio <= 0.75 && flowerNames.contains("Violet")) {
            return "Violet";
        } else if (whitePixelRatio > 0.75 && whitePixelRatio <= 0.80 && flowerNames.contains("Hibiscus")) {
            return "Hibiscus";
        } else if (whitePixelRatio > 0.80 && whitePixelRatio <= 0.85 && flowerNames.contains("Gerbera")) {
            return "Gerbera";
        } else if (whitePixelRatio > 0.85 && whitePixelRatio <= 0.90 && flowerNames.contains("Poppy")) {
            return "Poppy";
        } else if (whitePixelRatio > 0.90 && whitePixelRatio <= 0.95 && flowerNames.contains("Zinnia")) {
            return "Zinnia";
        } else if (whitePixelRatio > 0.95 && whitePixelRatio <= 1.00 && flowerNames.contains("Lotus")) {
            return "Lotus";
        }

        // If no match is found with any flower in the database, return null
        return null;
    }
}
