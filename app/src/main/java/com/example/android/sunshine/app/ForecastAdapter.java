package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private boolean mUseTodayLayout = true;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class WeatherViewHolder {
        public final ImageView miconView;
        public final TextView mdateView;
        public final TextView mforecastView;
        public final TextView mhighView;
        public final TextView mlowView;

        public WeatherViewHolder(View view) {
            miconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mdateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mforecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mhighView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mlowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }



    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
    Remember that these views are reused as needed.
 */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //chose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_TODAY:{
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY:{
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        WeatherViewHolder weatherViewHolder = (WeatherViewHolder) view.getTag();

        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        int viewType = getItemViewType(cursor.getPosition());
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        int defaultImage;
        switch (getItemViewType(cursor.getPosition())){
            case VIEW_TYPE_TODAY:
                defaultImage = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        if (Utility.usingLocalGraphics(mContext)){
            weatherViewHolder.miconView.setImageResource(defaultImage);
        }else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(weatherViewHolder.miconView);
        }


//        int fallbackIconId;
//        switch (viewType) {
//            case VIEW_TYPE_TODAY: {
//                // Get weather icon
//                fallbackIconId = Utility.getArtResourceForWeatherCondition(
//                        weatherId);
//                break;
//            }
//            default: {
//                // Get weather icon
//                fallbackIconId = Utility.getIconResourceForWeatherCondition(
//                        weatherId);
//                break;
//            }
//        }
//
//        Glide.with(mContext)
//                .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
//                .error(fallbackIconId)
//                .crossFade()
//                .into(weatherViewHolder.miconView);


        //read date info from cursor
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        weatherViewHolder.mdateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        // Read weather forecast from cursor
        String weatherForecst = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        weatherViewHolder.mforecastView.setText(weatherForecst);

        //for accessibilty, add a content description to the icon field
        weatherViewHolder.miconView.setContentDescription(weatherForecst);


        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        weatherViewHolder.mhighView.setText(Utility.formatTemperature(context, high, isMetric));

        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        weatherViewHolder.mlowView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType (int position){
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;
    }

}

