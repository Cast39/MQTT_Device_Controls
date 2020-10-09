package com.stecker.mqttdevicecontrols;

public class State {
    public boolean booleanState = false;
    public int intState = 0;
    public float floatState = 0;

    public State(boolean booleanState) {
        this.booleanState = booleanState;
    }

    public State(int intState) {
        this.intState = intState;
    }

    public State(float floatState) {
        this.floatState = floatState;
    }
}
