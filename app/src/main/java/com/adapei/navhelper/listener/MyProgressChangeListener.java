package com.adapei.navhelper.listener;

import android.location.Location;

import com.adapei.navhelper.activity.navigation.ArrowNavView;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteLegProgress;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.List;

/**
 * MyProgressChangeListener set the current progress of users on a route.
 */
public class MyProgressChangeListener implements ProgressChangeListener {

    /**
     * The arrow pointing to a destination with a path to follow
     */
    private ArrowNavView arrowNavView;

    /**
     * Constructor method of MyProgressChangeListener, setting the arrowNavView attribute
     * @param arrowNavView the arrow of the object
     */
    public MyProgressChangeListener(ArrowNavView arrowNavView) {
        if (arrowNavView != null) this.arrowNavView = arrowNavView;
    }

    /**
     * Setting up the next destination that the arrow is suppose to point at,
     * automatically called after a user progress.
     * @param location The user's location
     * @param routeProgress the actuel progress on the route
     */
    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        if (routeProgress != null) {
            List<Point> routePoints = routeProgress.upcomingStepPoints(); //Listing all the points on the route
            RouteLegProgress currentLegProgress = routeProgress.currentLegProgress(); //LegProgress for the current route

            if (routePoints != null) {
                if (routePoints.size() == 1) {
                    this.arrowNavView.setNextDestination(routePoints.get(0));
                } else {
                    this.arrowNavView.setNextDestination(routePoints.get(0), routePoints.get(1));
                }

                if (currentLegProgress != null) {
                    this.arrowNavView.setCurrentLegProgress(currentLegProgress);
                }

                this.arrowNavView.updateUI();
            }
        }
    }
}
