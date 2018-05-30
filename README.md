# Webex Teams Android SDK Wrapper

## Embed video calling capabilities into your Android App in just a few lines of code

The Webex Teams Android SDK Wrapper is an unofficial convenience library written on top of the Webex Teams Android SDK.  It provides a simple way to add Webex Teams video calling into your Android application.  This library provides a drop in Activity class that can be used to start a Webex Teams video call, without needing to understand the details of the underlying Webex Teams SDK.

This wrapper handles layout of the video streams for local and remote participants, the call setup process, in call functionality such as muting, as well as hanging up the call.

What this means is that you only need to declare permissions and start the SparkCall activity with an intent, passing in the TeamsID to call as well as the Guest Token (JWT) for your application.

## Requirements

This library can be used in three ways:

1. import release into gradle using JitPack (preferred)
2. download and import .aar directly in your Android Studio project
3. clone this repo and import the source into your Android Studio project

### JitPack instructions

This module is available through JitPack.  In order to include this module in your Android Studio project, do the following:

```
need to update
```


## Implementation
The Webex Teams SDK Wrapper uses an Activity as a drop in to display the Video call.  You will need to start the activity by passing an intent with your Guest Token and the TeamsID to call.

You can look at the sample MainActivity to see how this is done.  In this case, the onClick listener invokes the SparkCall activity by intent.

```
Intent intent = new Intent(MainActivity.this, SparkCall.class);
intent.putExtra(SparkCall.INTENT_CALLEE, mCallEdit.getText().toString());
intent.putExtra(SparkCall.INTENT_JWT, mTokenEdit.getText().toString());

startActivity(intent);
```

In this example, the call is started based on the contents of two EditText fields.

The SparkCall activity will return control back to your activity in the event of a call failure or a hangup event.


## License
Webex Teams Android SDK Wrapper is available under the MIT license. See the LICENSE file for more info.
