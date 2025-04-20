package com.example.flower;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class activity_register extends AppCompatActivity {

    EditText editTextEmail, Name;
    TextInputEditText editTextPassword;
    Button button1;
    FirebaseAuth mAuth;
    ProgressBar progressbar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        button1 = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        progressbar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.RegisterNow);
        Name = findViewById(R.id.name);

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
            finish();
        });

        button1.setOnClickListener(view -> {
            progressbar.setVisibility(View.VISIBLE);
            String email, password, name;
            name = String.valueOf(Name.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(activity_register.this, "Enter Your Name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(activity_register.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(activity_register.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressbar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name) // set the username
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(profileTask -> {
                                            if (profileTask.isSuccessful()) {
                                                Toast.makeText(activity_register.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(activity_register.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(activity_register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
