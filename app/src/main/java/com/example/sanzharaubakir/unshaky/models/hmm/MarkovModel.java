package com.example.sanzharaubakir.unshaky.models.hmm;

import android.content.Context;

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
import io.github.adrianulbona.hmm.ReachableStateFinder;
import io.github.adrianulbona.hmm.probability.ProbabilityCalculator;
import io.github.adrianulbona.hmm.solver.MostProbableStateSequenceFinder;

public class MarkovModel extends UnshakyModel implements AccelerometerListener {
    public static final String TAG = "Hidden Markov Model";

    private final float HMM_COEFFICIENT = 10f;

    private Accelerometer accelerometer;

    private Model<Displacement, Acceleration> model;
    private MarkovModelDataHolder data;

    private List<List<Acceleration>> observables;
    private List<List<Displacement>> evolutions;

    private ProbabilityCalculator<Displacement, Acceleration> probabilityCalculator() {
        return new ProbabilityCalculator<>(
                data.getStartingMap()::get,
                data.getEmissionMap()::get,
                data.getTransitionMap()::get);
    }

    private ReachableStateFinder<Displacement, Acceleration> reachableStatesFinder() {
        return observation -> data.getReachableStates();
    }

    public MarkovModel(Accelerometer accelerometer, Context context) {
        super();

        this.accelerometer = accelerometer;

        data = MarkovModelDataHolder.getSingleton(context);
        model = new Model<>(probabilityCalculator(), reachableStatesFinder());

        observables = Arrays.asList(
                new ArrayList<>(Collections.singletonList(new Acceleration(0))),
                new ArrayList<>(Collections.singletonList(new Acceleration(0))));

        evolutions = Arrays.asList(
                new MostProbableStateSequenceFinder<>(model).basedOn(observables.get(0)),
                new MostProbableStateSequenceFinder<>(model).basedOn(observables.get(1)));

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
        List<Displacement> evolution;
        List<Acceleration> observable;

        for (int i = 0; i < 2; ++i) {
            observable = observables.get(i);
            evolution = evolutions.get(i);

            order = (int) (Utils.rangeValue(acc[i], -2.2f, 2.8f) * 10f);
            observable.add(new Acceleration(order));

            pos[i] = HMM_COEFFICIENT * evolution.get(evolution.size() - 1).value;

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
