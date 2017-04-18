package com.group15.apps.carendar;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lei
 */

public class ExportCalendarTask extends AsyncTask {
    private OnExportFinishListener mOnExportFinishListener;
    private Context mContext;
    private Map<Integer, List<MyWeekViewEvent>> mEventsMap;

    public ExportCalendarTask(Context context, Map<Integer, List<MyWeekViewEvent>> eventsMap, OnExportFinishListener listener) {
        mContext = context.getApplicationContext();
        mOnExportFinishListener = listener;
        mEventsMap = deepCopy(eventsMap);
    }

    private Map<Integer, List<MyWeekViewEvent>> deepCopy(Map<Integer, List<MyWeekViewEvent>> eventsMap){
        if(eventsMap != null && eventsMap.size() > 0){
            Map<Integer, List<MyWeekViewEvent>> newEventsMap = new HashMap<>();
            for (Map.Entry<Integer, List<MyWeekViewEvent>> entry : eventsMap.entrySet()){
                newEventsMap.put(entry.getKey(), new ArrayList<MyWeekViewEvent>(entry.getValue()));
            }
            return newEventsMap;
        }
        return new HashMap<>(0);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        exportCalendar(mEventsMap);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(mOnExportFinishListener != null){
            mOnExportFinishListener.onExportFinish();
        }
    }

    private void exportCalendar(Map<Integer, List<MyWeekViewEvent>> eventsMap){
        if(eventsMap != null && eventsMap.size() > 0){
            Set<Integer> keys = eventsMap.keySet();
            Calendar icsCalendar = new Calendar();
            icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
            icsCalendar.getProperties().add(Version.VERSION_2_0);
            icsCalendar.getProperties().add(CalScale.GREGORIAN);
            for (Integer key : keys){
                List<MyWeekViewEvent> events = eventsMap.get(key);
                createEvents(events, icsCalendar);
            }
            outputToFile(icsCalendar);
        }
    }

    private void outputToFile(Calendar icsCalendar){
        FileOutputStream fout = null;
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            fout = new FileOutputStream(new File(path, "mycalendar.ics"));
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fout != null){
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void createEvents(List<MyWeekViewEvent> events, Calendar icsCalendar){
        if(events != null && events.size() > 0){
            for(MyWeekViewEvent event : events){
                try {
                    VEvent newEvent = Utils.weekViewEvent2IcsEvent(event);
                    icsCalendar.getComponents().add(newEvent);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public interface OnExportFinishListener{
        public void onExportFinish();
    }
}
