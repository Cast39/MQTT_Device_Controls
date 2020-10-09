package com.stecker.mqttdevicecontrols.settings;

public class Template {
    public String templateType; // toggletemplate, rangetemplate, togglerangetemplate, temperaturecontroltemplate, statelesstemplate

    // ToggleTemplate
    public String actionDescription;

    // RangeTemplate
    public float minValue;
    public float maxValue;
    public float stepValue;
    public String formatString;

    // TemperatureControlTemplate
    public int currentMode;
    public int currentActiveMode;
    public int modesFlag;

}
