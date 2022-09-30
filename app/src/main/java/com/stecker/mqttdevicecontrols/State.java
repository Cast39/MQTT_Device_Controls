package com.stecker.mqttdevicecontrols;

import android.util.Log;

public class State {
    public boolean booleanState = false;
    public int intState = 0;
    public float floatState = 0;
    public String autodecode = null;

    public State(boolean booleanState) {
        this.booleanState = booleanState;
    }

    public State(int intState) {
        this.intState = intState;
    }

    public State(float floatState) {
        this.floatState = floatState;
    }

    public State(float floatState, boolean booleanState) {
        this.floatState = floatState;
        this.booleanState = booleanState;
    }


    public State() {

    }

    // detects datatype, returns false if decoding isn't possible
    public boolean autodecode(String unknownType) {
        this.autodecode = unknownType;
        if (unknownType.equalsIgnoreCase("true") | unknownType.equalsIgnoreCase("on")) {
            //Log.println(Log.ASSERT, "decoder", "its a bool");
            this.booleanState = true;

        } else if (unknownType.equalsIgnoreCase("false") | unknownType.equalsIgnoreCase("off")) {
            //Log.println(Log.ASSERT, "decoder", "its a bool");
            this.booleanState = false;

        } else {
            try {
                floatState = Float.parseFloat(unknownType);
                intState = (int) floatState;

            } catch (NumberFormatException nfe) {
                floatState = 0;
                return false;
            }

        }
        return true;
    }
}
