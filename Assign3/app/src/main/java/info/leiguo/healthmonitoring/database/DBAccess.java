package info.leiguo.healthmonitoring.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import info.leiguo.healthmonitoring.data.PointData;

import static info.leiguo.healthmonitoring.database.PatientContract.PatientEntry.COLUMN_ACTION_LABEL;
import static info.leiguo.healthmonitoring.database.PatientContract.PatientEntry.COLUMN_DATA;
import static info.leiguo.healthmonitoring.database.PatientContract.PatientEntry.COLUMN_TIME_STEMP;

/**
 * Created by lei on 3/23/17.
 */

public class DBAccess {

    private SQLiteDatabase mDb;
    private String mDefaultTableName = PatientContract.PatientEntry.TABLE_NAME;

    public DBAccess(Context context){
        PatientDbHelper dbHelper = new PatientDbHelper(context, null);
        mDb = dbHelper.getWritableDatabase();
    }

    public long addNewRecords(long timeStamp, String data, int actionType, String tableName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIME_STEMP, timeStamp);
        cv.put(COLUMN_DATA, data);
        cv.put(COLUMN_ACTION_LABEL, actionType);
        return mDb.insert(tableName, null, cv);
    }

    public long addNewRecords(long timeStamp, String data, int actionType){
        return addNewRecords(timeStamp, data, actionType, mDefaultTableName);
    }


    public void createDefaultTable() {
        String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + mDefaultTableName + " (" +
                PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TIME_STEMP + " INTEGER NOT NULL, " +
                COLUMN_DATA + " TEXT NOT NULL, " +
                COLUMN_ACTION_LABEL + " INTEGER NOT NULL " +
                "); ";
        mDb.execSQL(CREATE_TABLE_SQL);
    }

    private ArrayList<PointData> readRecords(int actionType) {
        // Query the database for all the records in the descending order of the time stamp.
        Cursor cursor =  mDb.query(
                mDefaultTableName,
                new String[]{COLUMN_DATA, COLUMN_TIME_STEMP},
                null,
                null,
                null,
                null,
                null
        );
        if(cursor != null){
            ArrayList<PointData> dataList = new ArrayList<>();
            if(cursor.moveToFirst()){

            }
            cursor.close();
            Log.d("ReadData", "download: data size:  " + dataList.size());
            return dataList;
        }
        return new ArrayList<>(0);
    }

}
