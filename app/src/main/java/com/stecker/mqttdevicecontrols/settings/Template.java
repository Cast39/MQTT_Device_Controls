package com.stecker.mqttdevicecontrols.settings;

public class Template {
    public String templateType = ""; // toggletemplate, rangetemplate, togglerangetemplate, temperaturecontroltemplate, statelesstemplate

    // ToggleTemplate
    public String actionDescription = "";

    // RangeTemplate
    public float minValue = 0.0f;
    public float maxValue = 1000.0f;
    public float stepValue = 1.0f;
    public String formatString = "%.0f"; // eg 100.0 / 50.0

    // TemperatureControlTemplate
    public int currentMode;
    public int currentActiveMode;
    public int modesFlag;

}
