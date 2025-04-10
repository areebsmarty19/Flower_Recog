package com.example.flower;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flower.databinding.ActivityForgetPasswordBinding;
import com.example.flower.domain.DatabaseHelper;

public class activity_forget_password extends AppCompatActivity {
    private ActivityForgetPasswordBinding binding;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper(this);
        binding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrievePassword();
            }
        });

    }

    // Method to retrieve password if authentication is successful
    private void retrievePassword() {
        String email = binding.emailInputLayout.getEditText().getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            String password = databaseHelper.getPasswordByEmail(email);
            if (password != null) {
                // Password found, display it to the user (or send it via email)
                Toast.makeText(this, "Your password is : " + password, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            binding.emailInputLayout.setError("Please enter your email");
        }
    }
}

