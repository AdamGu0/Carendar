package info.leiguo.healthmonitoring.database;

import android.provider.BaseColumns;

/**
 * Created by Neo on 2/11/17.
 */

public class PatientContract {

    public PatientContract() {}

    public static class PatientEntry implements BaseColumns {
        public static final String TABLE_NAME = "ActivityData";
        public static final String COLUMN_TIME_STEMP = "timeStamp";
        public static final String COLUMN_ACTION_LABEL = "actLabel";
        public static final String  COLUMN_DATA = "data";

    }
}