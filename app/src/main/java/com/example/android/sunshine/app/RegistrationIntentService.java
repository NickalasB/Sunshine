package com.example.android.sunshine.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by nickbradshaw on 7/2/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            //in the (unlikely) event that multiple refresh operations occur simultaneousl
            //ensure that they are processed sequentially.
            synchronized (TAG) {
                //Initially this call goes out to the network to retrieve the token, subsequent calls
                //are local.
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                sendRegistrationToServer(token);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);

            //If an exception happens while fetching the new token or updating tour registration data
            //on a third-party server, this ensures that we'll attemot to update at a later time.
            sharedPreferences.edit().putBoolean(MainActivity.SENT_TOKEN_TO_SERVER, false).apply();
        }

    }

    /**
     * Normally, you would want to persist the registration to third-party servers. Because we do
     * not have a server, and are faking it with a website, you'll want to log the token instead.
     * That way you can see the value in logcat, and note it for future use in the website.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token: " + token);
    }
}
