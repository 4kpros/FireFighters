package com.example.firefighters.ui.bottomsheetfragment.mapview;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.example.firefighters.R;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mapbox.android.core.location.LocationEngineRequest;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

public class MapViewFragment extends BottomSheetDialogFragment {

    private Context context;
    private LocationManager locationManager;
    private MapView mapView;
    private LocationComponent locationComponent;

    public static MapViewFragment newInstance(Context context) {
        //this.context = context;
        return new MapViewFragment();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.form_map_view, container, false);

        initViews(view);
        setupMapView(view, savedInstanceState);

        return view;
    }

    private void setupMapView(View view, Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

//                        enableRotatingClick(mapboxMap);
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
//                // Set the component's camera mode
//                locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
//
//                // Set the component's render mode
//                locationComponent.setRenderMode(RenderMode.COMPASS);
//
//            }
//    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());

        });
    }

    private void initViews(View view) {
        mapView = (MapView) view.findViewById(R.id.map_view);
    }
}
