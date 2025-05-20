package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicjalizacja przycisków dla każdego poziomu
        Button level0Button = findViewById(R.id.level_button_0);
        Button level1Button = findViewById(R.id.level_button_1);
        Button level2Button = findViewById(R.id.level_button_2);
        Button exitButton = findViewById(R.id.exit_button);

        // Ustawienie listenera dla każdego przycisku
        level0Button.setOnClickListener(this);
        level1Button.setOnClickListener(this);
        level2Button.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        
        if (viewId == R.id.level_button_0) {
            startLevel(0);
        } else if (viewId == R.id.level_button_1) {
            startLevel(1);
        } else if (viewId == R.id.level_button_2) {
            startLevel(2);
        } else if (viewId == R.id.exit_button) {
            showExitConfirmation();
        }
    }
    
    private void startLevel(int levelIndex) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("LEVEL", levelIndex);
        startActivity(intent);
        // Dodajemy animację przejścia
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Wyjście z gry")
            .setMessage("Czy na pewno chcesz wyjść z gry?")
            .setPositiveButton("Tak", (dialog, which) -> {
                finish();
            })
            .setNegativeButton("Nie", null)
            .show();
    }
    
    @Override
    public void onBackPressed() {
        showExitConfirmation();
    }
}
