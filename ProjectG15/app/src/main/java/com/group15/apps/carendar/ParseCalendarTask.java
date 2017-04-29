package com.group15.apps.carendar;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lei
 */

public class ParseCalendarTask extends AsyncTask {
    private OnParseFinishListener mOnParseFinishListener;
    private Uri mUri;
    private Context mContext;

    public ParseCalendarTask(Uri uri, Context context, OnParseFinishListener listener){
        mUri = uri;
        mOnParseFinishListener = listener;
        if(context != null){
            mContext = context.getApplicationContext();
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        if(mUri != null && mContext != null){
            return parseIcsData(mUri, mContext);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object obj) {
        if(mOnParseFinishListener != null){
            List<MyWeekViewEvent> eventList = (List<MyWeekViewEvent>)obj;
            mOnParseFinishListener.onParseFinish(eventList);
        }
    }

    private List<MyWeekViewEvent> parseIcsData(Uri uri, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            CalendarBuilder builder = new CalendarBuilder();
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            net.fortuna.ical4j.model.Calendar calendar = builder.build(is);
            List<MyWeekViewEvent> eventList = new ArrayList<>();
            // reference: http://ical4j.sourceforge.net/introduction.html
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext(); ) {
                Component component = (Component) i.next();
                System.out.println("Component [" + component.getName() + "]");
                MyWeekViewEvent event = Utils.property2WeekViewEvent(component.getProperties());
                eventList.add(event);
                for (Iterator j = component.getProperties().iterator(); j.hasNext(); ) {
                    Property property = (Property) j.next();
                    System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                }
            }
            return eventList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }

    public interface OnParseFinishListener {
        public void onParseFinish(List<MyWeekViewEvent> eventList);
    }

}
