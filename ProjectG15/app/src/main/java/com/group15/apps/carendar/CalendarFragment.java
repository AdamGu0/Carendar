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
    private Map<Integer, List<MyWeekViewEvent>> mPersonalEventsList = new HashMap<>();

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }


    public void updatePersonalEventList(Map<Integer, List<MyWeekViewEvent>> list) {
        mPersonalEventsList = list;
        Log.v("test", " " + mPersonalEventsList.isEmpty());
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
        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) v.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
//        mWeekView.setOnEventClickListener(mEventClickListener);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);
//        mNewEvents = new ArrayList<WeekViewEvent>();

        // Set long press listener for events.
//        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
//        mWeekView.setEmptyViewLongPressListener(this);
        setupDateTimeInterpreter(true);

        mWeekView.setNumberOfVisibleDays(7);
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

//        mFloatingActionButton = (ImageButton) v.findViewById(R.id.fab_add);
//        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AddEventActivity.class);
//                startActivity(intent);
//
//            }
//        });


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
        if (mPersonalEventsList.isEmpty() || mPersonalEventsList.get(newMonth - 1) == null) {
            return null;
        }
        for (MyWeekViewEvent event : mPersonalEventsList.get(newMonth - 1)) {
            Log.v("test: title = ", event.getTitle());
            Log.v("test: location = ", event.getLocation());
            Log.v("test: month = ",  " " + event.getStartTime().get(Calendar.MONTH));
        }

        return mPersonalEventsList.get(newMonth - 1);
    }



    public List<MyWeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<MyWeekViewEvent> list = getDatatoDisPlay(newMonth - 1);
        if (list == null) {
            return new ArrayList<>();
        }
        for (MyWeekViewEvent event : list) {
            Log.v("test: title = ", event.getTitle());
            Log.v("test: location = ", event.getLocation());
            Log.v("test: month = ",  " " + event.getStartTime().get(Calendar.MONTH));
        }        // Populate the week view with some events.
//        return getDatatoDisPlay(newMonth - 1);
        return list;
//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR, 1);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        WeekViewEvent event = new MyWeekViewEvent("1", "kkk", startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//
//        events.add(event);


    }
}
