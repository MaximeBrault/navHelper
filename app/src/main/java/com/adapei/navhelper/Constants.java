package com.adapei.navhelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapei.navhelper.listener.MyLocationEngineListener;
import com.google.gson.Gson;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;

/**
 * The constants and global methods of the application.
 */
public class Constants {

  public static final double MPH_DOUBLE = 2.2369;
  public static final String MAPBOX_ACCESS_TOKEN = "pk.eyJ1IjoiYmdpcmF1ZHIiLCJhIjoiY2t1enh1ZmpzMTRubDJucXd6OW03cWdheiJ9.jIcOdljXABqk5IM6FScu3g";

  public static final double METER_MULTIPLIER = .00062137;
  public static final double EARTH_RADIUS = 6378.137;
  public static final int STRING_BUILDER_CAPACITY = 64;

  public static final Gson GSON = new Gson();//An open-source Java library to serialize and deserialize Java objects to (and from) JSON.
  public static final double NEAR_METER = 25;//Perimeter in which we are considered to be near
  public static final double NEAR_POINT_METER = 10;

  public static LocationEngine locationEngine;//Use for the location of the user

  public static final int DEFAULT_NB_COLUMNS = 2;//Display of parameters (number of columns)
  public static final int DEGREE_ARROW = 15; //Degree to update the arrow

  /*
   * Default parameters
   * useMap false/true
   * default city LORIENT/AURAY
   * pedestrian deplacement type
   * bike deplacement type
   * car deplacement type
   * bus deplacement type
   * type of user : false=user/true=guide
   * number of row 2 or 3
   * default number
   * default radius
   */
  public static final String[] defaultParam = {
          "false", //use map
          City.LORIENT.getName(), //default city
          "true", //pedestrian
          "false", //bike
          "false", //car
          "false", //bus
          "true",  //type of user
          Integer.toString(Constants.DEFAULT_NB_COLUMNS), //number of row
          "0000000000", //default number
          "150" //default radius
  };

  private static MyLocationEngineListener myLocationEngineListener; //Determine the location of a user and update this position with the user's movement

  /**
   * Initializes LocationEngine which is use to get latitude and longitude of a device.
   * @param context Information about the application environment.
   */
  public static void initLocationEngine(Context context) {

    //context : Interface to global information about an application environment.

    LocationEngineProvider locationEngineProvider = new LocationEngineProvider(context);//The main entry point for location engine integration.
    locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
    for(int i = 0; i < 100 && locationEngine == null; i++){
      locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
    }
    myLocationEngineListener = new MyLocationEngineListener(locationEngine, context); //determine the location of a user and update this position with the user's movement

    //LocationEngine is used to get latitude and longitude
    locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
    locationEngine.activate();
    locationEngine.setInterval(0);
    locationEngine.setFastestInterval(1000);
    locationEngine.addLocationEngineListener(myLocationEngineListener);
    locationEngine.activate();
  }

  /**
   * Deactivate LocationEngine.
   */
  public static void deactivateLocationEngine() {
    locationEngine.removeLocationUpdates();
    locationEngine.removeLocationEngineListener(myLocationEngineListener);
    locationEngine.deactivate();
  }

  /**
   * Convert dp (unity of measurement based on the physic density of a screen) to px.
   * @param context Information about the application environment.
   * @param dp Measure in dp.
   * @return Same measure but in px.
   */
  public static int dpToPx(Context context, int dp) {
    float density = context.getResources().getDisplayMetrics().density;
    return Math.round((float) dp * density);
  }

  /**
   * Create a button with an image which is linked to a destination.
   * @param context Information about the application environment.
   * @param counter tag of a button.
   * @return
   */
  public static ImageButton createDestinationImage(Context context, int counter) {
    //linearLayout : Layout information associated with ViewLinearLayout.
    //Layout params for the button
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT//The view wants to be as big as its parent, minus the parent's padding.
    );
    params.weight = 1;
    params.setMargins(Constants.dpToPx(context,5), Constants.dpToPx(context,5),
            Constants.dpToPx(context,5), Constants.dpToPx(context,5));

    //create the button with its param
    ImageButton destinationButton = new ImageButton(context);
    destinationButton.setLayoutParams(params);
    destinationButton.setPadding(Constants.dpToPx(context,5), Constants.dpToPx(context,5),
            Constants.dpToPx(context,5), Constants.dpToPx(context,5));
    destinationButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
    destinationButton.setAdjustViewBounds(true);
    destinationButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDestination));
    destinationButton.setTag(counter);
    destinationButton.setId(counter);

    return destinationButton;
  }

  /**
   * Generate the textView assigned for a specified destination
   * @param context the context
   * @param destination the destination
   * @return the new textView
   */
  public static TextView createDestinationTextView(Context context, Destination destination) {
    TextView t = new TextView(context);
    t.setText(destination.getNickname().toUpperCase());
    t.setTextColor(Color.BLACK);
    t.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
    t.setTextSize(22);
    t.setTypeface(null, Typeface.BOLD);
    t.setPadding(5,5,5,5);
    return t;
  }

  public static String getFoyer(Context context) {
    SharedPreferences mPrefs = context.getSharedPreferences(context.getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
    String ret = mPrefs.getString(context.getResources().getString(R.string.cityPreferencesName), City.LORIENT.getName());
    return ret;
  }
}
