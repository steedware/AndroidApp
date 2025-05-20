package com.example.myapplication;

import android.os.Bundle;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int level = getIntent().getIntExtra("LEVEL", 0);
        gameView = new GameView(this, level);
        setContentView(gameView);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitConfirmationDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Powrót do menu")
            .setMessage("Czy na pewno chcesz opuścić grę i wrócić do menu głównego?")
            .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            })
            .setNegativeButton("Nie", null)
            .show();
    }
}
