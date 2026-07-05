package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.flower.adapter.MyAdapter;
import com.example.flower.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<DataClass> dataList;
    MyAdapter adapter;
    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        if (loggedIn()) {
            binding.textView2.setText(UsernameOfLoggInUser());
        }

        binding.logout.setOnClickListener(v -> logout());

        binding.wishlistBtn.setOnClickListener(v -> {
            Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, favitems.class));
        });

        binding.cameraBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, flowerresult.class));  // Opens the flowerresult screen
        });

        binding.profileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
        });

        // ImageSlider
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.fl1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.fl2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.fl3, ScaleTypes.FIT));
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        binding.imageSlider.setSlideAnimation(AnimationTypes.DEPTH_SLIDE);

        // RecyclerView
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        dataList = new ArrayList<>();
        dataList.add(new DataClass("Rose", R.string.Rose, "Rose", R.drawable.rose));
        dataList.add(new DataClass("Sunflower", R.string.sunflower, "Sunflower", R.drawable.sunflower));
        dataList.add(new DataClass("Lily", R.string.lily, "Lily", R.drawable.lily));
        dataList.add(new DataClass("Hibiscus", R.string.hibiscus, "Hibiscus", R.drawable.hibiscus));
        adapter = new MyAdapter(this, dataList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();
    }

    private boolean loggedIn() {
        return auth.getCurrentUser() != null;
    }

    private String UsernameOfLoggInUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getDisplayName() != null ? user.getDisplayName() : "User";
        }
        return "Guest";
    }
}
