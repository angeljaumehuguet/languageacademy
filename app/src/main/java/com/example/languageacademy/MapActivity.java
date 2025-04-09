package com.example.languageacademy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        // Obtiene el fragmento del mapa y solicita ser notificado cuando el mapa esté listo
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Redimensiona el icono personalizado
            BitmapDescriptor smallMarkerIcon = createSmallMarkerIcon();

            // Coordenadas de las dos academias en Reus
            // Nota: Estas son coordenadas aproximadas, puedes ajustarlas si tienes las coordenadas exactas
            LatLng rieraLocation = new LatLng(41.1525, 1.1103); // Riera de Miró 76
            LatLng ravalLocation = new LatLng(41.1505, 1.1050); // Raval de Jesús 42

            // Añade los marcadores con tu icono personalizado redimensionado
            mMap.addMarker(new MarkerOptions()
                    .position(rieraLocation)
                    .title("Academia de Inglés - Riera")
                    .snippet("Riera de Miró 76-1º, 43203 Reus, (Tarragona)")
                    .icon(smallMarkerIcon));

            mMap.addMarker(new MarkerOptions()
                    .position(ravalLocation)
                    .title("Academia de Inglés - Raval")
                    .snippet("Raval de Jesús 42-1º, 43201 Reus, (Tarragona)")
                    .icon(smallMarkerIcon));

            // Crear límites que incluyan ambos marcadores
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(rieraLocation);
            builder.include(ravalLocation);
            LatLngBounds bounds = builder.build();

            // Mueve la cámara para incluir ambos marcadores con un padding
            int padding = 100; // en píxeles
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            // Habilita controles de zoom
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            Toast.makeText(this, "Mapa cargado correctamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar el mapa: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private BitmapDescriptor createSmallMarkerIcon() {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker_academy);
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // Redimensiona a aproximadamente 1/6 del tamaño original para hacerlo mucho más pequeño
        int newWidth = width / 6;
        int newHeight = height / 6;

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }
}