package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animacje
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Znajdź elementy UI
        ImageView logoImage = findViewById(R.id.splash_logo);
        TextView titleText = findViewById(R.id.splash_title);

        // Uruchom animacje
        logoImage.startAnimation(fadeInAnim);
        titleText.startAnimation(slideInAnim);

        // Przejdź do MainActivity po zakończeniu animacji
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                // Dodaj animację przejścia
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 2500); // 2.5 sekundy opóźnienia
    }
}
