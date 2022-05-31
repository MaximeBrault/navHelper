package com.adapei.navhelper.listener;

import android.location.Location;

import com.adapei.navhelper.activity.navigation.ArrowNavView;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;

/**
 * MyOffRouteListener detect if a user is going out of a selected route,
 * calling the ArrowNavView offRoute method in that case
 */
public class MyOffRouteListener implements OffRouteListener {

    /**
     * The arrow pointing to a destination with a path to follow
     */
    private ArrowNavView arrowNavView;

    /**
     * Constructor method of MyOffRouteListener, setting the arrowNavView attribute
     * @param arrowNavView the arrow of the object
     */
    public MyOffRouteListener(ArrowNavView arrowNavView) {
        if (arrowNavView != null) {
            this.arrowNavView = arrowNavView;
        }
    }

    /**
     * method automatically called when a user is off-route
     * @param location the current location of the user
     */
    @Override
    public void userOffRoute(Location location) {
        if (location != null) {
            arrowNavView.offRoute(location); //call the offRoute method of the arrowNavView object
        }
    }
}
