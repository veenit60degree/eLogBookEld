package com.adapter.logistic;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.R;
import com.models.DotDataModel;

import java.util.List;

public class DotLogAdapter  extends BaseAdapter {

    private Context mContext;
    Constants constants;
    private final List<DotDataModel> dotLogArray;
    LayoutInflater mInflater;

    public DotLogAdapter(Context c, Constants cons, List<DotDataModel> list) {
        mContext = c;
        constants = cons;
        dotLogArray = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return dotLogArray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dotLogArray.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_dot_odo_logs, null);

            holder.timeDotTV = (TextView) convertView.findViewById(R.id.timeDotTV);
            holder.locationDotTV = (TextView) convertView.findViewById(R.id.locationDotTV);
            holder.odoKmDotTV = (TextView) convertView.findViewById(R.id.odoKmDotTV);
            holder.odoMilesDotTV = (TextView) convertView.findViewById(R.id.odoMilesDotTV);
            holder.engineHrsDotTV = (TextView) convertView.findViewById(R.id.engineHrsDotTV);
            holder.eventTypeDotTV = (TextView) convertView.findViewById(R.id.eventTypeDotTV);
            holder.originDotTV = (TextView) convertView.findViewById(R.id.originDotTV);

            holder.itemOdometerLay  = (LinearLayout)convertView.findViewById(R.id.itemOdometerLay);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
//
        String OdometerInKm     = dotLogArray.get(position).getOdometerInKm();
        String OdometerInMiles  = dotLogArray.get(position).getOdometerInMiles();
        String engineHours      = dotLogArray.get(position).getEngineHours();
        String driverStatus      = dotLogArray.get(position).getDriverStatus();

        if(engineHours.equals("null")){
            engineHours = "--";
        }
        holder.timeDotTV.setText(dotLogArray.get(position).getTime());
        holder.locationDotTV.setText(dotLogArray.get(position).getLocation());
        holder.engineHrsDotTV.setText(engineHours);
        holder.eventTypeDotTV.setText(dotLogArray.get(position).getEventTypeStatus());
        holder.originDotTV.setText(dotLogArray.get(position).getOrigin());

        String[] odometerData = OdometerInKm.split(" ");
        if( OdometerInKm.contains("Malfunction") && (driverStatus.equals("On Duty") && driverStatus.equals("Driving")) ){

            if(odometerData.length > 1){
                holder.odoKmDotTV.setText(Html.fromHtml(odometerData[0] + " <font color='red'>" + odometerData[1] +"</font>"), TextView.BufferType.SPANNABLE);
            }else{
                holder.odoKmDotTV.setText(Html.fromHtml("<font color='red'>" + OdometerInKm +"</font>"), TextView.BufferType.SPANNABLE);
            }

        }else {
            if(OdometerInKm.equals("null")){
                holder.odoKmDotTV.setText("--");
            }else {
                if(odometerData.length > 0){
                    holder.odoKmDotTV.setText(odometerData[0]);
                }else{
                    holder.odoKmDotTV.setText(OdometerInKm);
                }


            }

        }

        String[] odometerMileData = OdometerInMiles.split(" ");
        if(OdometerInMiles.contains("Malfunction") && (driverStatus.equals("On Duty") || driverStatus.equals("Driving")) ){
            if(odometerMileData.length > 1){
                holder.odoMilesDotTV.setText(Html.fromHtml(odometerMileData[0] + " <font color='red'>" + odometerMileData[1] +"</font>"), TextView.BufferType.SPANNABLE);
            }else{
                holder.odoMilesDotTV.setText(Html.fromHtml("<font color='red'>" + OdometerInMiles +"</font>"), TextView.BufferType.SPANNABLE);
            }
        }else {
            if(OdometerInMiles.equals("null")){
                holder.odoMilesDotTV.setText("--");
            }else {
                if(odometerMileData.length > 0){
                    holder.odoMilesDotTV.setText(odometerMileData[0] );
                }else{
                    holder.odoMilesDotTV.setText(OdometerInMiles);
                }

               // holder.odoMilesDotTV.setText(OdometerInMiles);
            }
        }

     /*   try {
            DotWebViewFragment.inspectionLayHeight = holder.itemOdometerLay.getLayoutParams().height;  //holder.inspectionItemLay.getHeight()
        }catch (Exception e){
            e.printStackTrace();
            DotWebViewFragment.inspectionLayHeight = constants.intToPixel(mContext, 60);
        }*/





        return convertView;
    }


    public class ViewHolder {
        TextView timeDotTV, locationDotTV, odoKmDotTV, odoMilesDotTV, engineHrsDotTV, eventTypeDotTV, originDotTV;
        LinearLayout itemOdometerLay;
    }

}