package com.example.firefighters.ui.bottomsheetfragment.mapview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.example.firefighters.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.jetbrains.annotations.NotNull;

public class TestActivity extends AppCompatActivity {


    private Context context;
    private LocationManager locationManager;
    private MapView mapView;
    private LocationComponent locationComponent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        context = getApplicationContext();
        initViews();
        mapView.onCreate(savedInstanceState);
        setupMapView(savedInstanceState);

    }
    private void setupMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                        enableRotatingClick(mapboxMap);
//                        enableLocationComponent(mapboxMap, style);
                    }
                });

            }
        });
    }

    private void enableRotatingClick(MapboxMap mapboxMap) {
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull @NotNull LatLng point) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(51.50550, -0.07520)) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .bearing(180) // Rotate the camera
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 7000);
                return true;
            }
        });
    }

//    private void enableLocationComponent(MapboxMap mapboxMap, Style style) {
//
//        LocationComponentOptions locationComponentOptions =
//                LocationComponentOptions.builder(context)
//                        .pulseEnabled(true)
//                        .pulseColor(Color.GREEN)
//                        .pulseAlpha(.4f)
//                        .pulseInterpolator(new BounceInterpolator())
//                        .build();
//
//        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
//                .builder(context, style)
//                .locationComponentOptions(locationComponentOptions)
//                .build();
//
//        locationComponent = mapboxMap.getLocationComponent();
//        locationComponent.activateLocationComponent(locationComponentActivationOptions);
//
//
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(context)) {
//
//            // Get an instance of the component
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//
//            // Activate with a built LocationComponentActivationOptions object
//            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(context, style).build());
//
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            // Enable to make component visible
//            locationComponent.setLocationComponentEnabled(true);
//
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
//
//            // Set the component's render mode
//            locationComponent.setRenderMode(RenderMode.COMPASS);
//
//        }
//    }
    private void initViews() {
        mapView = (MapView) findViewById(R.id.map_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}