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
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import static android.view.Gravity.CENTER;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends Activity implements View.OnTouchListener{
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private boolean isViewfaded = false;
    private Timer myTimer;
    private static final String TAG = "com.b5n.pizzashake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            public void onShake() {
                Toast.makeText(MainActivity.this, "Shake!", Toast.LENGTH_SHORT).show();
                showRandomImage();
            }
        });
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        View view = findViewById(R.id.mainView);
        view.setOnTouchListener(this);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 1000);

        showRandomImage(); // first
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        AlphaAnimation fadeOut = new AlphaAnimation(1f,0.2f);
        fadeOut.setDuration(800);
        fadeOut.setFillAfter(true);
        AlphaAnimation fadeIn = new AlphaAnimation(0.2f,1f);
        fadeIn.setDuration(800);
        fadeIn.setFillAfter(true);

        ProgressWheel pw = (ProgressWheel) findViewById(R.id.pw_spinner);
        pw.setVisibility(View.VISIBLE);
        for(int i = 0;i<10;i++){
            pw.incrementProgress();
        }

        ImageView image = (ImageView) findViewById(R.id.imageView2);

        if(!isViewfaded){
            image.startAnimation(fadeOut);
            isViewfaded = true;
        }

        if(pw.progress > 360){
            pw.setProgress(0);
            pw.setVisibility(View.INVISIBLE);

            image.startAnimation(fadeIn);
            showRandomImage();
            isViewfaded = true;
        }

        return true;
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.
            //Do something to the UI thread here
            Log.d(TAG,"tick");
        }
    };
}
