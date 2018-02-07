# ZigzagView
[![](https://jitpack.io/v/beigirad/ZigzagView.svg)](https://jitpack.io/#beigirad/ZigzagView)

a zigzag view  for using for ticket or invoice 

<img src="https://raw.githubusercontent.com/beigirad/ZigzagView/master/shot/zigzag.png" alt="ZigzagView"  width="200" />


# Setup

This library requires `minSdkVersion` to be set to `14` or above, like the [Official Support Library](https://developer.android.com/topic/libraries/support-library/index.html#api-versions).

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
dependencies {
    compile 'com.github.beigirad:ZigzagView:1.0.3'

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
    app:zigzagPaddingContent="16dp">
    
    // add child view(s)
    
</ir.beigirad.zigzagview.ZigzagView>
```

# Used libraries

* [com.android.support:appcompat-v7](https://developer.android.com/topic/libraries/support-library/packages.html#v7-appcompat)


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
