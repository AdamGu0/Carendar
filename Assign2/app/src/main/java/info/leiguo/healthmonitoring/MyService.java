package info.leiguo.healthmonitoring;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import info.leiguo.healthmonitoring.data.PatientContract;
import info.leiguo.healthmonitoring.data.PatientDbHelper;

/**
 * Created by Lei on 3/4/2017.
 */

public class MyService extends Service implements SensorEventListener {
    public static final String KEY_TABLE_NAME = "table_name";
    private SQLiteDatabase mDb;
    private int sampleRate = 1000000;
    private float mTimeStamp;
    private float xValue;
    private float yValue;
    private float zValue;
    private String mTableName = "";

    @Override
    public void onCreate() {
        super.onCreate();
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

    private void registerSensorListener(){
        SensorManager mSensorManager;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sampleRate);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xValue = sensorEvent.values[0];
            yValue = sensorEvent.values[1];
            zValue = sensorEvent.values[2];
            mTimeStamp = System.currentTimeMillis();
            addNewRecords();
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
}
