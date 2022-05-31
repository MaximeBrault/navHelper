package com.adapei.navhelper.activity.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.adapei.navhelper.Checkpoint;
import com.adapei.navhelper.Constants;
import com.adapei.navhelper.Destination;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.CheckPointData;
import com.adapei.navhelper.listener.MyLocationEngineListener;
import com.adapei.navhelper.listener.MyPermissionsListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MapNavView extends NavView {

    private MapView mapView;

    // variables for adding location layer
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;

    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    // preferences containing parameters and destination
    private SharedPreferences sharedPreferences;

    // sharedPreferences variable
    private Destination destination;
    private String profile;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nav_view);

        Bundle extras = getIntent().getExtras();

        Mapbox.getInstance(this, getString(R.string.access_token));

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
        IconFactory iconFactory = IconFactory.getInstance(MapNavView.this);

        profile = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE).getString(getResources().getString(R.string.favoriteTypePreferencesName), DirectionsCriteria.PROFILE_WALKING);
        this.destination = Constants.GSON.fromJson(extras.getString(getResources().getString(R.string.DestinationHomeToNav)), Destination.class);

        originPosition = Point.fromLngLat(Constants.locationEngine.getLastLocation().getLongitude(), Constants.locationEngine.getLastLocation().getLatitude());
        destinationPosition = destination.toPoint();

        IconFactory iconFactory1 = IconFactory.getInstance(this);

        //When the map is ready, trigger the callback
        mapView.getMapAsync(mapboxMap -> {
            System.out.println("MAP EST READY");

            map = mapboxMap;
            this.enableLocationPlugin();

            // Add a marker on the destination and the origin point
            // A marker is a point like an arrow pointing the map on a specified location
            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(destinationPosition.latitude(), destinationPosition.longitude())));
            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(originPosition.latitude(), originPosition.longitude())));

            // Add a marker for each checkpoint
            for (Checkpoint checkpoint : new CheckPointData(this).getAllCheckpoints()) {
                //configuration of the marker
                MarkerOptions checkpointMarker = new MarkerOptions();
                checkpointMarker.setPosition(new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude()));
                checkpointMarker.setTitle(checkpoint.getDisplayName());

                // add it to the map
                mapboxMap.addMarker(checkpointMarker);
            }
        });
    }

    /**
     * Enable the location plugin for adding layers
     */
    private void enableLocationPlugin() {
        //the location plugin display the user's location on the map
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of a location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            // request the permission to use the location
            permissionsManager = new PermissionsManager(new MyPermissionsListener());
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Initialize the user location on the Mapbox map
     */
    private void initializeLocationEngine() {

        System.out.println("INITIALIZE LOCATION ENGINE 1");

        //the location engine gives us the user location
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);

        // Obtain the best possible location of the user and configuration of the location engine
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        locationEngine.setInterval(0);
        locationEngine.setFastestInterval(1000);

        com.adapei.navhelper.Location.checkLocationPermission(this);
        // get the last location of the user. May be null because it's stored in the cache of the phone.
        // When first initializing the location engine the location updates aren't immediate
        Location lastLocation = Constants.locationEngine.getLastLocation();

        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(originLocation);
        } else {
            locationEngine.addLocationEngineListener(new MyLocationEngineListener(locationEngine, this));
            locationEngine.activate();
        }

        System.out.println("INITIALIZE LOCATION ENGINE 2");
    }

    /**
     * Set the position of the user on the Mapbox map
     * @param location Location of the user
     */
    private void setCameraPosition(Location location) {

        System.out.println("SETCAMERAPOSITION(Location location)");

        map.moveCamera(mapboxMap -> new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                .zoom(13)
                .build());
    }

    /**
     * Set the route for the navigation
     * @param origin Starting point of the route
     * @param destination Ending point of the route
     */
    private void setRoute(Point origin, Point destination) {
        //create a route that we use to start the navigation + parameters
        NavigationRoute.builder(this)
                .accessToken(Constants.MAPBOX_ACCESS_TOKEN)
                .origin(origin)
                .destination(destination)
                .profile(profile)
                .language(Locale.FRENCH)
                .build()
                //do a request to Mapbox to have all of the routes you can take
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Timber.d("Response code: %s", response.code());
                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Timber.e("No routes found");
                            return;
                        }

                        List<DirectionsRoute> routes = response.body().routes();
                        System.out.println(response.body().routes());
                        //init the route, will find the best one to use
                        initRoute(routes);
                        launchRoute();
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Timber.e("Error: %s", throwable.getMessage());
                    }
                });
    }

    /**
     * init and draw the best route on the map
     * @param routes all of the routes you can
     */
    private void initRoute(List<DirectionsRoute> routes) {

        if(!routes.isEmpty()) {

            DirectionsRoute bestRoute = null;
            List<Checkpoint> checkpointsBestRoute = null;

            Log.d("MapNavView", "InitRoute1 : " + Arrays.toString(routes.toArray()));
            //Find the best route
            for(DirectionsRoute route : routes) {

                List<Checkpoint> checkpointsRoute = getCheckpointsForRoute(route);
                Log.d("MapNavView", "InitRoute2 : " + Arrays.toString(checkpointsRoute.toArray()));
                //if the number of checkpoints on the best route is less than the current route, change it
                if (bestRoute == null || checkpointsBestRoute.size() < checkpointsRoute.size()) {

                    bestRoute = route;
                    checkpointsBestRoute = checkpointsRoute;
                    System.out.println("InitRoute3 : " + Arrays.toString(checkpointsBestRoute.toArray()));
                }
            }

            // We have the best route now
            System.out.println("InitRoute4");
            currentRoute = bestRoute;

            // Draw the route on the map before the user has start the traject
            try {
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                } else {
                    navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                }
                navigationMapRoute.addRoute(currentRoute);
            } catch (Exception e) {
                System.out.println("EXCEPTION" + e);
            }
        }
    }

    /**
     * start the route
     */
    private void launchRoute() {
        //the start button can now be clicked
        Button button = findViewById(R.id.startButton);
        if(currentRoute != null) {
            button.setEnabled(true);
            button.setOnClickListener(v -> {
                // choose whether you want a simulation or the real deal by changing this value
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsProfile(profile)
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(false)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(MapNavView.this, options);
            });
        }
    }

    //the functions are explicit. Depends on the state of the app (pause, destroy...), do an action.
    @Override
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            //check if the location permission is granted
            com.adapei.navhelper.Location.checkLocationPermission(this);
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        setRoute(originPosition,destinationPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * Quit the view when the abort button is clicked
     * @param view The current view
     */
    public void abortButtonClicked(View view)
    {
        this.finish();
    }
}
