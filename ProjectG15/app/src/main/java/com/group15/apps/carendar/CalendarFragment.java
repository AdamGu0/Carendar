package com.group15.apps.carendar;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

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
public class CalendarFragment extends Fragment  implements MonthLoader.MonthChangeListener, WeekView.EventClickListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private ArrayList<MyWeekViewEvent> mNewEvents;
    private MyWeekViewEvent myWeekViewEvent;
    private Map<Integer, List<MyWeekViewEvent>> mPersonalEventsMap = new HashMap<>();
    private int mEventType = 0; //0:all 1:personal 2:group

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
        mWeekView.setOnEventClickListener(this);
        mWeekView.goToToday();

        setupDateTimeInterpreter(true);

        mWeekView.setNumberOfVisibleDays(7);
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

        setupDateTimeInterpreter(false);

    }

    public void notifyChange() {
        if (mWeekView == null) return;
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

    private List<MyWeekViewEvent> getDataToDisPlay(int newMonth) {
        if (mPersonalEventsMap.isEmpty() || mPersonalEventsMap.get(newMonth - 1) == null) {
            return null;
        }

        List<MyWeekViewEvent> list = mPersonalEventsMap.get(newMonth - 1);

        if (mEventType == 0) return list; // no filter selected

        Boolean isGroup;
        if (mEventType == 1) isGroup = false; //personal filter selected
        else isGroup = true; //group filter selected

        List<MyWeekViewEvent> fList = new ArrayList<>();
        for ( MyWeekViewEvent e : list) {
            if (e.getIsGroupEvent() == isGroup) fList.add(e);
        }
        return fList;
    }

    public List<MyWeekViewEvent> onMonthChange(int newYear, int newMonth) {

        List<MyWeekViewEvent> list = getDataToDisPlay(newMonth - 1);
        if (list == null) {
            return new ArrayList<>();
        }
        Calendar calendar = Calendar.getInstance();
        if(newMonth == (calendar.get(Calendar.MONTH)+1))
            mWeekView.goToToday();

        return list;
    }

    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent intent = new Intent();
        intent.setClass(this.getContext(), ShowEventActivity.class);

        MyWeekViewEvent e = (MyWeekViewEvent) event;

        intent.putExtra("mStartTimeMills", e.getStartTimeMills());
        intent.putExtra("mEndTimeMills", e.getEndTimeMills());

        intent.putExtra("mLocation", e.getLocation());
        intent.putExtra("mTitle", e.getName());
        String key = e.getEventKey();
        intent.putExtra("EVENT_KEY", key);

        this.startActivity(intent);
    }

    public void setEventType(int p) {
        mEventType = p;
        notifyChange();
    }
}
