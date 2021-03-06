package com.example.android.sunshine.app.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.sunshine.app.MainActivity;
import com.example.android.sunshine.app.Utility;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

/**
 * Created by nickbradshaw on 8/3/16.
 */
public class WeatherMuzeiSource extends MuzeiArtSource {


    private static final String[] FORECAST_COLUMNS = new String[]{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;


    public WeatherMuzeiSource() {
        super("WeatherMuzeiSource");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        boolean dataUpdated = intent != null &&
                SunshineSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction());
        if (dataUpdated && isEnabled()){
            onUpdate(UPDATE_REASON_OTHER);
        }
    }

    @Override
    protected void onUpdate(int reason) {

        String locationQuery = Utility.getPreferredLocation(this);
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                locationQuery, System.currentTimeMillis());

        Cursor cursor = getContentResolver().query(weatherUri, FORECAST_COLUMNS, null, null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        if (cursor.moveToFirst()) {
            int weatherId = cursor.getInt(INDEX_WEATHER_ID);
            String desc = cursor.getString(INDEX_SHORT_DESC);

            String imgUrl = Utility.getImageUrlForWeatherCondition(weatherId);
            if (imgUrl != null) {
                // Only publish a new wallpaper if we have a valid image
                publishArtwork(new Artwork.Builder()
                        .imageUri(Uri.parse(imgUrl))
                        .title(desc)
                        .byline(locationQuery)
                        .viewIntent(new Intent(this, MainActivity.class))
                        .build());


            }
        }
        cursor.close();
    }


}