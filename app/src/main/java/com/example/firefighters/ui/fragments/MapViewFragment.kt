package com.example.firefighters.ui.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.firefighters.R
import com.example.firefighters.databinding.FragmentMapViewBinding
import com.example.firefighters.models.EmergencyModel
import com.example.firefighters.models.WaterPointModel
import com.example.firefighters.utils.ConstantsValues
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.models.EmergencyViewModel
import com.example.firefighters.viewmodels.models.UnitViewModel
import com.example.firefighters.viewmodels.models.UserViewModel
import com.example.firefighters.viewmodels.models.WaterPointViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Objects

class MapViewFragment : Fragment() {

    private lateinit var mDataBinding: FragmentMapViewBinding

    //View models
    private val mUserViewModel by viewModels<UserViewModel> {
        InjectorUtils.provideUserViewModel()
    }
    private val mEmergencyViewModel by viewModels<EmergencyViewModel> {
        InjectorUtils.provideEmergencyViewModel()
    }
    private val mWaterPointViewModel by viewModels<WaterPointViewModel> {
        InjectorUtils.provideWaterPointViewModel()
    }
    private val mUnitViewModel by viewModels<UnitViewModel> {
        InjectorUtils.provideUserViewModel()
    }

    // variables for calculating and drawing a route
    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var mapboxMap: MapboxMap? = null
    private var waterPoints: ArrayList<WaterPointModel>? = null
    private var emergencies: ArrayList<EmergencyModel>? = null
    private var symbolManager: SymbolManager? = null
    private var symbolsEmergencies: List<Symbol>? = null
    private var symbolsWaterPoints: List<Symbol>? = null
    private var originPoint: Point? = null
    private var destinationPoint: Point? = null
    private var locationComponent: LocationComponent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_map_view, container, false)
        val view = mDataBinding.root

        //Load your UI content
        setupMapView(savedInstanceState)

        return view
    }

    private fun checkInteractions(mapboxMap: MapboxMap?) {
        mDataBinding.floatingButtonMyPosition.setOnClickListener { getMyLocation(mapboxMap) }
        mDataBinding.buttonStartNavigation.setOnClickListener { startNavigation() }
        mDataBinding.buttonBackMapview.setOnClickListener { backToPreviousPage() }
        mDataBinding.buttonWaterPoints.setOnClickListener { dialogBestWaterSources() }
        mDataBinding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    if (checkedId == R.id.button_emergency_points) {
                        emergenciesPoints
                    } else if (checkedId == R.id.button_water_points) {
                        getWaterPoints()
                    }
                } else {
                    if (checkedId == R.id.button_emergency_points) {
                        symbolManager?.delete(symbolsEmergencies)
                    } else if (checkedId == R.id.button_water_points) {
                        symbolManager?.delete(symbolsWaterPoints)
                    }
                }
            }
    }

    private fun backToPreviousPage() {
        if (activity != null) requireActivity().getSupportFragmentManager().popBackStack()
    }

    private fun dialogBestWaterSources() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_best_water_sources)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(), R.color.transparent))
        dialog.findViewById<View>(R.id.button_close_dialog)
            .setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(R.id.button_water_default)
            .setOnClickListener {
                dialog.dismiss()
                bestDefaultWaterRoute
            }
        dialog.findViewById<View>(R.id.button_water_default)
            .setOnClickListener {
                dialog.dismiss()
                bestAHPWaterRoute
            }
        dialog.show()
    }

    private val bestAHPWaterRoute: Unit
        get() {
            Toast.makeText(context, "Not implemented !", Toast.LENGTH_SHORT).show()
        }
    private val bestDefaultWaterRoute: Unit
        get() {
            mWaterPointViewModel.allWaterPointsQuery.observe(
                requireActivity(),
                Observer<QuerySnapshot?> {
                    if ((it?.documentChanges?.size ?: 0) > 0) {
                        //
                    }
                })
        }

    private fun startNavigation() {
        val options: NavigationLauncherOptions = NavigationLauncherOptions.builder()
            .directionsRoute(currentRoute)
            .shouldSimulateRoute(true)
            .lightThemeResId(R.style.customInstructionView)
            .build()
        // Call this method with Context from within an Activity
        requireActivity().runOnUiThread { NavigationLauncher.startNavigation(activity, options) }
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        mDataBinding.mapView.onSaveInstanceState(savedInstanceState!!)
        mDataBinding.mapView.getMapAsync { map ->
            mapboxMap = map
            mapboxMap?.setStyle(Style.MAPBOX_STREETS) { style ->
                mapboxMap?.uiSettings?.isCompassEnabled = false
                mapboxMap?.uiSettings?.isLogoEnabled = false
                mapboxMap?.uiSettings?.isAttributionEnabled = false
                enableLocationComponent(mapboxMap, style)
                getMyLocation(mapboxMap)
                checkInteractions(mapboxMap)
                symbolManager = SymbolManager(mDataBinding.mapView, mapboxMap!!, style)
                symbolManager?.iconAllowOverlap = true
                symbolManager?.textAllowOverlap = true
                eventsListener
            }
        }
    }

    private val eventsListener: Unit
        //    private void enableMapClicksToAddMarker(@NotNull Style style) {
        get() {
            symbolManager?.addClickListener(OnSymbolClickListener { symbol ->
                zoomCameraToPosition(
                    mapboxMap,
                    symbol.latLng.latitude,
                    symbol.latLng.longitude
                )
                destinationPoint = Point.fromLngLat(
                    symbol.latLng.longitude,
                    symbol.latLng.latitude
                )
                getRoute(destinationPoint, originPoint)
            })
        }
    private val emergenciesPoints: Unit
        get() {
            mEmergencyViewModel.getEmergenciesQuerySnapshot(null, null)
                .observe(requireActivity()) {
                    emergencies?.clear()
                    val allSymbolOptions: ArrayList<SymbolOptions> = ArrayList<SymbolOptions>()
                    for (document: DocumentSnapshot in it ?: listOf()) {
                        val emergencyModel: EmergencyModel? = document.toObject<EmergencyModel>(
                            EmergencyModel::class.java
                        )
                        if (emergencyModel != null) {
                            emergencies!!.add(emergencyModel)
                            val symbolOptions: SymbolOptions? = createEmergencySymbolOptions(
                                emergencyModel.latitude,
                                emergencyModel.longitude,
                                emergencyModel.id,
                                emergencyModel.status
                            )
                            allSymbolOptions.add(symbolOptions!!)
                        }
                    }
                    symbolsEmergencies = symbolManager?.create(allSymbolOptions)
                }
        }

    private fun createEmergencySymbolOptions(
        latitude: Double,
        longitude: Double,
        id: Long,
        status: String
    ): SymbolOptions? {
        if (symbolManager == null) {
            return null
        }
        val iconColor: Int
        //val textColor: Int = getIntFromColor(0, 255, 255)
        val redColor: Int = getIntFromColor(255, 0, 0)
        val greenColor: Int = getIntFromColor(0, 255, 0)
        val blueColor: Int = getIntFromColor(0, 0, 255)
        iconColor = if ((status == ConstantsValues.WORKING)) {
            greenColor
        } else if ((status == ConstantsValues.FINISHED)) {
            blueColor
        } else {
            redColor
        }

        // create a symbol
        return SymbolOptions()
            .withLatLng(LatLng(latitude, longitude))
            .withIconImage(FIRE_ICON)
            .withIconSize(1.3f)
            .withSymbolSortKey(10.0f)
            .withIconColor(ColorUtils.colorToRgbaString(iconColor))
            .withTextColor(ColorUtils.colorToRgbaString(iconColor))
            .withTextField("EM$id")
            .withTextAnchor((java.lang.String(Property.TEXT_ANCHOR_TOP) as String))
            .withTextJustify(Property.TEXT_JUSTIFY_AUTO)
            .withTextSize(12f)
            .withTextRadialOffset(.5f)
            .withIconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT)
            .withIconOffset(arrayOf<Float>(-10.0f, 35.0f))
            .withDraggable(false)
    }

    private fun getWaterPoints() {
        mWaterPointViewModel.waterPointsQuerySnapshot.observe(
            requireActivity()
        ) { value ->
            waterPoints!!.clear()
            val allSymbolOptions: ArrayList<SymbolOptions> = ArrayList<SymbolOptions>()
            for (document: DocumentSnapshot in value ?: listOf()) {
                val waterPointModel: WaterPointModel? = document.toObject<WaterPointModel>(
                    WaterPointModel::class.java
                )
                if (waterPointModel != null) {
                    waterPoints?.add(waterPointModel)
                    val symbolOptions: SymbolOptions? = createWaterPointSymbolOptions(
                        waterPointModel.latitude,
                        waterPointModel.longitude,
                        waterPointModel.id
                    )
                    allSymbolOptions.add(symbolOptions!!)
                }
            }
            symbolsWaterPoints = symbolManager?.create(allSymbolOptions)
        }
    }

    private fun createWaterPointSymbolOptions(
        latitude: Float,
        longitude: Float,
        id: Long
    ): SymbolOptions? {
        if (symbolManager == null) {
            return null
        }
        val textColor: Int = getIntFromColor(82, 42, 0)
        val iconColor: Int = getIntFromColor(0, 255, 255)

        // create a symbol
        return SymbolOptions()
            .withLatLng(LatLng(latitude.toDouble(), longitude.toDouble()))
            .withIconImage(WATER_ICON)
            .withIconSize(1.3f)
            .withSymbolSortKey(10.0f)
            .withIconColor(ColorUtils.colorToRgbaString(iconColor))
            .withTextColor(ColorUtils.colorToRgbaString(textColor))
            .withTextField("Water$id")
            .withTextAnchor((java.lang.String(Property.TEXT_ANCHOR_TOP) as String))
            .withTextJustify(Property.TEXT_JUSTIFY_AUTO)
            .withTextSize(12f)
            .withTextRadialOffset(.5f)
            .withIconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT)
            .withIconOffset(arrayOf<Float>(-10.0f, 35.0f))
            .withDraggable(false)
    }

    private fun getIntFromColor(Red: Int, Green: Int, Blue: Int): Int {
        val red = (Red shl 16) and 0x00FF0000 //Shift red 16-bits and mask out other stuff
        val green = (Green shl 8) and 0x0000FF00 //Shift Green 8-bits and mask out other stuff
        val blue = Blue and 0x000000FF //Mask out anything not blue.
        return -0x1000000 or red or green or blue //0xFF000000 for 100% Alpha. Bitwise OR everything together.
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
    private fun enableLocationComponent(mapboxMap: MapboxMap?, loadedMapStyle: Style) {
        val locationComponentOptions: LocationComponentOptions =
            LocationComponentOptions.builder(requireContext())
                .pulseEnabled(true)
                .pulseFadeEnabled(true)
                .build()
        val locationComponentActivationOptions: LocationComponentActivationOptions =
            LocationComponentActivationOptions
                .builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .build()
        locationComponent = mapboxMap?.locationComponent
        locationComponent?.activateLocationComponent(locationComponentActivationOptions)
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            Toast.makeText(context, "None permissions detected !", Toast.LENGTH_SHORT).show()
            return
        }
        locationComponent?.setLocationComponentEnabled(true)
        locationComponent?.setCameraMode(CameraMode.TRACKING_GPS)
        locationComponent?.setRenderMode(RenderMode.GPS)
    }

    private fun getMyLocation(mapboxMap: MapboxMap?) {
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            return
        }
        val locationRequest: LocationRequest = LocationRequest.Builder(3000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(10000)
            .build()
        LocationServices.getFusedLocationProviderClient(requireContext())
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .removeLocationUpdates(this)
                    if (locationResult.locations.size > 0) {
                        val latitude: Double = locationResult.locations[locationResult.locations.size - 1].latitude
                        val longitude: Double = locationResult.locations[locationResult.locations.size - 1].longitude
                        originPoint = Point.fromLngLat(longitude, latitude)
                        zoomCameraToPosition(mapboxMap, latitude, longitude)
                    } else {
                        Toast.makeText(context, "None location found !", Toast.LENGTH_SHORT).show()
                    }
                }
            }, Looper.getMainLooper())
    }

    private fun zoomCameraToPosition(mapboxMap: MapboxMap?, latitude: Double, longitude: Double) {
        val position: CameraPosition = CameraPosition.Builder()
            .target(LatLng(latitude, longitude)) // Sets the new camera position
            .zoom(17.0) // Sets the zoom
            .bearing(0.0) // Rotate the camera
            .tilt(30.0) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder
        mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000)
    }

    private fun getRoute2(origin: Point, destination: Point) {
        val client: MapboxDirections = MapboxDirections.builder()
            .origin(origin)
            .destination(destination)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(getString(R.string.mapbox_access_token))
            .build()
        client.enqueueCall(object : Callback<DirectionsResponse?> {
            override fun onResponse(
                call: Call<DirectionsResponse?>,
                response: Response<DirectionsResponse?>
            ) {
                if (response.body() == null) {
                    return
                } else if ((response.body()?.routes()?.size ?: 0) < 1) {
                    return
                }
                currentRoute = response.body()?.routes()?.get(0)
                Toast.makeText(
                    activity,
                    "distance : " + currentRoute?.distance(),
                    Toast.LENGTH_SHORT
                ).show()
                if (mapboxMap != null) {
                    mapboxMap?.getStyle { style -> // Retrieve and update the source designated for showing the directions route

                        // Create a LineString with the directions route's geometry and
                        // reset the GeoJSON source for the route LineLayer source
                        style.getSourceAs<GeoJsonSource>(
                            ROUTE_SOURCE_ID
                        )?.setGeoJson(
                            LineString.fromPolyline(
                                Objects.requireNonNull<String>(
                                    currentRoute?.geometry()
                                ), Constants.PRECISION_6
                            )
                        )
                    }
                } else {
                    Toast.makeText(context, "Error !", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                //
            }
        })
    }

    private fun getRoute(origin: Point?, destination: Point?) {
        NavigationRoute.builder(context)
            .accessToken(requireActivity().resources.getString(R.string.mapbox_access_token))
            .origin(origin!!)
            .destination(destination!!)
            .build()
            .getRoute(object : Callback<DirectionsResponse?> {
                override fun onResponse(
                    call: Call<DirectionsResponse?>,
                    response: Response<DirectionsResponse?>
                ) {
                    if (response.body() == null) {
                        return
                    } else if ((response.body()?.routes()?.size ?: 0) < 1) {
                        return
                    }
                    currentRoute = response.body()?.routes()?.get(0)
                    val tempDistance = currentRoute?.distance() ?: 0f
                    showNavigationView(tempDistance.toDouble())
                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute?.updateRouteVisibilityTo(false)
                    } else {
                        navigationMapRoute =
                            NavigationMapRoute(null, mDataBinding.mapView, mapboxMap!!, com.mapbox.services.android.navigation.ui.v5.R.style.NavigationMapRoute)
                    }
                    navigationMapRoute?.addRoute(currentRoute)
                }

                override fun onFailure(
                    call: Call<DirectionsResponse?>,
                    throwable: Throwable
                ) {
                    throwable.printStackTrace()
                }
            })
    }

    private fun showNavigationView(distance: Double) {
        mDataBinding.linearButtons.visibility = View.VISIBLE
        val tempDistance: String = "Distance : " + distance / 1000
        mDataBinding.textDistance.text = tempDistance
        mDataBinding.textDistanceTitle.text = " km"
    }

    private fun hideNavigationView() {
        mDataBinding.linearButtons.visibility = View.GONE
        mDataBinding.textDistance.text = ""
        mDataBinding.textDistanceTitle.text = ""
    }

    companion object {
        val WATER_ICON: String = "fuel-15"
        val FIRE_ICON: String = "fire-station-15"
        val ID_ICON: String = "id-icon"
        private val ROUTE_SOURCE_ID: String = "route-source-id"
    }
}
