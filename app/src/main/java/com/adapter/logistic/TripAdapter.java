package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.messaging.logistic.R;
import com.models.TripHistoryModel;

import java.util.List;

public class TripAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    List<TripHistoryModel> transferList;

    public TripAdapter(Context context, List<TripHistoryModel> transferList){
        this.context = context;
        this.transferList = transferList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return transferList.size();
    }

    @Override
    public Object getItem(int position) {
        return transferList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final TripHistoryModel transferItem = (TripHistoryModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item, null);

            holder.itemLoadTV = (TextView)convertView.findViewById(R.id.itemLoadTV);
            holder.itemDetailTV = (TextView)convertView.findViewById(R.id.itemDetailTV);
            holder.consigneeNameTV = (TextView)convertView.findViewById(R.id.consigneeNameTxtView);
            holder.consigneeNameTV.setVisibility(View.VISIBLE);


            holder.itemLoadTV.setText("LOAD# "+transferItem.getLoadNumber());
            holder.consigneeNameTV.setText(transferItem.getConsigneeName());

            holder.itemDetailTV.setText(transferItem.getConsigneeAddress()+", " + transferItem.getConsigneeStateCode()+", " +
                    transferItem.getConsigneeCity()+ ", " + transferItem.getConsigneePostal()+", " +  transferItem.getConsigneeCountryCode() );


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
        TextView itemLoadTV, itemDetailTV, consigneeNameTV;

    }

}
