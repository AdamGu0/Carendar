package info.leiguo.healthmonitoring.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static info.leiguo.healthmonitoring.data.PatientContract.PatientEntry.TABLE_NAME;

/**
 * Created by Neo on 2/11/17.
 */

public class PatientDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "assgn2_group15.db";

    private String mTableName;

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public PatientDbHelper(Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mTableName = tableName;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PATIENT_TABLE = "CREATE TABLE " + mTableName + " (" +
                PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientContract.PatientEntry.COLUMN_TIME_STEMP + " INTEGER NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_X_VALUE + " REAL NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_Y_VALUE + " REAL NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_Z_VALUE + " REAL NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_PATIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
