package com.group15.apps.carendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Lei
 */

public class ReminderService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        List<ReminderEvent> eventList = intent.getParcelableArrayListExtra("events");

        return result;
    }


    private void silentPhone() {
        AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //For Normal mode
//        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //For Silent mode
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //For Vibrate mode
//        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

}
