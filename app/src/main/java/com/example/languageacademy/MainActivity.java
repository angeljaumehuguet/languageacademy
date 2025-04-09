package com.example.languageacademy;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private Button btnLocations, btnVocabulary, btnWebsite;
    private GestureLibrary gestureLib;
    private SoundPool soundPool;
    private int soundButtonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnLocations = findViewById(R.id.btnLocations);
        btnVocabulary = findViewById(R.id.btnVocabulary);
        btnWebsite = findViewById(R.id.btnWebsite);

        // Setup gesture library
        setupGestures();

        // Setup SoundPool for button click sounds
        setupSoundPool();

        // Button click listeners
        setButtonListeners();
    }

    private void setupGestures() {
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            Toast.makeText(this, "Could not load gestures library", Toast.LENGTH_SHORT).show();
            finish();
        }

        GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
        gestureOverlayView.addOnGesturePerformedListener(this);
    }

    private void setupSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundButtonClick = soundPool.load(this, R.raw.button_click, 1);
    }

    private void setButtonListeners() {
        btnLocations.setOnClickListener(view -> {
            playButtonSound();
            openMapActivity();
        });

        btnVocabulary.setOnClickListener(view -> {
            playButtonSound();
            openWordsActivity();
        });

        btnWebsite.setOnClickListener(view -> {
            playButtonSound();
            openWebsite();
        });
    }

    private void playButtonSound() {
        soundPool.play(soundButtonClick, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private void openMapActivity() {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    private void openWordsActivity() {
        Intent intent = new Intent(MainActivity.this, WordsActivity.class);
        startActivity(intent);
    }

    private void openWebsite() {
        String url = "https://towerenglish.net/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String action = predictions.get(0).name;
            
            // Handle gesture actions
            switch (action) {
                case "map":
                    openMapActivity();
                    break;
                case "vocabulary":
                    openWordsActivity();
                    break;
                case "website":
                    openWebsite();
                    break;
                case "exit":
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}