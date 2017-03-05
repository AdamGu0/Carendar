package info.leiguo.healthmonitoring;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import info.leiguo.healthmonitoring.data.PatientContract;
import info.leiguo.healthmonitoring.data.PatientDbHelper;

;import static info.leiguo.healthmonitoring.data.PatientContract.PatientEntry.COLUMN_TIME_STEMP;
import static info.leiguo.healthmonitoring.data.PatientContract.PatientEntry.COLUMN_X_VALUE;
import static info.leiguo.healthmonitoring.data.PatientContract.PatientEntry.COLUMN_Y_VALUE;
import static info.leiguo.healthmonitoring.data.PatientContract.PatientEntry.COLUMN_Z_VALUE;

/**
 * All the code in this class are written by Group 15.
 * The only Activity to interact with user.
 * Created by Lei on 1/20/2017.
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private GraphView mGraphView;
    private float[] mValues;
    private boolean mRunning = true;
    private Handler mHandler = new Handler();
    private ReadDataRunnable mReadDataTask;
    // Used to control when will we add a large value to the array
    private int mRefreshDataTimes = 0;
    private final int INSERT_SUMMIT_INTERVAL = 8;
    private SQLiteDatabase mDb;
    private String mTableName = "a";
    private String mCreatedTableName = "";
    private EditText mPatientNameEditText;
    private EditText mPatientIdEditText;
    private EditText mPatientAgeEditText;
    private RadioButton mPatientSexRadioButton;
    private String CREATE_TABLE_SQL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.btn_run).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        break;
                    default:
                        break;
                }
            }
        });

        FrameLayout container = (FrameLayout)findViewById(R.id.container);
        mValues = new float[50];
        String[] horlabels = new String[]{"100", "200", "300", "400", "500"};
        String[] verlabels = new String[]{"100", "200", "300", "400", "500"};
        mGraphView = new GraphView(this, mValues, "Monitor--By Group 15", horlabels, verlabels, true);
        container.addView(mGraphView);

        PatientDbHelper dbHelper = new PatientDbHelper(this, getTableName());
        mDb = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDataService();
    }

    private String getTableName() {
        mPatientNameEditText = (EditText) findViewById(R.id.et_patientName);
        mPatientIdEditText = (EditText) findViewById(R.id.et_patientid);
        mPatientAgeEditText = (EditText) findViewById(R.id.et_age);
        RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mPatientSexRadioButton = (RadioButton) findViewById(sexRadioGroup.getCheckedRadioButtonId());

        mTableName = mPatientNameEditText.getText().toString() + "_" + mPatientIdEditText.getText().toString()
                + "_" + mPatientAgeEditText.getText().toString() + "_" + mPatientSexRadioButton.getText().toString();
        return mTableName;
    }


    private void createTable() {
        CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientContract.PatientEntry.COLUMN_TIME_STEMP + " REAL NOT NULL, " +
                COLUMN_X_VALUE + " REAL NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_Y_VALUE + " REAL NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_Z_VALUE + " REAL NOT NULL" +
                "); ";
        mDb.execSQL(CREATE_TABLE_SQL);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_run:
                onRunClicked();
                break;
            case R.id.btn_stop:
                onStopClicked();
                break;
            case R.id.btn_download:
                onDownloadClicked();
                break;
            case R.id.btn_upload:
                onUploadClicked();
                break;
            case R.id.btn_save:
                onSaveClicked();
                break;
            default:
        }
    }

    private void onSaveClicked(){
        createTableAndMarkTheName();
        beginDataService();
    }

    private void createTableAndMarkTheName(){
        // mTableName will be saved during createTable()
        createTable();
        mCreatedTableName = mTableName;
    }

    private void beginDataService(){
        // Stop the data service if it is running.
        stopDataService();
        // Start the data service
        Intent intent = new Intent(MyService.MY_ACTION);
        intent.setPackage(getPackageName());
        intent.putExtra(MyService.KEY_TABLE_NAME, mTableName);
        startService(intent);
    }

    private void stopDataService(){
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private void onRunClicked(){
        initializeData();
        getTableName();
        if(!mCreatedTableName.equals(mTableName)){
            // If the table is not created, create it now.
            createTableAndMarkTheName();
            beginDataService();
        }
    }

    private void onStopClicked(){
        clearView();
        mRunning = false;
    }

    private void onUploadClicked(){

    }

    private void onDownloadClicked() {
        String tableName = getTableName();
        Toast.makeText(this, "Downloading database: " + tableName, Toast.LENGTH_LONG).show();

        //download in background
//        new DownloadDBTask().execute("https://impact.asu.edu/CSE535Spring17Folder/" + tableName + ".db");
        new DownloadDBTask().execute("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
    }

    private class DownloadDBTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return download(params[0]);
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Download completed.", Toast.LENGTH_SHORT).show();
                //TODO .db文件已下载到data/data/info.leiguo.healthmonitoring/files/，还需读取并显示最后10秒的数据
            } else
                Toast.makeText(MainActivity.this, "Download failed, please try again.", Toast.LENGTH_LONG).show();
        }

        private boolean download(String s) {
            try {
                URL url = new URL(s);
                InputStream is = url.openStream();
                String filename = s.substring(s.lastIndexOf("/") + 1);
                OutputStream os = openFileOutput(filename, MODE_PRIVATE);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private void initializeData(){
        if (mRunning) {
//            mHandler.removeCallbacks(mTask);
            mHandler.removeCallbacks(mReadDataTask);
            mValues = new float[0];
            mRefreshDataTimes = 0;
        }
//        mTask = new MyRunnable();
        mRunning = true;
//        mHandler.post(mTask);
        // start read data task.
        mReadDataTask = new ReadDataRunnable();
        mHandler.post(mReadDataTask);
    }

//    private void refreshData(){
//        Random myRandom = new Random();
//        final int N = 50;
//        float[] temp = new float[N];
//        final int MIN_VALUE_SEED = 3;
//        final int MAX_VALUE_BASE = 9;
//        if(mValues == null || mValues.length == 0){
//            // Initializing
//            for(int i = 0; i < N - 1; i++){
//                temp[i] = myRandom.nextInt(MIN_VALUE_SEED);
//            }
//            temp[N - 1] = myRandom.nextInt(MIN_VALUE_SEED) + MAX_VALUE_BASE;
//        }else{
//            // The amount of data to be updated.
//            final int UPDATE_AMOUNT = 3;
//            for(int i = 0; i < N - UPDATE_AMOUNT; i++){
//                temp[i] = mValues[i + UPDATE_AMOUNT];
//            }
//            for(int i = N - UPDATE_AMOUNT; i< N - 1; i++){
//                temp[i] = myRandom.nextInt(MIN_VALUE_SEED);
//            }
//            if(mRefreshDataTimes % INSERT_SUMMIT_INTERVAL == 0){
//                temp[N - 1] = myRandom.nextInt(MIN_VALUE_SEED) + MAX_VALUE_BASE;
//                mRefreshDataTimes = 0;
//            }else{
//                temp[N - 1] = myRandom.nextInt(1);
//            }
//        }
//        mValues = temp;
//        mRefreshDataTimes++;
//    }

    private void clearView(){
        mGraphView.setValues(new float[0]);
        mGraphView.invalidate();
    }

    private void refreshView(){
//        refreshData();
        mGraphView.setValues(mValues);
        mGraphView.invalidate();
    }

//    private class MyRunnable implements Runnable{
//        @Override
//        public void run() {
//            if(mRunning){
//                refreshView();
//                mHandler.postDelayed(this, 150);
//            }
//        }
//    }

    private class ReadDataRunnable implements Runnable{
        @Override
        public void run() {
            if(mRunning){
//                new ReadDataThread(new ReadDataCallback() {
//                    @Override
//                    public void onReadDataFinish(ArrayList<SensorData> dataList) {
//                        updateData(dataList);
//                        refreshView();
//                        // read data from database every second
//                        mHandler.postDelayed(mReadDataTask, 1000);
//                    }
//                }).start();
                new ReadDataTask().execute();
            }
        }
    }

    private void updateData(ArrayList<SensorData> dataList){
        int length = dataList.size();
        float[] values = new float[length * 3];
        for(int i = 0; i < length; i++){
            SensorData data = dataList.get(0);
            Log.e("updateData", "X: " + data.x + "  y:" + data.y + "  z: " + data.z);
            values[i * 3 ] = (float) data.x;
            values[i * 3 + 1] = (float) data.y;
            values[i * 3 + 2] = (float) data.z;
        }
        mValues = values;
    }

    private static class SensorData{
        public double x;
        public double y;
        public double z;
    }

    private class ReadDataTask extends AsyncTask<Void, Void, ArrayList<SensorData>>{
        @Override
        protected ArrayList<SensorData> doInBackground(Void... params) {
            return readRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<SensorData> sensorDatas) {
//            super.onPostExecute(sensorDatas);
            updateData(sensorDatas);
            refreshView();
            // read data from database every second
            mHandler.postDelayed(mReadDataTask, 1000);
        }

        private  ArrayList<SensorData>  readRecords() {
            // Read the prior ten second data
            final int PRIOR_SECOND = 10;
            long now = System.currentTimeMillis();
            long timeBeginPoint = now - PRIOR_SECOND * 1000;
            Cursor cursor =  mDb.query(
                    mTableName,
                    new String[]{COLUMN_X_VALUE, COLUMN_Y_VALUE, COLUMN_Z_VALUE, COLUMN_TIME_STEMP},
                    COLUMN_TIME_STEMP + "> ?",
                    new String[]{String.valueOf(timeBeginPoint)},
                    null,
                    null,
                    COLUMN_TIME_STEMP
            );
            if(cursor != null){
                final int INIT_CAPACITY = PRIOR_SECOND + 5;
                ArrayList<SensorData> dataList = new ArrayList<>(INIT_CAPACITY);
                if(cursor.moveToFirst()){
                    do{
                        double x = cursor.getDouble(cursor.getColumnIndex(COLUMN_X_VALUE));
                        double y = cursor.getDouble(cursor.getColumnIndex(COLUMN_Y_VALUE));
                        double z = cursor.getDouble(cursor.getColumnIndex(COLUMN_Z_VALUE));
                        SensorData data = new SensorData();
                        data.x = x;
                        data.y = y;
                        data.z = z;
                        dataList.add(data);
                    }while (cursor.moveToNext());
                }
                cursor.close();
                return dataList;
            }
            return new ArrayList<>(0);
        }

    }

}
