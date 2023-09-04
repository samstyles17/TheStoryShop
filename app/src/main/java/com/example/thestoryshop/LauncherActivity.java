package com.example.thestoryshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity
{

    Button button;
    private TextView textView1, textView2, textView3;
    private Handler handler;
    private int currentTextView = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        button = findViewById(R.id.buttonStart);

        handler = new Handler();
        handler.postDelayed(animationRunnable, 20);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    Intent i = new Intent(LauncherActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(LauncherActivity.this, AuthenticationActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            crossFadeText();
            handler.postDelayed(this, 5000); // Repeat the animation every 20ms
        }
    };

    private void crossFadeText() {
        int fadeInDuration = 1000; // Duration of the fade-in animation
        int fadeOutDuration = 1000; // Duration of the fade-out animation

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(fadeOutDuration);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(fadeInDuration);

        // Hide the current text with fade-out animation
        switch (currentTextView) {
            case 1:
                textView1.startAnimation(fadeOut);
                break;
            case 2:
                textView2.startAnimation(fadeOut);
                break;
            case 3:
                textView3.startAnimation(fadeOut);
                break;
        }

        // Show the next text with fade-in animation
        currentTextView = (currentTextView % 3) + 1;
        switch (currentTextView) {
            case 1:
                textView1.setVisibility(View.VISIBLE);
                textView1.startAnimation(fadeIn);
                textView2.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                textView2.setVisibility(View.VISIBLE);
                textView2.startAnimation(fadeIn);
                textView1.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
                break;
            case 3:
                textView3.setVisibility(View.VISIBLE);
                textView3.startAnimation(fadeIn);
                textView1.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(animationRunnable);
    }
}