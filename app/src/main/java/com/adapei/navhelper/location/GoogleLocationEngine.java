package com.adapei.navhelper.location;
// maybe change API, check on the website of GoogleApiClient if you need to update/change the API, it's not a need right now (22/03/2022)

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.lang.ref.WeakReference;

public class GoogleLocationEngine extends LocationEngine implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

  /** class name */
  private static final String LOG_TAG = GoogleLocationEngine.class.getSimpleName();
  private static LocationEngine instance;

  /** to help the garbage collector */
  private WeakReference<Context> context;
  private GoogleApiClient googleApiClient;

  /**
   * enable the api and create the context
   * @param context, the context for the class
   */

  private GoogleLocationEngine(Context context) {
    super();
    this.context = new WeakReference<>(context);
    googleApiClient = new GoogleApiClient.Builder(this.context.get())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
  }

  /**
   * give a location instance for the specified context
   * @param context, application's information needed
   * @return the LocationEngine instance
   */

  public static synchronized LocationEngine getLocationEngine(Context context) {

    if (instance == null) {
      instance = new GoogleLocationEngine(context.getApplicationContext());
    }

    return instance;
  }

  @Override
  /**
   * activate the API only if it's not already connected
   */

  public void activate() {
    if (googleApiClient != null && !googleApiClient.isConnected()) {
      googleApiClient.connect();
    }
  }

  @Override
  /**
   * desactivate the API only if it's already connected
   */
  public void deactivate() {
    if (googleApiClient != null && googleApiClient.isConnected()) {
      googleApiClient.disconnect();
    }
  }

  @Override
  /**
   * check if the API is connected
   */
  public boolean isConnected() {
    return googleApiClient.isConnected();
  }

  @Override
  /**
   * connect all listeners
   * @param bundle, data to pass between activities
   */
  public void onConnected(@Nullable Bundle bundle) { // nullable make clear that the method accept null values
    for (LocationEngineListener listener : locationListeners) {
      listener.onConnected();
    }
  }

  @Override
  /**
   * manage an when the program is suspended with the program
   * @param cause, error number
   */
  public void onConnectionSuspended(int cause) {
    Log.d(LOG_TAG, "Connection suspended: " + cause);
  }

  @Override
  /**
   * manage errors from the connection
   * @param connectionResult, the error origin
   */
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(LOG_TAG, "Connection failed:" + connectionResult.getErrorMessage());
  }

  @SuppressWarnings({"MissingPermission"})
  @Override
  /**
   * give the last location the user has been on
   * @return the last location
   */
  public Location getLastLocation() {
    if (googleApiClient.isConnected() && PermissionsManager.areLocationPermissionsGranted(context.get())) {
      //noinspection MissingPermission
      return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    return null;
  }

  @SuppressWarnings({"MissingPermission"})
  @Override
  /**
   * set a priority to a request
   */
  public void requestLocationUpdates() {
    // Common params
    LocationRequest request = LocationRequest.create()
            .setInterval(0)
            .setSmallestDisplacement(3.0f)
            .setMaxWaitTime(1500);

    // Priority matching is straightforward
    if (priority == LocationEnginePriority.NO_POWER) {
      request.setPriority(LocationRequest.PRIORITY_NO_POWER);
    } else if (priority == LocationEnginePriority.LOW_POWER) {
      request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
    } else if (priority == LocationEnginePriority.BALANCED_POWER_ACCURACY) {
      request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    } else if (priority == LocationEnginePriority.HIGH_ACCURACY) {
      request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    if (googleApiClient.isConnected() && PermissionsManager.areLocationPermissionsGranted(context.get())) {
      //noinspection MissingPermission
      LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
    }
  }

  @Override
  /**
   * remove all the location updates in the of the user
   */
  public void removeLocationUpdates() {
    if (googleApiClient != null && googleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
  }

  @Override
  /**
   * i didn't find where this is used, but i keep it just in case
   */
  public Type obtainType() {
    return null;
  }

  @Override
  /**
   * advertise all listeners when the location changed
   * @param location, new location
   */
  public void onLocationChanged(Location location) {
    for (LocationEngineListener listener : locationListeners) {
      listener.onLocationChanged(location);
    }
  }
}
