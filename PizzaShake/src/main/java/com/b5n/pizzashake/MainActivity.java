package com.b5n.pizzashake;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends Activity implements ShakeDetector.Listener {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRandomImage();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
    }

    private void showRandomImage() {
        ImageView image = (ImageView) findViewById(R.id.imageView2);

        Resources res = getResources();
        TypedArray pizzas = res.obtainTypedArray(R.array.pizza);
        Random r = new Random();
        Drawable drawable = pizzas.getDrawable(r.nextInt(pizzas.length()));

        image.setImageDrawable(drawable);
    }

    public void hearShake() {
        showRandomImage();
    }
}
