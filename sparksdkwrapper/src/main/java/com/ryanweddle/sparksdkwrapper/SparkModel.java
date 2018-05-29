package com.ryanweddle.sparksdkwrapper;

import android.app.Application;
import android.util.Log;

import com.ciscospark.androidsdk.CompletionHandler;
import com.ciscospark.androidsdk.Spark;
import com.ciscospark.androidsdk.SparkError;
import com.ciscospark.androidsdk.auth.Authenticator;
import com.ciscospark.androidsdk.auth.JWTAuthenticator;
import com.ciscospark.androidsdk.phone.Call;
import com.ciscospark.androidsdk.phone.CallObserver;
import com.ciscospark.androidsdk.phone.MediaOption;
import com.ciscospark.androidsdk.phone.Phone;

/**
 * Created by ryweddle on 3/10/18.
 */

class SparkModel implements CallObserver {

    public static final String CLASS_TAG = "SparkModel";


    private static final SparkModel ourInstance = new SparkModel();

    private Spark mSpark;
    private Phone mPhone;
    private Authenticator mAuthenticator;
    private Application mApp;
    private Call mActiveCall;
    private CallObserver mObserver;

    private boolean mRegState = false;
    private boolean mFrontCam = true;



    static SparkModel getInstance() {
        return ourInstance;
    }

    private SparkModel() {
    }


    public Call getActiveCall() {
        return mActiveCall;
    }


    public void setObserver(CallObserver observer) {
        if(observer == this)
            clearObserver();
        else
            mObserver = observer;
    }

    public void clearObserver() {
        mObserver = null;
    }

    public void setApp(Application app) {
        mApp = app;
    }


    public void dial(String callee, MediaOption media, CompletionHandler<Call> callback) {
        if(!isInitSpark())
            initSpark();

        if(mPhone == null)
            mPhone = mSpark.phone();

        mPhone.dial(callee, media, result -> {
            if (result.isSuccessful()) {
                Log.i(CLASS_TAG, "Spark dial SUCCESSFUL");
                mActiveCall = result.getData();
                mActiveCall.setObserver(this);
            }
            else {
                Log.i(CLASS_TAG, "Spark dial UNSUCCESSFUL");
                SparkError e = result.getError();
                Log.e(CLASS_TAG, "SPARKERROR: " + e.toString());
            }
            callback.onComplete(result);

        });

    }

    public void hangup(CompletionHandler<Void> callback) {
        if (mActiveCall != null) {
            mActiveCall.hangup(result -> {
                if(result.isSuccessful()) {
                    Log.i(CLASS_TAG, "Spark hangup successful");

                }
                else {
                    Log.i(CLASS_TAG, "Spark hangup unsuccessful");
                    SparkError e = result.getError();
                    Log.e(CLASS_TAG, "SPARKERROR: " + e.toString());
                }
                callback.onComplete(result);
            });
            mActiveCall = null;
        }
    }

    public void register(CompletionHandler<Void> callback) {

        if(!isInitSpark())
            initSpark();

        mPhone = mSpark.phone();
        mPhone.register(r-> {
            if(r.isSuccessful()) {
                Log.i(CLASS_TAG, "phone registration successful");
                mRegState = true;
            } else {
                Log.i(CLASS_TAG, "phone registration failed");
                mRegState = false;
            }
            callback.onComplete(r);
        });
    }

    public void deRegister(CompletionHandler<Void> callback) {
        if(mRegState) {
            mPhone.deregister(r -> {
                if(r.isSuccessful()) {
                    Log.i(CLASS_TAG, "phone deregistration successful");
                } else {
                    Log.i(CLASS_TAG, "phone deregistration failed");
                }
                callback.onComplete(r);
            });
        }
    }

    private void initSpark() {
        if(mAuthenticator == null || mApp == null) {
            Log.e(CLASS_TAG, "initSpark called without Authenticator or App");
        } else {
            mSpark = new Spark(mApp, mAuthenticator);
        }
    }

    private boolean isInitSpark() {
        return mSpark != null;
    }



    public boolean isRegistered() {
        return mRegState;
    }

    public boolean isAuthenticated() {
        if(mAuthenticator != null)
            return mAuthenticator.isAuthorized();
        else
            return false;
    }

    public boolean isInCall() {
        return mActiveCall != null;
    }

    public boolean isSendingAudio() {
        if(mActiveCall == null) {
            return false;
        } else {
            return mActiveCall.isSendingAudio();
        }
    }

    public void setSendingAudio(boolean sending) {
        if(mActiveCall != null) {
            mActiveCall.setSendingAudio(sending);
        }
    }

    public void setCameraFacing(boolean frontCam) {
        if(frontCam)
            setCamUser();
        else
            setCamBack();
    }

    public boolean getCameraFacing() {
        return mFrontCam;
    }

    public void setCamUser() {
        mFrontCam = true;
        if (mActiveCall != null)
            mActiveCall.setFacingMode(Phone.FacingMode.USER);
    }

    public void setCamBack() {
        mFrontCam = false;
        if(mActiveCall != null)
            mActiveCall.setFacingMode(Phone.FacingMode.ENVIROMENT);
    }



    public void acknowledge(CompletionHandler<Void> callback) {
        if(mActiveCall != null) {
            mActiveCall.acknowledge(callback);
        }
    }

    public void authenticateJWT(String token, CompletionHandler<String> callback) {

        JWTAuthenticator jwta = new JWTAuthenticator();
        jwta.authorize(token);
        jwta.getToken(r -> {
            if(r.isSuccessful()) {
                Log.i(CLASS_TAG, "successful token auth");
                mAuthenticator = jwta;
            }
            else
                Log.i(CLASS_TAG, "failed token auth");
            callback.onComplete(r);
        });
    }

    public void authenticateJWT(String token) {
        authenticateJWT(token, r->{});
    }



    /*
     * Methods for CallObserver
     *  take locally required actions and delegate call to observer if set
     */

    @Override
    public void onRinging(Call call) {
        Log.i(CLASS_TAG, "onRinging called");
        if(mObserver != null)
            mObserver.onRinging(call);
    }

    @Override
    public void onConnected(Call call) {
        Log.i(CLASS_TAG, "onConnected called");
        if(mObserver != null)
            mObserver.onConnected(call);
    }

    @Override
    public void onDisconnected(CallDisconnectedEvent event) {
        Log.i(CLASS_TAG, "onDisconnected called");
        if(mObserver != null)
            mObserver.onDisconnected(event);
    }

    @Override
    public void onMediaChanged(MediaChangedEvent event) {
        Log.i(CLASS_TAG, "onMediaChanged called");
        if(mObserver != null)
            mObserver.onMediaChanged(event);
    }

    @Override
    public void onCallMembershipChanged(CallMembershipChangedEvent event) {
        Log.i(CLASS_TAG, "onCallMembershipsChanged called");
        if(mObserver != null)
            mObserver.onCallMembershipChanged(event);
    }
}
