package info.leiguo.healthmonitoring;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.leiguo.healthmonitoring.data.ActType;
import info.leiguo.healthmonitoring.data.PointData;
import info.leiguo.healthmonitoring.database.DBAccess;
import info.leiguo.healthmonitoring.database.PatientContract;
import info.leiguo.healthmonitoring.database.PatientDbHelper;

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
    private DBAccess mDBAccess;
    private String mTableName = "a";
    private int mActivityType = 0;
    private RadioButton mPatientSexRadioButton;
    private String CREATE_TABLE_SQL;
    private AlertDialog mAlertDialog;
    private boolean mIsPatientNameValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.btn_analyzing).setOnClickListener(this);
        findViewById(R.id.btn_plotting).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_data_type);
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
        RadioGroup sexRadioGroup = (RadioGroup) findViewById(R.id.radio_data_type);
        mPatientSexRadioButton = (RadioButton) findViewById(sexRadioGroup.getCheckedRadioButtonId());

        // setup spinner
        setupSpinner();
        // Setup GraphView
        FrameLayout container = (FrameLayout)findViewById(R.id.container);
        mValues = new float[50];
        String[] horlabels = new String[]{"100", "200", "300", "400", "500"};
        String[] verlabels = new String[]{"100", "200", "300", "400", "500"};
        mGraphView = new GraphView(this, mValues, "Monitor--By Group 15", horlabels, verlabels, true);
        container.addView(mGraphView);

        mDBAccess = new DBAccess(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDataService();
        mRunning = false;
    }

    private void setupSpinner(){
        Spinner spinner = (Spinner)findViewById(R.id.spinner_act_type);
        spinner.setAdapter(new MySpinnerAdapter(this, ActType.ACTION_STRING));
//        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // 0-eating, 1-walking, 2-running
                mActivityType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String getTableName(){
        return PatientContract.PatientEntry.TABLE_NAME;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_analyzing:
                onAnalyzingClicked();
                break;
            case R.id.btn_plotting:
                onPlottingClicked();
                break;
            case R.id.btn_save:
                onSaveClicked();
                break;
            default:
        }
    }

    private void onAnalyzingClicked() {
        // TODO: copy the database file to SDcard for part B
        String sd_card = Environment.getExternalStorageDirectory().toString();
        String path = sd_card + "/Log/Mei";

        String train_path = path + "/train.txt";
        String test_path = path + "/test.txt";
        String output_path = path + "/result.txt";
        String model_name = path + "/my_model.txt";

        String[] trainArgs = {train_path, model_name};
        String[] testArgs = {test_path, model_name, output_path};
        svm_train train = new svm_train();
        svm_predict predict = new svm_predict();
        try {
            long start_train_time = System.nanoTime();
            train.main(trainArgs);
            long end_train_time = System.nanoTime();
            Toast.makeText(this, "LibSVM has finished Training. The Training time is: \n", Toast.LENGTH_LONG);
            long train_time = (end_train_time - start_train_time) / 1000000;//get milliseconds
            Toast.makeText(this, String.valueOf(train_time) + "ms", Toast.LENGTH_LONG);
            long start_test_time = System.nanoTime();
            predict.main(testArgs);
            long end_test_time = System.nanoTime();
            Toast.makeText(this, "\nLibSVM has finished Testing. The Testing time is: \n", Toast.LENGTH_LONG);
            long test_time = (end_test_time - start_test_time) / 1000000;//get milliseconds
            Toast.makeText(this, String.valueOf(test_time) + "ms", Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void onPlottingClicked(){
        new TestDataTask().execute();
    }

    private void onSaveClicked(){
        String activityType = ActType.getActivityString(mActivityType);
        String msg = String.format("Recording [%s]...", activityType);
        showRecordingDialog(msg);
        createTable();
        beginDataService();
    }

    private void createTable(){
        mDBAccess.createDefaultTable();
    }

    private void beginDataService(){
        // Stop the data service if it is running.
        stopDataService();
        // Start the data service
        Intent intent = new Intent(MyService.MY_ACTION);
        intent.setPackage(getPackageName());
        intent.putExtra(MyService.KEY_TABLE_NAME, mTableName);
        intent.putExtra(MyService.KEY_ACTIVITY_TYPE, mActivityType);
        startService(intent);
    }

    private void stopDataService(){
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
    }

    private void toastInvalidPatientName(){
        longToast("Invalid patient name--Only letters and space are allowed in the patient name!");
    }

    private void onStopClicked(){
        clearView();
        mRunning = false;
    }

    private void showRecordingDialog(String msg){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setMessage(msg);
        adBuilder.setCancelable(false);
        adBuilder.setPositiveButton("Stop Recording", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopDataService();
                hideRecordingDialog();
            }
        });
        mAlertDialog = adBuilder.create();
        mAlertDialog.show();
    }

    private void hideRecordingDialog(){
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }else{
            Log.e("MaintActivity", "hideRecordingDialog null reference.");
        }
    }

    private AlertDialog mMsgDialog;
    private void showMessageDialog(String msg){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setMessage(msg);
        adBuilder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hidMessageDialog();
            }
        });
        mMsgDialog = adBuilder.create();
        mMsgDialog.show();
    }
    private void hidMessageDialog(){
        if(mMsgDialog != null){
            mMsgDialog.dismiss();
        }
    }

    // TODO: use android provided method instead
    private String getDatabaseFilePath(){
        return  getAppFolder() + "/databases/" + PatientDbHelper.DATABASE_NAME;
    }

    private String getAppFolder(){
        return "/data/data/" + getPackageName();
    }


    private void shortToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void longToast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void showData(){
        String filePath = getFilesDir().getAbsolutePath() + "/" +PatientDbHelper.DATABASE_NAME;
        try{
//            mDownloadDb = SQLiteDatabase.openDatabase(filePath, null, SQLiteDatabase.OPEN_READWRITE);
        }catch (SQLiteException e){
            e.printStackTrace();
            return;
        }
        plotDownloadData();
    }

    private void plotDownloadData(){
//        if(mDownloadDb != null){
//            new PlotDownloadedDataTask().execute();
//        }
    }

    private void clearView(){
        mGraphView.setValues(new float[0]);
        mGraphView.invalidate();
    }

    private void refreshView(){
        mGraphView.setValues(mValues);
        mGraphView.invalidate();
    }


    private void updateData(List<PointData> dataList){

    }

    private class PlotDownloadedDataTask extends AsyncTask<Void, Void, ArrayList<PointData>>{
        @Override
        protected ArrayList<PointData> doInBackground(Void... params) {
            return readRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<PointData> sensorDatas) {
            // Stop the current display firstly.
            onStopClicked();
            // Read the latest ten second data from the downloaded database and update the data for GraphView.
            updateData(sensorDatas);
            // Refresh the GraphView
            refreshView();
        }

        private  ArrayList<PointData>  readRecords() {
            // Query the database for all the records in the descending order of the time stamp.
            if(mDBAccess != null){
                int actEatingCount = 0;
                int actWalkingCount = 0;
                int actRunningCount = 0;
                int action = ActType.ACTION_EATING;
                List<List<PointData>>  activities = mDBAccess.readRecords(action);
                if(activities != null){
                    actEatingCount = activities.size();
                }

            }
            return null;
        }


    }

    /**
     * Heavy task, should run on background thread
     * @param activityType
     * @return
     */
    private int getActivityDataCount(int activityType){
        List<List<PointData>>  activities = mDBAccess.readRecords(activityType);
        if(activities != null){
            return activities.size();
        }
        return 0;
    }

    private class TestDataTask extends AsyncTask<Void, Void, int[]>{

        @Override
        protected int[] doInBackground(Void... voids) {
            copyDbToSdcard();
            int eatingCount = getActivityDataCount(ActType.ACTION_EATING);
            int walkingCount = getActivityDataCount(ActType.ACTION_WALKING);
            int runningCount = getActivityDataCount(ActType.ACTION_RUNNING);
            return new int[]{eatingCount, walkingCount, runningCount};
        }

        @Override
        protected void onPostExecute(int[] ints) {
            String msg = String.format("Activities Data count: [Eating: %d] [Walking: %d] [Running: %d]", ints[0], ints[1], ints[2]);
            showMessageDialog(msg);
        }


        private void copyDbToSdcard(){
            File path = getExternalFilesDir(null);
            String fileName = System.currentTimeMillis() + ".db";
            File outputFile = new File(path, fileName);
            File dbFile = new File(getDatabaseFilePath());
            try {
                FileInputStream fileInputStream = new FileInputStream(dbFile);
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[4096];
                int count;
                while((count = fileInputStream.read(buffer)) > 0){
                    fileOutputStream.write(buffer, 0, count);
                }
                fileInputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
