package com.example.languageacademy;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
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
    private boolean soundLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnLocations = findViewById(R.id.btnLocations);
        btnVocabulary = findViewById(R.id.btnVocabulary);
        btnWebsite = findViewById(R.id.btnWebsite);

        // Setup gesture library
        try {
            setupGestures();
        } catch (Exception e) {
            Toast.makeText(this, "Error with gestures: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Setup SoundPool for button click sounds
        setupSoundPool();

        // Button click listeners
        setButtonListeners();
    }

    private void setupGestures() {
        try {
            gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
            if (!gestureLib.load()) {
                Toast.makeText(this, "Could not load gestures library", Toast.LENGTH_SHORT).show();
            }

            GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
            if (gestureOverlayView != null) {
                gestureOverlayView.addOnGesturePerformedListener(this);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up gestures", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSoundPool() {
        try {
            // Para Android 5.0 (Lollipop) y versiones posteriores
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                soundPool = new SoundPool.Builder()
                        .setMaxStreams(10)
                        .setAudioAttributes(audioAttributes)
                        .build();
            } else {
                // Para versiones anteriores a Lollipop
                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            }

            // Configura un listener para saber cuando el sonido está cargado
            soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
                if (status == 0) {
                    soundLoaded = true;
                    Toast.makeText(MainActivity.this, "Sound loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load sound", Toast.LENGTH_SHORT).show();
                }
            });

            // Intenta cargar el sonido
            try {
                soundButtonClick = soundPool.load(this, R.raw.button_click, 1);
            } catch (Exception e) {
                // Si falla, intenta cargar un sonido del sistema
                soundButtonClick = 1; // Un ID temporal
                Toast.makeText(this, "Using system sound", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up sound: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        try {
            // Asegúrate de que el volumen esté al máximo
            float leftVolume = 1.0f;
            float rightVolume = 1.0f;
            int priority = 1;
            int loop = 0;
            float rate = 1.0f;

            // Verifica que el AudioManager no esté en modo silencio
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            float currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = currentVolume / maxVolume;

            // Si el volumen está muy bajo, usa un volumen mínimo
            if (volume < 0.1f) {
                volume = 0.3f;
            }

            // Reproduce el sonido
            if (soundPool != null && soundButtonClick > 0) {
                soundPool.play(soundButtonClick, volume, volume, priority, loop, rate);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error playing sound: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
        String url = "https://www.example.com";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        try {
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
        } catch (Exception e) {
            Toast.makeText(this, "Error with gesture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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