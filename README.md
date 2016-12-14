# Sunshine WatchFace README#
====================

**Quick summary**

This app is intended to showcase a digital watchface made for Android Wear devices.

The Sunshine Watchface displays the high and low temps for the day based on the user's location. 

The weather data is pulled from the Sunshine mobile app.

Sunshine is the companion Android app for the [Udacity Android Nanodgree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801?v=ad1) 

The app uses weather information obtained from [OpenWeatherMap.org](http://openweathermap.org/)

### How do I get set up? ###
   * To get this app up and running clone this repo or download the zip file
   - Open up Android Studio and simply select from the Menu-  File > Import Project
   - Once you have the app open in Android Studio you will need to obtain an API Key to access the weather data
     - You can get an OpenWeatherMap API key from [OpenWeatherMap.org/appid](http://openweathermap.org/appid) 
     - Once you've obtained an API Key add it to you app level "build.gradle" file by replacing ***Your API Key here*** with your API Key:

```
#!python

    buildTypes.each {
        it.buildConfigField 'String', 'OPEN_WEATHER_MAP_API_KEY', "\"Your API Key here\""
    }
```

  - Once you have the app installed- set your location in the Sunshine mobile app one time to sync things up and you're good to go!

**License**

Copyright 2015 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.