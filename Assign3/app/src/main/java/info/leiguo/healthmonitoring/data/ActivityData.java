package info.leiguo.healthmonitoring.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lei on 3/23/17.
 * The data of one Activity (eating, walking, running)
 */

public class ActivityData {
    private List<PointData> dataList;

    /**
     * Convert json string to list data.
     * @param str json string
     * @return null if failed or no data
     */
    public static ActivityData fromString(String str) {
        try {
            JSONArray jsonArray = new JSONArray(str);
            final int length = jsonArray.length();
            if(length > 0){
                ArrayList<PointData> list = new ArrayList<>();
                for(int i = 0; i < length; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    PointData data = new PointData();
                    data.x = jsonObject.getDouble("x");
                    data.y = jsonObject.getDouble("y");
                    data.z = jsonObject.getDouble("z");
                    list.add(data);
                }
                ActivityData activityData = new ActivityData(list);
                return activityData;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ActivityData(List<PointData> dataList){
        this.dataList = dataList;
    }


    /**
     * Convert data to json String
     *
     * @return json string of the list data
     */
    public String toString() {
        if (dataList != null && dataList.size() > 0) {
            JSONArray jsonArray = new JSONArray();
            try {
                for (PointData data : dataList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("x", data.x);
                    jsonObject.put("y", data.y);
                    jsonObject.put("z", data.z);
                    jsonArray.put(jsonObject);
                }
                return jsonArray.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONArray().toString();
    }

    public List<PointData> getDataList() {
        return dataList;
    }

    public void setDataList(List<PointData> dataList){
        this.dataList = dataList;
    }
}
