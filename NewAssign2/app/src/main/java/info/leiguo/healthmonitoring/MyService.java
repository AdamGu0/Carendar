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
import android.util.Log;

import info.leiguo.healthmonitoring.data.PatientContract;
import info.leiguo.healthmonitoring.data.PatientDbHelper;

/**
 * Created by Lei on 3/4/2017.
 */

public class MyService extends Service implements SensorEventListener {
    public static final String KEY_TABLE_NAME = "table_name";
    public static final String MY_ACTION = "info.leiguo.healthmonitoring.MY_SERVICE";
    private static final long ACCE_FILTER_DATA_MIN_TIME = 1000;
    private SQLiteDatabase mDb;
    private float mTimeStamp;
    private float xValue;
    private float yValue;
    private float zValue;
    private String mTableName = "";
    private SensorManager mSensorManager;
    private  long mLastSaved = -1;
//    private AddRecordTask mAddRecordTask;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mAddRecordTask = new AddRecordTask();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTableName = intent.getStringExtra(KEY_TABLE_NAME);
        PatientDbHelper dbHelper = new PatientDbHelper(this, mTableName);
        mDb = dbHelper.getWritableDatabase();
        registerSensorListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSensorListener();
    }

    private void registerSensorListener(){
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListener(){
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if ((System.currentTimeMillis() - mLastSaved) >= ACCE_FILTER_DATA_MIN_TIME) {
                xValue = sensorEvent.values[0];
                yValue = sensorEvent.values[1];
                zValue = sensorEvent.values[2];
                mTimeStamp = System.currentTimeMillis();
                unregisterSensorListener();
                // Add new record on background thread.
                new AddRecordTask().execute();
//                addNewRecords();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private long addNewRecords() {
        ContentValues cv = new ContentValues();
        cv.put(PatientContract.PatientEntry.COLUMN_TIME_STEMP, mTimeStamp);
        cv.put(PatientContract.PatientEntry.COLUMN_X_VALUE, xValue);
        cv.put(PatientContract.PatientEntry.COLUMN_Y_VALUE, yValue);
        cv.put(PatientContract.PatientEntry.COLUMN_Z_VALUE, zValue);
        return mDb.insert(mTableName, null, cv);
    }

    private class AddRecordTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            addNewRecords();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            registerSensorListener();
            Log.e("MyService", "Insert one row.");
        }
    }
}
