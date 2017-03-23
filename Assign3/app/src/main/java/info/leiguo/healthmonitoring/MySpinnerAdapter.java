package info.leiguo.healthmonitoring;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Created by lei on 3/23/17.
 */

public class MySpinnerAdapter implements SpinnerAdapter {
    private Context mContext;
    private String[] mData = null;

    public MySpinnerAdapter(Context context, String[] data){
        mContext = context;
        mData = data;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = new TextView(mContext);
            convertView.setPadding(10, 25, 10, 25);
        }
        TextView tv = (TextView)convertView;
        tv.setText(mData[position]);
//        tv.setTextColor(0x333333);
        return tv;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        if(mData == null){
            return 0;
        }else {
            return mData.length;
        }
    }

    @Override
    public Object getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
