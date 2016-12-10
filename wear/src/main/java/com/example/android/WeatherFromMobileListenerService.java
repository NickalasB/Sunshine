package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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

    private String mMinTemp = "";
    private String mMaxTemp = "";

    GoogleApiClient mGoogleApiClient;


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.v(LOG_TAG, "DataMap item made it to watch: ");

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                if (item.getUri().getPath().compareTo(WEATHER_DATA) == 0) {

                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    Log.i(LOG_TAG, "Nice here's the high temp" + dataMap.getString(HIGH_TEMP));
                    mMaxTemp = dataMap.getString(HIGH_TEMP);
                    mMinTemp = dataMap.getString(LOW_TEMP);

                    Toast.makeText(this, "High temp from data = " + mMaxTemp, Toast.LENGTH_SHORT).show();

                    Intent sendWeatherInent = new Intent("ACTION_WEATHER_CHANGED");
                    sendWeatherInent.putExtra("high-temp", HIGH_TEMP)
                            .putExtra("low-temp", LOW_TEMP);
                    sendBroadcast(sendWeatherInent);

                    }
                } else if (dataEvent.getType() == DataEvent.TYPE_DELETED) {

                }
            }

        }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "Google API Client was connected");

        Wearable.DataApi.addListener(mGoogleApiClient, this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.connect();

    }


}
