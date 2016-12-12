package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WeatherFromMobileListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    public final String LOG_TAG = WeatherFromMobileListenerService.class.getSimpleName();

    public static final String WEATHER_DATA = "/weather-data";
    public static final String HIGH_TEMP = "high-temp";
    public static final String LOW_TEMP = "low-temp";

    GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(LOG_TAG, "DataMap item made it to watch: ");

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String mMaxTemp = dataMap.getString(HIGH_TEMP);
                    String mMinTemp = dataMap.getString(LOW_TEMP);
                    Intent sendWeatherInent = new Intent("ACTION_WEATHER_CHANGED");
                    sendWeatherInent.putExtra("high-temp", mMaxTemp)
                            .putExtra("low-temp", mMinTemp);
                    sendBroadcast(sendWeatherInent);
                    Log.i(LOG_TAG, "Nice here's the high temp" + mMaxTemp);
                }
            } else if (dataEvent.getType() == DataEvent.TYPE_DELETED) {}
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);

        Log.d(LOG_TAG, "Google API Client was connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "Google API Client was suspended");


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.connect();
        Log.d(LOG_TAG, "Google API Client failed");


    }


}
