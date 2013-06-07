package com.b5n.pizzashake;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends Activity {
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    static final float ALPHA = 0.15f;
    private float[] accelVals;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                Toast.makeText(MainActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
            }
        });

        showRandomImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onStop();
    }



    private void showRandomImage() {
        ImageView image = (ImageView) findViewById(R.id.imageView2);

        Resources res = getResources();
        TypedArray pizzas = res.obtainTypedArray(R.array.pizza);
        Random r = new Random();
        Drawable drawable = pizzas.getDrawable(r.nextInt(pizzas.length()));

        image.setImageDrawable(drawable);
    }
}
