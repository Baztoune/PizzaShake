package com.b5n.pizzashake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.Arrays;


/**
 * Listener that detects shake gesture.
 */
public class ShakeEventListener implements SensorEventListener {


    /** Minimum movement force to consider. */
    private static final int MIN_FORCE = 8;

    /**
     * Minimum times in a shake gesture that the direction of movement needs to
     * change.
     */
    private static final int MIN_DIRECTION_CHANGE = 2;

    /** Maximum pause between movements. */
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 200;

    /** Maximum allowed time for shake gesture. */
    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;

    /** Time when the gesture started. */
    private long mFirstDirectionChangeTime = 0;

    /** Time when the last movement started. */
    private long mLastDirectionChangeTime;

    /** How many movements are considered so far. */
    private int mDirectionChangeCount = 0;

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

    float[] values = {0,0,0};


    /** OnShakeListener that is called when shake is detected. */
    private OnShakeListener mShakeListener;

    /**
     * Interface for shake gesture.
     */
    public interface OnShakeListener {

        /**
         * Called when shake gesture is detected.
         */
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        // get sensor data
        Log.d(this.getClass().getCanonicalName(),"last  : " + Arrays.toString(values));
        Log.d(this.getClass().getCanonicalName(),"event : " + Arrays.toString(se.values));
        values = lowPass(se.values.clone(),values);
        Log.d(this.getClass().getCanonicalName(),"new   : " + Arrays.toString(values) );

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float magnitude = (float)Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
        magnitude = Math.abs(magnitude - SensorManager.GRAVITY_EARTH);
        Log.d(this.getClass().getCanonicalName(),"magnitude = " + magnitude);

        // calculate movement
        float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);
        Log.d(this.getClass().getCanonicalName(),"movement = "+totalMovement);

        if (magnitude > 4) {

            Log.d(this.getClass().getCanonicalName(),"MOVING");

            // get time
            long now = System.currentTimeMillis();

            // store first movement time
            if (mFirstDirectionChangeTime == 0) {
                mFirstDirectionChangeTime = now;
                mLastDirectionChangeTime = now;
            }

            // check if the last movement was not long ago
            long lastChangeWasAgo = now - mLastDirectionChangeTime;
            if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

                // store movement data
                mLastDirectionChangeTime = now;
                mDirectionChangeCount++;

                // store last sensor data
                lastX = x;
                lastY = y;
                lastZ = z;
            } else {
                resetShakeParameters();
            }
        } else {
            Log.d(this.getClass().getCanonicalName(),"NOT MOVING");
            if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {
                Log.d(this.getClass().getCanonicalName(),"WAS MOVING ("+mDirectionChangeCount+" moves)");
                mShakeListener.onShake();
                resetShakeParameters();
            }
        }
    }

    /**
     * Resets the shake parameters to their default values.
     */
    private void resetShakeParameters() {
        mFirstDirectionChangeTime = 0;
        mDirectionChangeCount = 0;
        mLastDirectionChangeTime = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }


    /*
     * time smoothing constant for low-pass filter
     * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
     * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    static final float ALPHA = 0.75f;

    /**
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}