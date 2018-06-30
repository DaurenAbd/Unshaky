package com.example.sanzharaubakir.unshaky.models.hmm;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.github.adrianulbona.hmm.Emission;
import io.github.adrianulbona.hmm.Transition;

public class MarkovModelDataHolder {
    private static MarkovModelDataHolder instance;

    private void initialize(Context context) throws IOException {
        Scanner startingScanner = new Scanner(context.getAssets().open("starting"));
        Scanner transitionScanner = new Scanner(context.getAssets().open("transition"));
        Scanner emissionScanner = new Scanner(context.getAssets().open("emission"));

        while(startingScanner.hasNext()) {
            Displacement state = new Displacement(startingScanner.nextInt());
            Double probability = startingScanner.nextDouble();
            reachableStates.add(state);
            startingMap.put(state, probability);
        }

        while(emissionScanner.hasNext()) {
            Displacement state = new Displacement(emissionScanner.nextInt());
            Acceleration observable = new Acceleration(emissionScanner.nextInt());
            Double probability = emissionScanner.nextDouble();
            Emission<Displacement, Acceleration> emission = new Emission<>(state, observable);
            emissionMap.put(emission, probability);
        }

        while(transitionScanner.hasNext()) {
            Displacement stateFrom = new Displacement(transitionScanner.nextInt());
            Displacement stateTo = new Displacement(transitionScanner.nextInt());
            Double probability = transitionScanner.nextDouble();
            Transition<Displacement> transition = new Transition<>(stateFrom, stateTo);
            transitionMap.put(transition, probability);
        }
    }

    private static synchronized void createInstance(Context context) {
        instance = new MarkovModelDataHolder();
        try {
            instance.initialize(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MarkovModelDataHolder getSingleton(Context context) {
        if (instance == null) {
            createInstance(context);
        }

        return instance;
    }

    private final Map<Transition<Displacement>, Double> transitionMap;
    private final Map<Emission<Displacement, Acceleration>, Double> emissionMap;
    private final Map<Displacement, Double> startingMap;
    private final List<Displacement> reachableStates;

    private MarkovModelDataHolder() {
        transitionMap = new HashMap<>();
        emissionMap = new HashMap<>();
        startingMap = new HashMap<>();
        reachableStates = new ArrayList<>();
    }

    public Map<Transition<Displacement>, Double> getTransitionMap() {
        return transitionMap;
    }

    public Map<Emission<Displacement, Acceleration>, Double> getEmissionMap() {
        return emissionMap;
    }

    public Map<Displacement, Double> getStartingMap() {
        return startingMap;
    }

    public List<Displacement> getReachableStates() {
        return reachableStates;
    }
}
