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
import java.util.Iterator;

/**
 * Created by Lei on 4/15/2017.
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
            parseIcsData(mUri, mContext);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(mOnParseFinishListener != null){
            mOnParseFinishListener.onParseFinish();
        }
    }

    private void parseIcsData(Uri uri, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            CalendarBuilder builder = new CalendarBuilder();
            CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
            net.fortuna.ical4j.model.Calendar calendar = builder.build(is);
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext(); ) {
                Component component = (Component) i.next();
                System.out.println("Component [" + component.getName() + "]");

                for (Iterator j = component.getProperties().iterator(); j.hasNext(); ) {
                    Property property = (Property) j.next();
                    System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    public interface OnParseFinishListener {
        public void onParseFinish();
    }

}