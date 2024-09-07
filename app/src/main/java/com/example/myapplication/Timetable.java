package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        Button b1 = findViewById(R.id.button);
        ImageView timetableImageView = findViewById(R.id.timetableImageView);


        final Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        final Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);


        // Set the initial animation
        timetableImageView.startAnimation(animFadeIn);

        // Set up animation listeners
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                timetableImageView.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });






        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                timetableImageView.startAnimation(animFadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                timetableImageView.startAnimation(animFadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        b1.setOnClickListener(v -> startActivity(new Intent(Timetable.this, LoginActivity.class)));
    }
}
