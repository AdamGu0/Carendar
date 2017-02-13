package info.leiguo.healthmonitoring;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.Random;;
import info.leiguo.healthmonitoring.R;

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
    private MyRunnable mTask;
    // Used to control when will we add a large value to the array
    private int mRefreshDataTimes = 0;
    private final int INSERT_SUMMIT_INTERVAL = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.btn_run).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
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
            default:
        }
    }



    private void onRunClicked(){
        initializeData();
    }

    private void onStopClicked(){
        clearView();
        mRunning = false;
    }

    private void initializeData(){
        if (mRunning) {
            mHandler.removeCallbacks(mTask);
            mValues = new float[0];
            mRefreshDataTimes = 0;
        }
        mTask = new MyRunnable();
        mRunning = true;
        mHandler.post(mTask);
    }

    private void refreshData(){
        Random myRandom = new Random();
        final int N = 50;
        float[] temp = new float[N];
        final int MIN_VALUE_SEED = 3;
        final int MAX_VALUE_BASE = 9;
        if(mValues == null || mValues.length == 0){
            // Initializing
            for(int i = 0; i < N - 1; i++){
                temp[i] = myRandom.nextInt(MIN_VALUE_SEED);
            }
            temp[N - 1] = myRandom.nextInt(MIN_VALUE_SEED) + MAX_VALUE_BASE;
        }else{
            // The amount of data to be updated.
            final int UPDATE_AMOUNT = 3;
            for(int i = 0; i < N - UPDATE_AMOUNT; i++){
                temp[i] = mValues[i + UPDATE_AMOUNT];
            }
            for(int i = N - UPDATE_AMOUNT; i< N - 1; i++){
                temp[i] = myRandom.nextInt(MIN_VALUE_SEED);
            }
            if(mRefreshDataTimes % INSERT_SUMMIT_INTERVAL == 0){
                temp[N - 1] = myRandom.nextInt(MIN_VALUE_SEED) + MAX_VALUE_BASE;
                mRefreshDataTimes = 0;
            }else{
                temp[N - 1] = myRandom.nextInt(1);
            }
        }
        mValues = temp;
        mRefreshDataTimes++;
    }

    private void clearView(){
        mGraphView.setValues(new float[0]);
        mGraphView.invalidate();
    }

    private void refreshView(){
        refreshData();
        mGraphView.setValues(mValues);
        mGraphView.invalidate();
    }

    private class MyRunnable implements Runnable{
        @Override
        public void run() {
            if(mRunning){
                refreshView();
                mHandler.postDelayed(this, 150);
            }
        }
    }
}
