package com.example.languageacademy;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordsActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLib;
    
    // Media player instances
    private MediaPlayer mpHello;
    private MediaPlayer mpGoodbye;
    private MediaPlayer mpThankYou;
    private MediaPlayer mpPlease;
    private MediaPlayer mpWelcome;
    
    // Track current positions for each audio
    private final Map<String, Integer> audioPositions = new HashMap<>();
    
    // Button references
    private ImageButton btnPlayHello, btnPauseHello;
    private ImageButton btnPlayGoodbye, btnPauseGoodbye;
    private ImageButton btnPlayThankYou, btnPauseThankYou;
    private ImageButton btnPlayPlease, btnPausePlease;
    private ImageButton btnPlayWelcome, btnPauseWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI elements
        initializeUI();
        
        // Initialize media players
        initializeMediaPlayers();
        
        // Restore saved instance state
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
        
        // Setup gestures
        setupGestures();
    }

    private void initializeUI() {
        // Initialize play buttons
        btnPlayHello = findViewById(R.id.btnPlayHello);
        btnPauseHello = findViewById(R.id.btnPauseHello);
        
        btnPlayGoodbye = findViewById(R.id.btnPlayGoodbye);
        btnPauseGoodbye = findViewById(R.id.btnPauseGoodbye);
        
        btnPlayThankYou = findViewById(R.id.btnPlayThankYou);
        btnPauseThankYou = findViewById(R.id.btnPauseThankYou);
        
        btnPlayPlease = findViewById(R.id.btnPlayPlease);
        btnPausePlease = findViewById(R.id.btnPausePlease);
        
        btnPlayWelcome = findViewById(R.id.btnPlayWelcome);
        btnPauseWelcome = findViewById(R.id.btnPauseWelcome);
        
        // Set button click listeners
        setButtonListeners();
        
        // Setup back button
        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(view -> finish());
    }
    
    private void initializeMediaPlayers() {
        mpHello = MediaPlayer.create(this, R.raw.hello);
        mpGoodbye = MediaPlayer.create(this, R.raw.goodbye);
        mpThankYou = MediaPlayer.create(this, R.raw.thank_you);
        mpPlease = MediaPlayer.create(this, R.raw.please);
        mpWelcome = MediaPlayer.create(this, R.raw.welcome);
        
        // Initialize audio positions
        audioPositions.put("hello", 0);
        audioPositions.put("goodbye", 0);
        audioPositions.put("thank_you", 0);
        audioPositions.put("please", 0);
        audioPositions.put("welcome", 0);
    }
    
    private void setButtonListeners() {
        // Hello buttons
        btnPlayHello.setOnClickListener(v -> playAudio("hello", mpHello));
        btnPauseHello.setOnClickListener(v -> pauseAudio("hello", mpHello));
        
        // Goodbye buttons
        btnPlayGoodbye.setOnClickListener(v -> playAudio("goodbye", mpGoodbye));
        btnPauseGoodbye.setOnClickListener(v -> pauseAudio("goodbye", mpGoodbye));
        
        // Thank You buttons
        btnPlayThankYou.setOnClickListener(v -> playAudio("thank_you", mpThankYou));
        btnPauseThankYou.setOnClickListener(v -> pauseAudio("thank_you", mpThankYou));
        
        // Please buttons
        btnPlayPlease.setOnClickListener(v -> playAudio("please", mpPlease));
        btnPausePlease.setOnClickListener(v -> pauseAudio("please", mpPlease));
        
        // Welcome buttons
        btnPlayWelcome.setOnClickListener(v -> playAudio("welcome", mpWelcome));
        btnPauseWelcome.setOnClickListener(v -> pauseAudio("welcome", mpWelcome));
    }
    
    private void playAudio(String audioName, MediaPlayer mediaPlayer) {
        // Stop any other playing audio first
        stopAllAudioExcept(audioName);
        
        if (mediaPlayer != null) {
            int position = audioPositions.getOrDefault(audioName, 0);
            
            if (position > 0) {
                mediaPlayer.seekTo(position);
            }
            
            mediaPlayer.start();
        }
    }
    
    private void pauseAudio(String audioName, MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioPositions.put(audioName, mediaPlayer.getCurrentPosition());
        }
    }
    
    private void stopAllAudioExcept(String exceptAudio) {
        if (mpHello != null && mpHello.isPlaying() && !exceptAudio.equals("hello")) {
            audioPositions.put("hello", mpHello.getCurrentPosition());
            mpHello.pause();
        }
        
        if (mpGoodbye != null && mpGoodbye.isPlaying() && !exceptAudio.equals("goodbye")) {
            audioPositions.put("goodbye", mpGoodbye.getCurrentPosition());
            mpGoodbye.pause();
        }
        
        if (mpThankYou != null && mpThankYou.isPlaying() && !exceptAudio.equals("thank_you")) {
            audioPositions.put("thank_you", mpThankYou.getCurrentPosition());
            mpThankYou.pause();
        }
        
        if (mpPlease != null && mpPlease.isPlaying() && !exceptAudio.equals("please")) {
            audioPositions.put("please", mpPlease.getCurrentPosition());
            mpPlease.pause();
        }
        
        if (mpWelcome != null && mpWelcome.isPlaying() && !exceptAudio.equals("welcome")) {
            audioPositions.put("welcome", mpWelcome.getCurrentPosition());
            mpWelcome.pause();
        }
    }
    
    private void setupGestures() {
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }

        GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
        gestureOverlayView.addOnGesturePerformedListener(this);
    }
    
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String action = predictions.get(0).name;
            
            // Handle gesture actions
            switch (action) {
                case "map":
                    startActivity(new Intent(WordsActivity.this, MapActivity.class));
                    finish();
                    break;
                case "website":
                    String url = "https://www.example.com";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    break;
                case "home":
                    finish();
                    break;
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        // Save current audio positions
        if (mpHello != null) {
            outState.putInt("hello_position", mpHello.isPlaying() ? 
                    mpHello.getCurrentPosition() : audioPositions.get("hello"));
        }
        
        if (mpGoodbye != null) {
            outState.putInt("goodbye_position", mpGoodbye.isPlaying() ? 
                    mpGoodbye.getCurrentPosition() : audioPositions.get("goodbye"));
        }
        
        if (mpThankYou != null) {
            outState.putInt("thank_you_position", mpThankYou.isPlaying() ? 
                    mpThankYou.getCurrentPosition() : audioPositions.get("thank_you"));
        }
        
        if (mpPlease != null) {
            outState.putInt("please_position", mpPlease.isPlaying() ? 
                    mpPlease.getCurrentPosition() : audioPositions.get("please"));
        }
        
        if (mpWelcome != null) {
            outState.putInt("welcome_position", mpWelcome.isPlaying() ? 
                    mpWelcome.getCurrentPosition() : audioPositions.get("welcome"));
        }
    }
    
    private void restoreSavedState(Bundle savedInstanceState) {
        // Restore audio positions
        audioPositions.put("hello", savedInstanceState.getInt("hello_position", 0));
        audioPositions.put("goodbye", savedInstanceState.getInt("goodbye_position", 0));
        audioPositions.put("thank_you", savedInstanceState.getInt("thank_you_position", 0));
        audioPositions.put("please", savedInstanceState.getInt("please_position", 0));
        audioPositions.put("welcome", savedInstanceState.getInt("welcome_position", 0));
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Pause all media players and save positions
        if (mpHello != null && mpHello.isPlaying()) {
            audioPositions.put("hello", mpHello.getCurrentPosition());
            mpHello.pause();
        }
        
        if (mpGoodbye != null && mpGoodbye.isPlaying()) {
            audioPositions.put("goodbye", mpGoodbye.getCurrentPosition());
            mpGoodbye.pause();
        }
        
        if (mpThankYou != null && mpThankYou.isPlaying()) {
            audioPositions.put("thank_you", mpThankYou.getCurrentPosition());
            mpThankYou.pause();
        }
        
        if (mpPlease != null && mpPlease.isPlaying()) {
            audioPositions.put("please", mpPlease.getCurrentPosition());
            mpPlease.pause();
        }
        
        if (mpWelcome != null && mpWelcome.isPlaying()) {
            audioPositions.put("welcome", mpWelcome.getCurrentPosition());
            mpWelcome.pause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Release all media players
        if (mpHello != null) {
            mpHello.release();
            mpHello = null;
        }
        
        if (mpGoodbye != null) {
            mpGoodbye.release();
            mpGoodbye = null;
        }
        
        if (mpThankYou != null) {
            mpThankYou.release();
            mpThankYou = null;
        }
        
        if (mpPlease != null) {
            mpPlease.release();
            mpPlease = null;
        }
        
        if (mpWelcome != null) {
            mpWelcome.release();
            mpWelcome = null;
        }
    }
}