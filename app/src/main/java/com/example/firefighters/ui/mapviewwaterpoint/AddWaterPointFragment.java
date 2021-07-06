package com.example.firefighters.ui.mapviewwaterpoint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.FirebaseManager;
import com.example.firefighters.viewmodels.WaterPointViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class AddWaterPointFragment extends Fragment {

    MapView mapView;
    private MapboxMap mapboxMap;

    private MaterialButton buttonSave;
    private CircularProgressIndicator progressIndicator;
    private ImageView buttonBack;
    private TextView textTotalWaterPoints;

    WaterPointViewModel waterPointViewModel;

    private LocationComponent locationComponent;

    public static final String WATER_ICON = "water";
    private SymbolManager symbolManager;
    private List<Symbol> symbolsWaterPoints;

    FragmentActivity activity;

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private ArrayList<Object> waterPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_water_point, container, false);
        waterPoints = new ArrayList<>();
        activity = getActivity();
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
        waterPointViewModel = new ViewModelProvider(activity).get(WaterPointViewModel.class);
        waterPointViewModel.init();
    }

    private void getWaterPointOnMap(@NotNull Style style) {
        if (hoveringMarker.getVisibility() == View.VISIBLE) {
            // Use the map target's coordinates to make a reverse geocoding search
            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
            // Hide the hovering red hovering ImageView marker
//            hoveringMarker.setVisibility(View.INVISIBLE);
            // Show the SymbolLayer icon to represent the selected map location
            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                if (source != null) {
                    source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                }
                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                if (droppedMarkerLayer != null) {
                    droppedMarkerLayer.setProperties(visibility(VISIBLE));
                }
            }
            // Use the map camera target's coordinates to make a reverse geocoding search
            saveWaterPoint(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
        }else{
            Toast.makeText(activity, "Error saving !", Toast.LENGTH_SHORT).show();
            hideProgress();
        }
    }

    private void showProgress(){
        progressIndicator.show();
        progressIndicator.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.GONE);
    }

    private void hideProgress(){
        progressIndicator.hide();
        progressIndicator.setVisibility(View.GONE);
        buttonSave.setVisibility(View.VISIBLE);
    }

    private void saveWaterPoint(Point point) {
        WaterPointModel waterPointModel = new WaterPointModel();
        waterPointModel.setLongitude((float) point.longitude());
        waterPointModel.setLatitude((float) point.latitude());
        waterPointModel.setSenderMail(FirebaseManager.getInstance().getCurrentAuthUser().getEmail());
        waterPointViewModel.saveWaterPoint(waterPointModel).observe(activity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                hideProgress();
                if (integer >= 1){
                    Toast.makeText(requireContext(), "Water point saved !", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(requireContext(), "Error when saving !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void backToPreviousPage() {
        if (activity != null)
            activity.getSupportFragmentManager().popBackStack();
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView.onSaveInstanceState(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull @NotNull MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull @NotNull Style style) {
                        mapboxMap.getUiSettings().setCompassEnabled(false);
                        mapboxMap.getUiSettings().setLogoEnabled(false);
                        mapboxMap.getUiSettings().setAttributionEnabled(false);

                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);

                        enableLocationComponent(mapboxMap, style);
                        getMyLocation(mapboxMap);
                        setupViewMarker();
                        initDropMarker();
                        checkInteractions(style);
                        getWaterPoints();
                    }
                });
            }
        });
    }

    private void getWaterPoints(){
        waterPointViewModel.getWaterPointsQuerySnapshot().observe(activity, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                waterPoints.clear();
                String tempTotalWaterPoints = queryDocumentSnapshots.getDocuments().size()+ "";
                textTotalWaterPoints.setText(tempTotalWaterPoints);
                ArrayList<SymbolOptions> allSymbolOptions = new ArrayList<>();
                for (DocumentSnapshot document: queryDocumentSnapshots) {
                    WaterPointModel waterPointModel = document.toObject(WaterPointModel.class);
                    if (waterPointModel != null) {
                        waterPoints.add(waterPointModel);
                        SymbolOptions symbolOptions = createWaterPointSymbolOptions(
                                waterPointModel.getLatitude(),
                                waterPointModel.getLongitude(),
                                waterPointModel.getId());
                        allSymbolOptions.add(symbolOptions);
                    }
                }
                symbolsWaterPoints = symbolManager.create(allSymbolOptions);
            }
        });
    }

    private SymbolOptions createWaterPointSymbolOptions(float latitude, float longitude, long id) {
        if (symbolManager == null) {
            return null;
        }
        int optionColor;
        String textColor = "#" + Integer.toHexString(activity.getResources().getColor(R.color.light_500));
        optionColor = activity.getResources().getColor(R.color.dark_100);

        // create a symbol
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(WATER_ICON)
                .withIconSize(1.3f)
                .withSymbolSortKey(10.0f)
                .withIconColor(ColorUtils.colorToRgbaString(optionColor))
                .withTextColor(textColor)
                .withTextField("W"+ id)
                .withTextAnchor(new String(TEXT_ANCHOR_TOP))
                .withTextJustify(TEXT_JUSTIFY_AUTO)
                .withTextRadialOffset(.5f)
                .withIconAnchor(ICON_ANCHOR_BOTTOM_LEFT)
                .withIconOffset(new Float[] {-10.0f, 35.0f})
                .withDraggable(false);
    }

    private void setupViewMarker() {
        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
        // This is done by using an image view with the default marker found in the SDK. You can
        // swap out for your own marker image, just make sure it matches up with the dropped marker.
        hoveringMarker = new ImageView(requireContext());
        hoveringMarker.setImageResource(R.drawable.map_marker_dark);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        hoveringMarker.setLayoutParams(params);
        mapView.addView(hoveringMarker);
    }

    private void initDropMarker() {
        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
        // This is done by using an image view with the default marker found in the SDK. You can
        // swap out for your own marker image, just make sure it matches up with the dropped marker.
        ImageView hoveringMarker = new ImageView(requireContext());
        hoveringMarker.setImageResource(R.drawable.map_marker_dark);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        hoveringMarker.setLayoutParams(params);
        mapView.addView(hoveringMarker);
    }

    private void checkInteractions(@NotNull Style style) {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                getWaterPointOnMap(style);
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonSave.getVisibility() == View.VISIBLE) {
                    backToPreviousPage();
                }else{
                    Toast.makeText(activity, "Please wait after the update !", Toast.LENGTH_SHORT).show();
                }
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

        locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(locationComponentActivationOptions);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "None permissions detected !", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), "None location find !", Toast.LENGTH_SHORT).show();
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
        buttonSave = view.findViewById(R.id.button_save_water_point);
        progressIndicator = view.findViewById(R.id.progress_add_water_point);
        buttonBack = view.findViewById(R.id.button_back_profile);
        textTotalWaterPoints = view.findViewById(R.id.text_total_water_points);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param point The location to use for the search
     */
    private void reverseGeocode(final Point point) {
        String tempMessage = "Latitude : " +point.latitude() + " Longitude : " + point.longitude();
        Toast.makeText(requireContext(), tempMessage, Toast.LENGTH_SHORT).show();
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();
            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);
                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        Toast.makeText(requireContext(), "Place name : " +feature.placeName(), Toast.LENGTH_SHORT).show();
                                        saveWaterPoint(point);
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(requireContext(), "No result !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }
}