package com.adapei.navhelper.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.adapei.navhelper.activity.navigation.ArrowNavView;

/**
 * MySensorEventListener used to detect a changement in the orientation of the user's phone (GPS position).
 */
public class MySensorEventListener implements SensorEventListener {

    private ArrowNavView arrowNavView; //The arrow pointing to a destination with a path to follow

    /**
     * Constructor method of MySensorEventListener, setting the arrowNavView attribute
     * @param arrowNavView the arrow of the object
     */
    public MySensorEventListener(ArrowNavView arrowNavView) {
        if (arrowNavView != null) {
            this.arrowNavView = arrowNavView;
        }
    }

    /**
     * Automatically called when sensor values have changed
     * @param event the event who's calling this method
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] mLastAccelerometer = new float[3];
        float[] mLastMagnetometer = new float[3];

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) { //If the rotation as changed
            SensorManager.getRotationMatrixFromVector(rMat, event.values); //Obtain the orientation of the matrix
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //If the acceleration as changed
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length); //copying the array to an existant array of the System
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { //If the magnetic field as changed
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
        }

        // offset because the application is running in a vertical orientation
        arrowNavView.updateRotation(rMat);
    }

    /**
     * Automatically called when the accuracy of a sensor has changed
     * @param sensor The sensor being monitoring
     * @param accuracy The new accuracy of this sensor
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /**Unused**/ };
}
