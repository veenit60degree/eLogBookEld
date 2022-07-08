package com.adapter.logistic;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.OdometerModel;

import java.util.List;

public class OdometerAdapter extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    List<OdometerModel> odometerList;

    public OdometerAdapter(Context context, List<OdometerModel> list){
        this.context = context;
        this.odometerList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return odometerList.size();
    }

    @Override
    public Object getItem(int position) {
        return odometerList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        OdometerModel odometerM = odometerList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.items_odometers, null);

            holder.vehicleReadingTV = (TextView)convertView.findViewById(R.id.vehicleReadingTV);
            holder.startReadingTV   = (TextView)convertView.findViewById(R.id.startReadingTV);
            holder.endReadingTV     = (TextView)convertView.findViewById(R.id.endReadingTV);
            holder.distanceMilesTV  = (TextView)convertView.findViewById(R.id.distanceReadingTV);
            holder.distancekmTV     = (TextView)convertView.findViewById(R.id.distancekmTV);
            holder.personalTV       = (TextView)convertView.findViewById(R.id.personalTV);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }



        String DistanceKm = "", DistanceMiles = "", StartOdometer = "", EndOdometer = "";

        StartOdometer   = odometerM.getStartOdometer();
        EndOdometer     = odometerM.getEndOdometer();

        if(!StartOdometer.equalsIgnoreCase("null")) {
            if(StartOdometer.contains(".")){
                StartOdometer = StartOdometer.split("\\.")[0];
            }
            holder.startReadingTV.setText(StartOdometer);
        }else
            holder.startReadingTV.setText("--");

        if(!EndOdometer.equalsIgnoreCase("null")) {
            if(EndOdometer.contains(".")){
                EndOdometer = EndOdometer.split("\\.")[0];
            }
            holder.endReadingTV.setText(EndOdometer);
        }else
            holder.endReadingTV.setText("--");

        DistanceKm      = odometerM.getTotalKM();
        DistanceMiles   = odometerM.getTotalMiles();

        if(DistanceKm.equalsIgnoreCase("null") || DistanceKm.length() == 0)
            DistanceKm = "--";
        else{
            if(DistanceKm.contains(".")){
                DistanceKm = DistanceKm.split("\\.")[0];
            }

            DistanceKm    = DistanceKm + " km";// +DistanceType;
        }

        if(DistanceMiles.equalsIgnoreCase("null") || DistanceMiles.length() == 0)
            DistanceMiles = "--";
        else{
            if(DistanceMiles.contains(".")){
                DistanceMiles = DistanceMiles.split("\\.")[0];
            }

            DistanceMiles    = DistanceMiles + " miles";// +DistanceType;
        }

        holder.distanceMilesTV.setText(DistanceMiles);
        holder.distancekmTV.setText(DistanceKm);
        holder.vehicleReadingTV.setText(odometerM.getTruckOdometerId());

        if(odometerM.getDriverStatusID().equals(Globally.PERSONAL) ||
              (odometerM.getDriverStatusID().equals(Globally.OFF_DUTY) && odometerM.isPersonal())) { // Personal
            holder.personalTV.setText("(PC)");
            holder.personalTV.setVisibility(View.VISIBLE);
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
        TextView vehicleReadingTV, startReadingTV, endReadingTV, distanceMilesTV, distancekmTV, personalTV;

    }


}
