package com.example.sanzharaubakir.unshaky.models.hmm;

import io.github.adrianulbona.hmm.State;

public class Displacement implements State {
    public int value;

    public Displacement(int value) {
        this.value = value;
    }
}
