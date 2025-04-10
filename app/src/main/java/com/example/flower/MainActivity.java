package com.example.flower;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.flower.adapter.MyAdapter;
import com.example.flower.databinding.ActivityMainBinding;
import com.example.flower.domain.DatabaseHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<DataClass> dataList;
    MyAdapter adapter;
    TextView textView;
    ImageView logout;
    SearchView searchView;
    ActivityMainBinding binding;
    DatabaseHelper databaseHelper;
    private ImageView imageView;
    private Bitmap bitmap;

    FirebaseAuth auth;


    // Activity Result Launchers for camera and gallery
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult (new ActivityResultContracts.StartActivityForResult (),
                    result -> {
                        if (result.getResultCode () == RESULT_OK && result.getData () != null) {
                            Bitmap photo = (Bitmap) result.getData ().getExtras ().get ("data");
                            handleImage (photo);
                        }
                    });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult (new ActivityResultContracts.StartActivityForResult (),
                    result -> {
                        if (result.getResultCode () == RESULT_OK && result.getData () != null) {
                            Uri imageUri = result.getData ().getData ();
                            try {
                                ImageDecoder.Source source = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    source = ImageDecoder.createSource (getContentResolver (), imageUri);
                                }
                                Bitmap bitmap = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    bitmap = ImageDecoder.decodeBitmap (source);
                                }
                                handleImage (bitmap);
                            } catch (IOException e) {
                                Toast.makeText (this, "Error loading image: " + e.getMessage (), Toast.LENGTH_SHORT).show ();
                            }
                        }
                    });
    private String email;

    private void handleImage(Bitmap bitmap) {
        imageView.setImageBitmap (bitmap);
        textView.setText ("Image Loaded");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState ());
        binding = ActivityMainBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        databaseHelper = new DatabaseHelper(this);
        auth = FirebaseAuth.getInstance();


        if (loggedIn ()) {
            binding.textView2.setText (UsernameOfLoggInUser ());
        }

        binding.logout.setOnClickListener (v -> logout ());

        binding.wishlistBtn.setOnClickListener (v -> {
            Toast.makeText (this, "History", Toast.LENGTH_SHORT).show ();
            startActivity (new Intent (this, favitems.class));
        });

        binding.cameraBtn.setOnClickListener (v -> {
            startActivity (new Intent (this, flowerresult.class));  // Opens the flowerresult screen
        });

        binding.profileBtn.setOnClickListener (v -> {
            Toast.makeText (this, "Profile", Toast.LENGTH_SHORT).show ();
            startActivity (new Intent (this, ProfileActivity.class));
        });

        // ImageSlider
        ImageSlider imageSlider = findViewById (R.id.image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<> ();
        slideModels.add (new SlideModel (R.drawable.fl1, ScaleTypes.FIT));
        slideModels.add (new SlideModel (R.drawable.fl2, ScaleTypes.FIT));
        slideModels.add (new SlideModel (R.drawable.fl3, ScaleTypes.FIT));
        imageSlider.setImageList (slideModels, ScaleTypes.FIT);
        imageSlider.setSlideAnimation (AnimationTypes.DEPTH_SLIDE);

        // RecyclerView
        recyclerView = findViewById (R.id.recyclerView);
        recyclerView.setLayoutManager (new GridLayoutManager (this, 1));
        dataList = new ArrayList<> ();
        dataList.add (new DataClass ("Rose", R.string.Rose, "Rose", R.drawable.rose));
        dataList.add (new DataClass ("Sunflower", R.string.sunflower, "Sunflower", R.drawable.sunflower));
        dataList.add (new DataClass ("Lily", R.string.lily, "Lily", R.drawable.lily));
        dataList.add (new DataClass ("Hibiscus", R.string.hibiscus, "Hibiscus", R.drawable.hibiscus));
        adapter = new MyAdapter (this, dataList);
        recyclerView.setAdapter (adapter);
    }

    private Bundle savedInstanceState() {
        return null;
    }



    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo Source");
        String[] options = {"Take Photo", "Choose from Gallery"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            } else {
                galleryLauncher.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
            }
        });
        builder.show();
    }

    private void logout() {

        auth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();

    }

    private boolean loggedIn() {
        return databaseHelper.isLoggedIn();
    }

    private String UsernameOfLoggInUser() {
        return databaseHelper.getUsernameByIfLoggIn();
    }
}
