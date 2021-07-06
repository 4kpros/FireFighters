package com.example.firefighters.ui.mapview;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firefighters.R;
import com.example.firefighters.models.EmergencyModel;
import com.example.firefighters.models.WaterPointModel;
import com.example.firefighters.tools.ConstantsValues;
import com.example.firefighters.viewmodels.EmergencyViewModel;
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
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
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
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM_LEFT;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.mapbox.mapboxsdk.style.layers.Property.TEXT_JUSTIFY_AUTO;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MapViewFragment extends Fragment {

    Context context;
    FragmentActivity activity;

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    MapView mapView;
    private MapboxMap mapboxMap;
    private FloatingActionButton floatingButtonPosition;

    EmergencyViewModel emergencyViewModel;
    WaterPointViewModel waterPointViewModel;

    ArrayList<WaterPointModel> waterPoints;
    ArrayList<EmergencyModel> emergencies;

    public static final String WATER_ICON = "fuel-15";
    public static final String FIRE_ICON = "fire-station-15";

    public static final String ID_ICON = "id-icon";
    private SymbolManager symbolManager;

    private MaterialButton buttonStartNavigation;
    private ImageView buttonBackHome;
    private ImageView imageButtonBestWaterSource;

    private MaterialButtonToggleGroup toggleButton;
    private List<Symbol> symbolsEmergencies;
    private List<Symbol> symbolsWaterPoints;

    private Point originPoint;
    private Point destinationPoint;
    private LocationComponent locationComponent;

    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private LinearLayout linearButtons;
    private TextView textDistance;
    private TextView textDistanceTitle;

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
        context = getContext();
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
        emergencyViewModel = new ViewModelProvider(activity).get(EmergencyViewModel.class);
        emergencyViewModel.init();

        waterPointViewModel = new ViewModelProvider(activity).get(WaterPointViewModel.class);
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
        buttonBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToPreviousPage();
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

    private void backToPreviousPage() {
        if (activity != null)
            activity.getSupportFragmentManager().popBackStack();
    }

    private void dialogBestWaterSources() {
        Dialog dialog = new Dialog(context);
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
        Toast.makeText(context, "Not implemented !", Toast.LENGTH_SHORT).show();
    }

    private void getBestDefaultWaterRoute() {
        waterPointViewModel.getAllWaterPointsQuery().observe(activity, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocumentChanges().size() > 0){
                    //
                }
            }
        });
    }

    private void startNavigation() {
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(currentRoute)
                .shouldSimulateRoute(true)
                .lightThemeResId(R.style.customInstructionView)
                .build();
        // Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(activity, options);
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
//                        enableRotatingClick(mapboxMap);
                        checkInteractions(mapboxMap);
//                        enableMapClicksToAddMarker(style);
                        //Setup symbol manager
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);
//
//// Set the origin location to the Alhambra landmark in Granada, Spain.
//                        Point origin = Point.fromLngLat(-3.588098, 37.176164);
//
//// Set the destination location to the Plaza del Triunfo in Granada, Spain.
//                        Point destination = Point.fromLngLat(-3.601845, 37.184080);
//                        getRoute(origin, destination);
                        getEventsListener();
                    }
                });
            }
        });
    }

//    private void enableMapClicksToAddMarker(@NotNull Style style) {
//        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
//            @Override
//            public boolean onMapClick(@NonNull LatLng point) {
//                if (locationComponent.getLastKnownLocation() != null){
//                    Point destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
//                    Point originPoint = Point.fromLngLat(
//                            locationComponent.getLastKnownLocation().getLongitude(),
//                            locationComponent.getLastKnownLocation().getLatitude());
//                    if (mapboxMap.getStyle() != null) {
//                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
//                        if (source != null) {
//                            source.setGeoJson(Feature.fromGeometry(destinationPoint));
//                            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }else{
//                    Toast.makeText(context, "No location provided !", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });
//    }
//
//    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
//        loadedMapStyle.addImage("destination-icon-id",
//                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
//        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
//        loadedMapStyle.addSource(geoJsonSource);
//        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
//        destinationSymbolLayer.withProperties(
//                iconImage("destination-icon-id"),
//                iconAllowOverlap(true),
//                iconIgnorePlacement(true)
//        );
//        loadedMapStyle.addLayer(destinationSymbolLayer);
//    }

    private void getEventsListener() {
        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                zoomCameraToPosition(mapboxMap, symbol.getLatLng().getLatitude(), symbol.getLatLng().getLongitude());
                destinationPoint = Point.fromLngLat(symbol.getLatLng().getLongitude(), symbol.getLatLng().getLatitude());
                getRoute(destinationPoint, originPoint);
            }
        });
    }

    private void getEmergenciesPoints(){
        emergencyViewModel.getEmergenciesQuerySnapshot(null, null).observe(activity, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                emergencies.clear();
                ArrayList<SymbolOptions> allSymbolOptions = new ArrayList<>();
                for (DocumentSnapshot document: queryDocumentSnapshots) {
                    EmergencyModel emergencyModel = document.toObject(EmergencyModel.class);
                    if (emergencyModel != null) {
                        emergencies.add(emergencyModel);
                        SymbolOptions symbolOptions = createEmergencySymbolOptions(
                                emergencyModel.getLatitude(),
                                emergencyModel.getLongitude(),
                                emergencyModel.getId(),
                                emergencyModel.getStatus());
                        allSymbolOptions.add(symbolOptions);
                    }
                }
                symbolsEmergencies = symbolManager.create(allSymbolOptions);
            }
        });
    }

    private SymbolOptions createEmergencySymbolOptions(double latitude, double longitude, long id, String status) {
        if (symbolManager == null) {
            return null;
        }
        int iconColor;
        int textColor = getIntFromColor(0, 255, 255);
        int redColor = getIntFromColor(255, 0, 0);
        int greenColor = getIntFromColor(0, 255, 0);
        int blueColor = getIntFromColor(0, 0, 255);
        if (status.equals(ConstantsValues.WORKING)) {
            iconColor = greenColor;
        }else if (status.equals(ConstantsValues.FINISHED)){
            iconColor = blueColor;
        }else {
            iconColor = redColor;
        }

        // create a symbol
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(FIRE_ICON)
                .withIconSize(1.3f)
                .withSymbolSortKey(10.0f)
                .withIconColor(ColorUtils.colorToRgbaString(iconColor))
                .withTextColor(ColorUtils.colorToRgbaString(iconColor))
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
        waterPointViewModel.getWaterPointsQuerySnapshot().observe(activity, new Observer<QuerySnapshot>() {
            @Override
            public void onChanged(QuerySnapshot queryDocumentSnapshots) {
                waterPoints.clear();
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
        int textColor = getIntFromColor(82, 42, 0);
        int iconColor = getIntFromColor(0, 255, 255);

        // create a symbol
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(WATER_ICON)
                .withIconSize(1.3f)
                .withSymbolSortKey(10.0f)
                .withIconColor(ColorUtils.colorToRgbaString(iconColor))
                .withTextColor(ColorUtils.colorToRgbaString(textColor))
                .withTextField("Water"+ id)
                .withTextAnchor(new String(TEXT_ANCHOR_TOP))
                .withTextJustify(TEXT_JUSTIFY_AUTO)
                .withTextSize(12f)
                .withTextRadialOffset(.5f)
                .withIconAnchor(ICON_ANCHOR_BOTTOM_LEFT)
                .withIconOffset(new Float[] {-10.0f, 35.0f})
                .withDraggable(false);
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
//
//    private void enableRotatingClick(MapboxMap mapboxMap) {
//        if (mapboxMap == null)
//            return;
//        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
//            @Override
//            public boolean onMapClick(@NonNull @NotNull LatLng point) {
//                CameraPosition position = new CameraPosition.Builder()
//                        .target(new LatLng(point.getLatitude(), point.getLongitude())) // Sets the new camera position
//                        .zoom(17) // Sets the zoom
//                        .bearing(0) // Rotate the camera
//                        .tilt(30) // Set the camera tilt
//                        .build(); // Creates a CameraPosition from the builder
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
//                return false;
//            }
//        });
//    }

    private void enableLocationComponent(MapboxMap mapboxMap, @NonNull Style loadedMapStyle) {
        LocationComponentOptions locationComponentOptions =
                LocationComponentOptions.builder(context)
                        .pulseEnabled(true)
                        .pulseFadeEnabled(true)
                        .build();

        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(context, loadedMapStyle)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .build();

        locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(locationComponentActivationOptions);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "None permissions detected !", Toast.LENGTH_SHORT).show();
            return;
        }
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
        locationComponent.setRenderMode(RenderMode.GPS);
    }

    private void getMyLocation(MapboxMap mapboxMap) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context)
                                .removeLocationUpdates(this);
                        if (locationResult.getLocations().size() > 0) {
                            double latitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLatitude();
                            double longitude = locationResult.getLocations().get(locationResult.getLocations().size() - 1).getLongitude();
                            originPoint = Point.fromLngLat(longitude, latitude);
                            zoomCameraToPosition(mapboxMap, latitude, longitude);
                        } else {
                            Toast.makeText(context, "None location found !", Toast.LENGTH_SHORT).show();
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

    private void getRoute2(Point origin, Point destination){
        MapboxDirections client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }
                currentRoute = response.body().routes().get(0);
                Toast.makeText(activity, "distance : " +currentRoute.distance(), Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                                // Retrieve and update the source designated for showing the directions route
                                GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                                // Create a LineString with the directions route's geometry and
                                // reset the GeoJSON source for the route LineLayer source
                                if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(Objects.requireNonNull(currentRoute.geometry()), PRECISION_6));
                            }
                        }
                    });
                }else{
                    Toast.makeText(context, "Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(activity, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(context)
                .accessToken(activity.getResources().getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Timber.d("Response code: " + response.code());
                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Timber.e("No routes found");
                            return;
                        }
                        currentRoute = response.body().routes().get(0);
                        double tempDistance = 0;
                        if (currentRoute.distance() != null)
                            tempDistance = currentRoute.distance();
                        showNavigationView(tempDistance);
                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.updateRouteVisibilityTo(false);
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Timber.e("Error: " + throwable.getMessage());
                    }
                });
    }

    private void showNavigationView(double distance) {
        linearButtons.setVisibility(View.VISIBLE);
        String tempDistance = "Distance : " +distance/1000;
        textDistance.setText(tempDistance);
        textDistanceTitle.setText(" km");
    }

    private void hideNavigationView() {
        linearButtons.setVisibility(View.GONE);
        textDistance.setText("");
        textDistanceTitle.setText("");
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.map_view);
        floatingButtonPosition = view.findViewById(R.id.floating_button_my_position);
        buttonStartNavigation = view.findViewById(R.id.button_start_navigation);
        buttonBackHome = view.findViewById(R.id.button_back_mapview);
        imageButtonBestWaterSource = view.findViewById(R.id.button_image_add_water_point);

        toggleButton = view.findViewById(R.id.toggle_button);

        linearButtons = view.findViewById(R.id.linear_buttons);
        textDistance = view.findViewById(R.id.text_distance);
        textDistanceTitle = view.findViewById(R.id.text_distance_title);
    }

}
