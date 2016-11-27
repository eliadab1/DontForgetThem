package com.example.eliad.dontforgetthem;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistrationService";
    public String tokenOrRegistrationID;



    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            //get unique id from gcm by project id (SENDER_ID)
            tokenOrRegistrationID = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);



            //send regId to my server
            Log.d(TAG, "token: " + tokenOrRegistrationID);

            //save token in shared preferences
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
