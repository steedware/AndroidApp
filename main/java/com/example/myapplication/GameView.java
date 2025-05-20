package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.hardware.*;
import android.view.*;
import android.widget.Toast;

public class GameView extends View implements SensorEventListener {
    private int[][][] levels = {
        // 0 - puste, 1 - ściana, 2 - start, 3 - kryształ
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,2,0,0,1,0,0,0,3,1},
            {1,0,1,0,1,0,1,0,1,1},
            {1,0,1,0,0,0,1,0,0,1},
            {1,0,1,1,1,0,1,1,0,1},
            {1,0,0,0,1,0,0,1,0,1},
            {1,1,1,0,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,1,0,1},
            {1,0,1,1,1,1,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1}
        },
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,2,0,1,0,0,1,0,3,1},
            {1,0,0,1,0,1,1,0,1,1},
            {1,1,0,1,0,0,0,0,0,1},
            {1,0,0,0,1,1,1,1,0,1},
            {1,0,1,0,0,0,1,0,0,1},
            {1,0,1,1,1,0,1,1,1,1},
            {1,0,0,0,1,0,0,0,0,1},
            {1,1,1,0,1,1,1,1,0,1},
            {1,1,1,1,1,1,1,1,1,1}
        },
        {
            {1,1,1,1,1,1,1,1,1,1},
            {1,2,0,0,0,1,0,0,3,1},
            {1,1,1,1,0,1,0,1,1,1},
            {1,0,0,1,0,0,0,1,0,1},
            {1,0,1,1,1,1,0,1,0,1},
            {1,0,1,0,0,0,0,1,0,1},
            {1,0,1,0,1,1,1,1,0,1},
            {1,0,0,0,1,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1}
        }
    };

    private int[][] map;
    private int cellSize;
    private float ballX, ballY, ballRadius;
    private float velocityX = 0, velocityY = 0;
    private int startX, startY, crystalX, crystalY;
    private Paint wallPaint, ballPaint, crystalPaint, bgPaint;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long startTime, endTime;
    private boolean finished = false;
    private int level;

    private Paint backButtonPaint;
    private RectF backButtonRect;
    private boolean isBackButtonPressed = false;
    private final float TIME_FORMAT_REFRESH_RATE = 100; // ms
    private long lastTimeUpdate = 0;
    private String timeString = "00:00.00";

    public GameView(Context context, int level) {
        super(context);
        this.level = level;
        map = levels[level];
        wallPaint = new Paint();
        wallPaint.setColor(Color.parseColor("#22223B"));
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setShadowLayer(12, 0, 0, Color.parseColor("#66000000"));
        setLayerType(LAYER_TYPE_SOFTWARE, wallPaint);

        ballPaint = new Paint();
        ballPaint.setColor(Color.parseColor("#2196F3"));
        ballPaint.setShadowLayer(16, 0, 0, Color.parseColor("#80000000"));

        crystalPaint = new Paint();
        crystalPaint.setColor(Color.parseColor("#E040FB"));
        crystalPaint.setShadowLayer(16, 0, 0, Color.parseColor("#80000000"));

        bgPaint = new Paint();
        // Gradient tła rysowany w onDraw

        // Znajdź pozycję startową i kryształu
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 2) {
                    startX = j;
                    startY = i;
                }
                if (map[i][j] == 3) {
                    crystalX = j;
                    crystalY = i;
                }
            }
        }

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        startTime = System.currentTimeMillis();

        // Inicjalizacja przycisku powrotu
        backButtonPaint = new Paint();
        backButtonPaint.setColor(Color.parseColor("#AA22223B"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        cellSize = Math.min(w, h) / map.length;
        ballRadius = cellSize * 0.35f;

        // Gradientowe tło
        Paint gradientPaint = new Paint();
        LinearGradient gradient = new LinearGradient(0, 0, w, h,
                Color.parseColor("#2b5876"), Color.parseColor("#4e4376"), Shader.TileMode.CLAMP);
        gradientPaint.setShader(gradient);
        canvas.drawRect(0, 0, w, h, gradientPaint);

        // Wyśrodkowanie planszy
        int offsetX = (w - cellSize * map.length) / 2;
        int offsetY = (h - cellSize * map.length) / 2;

        // Rysuj labirynt
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    canvas.drawRoundRect(
                        offsetX + j * cellSize, offsetY + i * cellSize,
                        offsetX + (j + 1) * cellSize, offsetY + (i + 1) * cellSize,
                        18, 18, wallPaint);
                }
                if (map[i][j] == 3) {
                    float cx = offsetX + j * cellSize + cellSize / 2f;
                    float cy = offsetY + i * cellSize + cellSize / 2f;
                    canvas.drawCircle(cx, cy, ballRadius, crystalPaint);
                    // Efekt błysku na krysztale
                    Paint shine = new Paint();
                    shine.setColor(Color.WHITE);
                    shine.setAlpha(80);
                    canvas.drawCircle(cx - ballRadius/3, cy - ballRadius/3, ballRadius/2.5f, shine);
                }
            }
        }

        // Inicjalizacja pozycji kulki
        if (ballX == 0 && ballY == 0) {
            ballX = offsetX + startX * cellSize + cellSize / 2f;
            ballY = offsetY + startY * cellSize + cellSize / 2f;
        }

        // Rysuj kulkę
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);

        // Aktualizuj czas
        long now = finished ? endTime : System.currentTimeMillis();
        long elapsedTime = now - startTime;
        
        // Aktualizuj ciąg znaków czasu tylko co 100ms dla lepszej wydajności
        if (now - lastTimeUpdate > TIME_FORMAT_REFRESH_RATE) {
            timeString = formatTime(elapsedTime);
            lastTimeUpdate = now;
        }

        // Rysuj panel dolny z czasem i przyciskiem powrotu (teraz wyższy by pomieścić przycisk pod czasem)
        Paint textBg = new Paint();
        textBg.setColor(Color.parseColor("#AA22223B"));
        textBg.setStyle(Paint.Style.FILL);
        float infoHeight = cellSize * 2.8f; // Zwiększona wysokość panelu
        float bottomY = offsetY + map.length * cellSize + 10; // 10px odstęp od planszy
        RectF infoRect = new RectF(0, bottomY, w, bottomY + infoHeight);
        canvas.drawRoundRect(infoRect, 15, 15, textBg);

        // Rysuj tekst czasu - wyśrodkowany w górnej części panelu
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(cellSize * 0.6f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Czas wyśrodkowany w górnej części panelu
        canvas.drawText(timeString, w / 2f, bottomY + cellSize * 0.9f, textPaint);

        // Rysuj małe info o poziomie
        textPaint.setTextSize(cellSize * 0.4f);
        canvas.drawText("Poziom " + (level + 1), w / 2f, bottomY + cellSize * 0.35f, textPaint);

        // Narysuj przycisk powrotu (teraz pod zegarem)
        float buttonWidth = cellSize * 2.0f;
        float buttonHeight = cellSize * 1.0f;
        float buttonX = (w - buttonWidth) / 2f; // Wyśrodkowanie przycisku
        float buttonY = bottomY + cellSize * 1.5f; // Pozycja pod zegarem
        
        backButtonRect = new RectF(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight);
        backButtonPaint.setColor(isBackButtonPressed ? Color.parseColor("#E04040") : Color.parseColor("#FF9800"));
        canvas.drawRoundRect(backButtonRect, cellSize * 0.2f, cellSize * 0.2f, backButtonPaint);

        // Narysuj strzałkę powrotu
        Paint arrowPaint = new Paint();
        arrowPaint.setColor(Color.WHITE);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeWidth(cellSize * 0.1f);

        float centerX = backButtonRect.centerX();
        float centerY = backButtonRect.centerY();
        float arrowSize = cellSize * 0.3f;

        Path arrowPath = new Path();
        arrowPath.moveTo(centerX + arrowSize, centerY - arrowSize);
        arrowPath.lineTo(centerX - arrowSize, centerY);
        arrowPath.lineTo(centerX + arrowSize, centerY + arrowSize);

        canvas.drawPath(arrowPath, arrowPaint);

        if (finished) {
            // Dodaj półprzezroczyste tło dla wyniku
            Paint scoreBgPaint = new Paint();
            scoreBgPaint.setColor(Color.parseColor("#AA000000"));
            canvas.drawRect(0, 0, w, h, scoreBgPaint);

            // Rysuj panel wyniku
            Paint scorePanelPaint = new Paint();
            scorePanelPaint.setColor(Color.parseColor("#FFFFFF"));

            RectF scorePanel = new RectF(w/2 - w/3, h/2 - h/6, w/2 + w/3, h/2 + h/6);
            canvas.drawRoundRect(scorePanel, 30, 30, scorePanelPaint);

            // Tekst wyniku
            Paint scorePaint = new Paint();
            scorePaint.setColor(Color.parseColor("#222222"));
            scorePaint.setTextSize(cellSize * 0.7f);
            scorePaint.setTextAlign(Paint.Align.CENTER);
            scorePaint.setFakeBoldText(true);

            int score = (int)(10000 / (elapsedTime / 1000f));
            canvas.drawText("UKOŃCZONO!", w/2, h/2 - cellSize * 0.5f, scorePaint);
            canvas.drawText("Czas: " + timeString, w/2, h/2, scorePaint);
            canvas.drawText("Wynik: " + score, w/2, h/2 + cellSize * 0.8f, scorePaint);
        }
    }

    private String formatTime(long timeInMillis) {
        int minutes = (int) (timeInMillis / 60000);
        int seconds = (int) (timeInMillis / 1000) % 60;
        int millis = (int) (timeInMillis % 1000) / 10;

        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (backButtonRect != null && backButtonRect.contains(x, y)) {
                    isBackButtonPressed = true;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (isBackButtonPressed) {
                    isBackButtonPressed = false;
                    invalidate();

                    // Wróć do menu głównego
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).finish();
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                isBackButtonPressed = false;
                invalidate();
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (finished) return;
        float ax = event.values[0];
        float ay = event.values[1];

        // Sterowanie: odwróć osie dla naturalnego ruchu
        velocityX += -ax * 0.7f;
        velocityY += ay * 0.7f;

        // Tłumienie
        velocityX *= 0.92f;
        velocityY *= 0.92f;

        float nextX = ballX + velocityX;
        float nextY = ballY + velocityY;

        // Obliczamy offset centrowania planszy
        int w = getWidth();
        int h = getHeight();
        int offsetX = (w - cellSize * map.length) / 2;
        int offsetY = (h - cellSize * map.length) / 2;

        // Kolizje ze ścianami - uwzględniamy przesunięcie planszy
        int cellX = (int)((nextX - offsetX) / cellSize);
        int cellY = (int)((nextY - offsetY) / cellSize);
        
        // Sprawdź, czy jesteśmy w granicach planszy
        if (cellX < 0 || cellY < 0 || cellX >= map.length || cellY >= map.length) {
            velocityX = 0;
            velocityY = 0;
        }
        // Sprawdź kolizję ze ścianą
        else if (map[cellY][cellX] == 1) {
            velocityX = 0;
            velocityY = 0;
        } 
        else {
            ballX = nextX;
            ballY = nextY;
            
            // Sprawdź czy dotarliśmy do kryształu
            if (cellX == crystalX && cellY == crystalY) {
                finished = true;
                endTime = System.currentTimeMillis();
                sensorManager.unregisterListener(this);
                post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Gratulacje! Twój wynik: " + (int)(10000 / ((endTime - startTime) / 1000f)),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }
}
