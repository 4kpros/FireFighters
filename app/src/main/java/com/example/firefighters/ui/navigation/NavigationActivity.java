package com.example.firefighters.ui.navigation;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;

import com.example.firefighters.R;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.navigation.base.internal.route.RouteUrl;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.ui.NavigationViewOptions;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity {

    Point originPoint;
    Point destinationPoint;
    private NavigationOptions navigationOptions ;
    private MapboxNavigation mapboxNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        navigationOptions = new NavigationOptions.Builder(this).
                accessToken(getString(R.string.mapbox_access_token)).
                build();

        //Get points list
        originPoint = Point.fromLngLat(0, 0);
        destinationPoint = Point.fromLngLat(1, 1);
        List<Point> pointList = new ArrayList<>();
        pointList.add(originPoint);
        pointList.add(destinationPoint);

        //Setup navigation view
        mapboxNavigation.requestRoutes(
                RouteOptions.builder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .coordinates(pointList)
                        .profile(RouteUrl.PROFILE_DRIVING)
                        .alternatives(true)
                        .bannerInstructions(true)
                        .steps(true)
                        .build()
        );
    }
}