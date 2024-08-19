package com.example.inventorymanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class SplashActivity extends AppCompatActivity {

    private AppCompatImageView phone;
    private AppCompatImageView perfume;
    private AppCompatImageView glasses;
    private AppCompatImageView box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();
        startAnimations();
    }

    private void findViews() {
        phone = findViewById(R.id.phone);
        perfume = findViewById(R.id.perfume);
        glasses = findViewById(R.id.glasses);
        box = findViewById(R.id.box);
    }

    private void startAnimations() {
        // Calculate the distance to move the images to align with the box
        box.post(() -> {
            int boxCenterY = box.getTop() + box.getHeight() / 6;
            int phoneStartY = phone.getTop();
            int moveDistance = boxCenterY - phoneStartY;

            // Create translation animations for each item
            TranslateAnimation phoneFall = new TranslateAnimation(0, 0, 0, moveDistance);
            TranslateAnimation perfumeFall = new TranslateAnimation(0, 0, 0, moveDistance);
            TranslateAnimation glassesFall = new TranslateAnimation(0, 0, 0, moveDistance);

            phoneFall.setDuration(1000);
            perfumeFall.setDuration(1000);
            glassesFall.setDuration(1000);

            // Set animation listeners to start the next animation and make the current image invisible
            phone.startAnimation(phoneFall);
            phoneFall.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    phone.setVisibility(View.INVISIBLE);
                    perfume.setVisibility(View.VISIBLE);
                    perfume.startAnimation(perfumeFall);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            perfumeFall.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    perfume.setVisibility(View.INVISIBLE);
                    glasses.setVisibility(View.VISIBLE);
                    glasses.startAnimation(glassesFall);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            glassesFall.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    glasses.setVisibility(View.INVISIBLE);
                    // Delay for 0.2 seconds after the last animation before switching to MainActivity
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }, 200);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });
    }
}
