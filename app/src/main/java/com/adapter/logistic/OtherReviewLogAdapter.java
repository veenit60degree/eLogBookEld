package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.constants.SharedPref;
import com.messaging.logistic.R;
import com.models.RecapModel;

import java.util.List;

public class OtherReviewLogAdapter extends BaseAdapter {

    Context context;
    SharedPref sharedPref;
    LayoutInflater mInflater;
    List<RecapModel> LogList;

    public OtherReviewLogAdapter(Context context,List<RecapModel> logList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        LogList = logList;
        sharedPref = new SharedPref();
    }


    @Override
    public int getCount() {
        return LogList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return LogList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final RecapModel LogItem = (RecapModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_other_options, null);


            holder.otherFeatureImgView = (ImageView) convertView.findViewById(R.id.otherFeatureImgView);
            holder.otherFeatureTxtView = (TextView) convertView.findViewById(R.id.otherFeatureTxtView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.otherFeatureImgView.setVisibility(View.GONE);
        holder.otherFeatureTxtView.setText(LogItem.getDay());
        holder.otherFeatureTxtView.setTextColor(context.getResources().getColor(R.color.blue_button));

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }




    public class ViewHolder {
        TextView otherFeatureTxtView;
        ImageView otherFeatureImgView;

    }
}