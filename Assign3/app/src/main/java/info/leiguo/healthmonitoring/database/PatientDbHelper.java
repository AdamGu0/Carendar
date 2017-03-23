package info.leiguo.healthmonitoring.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static info.leiguo.healthmonitoring.database.PatientContract.PatientEntry.TABLE_NAME;

/**
 * Created by Neo on 2/11/17.
 */

public class PatientDbHelper extends SQLiteOpenHelper {

    // The database name
    public static final String DATABASE_NAME = "Group15.db";

    private String mTableName = PatientContract.PatientEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor
     * @param context
     * @param tableName Use default table name if null.
     */
    public PatientDbHelper(Context context, String tableName) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if(tableName != null){
            mTableName = tableName;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PATIENT_TABLE = "CREATE TABLE " + mTableName + " (" +
                PatientContract.PatientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PatientContract.PatientEntry.COLUMN_TIME_STEMP + " INTEGER NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_DATA + " TEXT NOT NULL, " +
                PatientContract.PatientEntry.COLUMN_ACTION_LABEL + " INTEGER NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_PATIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
