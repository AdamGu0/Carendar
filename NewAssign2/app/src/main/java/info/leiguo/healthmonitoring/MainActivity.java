package info.leiguo.healthmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

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
    private GraphView mGraphView;
    private float[] mValues;
    private boolean mRunning = true;
    private Handler mHandler = new Handler();
    private ReadDataRunnable mReadDataTask;
    // Used to control when will we add a large value to the array
//    private int mRefreshDataTimes = 0;
//    private final int INSERT_SUMMIT_INTERVAL = 8;
    private SQLiteDatabase mDb;
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
        mRunning = false;
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
        if(mIsUploading){
            return;
        }
        if(!Utils.checkNetwork(this)){
            shortToast("No network.");
            return;
        }
        mIsUploading = true;
        new UploadDBTask().execute(SERVER_URL);
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
//        String tableName = getTableName();
//        Toast.makeText(this, "Downloading database: " + tableName, Toast.LENGTH_LONG).show();
        final String dbName = PatientDbHelper.DATABASE_NAME;
        //download in background
//        new DownloadDBTask().execute("https://impact.asu.edu/CSE535Spring17Folder/" + dbName);
        new DownloadDBTask().execute("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
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
            final String boundary =  "********";
            trustAllServer();
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            setupHttpUrlConnection(connection, boundary);
            DataOutputStream upStream = new DataOutputStream(
                    connection.getOutputStream());
            writeContentStartBoundary(upStream, boundary);
            // write database file
            writeContent(upStream);
            writeContentEndBoundary(upStream, boundary);
            upStream.flush();
            upStream.close();
            final int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                Log.e("uploadDb", "Failed with http status: " + status);
                return false;
            }else{
                return true;
            }
        }

        private void setupHttpUrlConnection(HttpURLConnection connection, String boundary) throws ProtocolException{
            final int connectTimeout = 10000;
            final int readTimeout = 10000;
            connection.setUseCaches(false);
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
            final String attachmentName = "database";
            final String attachmentFileName = PatientDbHelper.DATABASE_NAME;
            upStream.writeBytes("--" + boundary + "\r\n");
            upStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"\r\n");
            upStream.writeBytes("\r\n");
        }

        private void writeContentEndBoundary(DataOutputStream upStream, String boundary) throws IOException{
            upStream.writeBytes("\r\n--" + boundary + "--\r\n");;
        }

        // This code is used to trust the https server for the assignment 2. I think it's ok to do things
        // in such a way for an assignment. I will not use the same code for a production application, because
        // it's not secure
        private void trustAllServer() throws NoSuchAlgorithmException, KeyManagementException{
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        }

    }

    private String getDatabaseFilePath(){
        return  getAppFolder() + "/databases/" + PatientDbHelper.DATABASE_NAME;
    }

    private String getAppFolder(){
        return "/data/data/" + getPackageName();
    }

    private String getFilesFolder(){
        return getAppFolder() + "files/";
    }

    private void shortToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void longToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showData(){
        String filePath = getFilesFolder() + PatientDbHelper.DATABASE_NAME;
        // TODO: may not use mDb, use a new Db reference?
        mDb = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private class DownloadDBTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog("Database is downloading, please wait...");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return download(params[0]);
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

        private boolean download(String s) {
            try {
                URL url = new URL(s);
                InputStream is = url.openStream();
//                String filename = s.substring(s.lastIndexOf("/") + 1);
                String fileName = PatientDbHelper.DATABASE_NAME;
                OutputStream os = openFileOutput(fileName, MODE_PRIVATE);
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
//            mRefreshDataTimes = 0;
        }
//        mTask = new MyRunnable();
        mRunning = true;
//        mHandler.post(mTask);
        // start read data task.
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
//            Log.e("updateData", "X: " + data.x + "  y:" + data.y + "  z: " + data.z);
            values[i * 3 ] = (float) data.x;
            values[i * 3 + 1] = (float) data.y;
            values[i * 3 + 2] = (float) data.z;
            // normalization
//            double total = data.x + data.y + data.z;
//            values[i * 3 ] = (float) (data.x / total);
//            values[i * 3 + 1] = (float) (data.y / total);
//            values[i * 3 + 2] = (float) (data.z / total);
        }
        mValues = values;
        if(length > 0){
            Log.e("updateData", "X:Y Z");
        }else{
            Log.e("updateData", "No data");
        }
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
                        double x = cursor.getDouble(cursor.getColumnIndex(COLUMN_X_VALUE));
                        double y = cursor.getDouble(cursor.getColumnIndex(COLUMN_Y_VALUE));
                        double z = cursor.getDouble(cursor.getColumnIndex(COLUMN_Z_VALUE));
//                        long timeStamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_STEMP));
//                        Log.e("ReadData", "timeStamp ： timeBeginPoint:  " + timeStamp + " : " + timeBeginPoint);
//                        if(timeStamp < timeBeginPoint){
//                            Log.e("ReadData", "timeStamp - timeBeginPoint:  " + (timeStamp - timeBeginPoint));
//                            continue;
//                        }
                        SensorData data = new SensorData();
                        data.x = x;
                        data.y = y;
                        data.z = z;
                        dataList.add(data);
                    }while (cursor.moveToNext());
                }
                cursor.close();
                Log.e("ReadData", "data size:  " + dataList.size());
                return dataList;
            }
            return new ArrayList<>(0);
        }

    }

}
