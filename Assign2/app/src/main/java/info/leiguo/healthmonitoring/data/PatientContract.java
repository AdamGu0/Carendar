package info.leiguo.healthmonitoring.data;

import android.provider.BaseColumns;

/**
 * Created by Neo on 2/11/17.
 */

public class PatientContract {

    public PatientContract() {}

    public static class PatientEntry implements BaseColumns {
        //public static final String TABLE_NAME = "NAME_ID_SEX_AGE";
        public static final String COLUMN_TIME_STEMP = "timeStamp";
        public static final String COLUMN_X_VALUE = "x";
        public static final String COLUMN_Y_VALUE = "y";
        public static final String COLUMN_Z_VALUE = "z";

    }
}