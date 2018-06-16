package com.example.sanzharaubakir.unshaky.models.hmm;

import com.example.sanzharaubakir.unshaky.models.UnshakyModel;
import com.example.sanzharaubakir.unshaky.sensor.Accelerometer;
import com.example.sanzharaubakir.unshaky.sensor.AccelerometerListener;
import com.example.sanzharaubakir.unshaky.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.adrianulbona.hmm.Model;
import io.github.adrianulbona.hmm.solver.MostProbableStateSequenceFinder;

public class HiddenMarkovModel extends UnshakyModel implements AccelerometerListener {
    public static final String TAG = "Hidden Markov Model";

    private final float HMM_COEFFICIENT = 2f;

    private Accelerometer accelerometer;

    private Model<HMM.Delta, HMM.Acc> model = HMM.INSTANCE.model;

    private List<List<HMM.Acc>> observables = Arrays.asList(
            new ArrayList<>(Collections.singletonList(HMM.Acc.ACC_0)),
            new ArrayList<>(Collections.singletonList(HMM.Acc.ACC_0)));

    private List<List<HMM.Delta>> evolutions = Arrays.asList(
            new MostProbableStateSequenceFinder<>(model).basedOn(observables.get(0)),
            new MostProbableStateSequenceFinder<>(model).basedOn(observables.get(1)));

    public HiddenMarkovModel(Accelerometer accelerometer) {
        super();
        this.accelerometer = accelerometer;
        accelerometer.registerListener(this);
    }


    @NotNull
    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void enable() {
        accelerometer.enable();
    }

    @Override
    public void disable() {
        accelerometer.disable();
    }

    @Override
    public void reset() {
        for (List list : observables) {
            list.subList(1, list.size()).clear();
        }
        for (List list : evolutions) {
            list.subList(1, list.size()).clear();
        }
        safelyCallListener(new float[] {0f, 0f, 0f});
    }
    @Override
    public void onSensorChanged(long timestamp, @NotNull float[] acc) {
        int order;
        float[] pos = new float[] {0f, 0f, 0f};
        List<HMM.Delta> evolution;
        List<HMM.Acc> observable;

        for (int i = 0; i < 2; ++i) {
            observable = observables.get(i);
            evolution = evolutions.get(i);

            order = (int) (Utils.rangeValue(acc[i], -2.2f, 2.8f) * 10f) + 22;
            observable.add(HMM.Acc.values()[order]);

            pos[i] = HMM_COEFFICIENT * acc[i] * evolution.get(evolution.size() - 1).ordinal();

            if (Math.abs(pos[i]) < 2f) {
                pos[i] = 0f;
            }
        }

        safelyCallListener(pos);
    }

    private void safelyCallListener(float[] position) {
        if (getListener() != null) {
            getListener().onPositionChanged(position);
        }
    }
}
