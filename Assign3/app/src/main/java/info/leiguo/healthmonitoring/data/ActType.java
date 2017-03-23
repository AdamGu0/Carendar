package info.leiguo.healthmonitoring.data;

/**
 * Created by lei on 3/23/17.
 */

public class ActType {
    // The action label name
    public static final int ACTION_EATING = 0;
    public static final int ACTION_WALKING = 1;
    public static final int ACTION_RUNNING = 2;
    public static final String[] ACTION_STRING = {"Eating", "Walking", "Running"};

    /**
     * Convert integer activity type to string
     * @param actType
     * @return null if invalid type
     */
    public static String getActivityString(int actType) {
        if (actType >= 0 && actType < ACTION_STRING.length) {
            return ACTION_STRING[actType];
        }
        return null;
    }

}
