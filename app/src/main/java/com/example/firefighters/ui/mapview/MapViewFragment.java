package com.example.firefighters.ui.mapview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.FireTruckModel;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.FireTruckViewModel;
import com.example.firefighters.viewmodels.WaterPointViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.Mapbox;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class MapViewFragment extends Fragment {

    MapView mapView;
    private ImageView buttonImagePosition;
    private FloatingActionButton floatingButtonPosition;

    EmergencyViewModel emergencyViewModel;
    WaterPointViewModel waterPointViewModel;
    FireTruckViewModel fireTruckViewModel;
    private MaterialButton buttonStartNavigation;

    int startLon;
    int startLat;
    int endLon;
    int endLat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initViewModels();
        setupMapView(savedInstanceState);
    }

    private void initViewModels() {
        emergencyViewModel = new ViewModelProvider(requireActivity()).get(EmergencyViewModel.class);
        emergencyViewModel.init();
        waterPointViewModel = new ViewModelProvider(requireActivity()).get(WaterPointViewModel.class);
        emergencyViewModel.init();
        fireTruckViewModel = new ViewModelProvider(requireActivity()).get(FireTruckViewModel.class);
        emergencyViewModel.init();
    }

    private void checkInteractions(MapboxMap mapboxMap) {
        floatingButtonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation(mapboxMap);
            }
        });
        buttonStartNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation();
            }
        });
    }

    private void startNavigation() {
        //
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView.onSaveInstanceState(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull @NotNull Style style) {
                        mapboxMap.getUiSettings().setCompassEnabled(false);
                        mapboxMap.getUiSettings().setLogoEnabled(false);
                        mapboxMap.getUiSettings().setAttributionEnabled(false);
//                        new LoadPoiDataTask(SymbolLayerMapillaryActivity.this).execute();
//                        mapboxMap.addOnMapClickListener(SymbolLayerMapillaryActivity.this);
                        enableLocationComponent(mapboxMap, style);
                        enableRotatingClick(mapboxMap);
                        checkInteractions(mapboxMap);
                        observeEmergenciesPoints(mapboxMap, style);
//                        observeWaterPoints(mapboxMap, style);
//                        observeFireTrucksPoints(mapboxMap, style);
                    }
                });
            }
        });
    }

    private void observeEmergenciesPoints(MapboxMap mapboxMap, Style style){
        emergencyViewModel.getEmergenciesQuerySnapshot(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                addEmergenciesOnMap(mapboxMap, style, queryDocumentSnapshots);
            }
        });
    }

//    private void observeWaterPoints(MapboxMap mapboxMap, Style style){
//        waterPointViewModel.getWaterSourcesQuerySnapshot(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
//            @Override
//            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
//                //
//            }
//        });
//    }

//    private void observeFireTrucksPoints(MapboxMap mapboxMap, Style style){
//        fireTruckViewModel.getFireTrucksQuerySnapshot(requireActivity()).observe(requireActivity(), new Observer<QuerySnapshot>() {
//            @Override
//            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
//                //
//            }
//        });
//    }

    private void addEmergenciesOnMap(MapboxMap mapboxMap, Style style, QuerySnapshot queryDocumentSnapshots) {
        //
    }

    private void setWaterPoints(MapboxMap mapboxMap){
        //
    }

    private void setFireTrucksPoints(MapboxMap mapboxMap){
        //
    }

    private void getBestRoutesForPoint(){
        //
    }

    private void enableRotatingClick(MapboxMap mapboxMap) {
        if (mapboxMap == null)
            return;
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull @NotNull LatLng point) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(point.getLatitude(), point.getLongitude())) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(30) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
                return true;
            }
        });
    }

    private void enableLocationComponent(MapboxMap mapboxMap, @NonNull Style loadedMapStyle) {
        LocationComponentOptions locationComponentOptions =
                LocationComponentOptions.builder(requireContext())
                        .pulseEnabled(true)
                        .pulseFadeEnabled(true)
                        .build();

        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .build();

        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(locationComponentActivationOptions);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "None permissions detected", Toast.LENGTH_SHORT).show();
            return;
        }
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
        locationComponent.setRenderMode(RenderMode.GPS);
    }

    private void getMyLocation(MapboxMap mapboxMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(requireContext())
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            double latitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude();
                            double longitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude();
                            zoomCameraToPosition(mapboxMap, latitude, longitude);
                        } else {
                            Toast.makeText(requireContext(), "None location finded", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLocationAvailability(@NotNull LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                }, Looper.getMainLooper());
    }

    private void zoomCameraToPosition(MapboxMap mapboxMap, double latitude, double longitude){
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(0) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.map_view);
        floatingButtonPosition = view.findViewById(R.id.floating_button_my_position);
        buttonImagePosition = view.findViewById(R.id.button_image_my_position);
        buttonStartNavigation = view.findViewById(R.id.button_start_navigation);
    }
}
