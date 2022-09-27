# MQTT_Device_Controls
A handy tool to use Android 11's new Device Control feature to control Smart Home devices with MQTT

## Overview
With this App you can create multiple Device Controls in the Power Menu of Android 11.
You can create them in different categories:
![Screenshot of different categories](https://github.com/Cast39/MQTT_Device_Controls/blob/master/images/Screenshot_20201011-153858.png?raw=true)

and customize what is written on them (yes there is Emoji support):
![Screenshot of Emojis](https://github.com/Cast39/MQTT_Device_Controls/blob/master/images/Screenshot_20201011-155017.png?raw=true)


## How to use
tbd

### Example Config
```
[
  {
    "controls": [
      {
        "MQTTtopic": "home/carsten/deckenlampe/warmth",
        "PIFlags": 134217728,
        "controlID": "0",
        "deviceType": 49,
        "enabled": true,
        "retain": false,
        "structure": "Carsten",
        "subtitle": "Wärme",
        "template": {
          "formatString": "%.0f",
          "maxValue": 100.0,
          "minValue": 0.0,
          "stepValue": 1.0,
          "templateType": "rangetemplate"
        },
        "title": "Deckenlampe"
      },
      {
        "MQTTtopic": "home/carsten/deckenlampe/brightness",
        "PIFlags": 134217728,
        "controlID": "1",
        "deviceType": 13,
        "enabled": true,
        "retain": false,
        "structure": "Carsten",
        "subtitle": "Helligkeit",
        "template": {
          "formatString": "%.0f",
          "maxValue": 100.0,
          "minValue": 0.0,
          "stepValue": 1.0,
          "templateType": "rangetemplate"
        },
        "title": "Deckenlampe"
      },
      {
        "MQTTtopic": "home/carsten/steckdose1/state",
        "PIFlags": 134217728,
        "controlID": "2",
        "deviceType": 15,
        "enabled": true,
        "retain": false,
        "structure": "Carsten",
        "subtitle": "",
        "template": {
          "actionDescription": "BUTTON",
          "offCommand": "false",
          "onCommand": "true",
          "templateType": "toggletemplate"
        },
        "title": "😻"
      },
      {
        "MQTTtopic": "home/clemens/deckenlampe/state",
        "PIFlags": 134217728,
        "controlID": "3",
        "deviceType": 13,
        "enabled": true,
        "retain": false,
        "structure": "Clemens",
        "subtitle": "",
        "template": {
          "actionDescription": "BUTTON",
          "offCommand": "off",
          "onCommand": "on",
          "templateType": "toggletemplate"
        },
        "title": "😻"
      },
      {
        "MQTTtopic": "home/living_room/ceilinglamp/state",
        "PIFlags": 134217728,
        "controlID": "4",
        "deviceType": 13,
        "enabled": true,
        "retain": false,
        "structure": "Living Room",
        "subtitle": "",
        "template": {
          "actionDescription": "BUTTON",
          "offCommand": "false",
          "onCommand": "true",
          "templateType": "toggletemplate"
        },
        "title": "Deckenlampe"
      },
      {
        "MQTTtopic": "home/dining_room/ceilinglamp/state",
        "PIFlags": 134217728,
        "controlID": "5",
        "deviceType": 13,
        "enabled": true,
        "retain": false,
        "structure": "Dining Room",
        "subtitle": "",
        "template": {
          "actionDescription": "BUTTON",
          "offCommand": "",
          "onCommand": "switch mode!",
          "templateType": "toggletemplate"
        },
        "title": "ToggleDeckenlampe"
      },
      {
        "MQTTtopic": "home/dining_room/ceilinglamp/state2",
        "PIFlags": 134217728,
        "controlID": "6",
        "deviceType": 13,
        "enabled": true,
        "retain": false,
        "structure": "Dining Room",
        "subtitle": "",
        "template": {
          "command": "switch mode!",
          "templateType": "statelesstemplate"
        },
        "title": "StatelessDeckenlampe"
      }
    ],
    "enabled": true,
    "password": "",
    "port": 1883,
    "protocol": "tcp",
    "url": "test.mosquitto.org",
    "username": ""
  }
]
```
