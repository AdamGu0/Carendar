package com.group15.apps.carendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Neo on 3/18/17.
 */

public class CalendarFragment extends Fragment  implements MonthLoader.MonthChangeListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private ArrayList<MyWeekViewEvent> mNewEvents;
    private MyWeekViewEvent myWeekViewEvent;
    private Map<Integer, List<MyWeekViewEvent>> mPersonalEventsMap = new HashMap<>();

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }


    public void updatePersonalEventMap(Map<Integer, List<MyWeekViewEvent>> map) {
        mPersonalEventsMap = map;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caldendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        mWeekView = (WeekView) v.findViewById(R.id.weekView);

        mWeekView.setMonthChangeListener(this);

        setupDateTimeInterpreter(true);

        mWeekView.setNumberOfVisibleDays(7);
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

        setupDateTimeInterpreter(false);
    }

    public void notifyChange() {
        mWeekView.notifyDatasetChanged();
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    private List<MyWeekViewEvent> getDatatoDisPlay(int newMonth) {
        if (mPersonalEventsMap.isEmpty() || mPersonalEventsMap.get(newMonth - 1) == null) {
            return null;
        }
        for (MyWeekViewEvent event : mPersonalEventsMap.get(newMonth - 1)) {
//            Log.v("test: title = ", event.getTitle());
//            Log.v("test: location = ", event.getLocation());
//            Log.v("test: month = ",  " " + event.getStartTime().get(Calendar.MONTH));
////            Log.v("test: Date = ",  " " + event.getStartTime().get(Calendar.DATE));
//            Log.v("test: Day of Month = ",  " " + event.getStartTime().get(Calendar.DAY_OF_MONTH));
//            Log.v("test: Hour = ",  " " + event.getStartTime().get(Calendar.HOUR_OF_DAY));


        }

        List<MyWeekViewEvent> data = mPersonalEventsMap.get(newMonth - 1);
        // TODO: test
        if(data != null && data.size() > 0){
            MyWeekViewEvent event = data.get(0);
            Calendar startTime = event.getStartTime();
            int hour = startTime.get(Calendar.HOUR_OF_DAY);
            Log.v("test: Start Hour = ",  " " + event.getStartTime().get(Calendar.HOUR_OF_DAY));
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            Calendar endTime = event.getEndTime();
            hour = endTime.get(Calendar.HOUR_OF_DAY);
            Log.v("test: End Hour = ",  " " + event.getEndTime().get(Calendar.HOUR_OF_DAY));
            endTime.set(Calendar.HOUR_OF_DAY, hour);
        }
//
//        return data;
        return data;
    }



    public List<MyWeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<MyWeekViewEvent> list = getDatatoDisPlay(newMonth - 1);
        if (list == null) {
            return new ArrayList<>();
        }
        for (MyWeekViewEvent event : list) {

        }        // Populate the week view with some events.
        return list;



    }
}
