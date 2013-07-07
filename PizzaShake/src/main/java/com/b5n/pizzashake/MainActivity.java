package com.b5n.pizzashake;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnTouchListener{
    private static final String TAG = "com.b5n.pizzashake";

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    private Timer myTimer;
    private boolean isProgressWheelVisible = false;
    private long latestPizzaChange = 0;
    private long latestProgressWheelUpdate = 0;
    private static long TIME_AFTER_PIZZA_CHANGE = 800;
    private static long TIME_BEFORE_PROGRESSWHEEL_RESET = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            public void onShake() {
                updateProgressWheel(10);
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

        latestPizzaChange = System.currentTimeMillis();
    }

    private void updateProgressWheel(int percentToAdd){
        /* Get components */
        ProgressWheel pw = (ProgressWheel) findViewById(R.id.pw_spinner);
        ImageView image = (ImageView) findViewById(R.id.imageView2);

        /* Update view */
        long now = System.currentTimeMillis();
        if(now - latestPizzaChange > TIME_AFTER_PIZZA_CHANGE){
            pw.setProgress(pw.progress + Double.valueOf(3.6 * percentToAdd).intValue());
            if(pw.progress > 360){
                // progressWheel full
                hideProgressWheel();
                showRandomImage();
            } else {
                if(!isProgressWheelVisible){
                    showProgressWheel();
                }
            }
            latestProgressWheelUpdate = now;
        }
    }

   private void showProgressWheel(){
        /*Get components*/
       ProgressWheel pw = (ProgressWheel) findViewById(R.id.pw_spinner);
       ImageView image = (ImageView) findViewById(R.id.imageView2);

       AlphaAnimation fadeOutView = new AlphaAnimation(1f,0.2f);
       fadeOutView.setDuration(800);
       fadeOutView.setFillAfter(true);

       pw.setVisibility(View.VISIBLE);
       image.startAnimation(fadeOutView);
       isProgressWheelVisible = true;
   }

    private void hideProgressWheel(){
        /*Get components*/
        ProgressWheel pw = (ProgressWheel) findViewById(R.id.pw_spinner);
        ImageView image = (ImageView) findViewById(R.id.imageView2);

        AlphaAnimation fadeInView = new AlphaAnimation(0.2f,1f);
        fadeInView.setDuration(800);
        fadeInView.setFillAfter(true);
        image.startAnimation(fadeInView);

        pw.setProgress(0);
        pw.setVisibility(View.INVISIBLE);
        isProgressWheelVisible = false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        updateProgressWheel(2);
        return true;
    }

    private void TimerMethod() {
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            Log.d(TAG,"tick");
            long now = System.currentTimeMillis();
            if(isProgressWheelVisible && (now - latestProgressWheelUpdate > TIME_BEFORE_PROGRESSWHEEL_RESET)){
                //reset and hide progress wheel
                hideProgressWheel();
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
