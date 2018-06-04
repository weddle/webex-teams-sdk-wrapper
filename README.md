[![Release](https://jitpack.io/v/weddle/webex-teams-sdk-wrapper.svg)](https://jitpack.io/#weddle/webex-teams-sdk-wrapper)
[![LICENSE](https://img.shields.io/github/license/weddle/webex-teams-sdk-wrapper.svg)](https://github.com/weddle/webex-teams-sdk-wrapper/blob/master/LICENSE)
# Webex Teams Android SDK Wrapper

## Embed video calling capabilities into your Android App in just a few lines of code

The Webex Teams Android SDK Wrapper is an unofficial convenience library written on top of the [Webex Teams Android SDK](https://developer.webex.com/sdk-for-android.html).  It provides a simple way to add Webex Teams video calling into your Android application.  This library provides a drop in Activity class that can be used to start a Webex Teams video call, without needing to understand the details of the underlying Webex Teams SDK.

This wrapper handles layout of the video streams for local and remote participants, the call setup process, in call functionality such as muting, as well as hanging up the call.

What this means is that you only need to declare permissions and start the SparkCall activity with an intent, passing in the TeamsID to call as well as the [Guest Token (JWT)](https://developer.webex.com/guest-issuer.html) for your application.

To see an example of how to import and use this wrapper, please refer to the accompanying [Webex Teams Android SDK Wrapper Sample](https://github.com/weddle/webex-teams-sdk-wrapper-sample) Application.

## Requirements

This library can be used in three ways:

1. import release into gradle using [JitPack](https://jitpack.io/) (preferred)
2. download and import .aar directly in your [Android Studio](https://developer.android.com/studio/) project
3. clone this repo and import the source into your [Android Studio](https://developer.android.com/studio/) project

### JitPack instructions

[![Release](https://jitpack.io/v/weddle/webex-teams-sdk-wrapper.svg)](https://jitpack.io/#weddle/webex-teams-sdk-wrapper)

This module is available through JitPack.  In order to include this module in your Android Studio project, do the following:

In your root build.gradle, add the following:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependencies to your application build.gradle
```
dependencies {
    compile('com.ciscospark:androidsdk:1.3.0@aar', {
        transitive = true
    })
    implementation 'com.github.weddle:webex-teams-sdk-wrapper:v0.2'
}
```

Make sure to declare permissions in your AndroidManifest.xml
```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

```


## Implementation
The Webex Teams SDK Wrapper uses an Activity as a drop in to display the Video call.  You will need to start the activity by passing an intent with your Guest Token and the TeamsID to call.

You can look at the [Sample Application's](https://github.com/weddle/webex-teams-sdk-wrapper-sample) MainActivity of the sample app to see how this is done.  In this case, the onClick listener invokes the SparkCall activity by intent.

```
Intent intent = new Intent(MainActivity.this, SparkCall.class);
intent.putExtra(SparkCall.INTENT_CALLEE, mCallEdit.getText().toString());
intent.putExtra(SparkCall.INTENT_JWT, mTokenEdit.getText().toString());

startActivity(intent);
```

The SparkCall activity will return control back to your activity in the event of a call failure or a hangup event.


## License
Webex Teams Android SDK Wrapper is available under the MIT license. See the LICENSE file for more info.

