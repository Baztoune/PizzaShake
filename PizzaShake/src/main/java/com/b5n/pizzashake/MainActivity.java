package com.b5n.pizzashake;

import android.app.Activity;
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

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager manager;
    private Sensor accel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        showRandomImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not going to do anything with this.
    }
    public void onSensorChanged(SensorEvent event) {
        float xValue = event.values[0];
        float yValue = event.values[1];
        float zValue = event.values[2];
        if (xValue + yValue + zValue > 3 ) {
            showRandomImage();
        }
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
