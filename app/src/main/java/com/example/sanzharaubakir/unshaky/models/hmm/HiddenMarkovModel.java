package com.example.sanzharaubakir.unshaky.models.hmm;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.sanzharaubakir.unshaky.models.sensor.AbstractSensor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.adrianulbona.hmm.Model;
import io.github.adrianulbona.hmm.solver.MostProbableStateSequenceFinder;

public class HiddenMarkovModel extends AbstractSensor implements SensorEventListener {
    private SensorManager manager;

    public HiddenMarkovModel(SensorManager manager) {
        super(manager, Sensor.TYPE_LINEAR_ACCELERATION);
        observablesX.add(HMM.Acc.ACC_0);
        evolutionX = new MostProbableStateSequenceFinder<>(model).basedOn(observablesX);
        observablesY.add(HMM.Acc.ACC_0);
        evolutionY = new MostProbableStateSequenceFinder<>(model).basedOn(observablesY);
    }

    private Long timestamp = 0L;
    public static String TAG = "Hidden Markov Model";

    private float[] data = new float[3];
    private float[] pos = new float[3];

    Model<HMM.Delta, HMM.Acc> model = HMM.INSTANCE.model;
    List<HMM.Acc> observablesX = new ArrayList<>();
    List<HMM.Delta> evolutionX;
    List<HMM.Acc> observablesY = new ArrayList<>();
    List<HMM.Delta> evolutionY;

    @Override
    public void enable() {
        registerListener(this, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void disable() {
        unregisterListener(this);
    }

    @Override
    public void reset() {
        timestamp = 0L;
        getListener().onPositionChanged(pos);
    }

    @NotNull
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Double accY = (double) sensorEvent.values[1] * 10;
        Integer accy = accY.intValue();
        if (accy < -22) {
            accy = -22;
        }
        if (accy > 28) {
            accy = 28;
        }
        Double accX = (double) sensorEvent.values[0] * 10;
        Integer accx = accY.intValue();
        if (accx < -22) {
            accx = -22;
        }
        if (accx > 28) {
            accx = 28;
        }
        observablesY.add(HMM.Acc.values()[accy + 22]);
        observablesX.add(HMM.Acc.values()[accx + 22]);

        float dx = (float) (0.2 * accX * evolutionX.get(evolutionX.size() - 1).ordinal());
        float dy = (float) (0.2 * accY * evolutionY.get(evolutionY.size() - 1).ordinal());

        if (dx < 2) dx = 0;
        if (dy < 2) dy = 0;

        getListener().onPositionChanged(new float[]{
                dx,
                dy});
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
