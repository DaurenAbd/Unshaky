package com.example.sanzharaubakir.unshaky;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.sanzharaubakir.unshaky.sensor.Accelerometer;
import com.example.sanzharaubakir.unshaky.sensor.AccelerometerListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends Activity implements AccelerometerListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private View layoutSensor;

    private SensorManager sensorManager;
    private Accelerometer accelerometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = new Accelerometer(sensorManager);
        accelerometer.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.disable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        View layoutRoot = findViewById(R.id.layout_root);

        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accelerometer.reset();
            }
        });

        layoutSensor = findViewById(R.id.layout_sensor);
    }

    @Override
    public void onPositionChanged(@NotNull float[] position) {
        layoutSensor.setTranslationX(-position[0]);
        layoutSensor.setTranslationY(position[1]);
    }
}