package com.example.languageacademy;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup back button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());

        try {
            // Configuración del WebView
            WebView webView = findViewById(R.id.webView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // El mapa se ha cargado completamente
                    Toast.makeText(MapActivity.this, "Mapa cargado correctamente", Toast.LENGTH_SHORT).show();
                }
            });

            // Cargar un mapa de Google Maps directamente
            webView.loadUrl("https://www.google.com/maps/search/?api=1&query=Riera+de+Miró+76+43203+Reus+Tarragona");
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar el mapa: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}