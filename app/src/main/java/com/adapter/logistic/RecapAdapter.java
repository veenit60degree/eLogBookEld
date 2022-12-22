package com.adapter.logistic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.als.logistic.R;
import com.models.RecapModel;

import java.util.List;

public class RecapAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<RecapModel> recapList;

    public RecapAdapter(Context context, List<RecapModel> transferList){
        this.context = context;
        this.recapList = transferList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return recapList.size();
    }

    @Override
    public Object getItem(int position) {
        return recapList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_recap, null);

            holder.dayRecapTV = (TextView)convertView.findViewById(R.id.dayRecapTV);
            holder.dateRecapTV = (TextView)convertView.findViewById(R.id.dateRecapTV);
            holder.hourRecapTV = (TextView)convertView.findViewById(R.id.hourRecapTV);

            holder.dayRecapTV.setText(recapList.get(position).getDay());
            holder.dateRecapTV.setText(recapList.get(position).getDate());
            holder.hourRecapTV.setText(recapList.get(position).getHourWorked());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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
        TextView dayRecapTV, dateRecapTV, hourRecapTV;

    }

}
