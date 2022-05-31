package com.adapei.navhelper.listener;

import android.content.Context;
import android.location.Location;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.geojson.Point;

/**
 * MyLocationEngineListener determine the location of a user
 * and update this position with the user's movement
 */
public class MyLocationEngineListener implements LocationEngineListener {

    private LocationEngine locationEngine; //The engine location of the application
    private Point currentUserPosition; //The current position of the user
    private Context context;

    /**
     * Constructor method of MyLocationEngineListener objects
     * @param locationEngine the generic location engine interface for the object to use
     */
    public MyLocationEngineListener(LocationEngine locationEngine, Context context) {
        if (locationEngine != null) this.locationEngine = locationEngine;
        if (context != null) this.context = context;
    }

    /**
     * Determine the current location of the user
     * Method automatically launched when the API is connected
     */
    @Override
    public void onConnected() {
        // Check if the permission is granted
        com.adapei.navhelper.Location.checkLocationPermission(context);
        locationEngine.requestLocationUpdates();

        if (locationEngine.getLastLocation() != null) {
            Location lastLocation = locationEngine.getLastLocation();
            currentUserPosition = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
        }
    }

    /**
     * Update the location of the user
     * @param location, The new location of the user
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            currentUserPosition = Point.fromLngLat(location.getLongitude(), location.getLatitude());
        }
    }

    /**
     * Getter of currentUserPosition attribut
     * @return currentUserPosition attribut of the object
     */
    public Point getCurrentUserPosition() {
        return this.currentUserPosition;
    }
}
