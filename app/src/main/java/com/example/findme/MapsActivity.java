package com.example.findme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int REQUEST_LOCATION_PERMISSIONS = 0;

    private GoogleMap mMap;
    private FusedLocationProviderClient mClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private float mZoomLevel = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Create location request
        mLocationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    updateMap(location);
                }
            }
        };

        mClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void updateMap(Location location) {

        // TODO: Show user's location on the map
        // Get current location
        LatLng myLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());

        // Place a marker at the current location
        MarkerOptions myMarker = new MarkerOptions()
                .title("Here you are!")
                .position(myLatLng);

        // Remove previous marker
        mMap.clear();

        // Add new marker
        mMap.addMarker(myMarker);

        // Move and zoom to current location at the street level
        CameraUpdate update = CameraUpdateFactory.
                newLatLngZoom(myLatLng, 15);
        mMap.animateCamera(update);

        // Zoom to previously saved level
        CameraUpdate updateTwo = CameraUpdateFactory.newLatLngZoom(myLatLng, mZoomLevel);
        mMap.animateCamera(updateTwo);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Save zoom level
        mMap.setOnCameraMoveListener(() -> {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            mZoomLevel = cameraPosition.zoom;
        });

        // Handle marker click
        mMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(MapsActivity.this, "Lat: " + marker.getPosition().latitude +
                            System.getProperty("line.separator") + "Long: " + marker.getPosition().longitude,
                    Toast.LENGTH_LONG).show();
            return false;
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mClient.removeLocationUpdates(mLocationCallback);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        if (hasLocationPermission()) {
            mClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                    Looper.getMainLooper());
        }
    }

    private boolean hasLocationPermission() {

        // Request fine location permission if not already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_LOCATION_PERMISSIONS);

            return false;
        }

        return true;
    }
}