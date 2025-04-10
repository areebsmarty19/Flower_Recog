package com.example.flower;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class startActivity extends AppCompatActivity {

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button button1, button2;
        final RelativeLayout relativeLayout;
        button1 = findViewById(R.id.loginButton);
        button2 = findViewById(R.id.signupButton);
        relativeLayout = findViewById(R.id.rl);
        TextView textView = findViewById(R.id.textView);
//
//        Typeface typeface = ResourcesCompat.getFont(this, R.font.raleway);
//        textView.setTextColor(R.color.white);
//        textView.setTypeface(typeface);


        button1.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btn));
        button2.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btn));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the color to relative layout
               // relativeLayout.setBackgroundResource(R.drawable.button_border);
                Intent intent = new Intent(startActivity.this, LoginScreen.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set the color to relative layout
              //  relativeLayout.setBackgroundResource(R.drawable.button_border);
                Intent intent = new Intent(startActivity.this,activity_register.class);
                startActivity(intent);
            }
        });

    }
}