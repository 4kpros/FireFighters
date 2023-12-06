package com.example.firefighters.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firefighters.R
import com.example.firefighters.databinding.FragmentAddWaterPointBinding
import com.example.firefighters.models.WaterPointModel
import com.example.firefighters.utils.FirebaseUtils
import com.example.firefighters.utils.InjectorUtils
import com.example.firefighters.viewmodels.models.WaterPointViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.DocumentSnapshot
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.exceptions.ServicesException
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.ColorUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddWaterPointFragment : Fragment() {

    private lateinit var mDataBinding: FragmentAddWaterPointBinding

    //View models
    private val mWaterPointViewModel by viewModels<WaterPointViewModel> {
        InjectorUtils.provideWaterPointViewModel()
    }

    private var mapboxMap: MapboxMap? = null

    private var locationComponent: LocationComponent? = null
    private var symbolManager: SymbolManager? = null
    private var symbolsWaterPoints: List<Symbol>? = null
    private var hoveringMarker: ImageView? = null
    private var droppedMarkerLayer: Layer? = null
    private var waterPoints: ArrayList<Any>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Set content with data biding util
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_water_point, container, false)
        val view = mDataBinding.root

        setupMapView(savedInstanceState)

        return view
    }

    private fun getWaterPointOnMap(style: Style) {
        if (hoveringMarker?.visibility == View.VISIBLE) {
            // Use the map target's coordinates to make a reverse geocoding search
            val mapTargetLatLng: LatLng = mapboxMap?.getCameraPosition()?.target!!
            // Hide the hovering red hovering ImageView marker
//            hoveringMarker.setVisibility(View.INVISIBLE);
            // Show the SymbolLayer icon to represent the selected map location
            if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                val source: GeoJsonSource? =
                    style.getSourceAs<GeoJsonSource>("dropped-marker-source-id")
                if (source != null) {
                    source.setGeoJson(
                        Point.fromLngLat(
                            mapTargetLatLng.longitude,
                            mapTargetLatLng.latitude
                        )
                    )
                }
                droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID)
                if (droppedMarkerLayer != null) {
                    droppedMarkerLayer!!.setProperties(PropertyFactory.visibility(Property.VISIBLE))
                }
            }
            // Use the map camera target's coordinates to make a reverse geocoding search
            saveWaterPoint(
                Point.fromLngLat(
                    mapTargetLatLng.longitude,
                    mapTargetLatLng.latitude
                )
            )
        } else {
            Toast.makeText(requireActivity(), "Error saving !", Toast.LENGTH_SHORT).show()
            hideProgress()
        }
    }

    private fun showProgress() {
        mDataBinding.progressAddWaterPoint.show()
        mDataBinding.progressAddWaterPoint.visibility = View.VISIBLE
        mDataBinding.buttonSaveWaterPoint.visibility = View.GONE
    }

    private fun hideProgress() {
        mDataBinding.progressAddWaterPoint.hide()
        mDataBinding.progressAddWaterPoint.visibility = View.GONE
        mDataBinding.buttonSaveWaterPoint.visibility = View.VISIBLE
    }

    private fun saveWaterPoint(point: Point) {
        val waterPointModel = WaterPointModel()
        waterPointModel.longitude = point.longitude().toFloat()
        waterPointModel.latitude = point.latitude().toFloat()
        waterPointModel.senderMail = FirebaseUtils.instance?.currentAuthUser?.email
        mWaterPointViewModel.saveWaterPoint(waterPointModel)
            .observe(requireActivity()) { value ->
                hideProgress()
                if ((value ?: 0) >= 1) {
                    Toast.makeText(requireContext(), "Water point saved !", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Error when saving !", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun backToPreviousPage() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        mDataBinding.mapView.onSaveInstanceState(savedInstanceState!!)
        mDataBinding.mapView.getMapAsync { map ->
            mapboxMap = map
            mapboxMap?.setStyle(Style.MAPBOX_STREETS
            ) { style ->
                mapboxMap?.uiSettings?.isCompassEnabled = false
                mapboxMap?.uiSettings?.isLogoEnabled = false
                mapboxMap?.uiSettings?.isAttributionEnabled = false
                symbolManager = SymbolManager(mDataBinding.mapView, mapboxMap!!, style)
                symbolManager?.iconAllowOverlap = true
                symbolManager?.textAllowOverlap = true
                enableLocationComponent(mapboxMap, style)
                getMyLocation(mapboxMap)
                setupViewMarker()
                initDropMarker()
                checkInteractions(style)
                getWaterPoints()
            }
        }
    }

    private fun getWaterPoints() {
        mWaterPointViewModel.waterPointsQuerySnapshot.observe(
            requireActivity()
        ) { value ->
            waterPoints!!.clear()
            val tempTotalWaterPoints: String =
                value?.documents?.size.toString() + ""
            mDataBinding.textTotalWaterPoints.text = tempTotalWaterPoints
            val allSymbolOptions: ArrayList<SymbolOptions> = ArrayList<SymbolOptions>()
            for (document: DocumentSnapshot in value ?: listOf()) {
                val waterPointModel: WaterPointModel? = document.toObject<WaterPointModel>(
                    WaterPointModel::class.java
                )
                if (waterPointModel != null) {
                    waterPoints!!.add(waterPointModel)
                    val symbolOptions: SymbolOptions? = createWaterPointSymbolOptions(
                        waterPointModel.latitude,
                        waterPointModel.longitude,
                        waterPointModel.id
                    )
                    if (symbolOptions != null) {
                        allSymbolOptions.add(symbolOptions)
                    }
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
        val optionColor = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requireActivity().getColor(R.color.dark_100)
        }else {
            requireActivity().resources.getColor(R.color.dark_100)
        }
        val textColorInt = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requireActivity().getColor(R.color.light_500)
        }else {
            requireActivity().resources.getColor(R.color.light_500)
        }
        val textColor: String =
            "#" + Integer.toHexString(textColorInt)

        // create a symbol
        return SymbolOptions()
            .withLatLng(LatLng(latitude.toDouble(), longitude.toDouble()))
            .withIconImage(WATER_ICON)
            .withIconSize(1.3f)
            .withSymbolSortKey(10.0f)
            .withIconColor(ColorUtils.colorToRgbaString(optionColor))
            .withTextColor(textColor)
            .withTextField("W$id")
            .withTextAnchor((java.lang.String(Property.TEXT_ANCHOR_TOP) as String))
            .withTextJustify(Property.TEXT_JUSTIFY_AUTO)
            .withTextRadialOffset(.5f)
            .withIconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT)
            .withIconOffset(arrayOf<Float>(-10.0f, 35.0f))
            .withDraggable(false)
    }

    private fun setupViewMarker() {
        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
        // This is done by using an image view with the default marker found in the SDK. You can
        // swap out for your own marker image, just make sure it matches up with the dropped marker.
        hoveringMarker = ImageView(requireContext())
        hoveringMarker!!.setImageResource(com.mapbox.services.android.navigation.ui.v5.R.drawable.map_marker_dark)
        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
        )
        hoveringMarker?.layoutParams = params
        mDataBinding.mapView.addView(hoveringMarker)
    }

    private fun initDropMarker() {
        // When user is still picking a location, we hover a marker above the mapboxMap in the center.
        // This is done by using an image view with the default marker found in the SDK. You can
        // swap out for your own marker image, just make sure it matches up with the dropped marker.
        val hoveringMarker = ImageView(requireContext())
        hoveringMarker.setImageResource(com.mapbox.services.android.navigation.ui.v5.R.drawable.map_marker_dark)
        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
        )
        hoveringMarker.layoutParams = params
        mDataBinding.mapView.addView(hoveringMarker)
    }

    private fun checkInteractions(style: Style) {
        mDataBinding.buttonSaveWaterPoint.setOnClickListener {
            showProgress()
            getWaterPointOnMap(style)
        }
        mDataBinding.buttonBackProfile.setOnClickListener {
            if (mDataBinding.buttonSaveWaterPoint.visibility == View.VISIBLE) {
                backToPreviousPage()
            } else {
                Toast.makeText(requireActivity(), "Please wait after the update !", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

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
            Toast.makeText(requireContext(), "None permissions detected !", Toast.LENGTH_SHORT)
                .show()
            return
        }
        locationComponent?.isLocationComponentEnabled = true
        locationComponent?.cameraMode = CameraMode.TRACKING_GPS
        locationComponent?.renderMode = RenderMode.GPS
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
                        zoomCameraToPosition(mapboxMap, latitude, longitude)
                    } else {
                        Toast.makeText(requireContext(), "None location find !", Toast.LENGTH_SHORT)
                            .show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        mDataBinding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mDataBinding.mapView.onLowMemory()
    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param point The location to use for the search
     */
    private fun reverseGeocode(point: Point) {
        val tempMessage: String =
            "Latitude : " + point.latitude() + " Longitude : " + point.longitude()
        Toast.makeText(requireContext(), tempMessage, Toast.LENGTH_SHORT).show()
        try {
            val client: MapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build()
            client.enqueueCall(object : Callback<GeocodingResponse?> {
                override fun onResponse(
                    call: Call<GeocodingResponse?>,
                    response: Response<GeocodingResponse?>
                ) {
                    if (response.body() != null) {
                        val results: List<CarmenFeature>? = response.body()?.features()
                        if (results.isNullOrEmpty()) {
                            val feature: CarmenFeature? = results?.get(0)
                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap?.getStyle { style ->
                                if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Place name : " + feature?.placeName(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    saveWaterPoint(point)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "No result !", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<GeocodingResponse?>,
                    throwable: Throwable
                ) {
                    throwable.printStackTrace()
                }
            })
        } catch (servicesException: ServicesException) {
            servicesException.printStackTrace()
        }
    }

    companion object {
        const val WATER_ICON: String = "water"
        private const val DROPPED_MARKER_LAYER_ID: String = "DROPPED_MARKER_LAYER_ID"
    }
}