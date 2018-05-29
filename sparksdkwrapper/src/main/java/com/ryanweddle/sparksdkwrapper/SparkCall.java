package com.ryanweddle.sparksdkwrapper;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ciscospark.androidsdk.phone.Call;
import com.ciscospark.androidsdk.phone.CallObserver;
import com.ciscospark.androidsdk.phone.MediaOption;

// import com.webex.wseclient.WseSurfaceView;

public class SparkCall extends AppCompatActivity implements CallObserver {


    public static final String CLASS_TAG = "SparkCall";
    public static final String INTENT_CALLEE = "com.ryanweddle.sparksdkwrapper.CALLEE_ID";
    public static final String INTENT_JWT = "com.ryanweddle.sparksdkwrapper.CALLEE_JWT";

    private SparkModel mSparkModel;
    private View mRemoteView;
    private View mLocalView;
    private String mCallString;
    private String mToken;
    private MediaOption mMedia;
    private ImageButton mButtonMute;
    private ImageButton mButtonHangup;
    private ImageButton mButtonRotate;
    private ConstraintLayout mButtonGroup;

    private boolean mSendingAudio = false;
    private boolean mFrontCamSelected = true;

    private ProgressDialog mProgress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spark_call);

        Log.v(CLASS_TAG, "onCreate");

        mSparkModel = SparkModel.getInstance();
        mSparkModel.setApp(this.getApplication());
        mSparkModel.setObserver(this);

        mLocalView = findViewById(R.id.view_calllocal);
        mRemoteView = findViewById(R.id.view_callremote);

        mButtonRotate = findViewById(R.id.button_rotate);
        mButtonHangup = findViewById(R.id.button_hangup);
        mButtonMute = findViewById(R.id.button_mute);
        mButtonGroup = findViewById(R.id.group_buttons);


        handleIntent();

        mMedia = MediaOption.audioVideo(mLocalView, mRemoteView);

        // permission request code
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        this.requestPermissions(permissions,0);

        mButtonHangup.setOnClickListener(view -> {
            doHangup();
        });

        mButtonMute.setOnClickListener(view -> {
            doMute();
        });

        mButtonRotate.setOnClickListener(view -> {
            doRotate();
        });

        if(!mSparkModel.isInCall()) {
            mSendingAudio = true;
            hideButtonGroup(); // don't show button group until we are in call
            placeCall();
        } else {
            mSendingAudio = mSparkModel.isSendingAudio();
            mFrontCamSelected = mSparkModel.getCameraFacing();
            setButtonViews();
            // setMuteButtonView();
        }

    }

    /*
     * Lifecycle Methods
     *
     */

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isFinishing()) {
            mSparkModel.deRegister(result -> {
                Log.i(CLASS_TAG, "Spark Phone Deregistered");
            });
        }
    }

    /*
     * CallObserver Methods
     *
     */

    @Override
    public void onRinging(Call call) {
        toast("Ringing");
    }

    @Override
    public void onConnected(Call call) {
        // dismissWithToast("Call Connected");
        dismissBusyIndicator();
        showButtonGroup();
    }

    @Override
    public void onDisconnected(CallDisconnectedEvent event) {
        dismissWithToast("Call Disconnected");
        hideButtonGroup();
        finish();
    }

    @Override
    public void onMediaChanged(MediaChangedEvent event) {
        // toast("Media Changed");
    }

    @Override
    public void onCallMembershipChanged(CallMembershipChangedEvent event) {
        // toast("Call Membership Changed");
    }

    protected void updateViews() {
        if(mSparkModel.isInCall())
            mSparkModel.getActiveCall().setVideoRenderViews(new Pair<View, View>(mLocalView, mRemoteView));
    }


    /*
     * Button Methods
     */

    protected void doHangup() {
        //toast("Hangup Pressed");
        mSparkModel.hangup(result -> {
            //toast("Hangup Processed");
            finish();
        });
    }

    protected void doMute() {
        //toast("Mute Pressed");
        mSendingAudio = !mSparkModel.isSendingAudio();
        mSparkModel.setSendingAudio(mSendingAudio);
        setMuteButtonView();
    }

    protected void doRotate() {

        mFrontCamSelected = !mSparkModel.getCameraFacing();
        mSparkModel.setCameraFacing(mFrontCamSelected);
        setRotateButtonView();
    }


    protected void setButtonViews() {
        setMuteButtonView();
        setRotateButtonView();
    }

    protected void setMuteButtonView() {
        if(mSendingAudio)
            mButtonMute.setImageResource(R.drawable.mute3x);
        else
            mButtonMute.setImageResource(R.drawable.muteactive3x);
    }

    protected void setRotateButtonView() {
        if(mFrontCamSelected)
            mButtonRotate.setImageResource(R.drawable.rotate3x);
        else
            mButtonRotate.setImageResource(R.drawable.rotatective3x);
    }

    protected void hideButtonGroup() {
        mButtonGroup.setVisibility(View.INVISIBLE);
    }

    protected void showButtonGroup() {
        mButtonGroup.setVisibility(View.VISIBLE);
    }


    protected void placeCall() {
        showBusyIndicator("Auth Token", "Waiting for auth...");
        mSparkModel.authenticateJWT(mToken, authR -> {
            if(authR.isSuccessful()) {
                dismissBusyIndicator();
                showBusyIndicator("Registering", "Registering device...");
                mSparkModel.register(regR -> {
                    if(regR.isSuccessful()) {
                        dismissBusyIndicator();
                        showBusyIndicator("Calling", "Calling");
                        mSparkModel.dial(mCallString,mMedia, dialR -> {
                            if(dialR.isSuccessful())
                                toast("Ringing");
                            else dismissWithToast("Call Failed");
                        });
                    } else dismissWithToast("Device Registration Failed");
                });
            } else dismissWithToast("Token Auth Failed");
        });
    }




    protected void handleIntent() {
        Intent intent = getIntent();
        mToken = intent.getStringExtra(INTENT_JWT);
        mCallString = intent.getStringExtra(INTENT_CALLEE);
    }




    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showBusyIndicator(String title, String message) {
        mProgress = ProgressDialog.show(this, title, message);
    }

    public void dismissBusyIndicator() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    public void dismissWithToast(String message) {
        Log.i(CLASS_TAG, message);
        dismissBusyIndicator();
        toast(message);
    }
}
