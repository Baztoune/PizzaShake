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

    private static final int MIN_FORCE = 4;
    float[] values = {0, 0, 0};

    /**
     * OnShakeListener that is called when shake is detected.
     */
    private OnShakeListener mShakeListener;

    /**
     * Interface for shake gesture.
     */
    public interface OnShakeListener {
        // Called when shake gesture is detected.
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        // get sensor data
        values = lowPass(se.values.clone(), values);

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
        magnitude = Math.abs(magnitude - SensorManager.GRAVITY_EARTH);
        Log.d(this.getClass().getCanonicalName(), "magnitude = " + magnitude);

        if (magnitude > MIN_FORCE) {
            Log.d(this.getClass().getCanonicalName(), "MOVING");
            mShakeListener.onShake();
        }
    }

    /*
     * time smoothing constant for low-pass filter
     * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
     * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
     */
    static final float ALPHA = 0.5f;

    /**
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) {
            return input;
        } else {
            for (int i = 0; i < input.length; i++) {
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}