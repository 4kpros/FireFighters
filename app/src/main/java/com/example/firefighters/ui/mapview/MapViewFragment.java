package com.example.firefighters.ui.mapview;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.UserModel;
import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.viewmodels.EmergencyViewModel;
import com.example.firefighters.viewmodels.UserViewModel;
import com.example.firefighters.viewmodels.WaterPointViewModel;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import com.mapbox.navigation.ui.route.NavigationMapRoute;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_RIGHT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textVariableAnchor;

public class MapViewFragment extends Fragment {

    MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton floatingButtonPosition;

    EmergencyViewModel emergencyViewModel;
    WaterPointViewModel waterPointViewModel;

    ArrayList<WaterPointModel> waterPoints;
    ArrayList<EmergencyModel> emergencies;

    public static final String WATER_ICON = "water-15";
    public static final String FIRE_ICON = "fire-station-15";

    public static final String ID_ICON = "id-icon";
    private SymbolManager symbolManager;

    private MaterialButton buttonStartNavigation;
    private ImageView imageButtonBestWaterSource;

    private MaterialButtonToggleGroup toggleButton;
    private List<Symbol> symbolsEmergencies;
    private List<Symbol> symbolsWaterPoints;

    private double latitudeDest;
    private double longitudeDest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        waterPoints = new ArrayList<>();
        emergencies = new ArrayList<>();
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
        waterPointViewModel.init();
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
        imageButtonBestWaterSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBestWaterSources();
            }
        });
        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked){
                    if (checkedId == R.id.button_emergency_points) {
                        getEmergenciesPoints();
                    } else if (checkedId == R.id.button_water_points) {
                        getWaterPoints();
                    }
                }else {
                    if (checkedId == R.id.button_emergency_points) {
                        symbolManager.delete(symbolsEmergencies);
                    } else if (checkedId == R.id.button_water_points) {
                        symbolManager.delete(symbolsWaterPoints);
                    }
                }
            }
        });
    }

    private void dialogBestWaterSources() {
        Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_best_water_sources);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        dialog.findViewById(R.id.button_close_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.button_water_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getBestDefaultWaterRoute();
            }
        });
        dialog.findViewById(R.id.button_water_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getBestAHPWaterRoute();
            }
        });
        dialog.show();
    }

    private void getBestAHPWaterRoute() {
        Toast.makeText(requireContext(), "Not implemented !", Toast.LENGTH_SHORT).show();
    }

    private void getBestDefaultWaterRoute() {
        waterPointViewModel.getAllWaterPointsQuery().observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocumentChanges().size() > 0){
                    //
                }
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
            public void onMapReady(@NonNull @NotNull MapboxMap map) {
                mapboxMap = map;
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull @NotNull Style style) {
                        mapboxMap.getUiSettings().setCompassEnabled(false);
                        mapboxMap.getUiSettings().setLogoEnabled(false);
                        mapboxMap.getUiSettings().setAttributionEnabled(false);

                        enableLocationComponent(mapboxMap, style);
                        getMyLocation(mapboxMap);
                        enableRotatingClick(mapboxMap);
                        checkInteractions(mapboxMap);
                        //Setup symbol manager
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);
                        getEventsListener();
                    }
                });
            }
        });
    }

    private void getEventsListener() {
        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public boolean onAnnotationClick(Symbol symbol) {
                zoomCameraToPosition(mapboxMap, symbol.getLatLng().getLatitude(), symbol.getLatLng().getLongitude());
                latitudeDest = symbol.getLatLng().getLatitude();
                longitudeDest = symbol.getLatLng().getLongitude();
                showDetailsEmergencyPoint(symbol);
                return false;
            }
        });
    }

    private void showDetailsEmergencyPoint(Symbol symbol) {
        Toast.makeText(requireContext(), symbol.getTextField()+"", Toast.LENGTH_SHORT).show();
    }
    private void showDetailsWaterPoint(Symbol symbol) {
        Toast.makeText(requireContext(), symbol.getTextField()+"", Toast.LENGTH_SHORT).show();
    }

    private void getEmergenciesPoints(){
        emergencyViewModel.getEmergenciesQuerySnapshot(null, null).observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                emergencies.clear();
                ArrayList<SymbolOptions> allSymbolOptions = new ArrayList<>();
                for (DocumentSnapshot document: queryDocumentSnapshots) {
                    EmergencyModel emergencyModel = document.toObject(EmergencyModel.class);
                    emergencies.add(emergencyModel);
                    SymbolOptions symbolOptions = createEmergencySymbolOptions(
                            emergencyModel.getLatitude(),
                            emergencyModel.getLongitude(),
                            emergencyModel.getId(),
                            emergencyModel.getStatus());
                    allSymbolOptions.add(symbolOptions);
                }
                symbolsEmergencies = symbolManager.create(allSymbolOptions);
            }
        });
    }

    private SymbolOptions createEmergencySymbolOptions(double latitude, double longitude, long id, String status) {
        if (symbolManager == null) {
            return null;
        }
        int optionColor;
        int textColor = getResources().getColor(R.color.dark);
        int redColor = getResources().getColor(R.color.red_200);
        int greenColor = getResources().getColor(R.color.green_500);
        int blueColor = getResources().getColor(R.color.blue_200);
        if (status.equals(ConstantsValues.WORKING)) {
            optionColor = greenColor;
        }else if (status.equals(ConstantsValues.FINISHED)){
            optionColor = blueColor;
        }else {
            optionColor = redColor;
        }

        // create a symbol
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(FIRE_ICON)
                .withIconSize(1.3f)
                .withSymbolSortKey(10.0f)
                .withIconColor(ColorUtils.colorToRgbaString(optionColor))
                .withTextColor(ColorUtils.colorToRgbaString(textColor))
                .withTextField("EM"+ id)
                .withTextAnchor(new String(TEXT_ANCHOR_TOP))
                .withTextJustify(TEXT_JUSTIFY_AUTO)
                .withTextSize(12f)
                .withTextRadialOffset(.5f)
                .withIconAnchor(ICON_ANCHOR_BOTTOM_LEFT)
                .withIconOffset(new Float[] {-10.0f, 35.0f})
                .withDraggable(false);
    }

    private void getWaterPoints(){
        waterPointViewModel.getWaterPointsQuerySnapshot().observe(requireActivity(), new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                waterPoints.clear();
                ArrayList<SymbolOptions> allSymbolOptions = new ArrayList<>();
                for (DocumentSnapshot document: queryDocumentSnapshots) {
                    WaterPointModel waterPointModel = document.toObject(WaterPointModel.class);
                    waterPoints.add(waterPointModel);
                    SymbolOptions symbolOptions = createWaterPointSymbolOptions(
                            waterPointModel.getLatitude(),
                            waterPointModel.getLongitude(),
                            waterPointModel.getId());
                    allSymbolOptions.add(symbolOptions);
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
        String textColor = "#" + Integer.toHexString(getResources().getColor(R.color.light_500));
        optionColor = getResources().getColor(R.color.dark_100);

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
                return false;
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

    private void getRoute(Point origin, Point destination) {
        //
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.map_view);
        floatingButtonPosition = view.findViewById(R.id.floating_button_my_position);
        buttonStartNavigation = view.findViewById(R.id.button_start_navigation);
        imageButtonBestWaterSource = view.findViewById(R.id.button_image_add_water_point);

        toggleButton = view.findViewById(R.id.toggle_button);
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
}
