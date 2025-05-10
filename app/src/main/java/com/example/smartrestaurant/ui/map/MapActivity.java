package com.example.smartrestaurant.ui.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.example.smartrestaurant.BaseActivity;
import com.example.smartrestaurant.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng restaurantLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fullscreen_map);
        mapFragment.getMapAsync(this);

        // Default location - should get from server in real app
        restaurantLocation = new LatLng(40.7128, -74.0060); // Example: New York

        // Set up directions FAB
        FloatingActionButton fabDirections = findViewById(R.id.fab_directions);
        fabDirections.setOnClickListener(view -> openDirections());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for our restaurant and move the camera
        mMap.addMarker(new MarkerOptions()
                .position(restaurantLocation)
                .title("Smart Restaurant"));

        // Zoom level: 1 (world), 5 (continent), 10 (city), 15 (streets), 20 (buildings)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 15f));

        // Enable zoom controls and compass
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    // Launch Google Maps for directions to restaurant
    private void openDirections() {
        // Create a URI for Google Maps directions
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                restaurantLocation.latitude + "," + restaurantLocation.longitude);

        // Create an Intent to launch Google Maps
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if Google Maps is installed
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
}