package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class Timetable extends AppCompatActivity {

    private static final int DELAY_AFTER_FADE_IN = 20000; // 10 seconds delay in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Button b1 = findViewById(R.id.button);
        ImageView timetableImageView = findViewById(R.id.timetableImageView);

        // Load animations
        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        final Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        // Start the fade-in animation
        timetableImageView.startAnimation(animFadeIn);

        // Set up animation listeners to create a continuous loop
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Wait for 10 seconds before starting the fade-out animation
                new Handler().postDelayed(() -> {
                    timetableImageView.startAnimation(animFadeOut);
                }, DELAY_AFTER_FADE_IN);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the fade-in animation after fade-out ends
                timetableImageView.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Set click listener to navigate to LoginActivity
        b1.setOnClickListener(v -> {
            startActivity(new Intent(Timetable.this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // Apply fade animation on activity transition
        });
    }
}