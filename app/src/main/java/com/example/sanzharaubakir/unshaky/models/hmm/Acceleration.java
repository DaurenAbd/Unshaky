package com.example.sanzharaubakir.unshaky.models.hmm;

import io.github.adrianulbona.hmm.Observation;

public class Acceleration implements Observation {
    public int value;

    public Acceleration(int value) {
        this.value = value;
    }
}
