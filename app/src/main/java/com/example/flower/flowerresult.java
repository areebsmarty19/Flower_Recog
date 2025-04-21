package com.example.flower;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.widget.Toast;


public class flowerresult extends AppCompatActivity {
    private TextView textViewResult;
    private ImageView imageView;
    String imageBase64;
    private FlowerClassifier flowerClassifier;

    private DatabaseHelper databaseHelper;
    StorageReference storageReference;
    LinearProgressIndicator progress;
    Uri image;
    MaterialButton selectimage;
    Bitmap input_image;


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult (new ActivityResultContracts.StartActivityForResult (),
                    result -> {
                        if (result.getResultCode () == RESULT_OK && result.getData () != null) {
                            Uri imageUri = result.getData ().getData ();
                            handleSelectedImage (imageUri);
                        }
                    });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult (new ActivityResultContracts.StartActivityForResult (),
                    result -> {
                        if (result.getResultCode () == RESULT_OK && result.getData () != null) {
                            Bitmap photo = (Bitmap) result.getData ().getExtras ().get ("data");
                            handleCapturedImage (photo);
                        }
                    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_flowerresult);

        textViewResult = findViewById (R.id.flower_info_textview);
        imageView = findViewById (R.id.imageView);
        databaseHelper = new DatabaseHelper (this);

        checkPermissions ();

        Button btnSelectPhoto = findViewById (R.id.button);
        // Initialize TensorFlow Lite Model
        try {
            flowerClassifier = new FlowerClassifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSelectPhoto.setOnClickListener (v -> showPhotoSelectionDialog ());
    }

    private void showPhotoSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Select Photo Source");
        String[] options = {"Choose from Gallery", "Take Photo"};
        builder.setItems (options, (dialog, which) -> {
            if (which == 0) {
                selectImage ();
            } else if (which == 1) {
                captureImage ();
            }
        });
        builder.show ();
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission (this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this, permissions, 101);
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch (intent);
    }

    private void captureImage() {
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch (intent);
    }

    private void handleSelectedImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap (this.getContentResolver (), imageUri);
            imageView.setImageBitmap (bitmap);
            input_image=bitmap;
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(input_image, 224, 224, true);
            classifyImage(resizedBitmap);
        } catch (IOException e) {
            textViewResult.setText ("Please select an image first.");
        }
    }

    private void handleCapturedImage(Bitmap bitmap) {

        imageView.setImageBitmap (bitmap);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        classifyImage(resizedBitmap);
    }


    private void saveResultToSharedPreferences(Bitmap image, String result) {
        // Get SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("flowerResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert image to Base64 string
        String imageBase64 = encodeImageToBase64(image);

        // Get current index, default is 0
        int index = sharedPreferences.getInt("index", 0);

        // Save result and image using keys with index
        editor.putString("result_" + index, result);
        editor.putString("image_" + index, imageBase64);

        // Update index (0 → 1 → 2 … → 4 → 0)
        index = (index + 1) % 5;
        editor.putInt("index", index);

        editor.apply();
    }

    // Method to encode the Bitmap image to Base64
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray ();
        return Base64.encodeToString (byteArray, Base64.DEFAULT);

    }
    private void classifyImage(Bitmap bitmap) {
        String result = flowerClassifier.classify(bitmap, this); // Pass context
        textViewResult.setText("Predicted Flower: " + result);
    }

}