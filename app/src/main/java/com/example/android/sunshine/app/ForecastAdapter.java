package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
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
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private Cursor mCursor;
    final private Context mContext;

    


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mIconView;
        public final TextView mdateView;
        public final TextView mforecastView;
        public final TextView mhighView;
        public final TextView mlowView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mdateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mforecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mhighView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mlowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    /**
     * Cache of the children views for a forecast list item.
     */
//    public static class WeatherViewHolder {
//        public final ImageView miconView;
//        public final TextView mdateView;
//        public final TextView mforecastView;
//        public final TextView mhighView;
//        public final TextView mlowView;
//
//        public WeatherViewHolder(RecyclerView recyclerView) {
//            miconView = (ImageView) recyclerView.findViewById(R.id.list_item_icon);
//            mdateView = (TextView) recyclerView.findViewById(R.id.list_item_date_textview);
//            mforecastView = (TextView) recyclerView.findViewById(R.id.list_item_forecast_textview);
//            mhighView = (TextView) recyclerView.findViewById(R.id.list_item_high_textview);
//            mlowView = (TextView) recyclerView.findViewById(R.id.list_item_low_textview);
//        }
//    }
    public ForecastAdapter(Context context) {
        mContext = context;

    }

        /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.
        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.
     */
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //chose the layout type
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerViewSelection");
        }

    }

//    /*
//    Remember that these views are reused as needed.
// */
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        //chose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
//        int layoutId = -1;
//        switch (viewType) {
//            case VIEW_TYPE_TODAY: {
//                layoutId = R.layout.list_item_forecast_today;
//                break;
//            }
//            case VIEW_TYPE_FUTURE_DAY: {
//                layoutId = R.layout.list_item_forecast;
//                break;
//            }
//        }
//        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(context).inflate(layoutId, parent, false);
//        WeatherViewHolder viewHolder = new WeatherViewHolder(recyclerView);
//        recyclerView.setTag(viewHolder);
//
//        return recyclerView;
//    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {

        mCursor.moveToPosition(position);
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int defaultImage;

        switch (getItemViewType(position)) {
            case VIEW_TYPE_TODAY:
                defaultImage = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = Utility.getIconResourceForWeatherCondition(weatherId);
        }

        if ( Utility.usingLocalGraphics(mContext) ) {
            forecastAdapterViewHolder.mIconView.setImageResource(defaultImage);
        } else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(forecastAdapterViewHolder.mIconView);
        }


        //read date info from cursor
        long dateInMillis = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        forecastAdapterViewHolder.mdateView.setText(Utility.getFriendlyDayString(mContext, dateInMillis));

        // Read weather forecast from cursor
        String weatherForecst = mCursor.getString(ForecastFragment.COL_WEATHER_DESC);
        forecastAdapterViewHolder.mforecastView.setText(weatherForecst);

        //for accessibilty, add a content description to the icon field
        forecastAdapterViewHolder.mIconView.setContentDescription(weatherForecst);


        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(mContext);

        // Read high temperature from cursor
        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        forecastAdapterViewHolder.mhighView.setText(Utility.formatTemperature(mContext, high, isMetric));

        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        forecastAdapterViewHolder.mlowView.setText(Utility.formatTemperature(mContext, low, isMetric));
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }

}

