package com.example.flower;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flower.databinding.ActivityProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CAMERA_PHOTO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load username and email (for now static, can be updated)
        binding.usernameTextView.setText("No Username");
        binding.emailTextView.setText("example@example.com");

        // Load profile image from SharedPreferences
        loadImageFromSharedPreferences();

        // Handle the image picker dialog
        binding.btn1.setOnClickListener(v -> showImagePickerDialog());

        // Handle the back button
        binding.backButton.setOnClickListener(v -> finish());
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setPositiveButton("Camera", (dialog, which) -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("Gallery", (dialog, which) -> openGallery());
        builder.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == CAMERA_PHOTO_REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Bitmap photo = (Bitmap) bundle.get("data");
                binding.profilePhoto.setImageBitmap(photo);
                saveImageToSharedPreferences(photo);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                binding.profilePhoto.setImageBitmap(bitmap);
                saveImageToSharedPreferences(bitmap);
            } catch (IOException e) {
                Log.e("ProfileActivity", "Error fetching bitmap", e);
            }
        }
    }

    private void saveImageToSharedPreferences(Bitmap bitmap) {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        editor.putString("profile_image", imageBase64);
        editor.apply();
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
    }

    private void loadImageFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String imageBase64 = prefs.getString("profile_image", null);
        if (imageBase64 != null) {
            byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            binding.profilePhoto.setImageBitmap(bitmap);
        }
    }
}
