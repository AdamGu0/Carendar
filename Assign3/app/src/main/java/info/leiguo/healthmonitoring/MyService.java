package info.leiguo.healthmonitoring;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import info.leiguo.healthmonitoring.data.ActivityData;
import info.leiguo.healthmonitoring.data.PointData;
import info.leiguo.healthmonitoring.database.DBAccess;
import info.leiguo.healthmonitoring.database.PatientContract;
import info.leiguo.healthmonitoring.database.PatientDbHelper;

/**
 * Created by Lei on 3/4/2017.
 */

public class MyService extends Service implements SensorEventListener {
    public static final String KEY_TABLE_NAME = "table_name";
    public static final String KEY_ACTIVITY_TYPE = "activity_type";
    public static final String MY_ACTION = "info.leiguo.healthmonitoring.MY_SERVICE";
    //    private static final long ACCE_FILTER_DATA_MIN_TIME = 1000;
    // There is no way to make sure that the sampling rate is 1Hz which means one record per second.
    private static final int SAMPLING_PERIOD = 100000; // Micro second
    private DBAccess mDBAccess;
    private String mTableName = "";
    private int mActivityType = 0;
    private SensorManager mSensorManager;
    private long mStartTime = -1;
    private long mLastTime = -1;
    private List<PointData> dataList = new ArrayList<>();
    private final int POINTS_COUNT_IN_AN_ACTIVITY = 50;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTableName = intent.getStringExtra(KEY_TABLE_NAME);
        mActivityType = intent.getIntExtra(KEY_ACTIVITY_TYPE, 0);
        mDBAccess = new DBAccess(getApplicationContext());
        mStartTime = System.currentTimeMillis();
        dataList = new ArrayList<>();
        registerSensorListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensorListener();
    }

    private void registerSensorListener() {
        // TODO: fix the sampling rate 10 Hz
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SAMPLING_PERIOD);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            PointData data = new PointData();
            data.x = sensorEvent.values[0];
            data.y = sensorEvent.values[1];
            data.z = sensorEvent.values[2];
            dataList.add(data);
            long now = System.currentTimeMillis();
            // TODO: Can we fix the samping rate?
            if(dataList.size() >= POINTS_COUNT_IN_AN_ACTIVITY && now - mLastTime >= 5000){
                long timeStamp = System.currentTimeMillis();
                new AddRecordTask(dataList, timeStamp, mActivityType).execute();
                dataList = new ArrayList<>();
                mLastTime = timeStamp;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private class AddRecordTask extends AsyncTask<Void, Void, Void> {
        private List<PointData> dataList;
        private long timeStamp;
        private int activityType;

        AddRecordTask(List<PointData> dataList, long timeStamp, int activityType) {
            this.dataList = dataList;
            this.timeStamp = timeStamp;
            this.activityType = activityType;
        }

        @Override
        protected Void doInBackground(Void... params) {
            addNewRecords();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        private void addNewRecords() {
            String data = new ActivityData(dataList).toString();
            mDBAccess.addNewRecords(timeStamp, data, mActivityType);
        }
    }
}
