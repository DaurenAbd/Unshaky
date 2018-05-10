package com.example.sanzharaubakir.unshaky;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sanzharaubakir.unshaky.Utilities.Constants;
import com.example.sanzharaubakir.unshaky.Utilities.Utils;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private View layoutSensor;


    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final float[] tempAcc = new float[3];
    private final float[] acc = new float[3];
    private final float[] velocity = new float[3];
    private final float[] position = new float[3];
    private long timestamp = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViews();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean ret = sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        if (!ret) {
            Log.wtf(TAG, "Sensor listener registration failed");
            Toast.makeText(this, "Sensor listener registration failed", Toast.LENGTH_SHORT).show();
        }
        //setSvcEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }


    private void initViews() {
        View layoutRoot = findViewById(R.id.layout_root);
        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        layoutSensor = findViewById(R.id.layout_sensor);


    }

    private void reset() {
        position[0] = position[1] = position[2] = 0;
        velocity[0] = velocity[1] = velocity[2] = 0;
        acc[0] = acc[1] = acc[2] = 0;
        timestamp = 0;

        layoutSensor.setTranslationX(0);
        layoutSensor.setTranslationY(0);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (timestamp != 0) {
                tempAcc[0] = Utils.rangeValue(event.values[0], -Constants.MAX_ACC, Constants.MAX_ACC);
                tempAcc[1] = Utils.rangeValue(event.values[1], -Constants.MAX_ACC, Constants.MAX_ACC);
                tempAcc[2] = Utils.rangeValue(event.values[2], -Constants.MAX_ACC, Constants.MAX_ACC);

                Utils.lowPassFilter(tempAcc, acc, Constants.LOW_PASS_ALPHA_DEFAULT);

                float dt = (event.timestamp - timestamp) * Constants.NS2S;

                for (int index = 0; index < 3; ++index) {
                    velocity[index] += acc[index] * dt - Constants.VELOCITY_FRICTION_DEFAULT * velocity[index];
                    velocity[index] = Utils.fixNanOrInfinite(velocity[index]);

                    position[index] += velocity[index] * Constants.VELOCITY_AMPL_DEFAULT * dt - Constants.POSITION_FRICTION_DEFAULT * position[index];
                    position[index] = Utils.rangeValue(position[index], -Constants.MAX_POS_SHIFT, Constants.MAX_POS_SHIFT);
                }
            } else {
                velocity[0] = velocity[1] = velocity[2] = 0f;
                position[0] = position[1] = position[2] = 0f;

                acc[0] = Utils.rangeValue(event.values[0], -Constants.MAX_ACC, Constants.MAX_ACC);
                acc[1] = Utils.rangeValue(event.values[1], -Constants.MAX_ACC, Constants.MAX_ACC);
                acc[2] = Utils.rangeValue(event.values[2], -Constants.MAX_ACC, Constants.MAX_ACC);
            }

            timestamp = event.timestamp;

            layoutSensor.setTranslationX(-position[0]);
            layoutSensor.setTranslationY(position[1]);

        }
    };
}