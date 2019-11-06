# ZigzagView
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![](https://jitpack.io/v/beigirad/ZigzagView.svg)](https://jitpack.io/#beigirad/ZigzagView)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ZigzagView-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/6787)

a zigzag view  for using for ticket or invoice 

<img src="https://github.com/oradkovsky/ZigzagView/blob/master/shot/zigzag.png" alt="ZigzagView"  width="200" />


# Setup
#### Step #1. Add the JitPack repository to root build.gradle file:

```gradle
allprojects {
    repositories {
	...
	maven { url "https://jitpack.io" }
    }
}
```

#### Step #2. Add the dependency

```groovy

//INFO: please build aar to use `soft` addition

dependencies {
    implementation 'com.github.beigirad:ZigzagView:VERSION'

}
```
# Implementation

```xml
<ir.beigirad.zigzagview.ZigzagView
    android:layout_width="match_parent"
    android:layout_height="240dp"
    app:zigzagBackgroundColor="#8bc34a"
    app:zigzagElevation="8dp"
    app:zigzagHeight="10dp"
    app:zigzagShadowAlpha="0.9"
    app:zigzagSides="top|bottom"
    app:zigzagPaddingContent="16dp">
    
    // add child view(s)
    
</ir.beigirad.zigzagview.ZigzagView>
```
# Attributes
|       Attribute       |       Type      | Default Value   |          Description          |
|:---------------------:|:---------------:|-----------------|:-----------------------------:|
|      zigzagHeight     |    dimension    | `0dp`           |     height of zigzag jags     |
|    zigzagElevation    |    dimension    | `0dp`           |         side of shadow        |
| zigzagBackgroundColor |      color      | `Color.WHITE`   |        background color       |
|  zigzagPaddingContent |    dimension    | `0dp`           |        content padding        |
|     zigzagPadding     |    dimension    | `0dp`           |          view padding         |
|   zigzagPaddingLeft   |    dimension    | `zigzagPadding` |     left side view padding    |
|   zigzagPaddingRight  |    dimension    | `zigzagPadding` |    right side view padding    |
|  zigzagPaddingBottom  |    dimension    | `zigzagPadding` |    bottom side view padding   |
|    zigzagPaddingTop   |    dimension    | `zigzagPadding` |     top side view padding     |
|      zigzagSides      |      enum       | `bottom`        |     choosing zigzag sides     |
|   zigzagShadowAlpha   | float `[0,1.0]` | `0.5`           | amount of shadow transparency |
|       zigzagType      |      enum       | `sharp`         | sharp or soft shape of zigzag |


# Thanks
[**Reza Kardoost**](https://github.com/RezaKardoost) for helping me out with ZigzagView Shadow/Elevation.

# License
Copyright 2018 Farhad Beigirad

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
