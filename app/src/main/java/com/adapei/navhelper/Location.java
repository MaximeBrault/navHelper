package com.adapei.navhelper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.mapbox.geojson.Point;

import java.util.Objects;

/**
 * Represents a location with her its coordinates and its name.
 */
public class Location {

    /**
     * The location's latitude
     */
    private double latitude;

    /**
     * The location's longitude
     */
    private double longitude;

    /**
     * The location's name
     */
    private String displayName;

    /**
     * The location's pronunciation
     */
    private String pronunciation;

    /**
     * Location's constructor
     * @param latitude The location's latitude
     * @param longitude The location's longitude
     * @param displayName The location's name
     * @param pronunciation The location's pronunciation
     */
    public Location(double latitude, double longitude, String displayName, String pronunciation) {

        if (displayName != null && pronunciation != null) {

            if (longitude >= -180 && longitude <= 180) {

                if (latitude >= -90 && latitude <= 90) {
                    this.latitude = latitude;
                    this.longitude = longitude;
                    this.displayName = displayName;
                    this.pronunciation = pronunciation;
                } else System.err.println("ERROR-public Location(double latitude, double longitude, String displayName, String pronunciation) : The latitude does not comply.");

            } else System.err.println("ERROR-public Location(double latitude, double longitude, String displayName, String pronunciation) : The longitude does not comply.");
        } else if (displayName == null) System.err.println("ERROR-public Location(double latitude, double longitude, String displayName, String pronunciation) : displayName can't be null.");
        else if (pronunciation == null) System.err.println("ERROR-public Location(double latitude, double longitude, String displayName, String pronunciation) : pronunciation can't be null.");
    }

    /**
     * Check if the location permission is granted, ask it if not
     * @param context Context of the view
     */
    public static void checkLocationPermission(Context context) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();//Provides a feedback about an operation in a small popup.
        }
    }

    /**
     * Get the Location's latitude
     * @return The location's latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Get the location's longitude
     * @return The location's longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Get the location's name
     * @return The location's name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Get the pronunciation of the location
     * @return The location's pronunciation
     */
    public String getPronunciation() {
        return this.pronunciation;
    }

    @Override
    /**
     * Check if two locations are the same.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Objects.equals(displayName, location.displayName) &&
                Objects.equals(pronunciation, location.pronunciation);
    }
    
    @Override
    /**
     * Hash a location.
     */
    public int hashCode() {
        return Objects.hash(latitude, longitude, displayName, pronunciation);
    }

    /**
     * Check if the location is near the given location when the location is express as an
     * object Location.
     * @param lastLocation Current location.
     * @return call the other method isNear with lastLocation express with latitude and
     * longitude.
     */
    public boolean isNear(android.location.Location lastLocation) {
        double currentLatitudeDegree = lastLocation.getLatitude();
        double currentLongitudeDegree = lastLocation.getLongitude();

        return isNear(currentLatitudeDegree, currentLongitudeDegree);
    }

    public boolean isNear(Location location) {
        return isNear(location.getLatitude(), location.getLongitude());
    }

    /**
     * Check if the location is near the given location when the location is express with latitude and
     * longitude.
     * @param latitude The location's latitude to compare with.
     * @param longitude The location's longitude to compare with.
     * @return True if the two location are near, false otherwise.
     */
    public boolean isNear(double latitude, double longitude) {
        double distance = Location.getDistanceBetweenCoordinates(latitude, longitude, this.getLatitude(), this.getLongitude());
        return distance <= Constants.NEAR_METER;//check if the location is less than 10 meter from the location checkpoint.
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static double getDistanceBetweenCoordinates(Location a, Location b) { return Location.getDistanceBetweenCoordinates(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude()); }

    /**
     * Calculate the distance in meters between to earth coordinates.
     * @param lat1 Latitude of the first point.
     * @param lon1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon2 Longitude of the second point.
     * @return The distance in kilometers between the two points.
     */
    public static double getDistanceBetweenCoordinates(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = Constants.EARTH_RADIUS * c; // in km

        return d * 1000; // km to meter
    }

    /**
     * Create a Point from a longitude and a latitude.
     * @return The point created.
     */
    public Point toPoint() {
        return Point.fromLngLat(this.longitude, this.latitude);
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setLatitude(double lat) { this.latitude = lat; }

    public void setName(String name) { this.displayName = name; }
}
