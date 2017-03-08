package info.leiguo.healthmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
    private final String SERVER_URL = "https://impact.asu.edu/CSE535Spring17Folder/";
    private final String UPLOAD_URL = SERVER_URL + "UploadToServer.php";
    private GraphView mGraphView;
    private float[] mValues;
    private boolean mRunning = true;
    private Handler mHandler = new Handler();
    private ReadDataRunnable mReadDataTask;
    private SQLiteDatabase mDb;
    private SQLiteDatabase mDownloadDb;
    private String mTableName = "a";
    private String mCreatedTableName = "";
    private EditText mPatientNameEditText;
    private EditText mPatientIdEditText;
    private EditText mPatientAgeEditText;
    private RadioButton mPatientSexRadioButton;
    private String CREATE_TABLE_SQL;
    private boolean mIsDownloading = false;
    private boolean mIsUploading = false;
    private AlertDialog mAlertDialog;
    private boolean mIsPatientNameValid = true;

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
        mPatientIdEditText = (EditText) findViewById(R.id.et_patientid);
        mPatientAgeEditText = (EditText) findViewById(R.id.et_age);
        RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mPatientSexRadioButton = (RadioButton) findViewById(sexRadioGroup.getCheckedRadioButtonId());
        mPatientNameEditText = (EditText) findViewById(R.id.et_patientName);
        // The patient name should not begin with a digit because the table name should not
        // start with a digit.
        setTextWatcherForName();
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
        mRunning = false;
    }

    private String getTableName(){
        mTableName = mPatientNameEditText.getText().toString() + "_" + mPatientIdEditText.getText().toString()
                + "_" + mPatientAgeEditText.getText().toString() + "_" + mPatientSexRadioButton.getText().toString();
        return mTableName;
    }

    private void setTextWatcherForName(){
        mPatientNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // The patient name cannot start with a digit because the patient name will be the begging part of
                // the sqlite table name which cannot begin with a digit.
                if(s != null && s.length() > 0 && Character.isDigit(s.charAt(0))){
                    mIsPatientNameValid = false;
                    longToast("The patient name should NOT begin with a digit or you cannot create the patient information table.");
                }else{
                    mIsPatientNameValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void createTable() {
        CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientContract.PatientEntry.COLUMN_TIME_STEMP + " INTEGER NOT NULL, " +
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
        if(!mIsPatientNameValid){
            longToast("Invalid patient name--the patient name should not begin with a digit!");
            return;
        }
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
        if(!mIsPatientNameValid){
            longToast("Invalid patient name--the patient name should not begin with a digit!");
            return;
        }
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
        if(mIsUploading){
            return;
        }
        if(!Utils.checkNetwork(this)){
            shortToast("No network.");
            return;
        }
        if(!isDatabaseFileExists()){
            longToast("Database is not created yet, please press \"SAVE INFOMATION\" or \"RUN\" button to create database.");
            return;
        }
        mIsUploading = true;
        new UploadDBTask().execute(UPLOAD_URL);
    }

    private boolean isDatabaseFileExists(){
        File file = new File(getDatabaseFilePath());
        if(file.exists()){
            Log.d("MainActivity", "file exists: " + getDatabaseFilePath());
            return true;
        }else{
            Log.d("MainActivity", "file NOT exists");
            return false;
        }
    }

    private void onDownloadClicked() {
        if(mIsDownloading){
            return;
        }
        if(!Utils.checkNetwork(this)){
            shortToast("No network.");
            return;
        }
        mIsDownloading = true;
        final String dbName = PatientDbHelper.DATABASE_NAME;
        //download in background
        new DownloadDBTask().execute(SERVER_URL + dbName);
    }

    private void showLoadingDialog(String msg){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setMessage(msg);
        mAlertDialog = adBuilder.create();
        mAlertDialog.show();
    }

    private void hideLoadingDialog(){
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }else{
            Log.e("MaintActivity", "hideLoadingDialog null reference.");
        }
    }

    private class UploadDBTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected void onPreExecute() {
            showLoadingDialog("Database is uploading, please wait...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            boolean result = false;
            try{
                result = uploadDb(url);
            }catch (IOException e){
                e.printStackTrace();
            }catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            }catch(KeyManagementException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mIsUploading = false;
            hideLoadingDialog();
            if(result){
                shortToast("Uploading success.");
            }else {
                longToast("Uploading failed, please try again.");
            }
        }

        private boolean uploadDb(String urlStr) throws NoSuchAlgorithmException, KeyManagementException, IOException{
            final String boundary =  "*****";
            Utils.trustAllServer();
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, boundary);
            DataOutputStream upStream = new DataOutputStream(
                    connection.getOutputStream());
            writeContentStartBoundary(upStream, boundary);
            // write database file
            writeContent(upStream);
            writeContentEndBoundary(upStream, boundary);
            upStream.flush();
            upStream.close();
            final int status = connection.getResponseCode();
//            String response = getResponse(connection);
//            Log.e("uploadDb", "The response content: " + response);
            if (status != HttpURLConnection.HTTP_OK) {
                Log.e("uploadDb", "Failed with http status: " + status);
                return false;
            }else{
                return true;
            }
        }

        private void setupConnection(HttpURLConnection connection, String boundary) throws ProtocolException{
            final int connectTimeout = 10000;
            final int readTimeout = 10000;
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
        }

        private void writeContent(DataOutputStream upStream) throws IOException{
            FileInputStream fis = new FileInputStream(getDatabaseFilePath());
            byte[] buffer = new byte[1024];
            int count = -1;
            while((count = fis.read(buffer)) > 0){
                upStream.write(buffer, 0, count);
            }
        }

        private void writeContentStartBoundary(DataOutputStream upStream, String boundary) throws IOException{
            final String attachmentName = "uploaded_file";
            final String attachmentFileName = PatientDbHelper.DATABASE_NAME;
            upStream.writeBytes("--" + boundary + "\r\n");
            upStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"\r\n");
            upStream.writeBytes("\r\n");
        }

        private void writeContentEndBoundary(DataOutputStream upStream, String boundary) throws IOException{
            upStream.writeBytes("\r\n");
            upStream.writeBytes("--" + boundary + "--\r\n");;
        }

//        private String getResponse(HttpURLConnection connection)throws IOException{
//            InputStream responseStream = new
//                    BufferedInputStream(connection.getInputStream());
//            BufferedReader responseStreamReader =
//                    new BufferedReader(new InputStreamReader(responseStream));
//            String line = "";
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = responseStreamReader.readLine()) != null) {
//                stringBuilder.append(line).append("\n");
//            }
//            responseStreamReader.close();
//            String response = stringBuilder.toString();
//            return response;
//        }

    }

    private String getDatabaseFilePath(){
        return  getAppFolder() + "/databases/" + PatientDbHelper.DATABASE_NAME;
    }

    private String getAppFolder(){
        return "/data/data/" + getPackageName();
    }

    private String getFilesFolder(){
        return getAppFolder() + "/files/";
    }

    private void shortToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void longToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showData(){
        String filePath = getFilesFolder() + PatientDbHelper.DATABASE_NAME;
        try{
            mDownloadDb = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteException e){
            e.printStackTrace();
            return;
        }
        plotDownloadData();
    }

    private void plotDownloadData(){
        if(mDownloadDb != null){
            new PlotDownloadedDataTask().execute();
        }
    }

    private class DownloadDBTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog("Database is downloading, please wait...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            return downloadDb(url);
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                shortToast("Download completed.");
                showData();
            } else {
                longToast("Download failed, please try again.");
            }
            mIsDownloading = false;
            hideLoadingDialog();
        }

        private boolean downloadDb(String urlStr) {
            try {
                Utils.trustAllServer();
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();
//                File dir = new File(getFilesFolder());

                File file = new File(getFilesFolder(), PatientDbHelper.DATABASE_NAME);
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = connection.getInputStream();

                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyManagementException e){
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            return false;
        }

    }

    private void initializeData(){
        if (mRunning) {
            mHandler.removeCallbacks(mReadDataTask);
            mValues = new float[0];
        }
        mRunning = true;
        mReadDataTask = new ReadDataRunnable();
        mHandler.post(mReadDataTask);
    }

    private void clearView(){
        mGraphView.setValues(new float[0]);
        mGraphView.invalidate();
    }

    private void refreshView(){
        mGraphView.setValues(mValues);
        mGraphView.invalidate();
    }

    private class ReadDataRunnable implements Runnable{
        @Override
        public void run() {
            if(mRunning){
                new ReadDataTask().execute();
            }
        }
    }

    private void updateData(ArrayList<SensorData> dataList){
        int length = dataList.size();
        float[] values = new float[length * 3];
        for(int i = 0; i < length; i++){
            SensorData data = dataList.get(0);
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
            updateData(sensorDatas);
            refreshView();
            // read data from database every second
            mHandler.postDelayed(mReadDataTask, 1000);
        }

        private  ArrayList<SensorData>  readRecords() {
            // Read the prior ten second data
            final int PRIOR_SECOND = 10;
            long now = System.currentTimeMillis();
            final long timeBeginPoint = now - PRIOR_SECOND * 1000;
            Cursor cursor =  mDb.query(
                    mTableName,
                    new String[]{COLUMN_X_VALUE, COLUMN_Y_VALUE, COLUMN_Z_VALUE, COLUMN_TIME_STEMP},
                    COLUMN_TIME_STEMP + " >= ?",
                    new String[]{String.valueOf(timeBeginPoint)},
                    null,
                    null,
                    COLUMN_TIME_STEMP + " ASC"
            );
            if(cursor != null){
                final int INIT_CAPACITY = PRIOR_SECOND + 5;
                ArrayList<SensorData> dataList = new ArrayList<>(INIT_CAPACITY);
                if(cursor.moveToFirst()){
                    do{
                        SensorData data = readARecord(cursor);
                        dataList.add(data);
                    }while (cursor.moveToNext());
                }
                cursor.close();
//                Log.e("ReadData", "data size:  " + dataList.size());
                return dataList;
            }
            return new ArrayList<>(0);
        }

    }

    private static SensorData readARecord(Cursor cursor){
        double x = cursor.getDouble(cursor.getColumnIndex(COLUMN_X_VALUE));
        double y = cursor.getDouble(cursor.getColumnIndex(COLUMN_Y_VALUE));
        double z = cursor.getDouble(cursor.getColumnIndex(COLUMN_Z_VALUE));
        SensorData data = new SensorData();
        data.x = x;
        data.y = y;
        data.z = z;
        return data;
    }

    private class PlotDownloadedDataTask extends AsyncTask<Void, Void, ArrayList<SensorData>>{
        @Override
        protected ArrayList<SensorData> doInBackground(Void... params) {
            return readRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<SensorData> sensorDatas) {
            // Stop the current display firstly.
            onStopClicked();
            // Read the latest ten second data from the downloaded database and update the data for GraphView.
            updateData(sensorDatas);
            // Refresh the GraphView
            refreshView();
        }

        private  ArrayList<SensorData>  readRecords() {
            // Query the database for all the records in the descending order of the time stamp.
            Cursor cursor =  mDownloadDb.query(
                    mTableName,
                    new String[]{COLUMN_X_VALUE, COLUMN_Y_VALUE, COLUMN_Z_VALUE, COLUMN_TIME_STEMP},
                    null,
                    null,
                    null,
                    null,
                    COLUMN_TIME_STEMP + " DESC"
            );
            if(cursor != null){
                ArrayList<SensorData> dataList = new ArrayList<>(15);
                if(cursor.moveToFirst()){
                    final long latestTimeStamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_STEMP));
                    SensorData data = readARecord(cursor);
                    dataList.add(data);
                    // The start time of the last 10 second
                    long startTime = latestTimeStamp - 10 * 1000;
                    while (cursor.moveToNext()){
                        long timeStamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_STEMP));
                        if(timeStamp < startTime){
                            break;
                        }
                        data = readARecord(cursor);
                        dataList.add(data);
                    }
                }
                cursor.close();
                Log.d("ReadData", "download: data size:  " + dataList.size());
                return dataList;
            }
            return new ArrayList<>(0);
        }

    }

}
