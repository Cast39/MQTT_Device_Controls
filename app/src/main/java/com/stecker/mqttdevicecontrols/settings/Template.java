package com.stecker.mqttdevicecontrols.settings;

public class Template {
    public String templateType = "undefined"; // toggletemplate, rangetemplate, togglerangetemplate, temperaturecontroltemplate, statelesstemplate

    // TemperatureControlTemplate
    public Integer currentMode = null;
    public Integer currentActiveMode = null;
    public Integer modesFlag = null;

    // RangeTemplate & ToggleRangeTemplate
    public Float minValue = null;
    public Float maxValue = null;
    public Float stepValue = null;
    public String formatString = null;

    // ToggleTemplate & ToggleRangeTemplate
    public String onCommand = null;
    public String offCommand = null;

    // StatelessTemplate
    public String command = null;
}
