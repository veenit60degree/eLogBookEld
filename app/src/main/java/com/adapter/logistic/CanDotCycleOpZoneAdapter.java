package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.CanadaDutyStatusModel;

import java.util.List;

public class CanDotCycleOpZoneAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotCycleOpZoneAdapter(Context c, List<CanadaDutyStatusModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();

    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return itemsList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_cycle_op_zone_dot, null);

            holder.addHrsDotLay             = (LinearLayout) convertView.findViewById(R.id.addHrsDotLay);

            holder.dateTimeDiffCycleTV      = (TextView) convertView.findViewById(R.id.dateTimeDiffCycleTV);

            holder.dateCycleDotTV           = (TextView) convertView.findViewById(R.id.dateCycleDotTV);
            holder.eventCycleEventTV        = (TextView) convertView.findViewById(R.id.eventCycleEventTV);
            holder.geoLocCycleTV            = (TextView) convertView.findViewById(R.id.geoLocCycleTV);

            holder.latLongCycleTV           = (TextView) convertView.findViewById(R.id.latLongCycleTV);
            holder.distanceLastCoCycleTV    = (TextView) convertView.findViewById(R.id.distanceLastCoCycleTV);
            holder.cmvAHDotTV               = (TextView) convertView.findViewById(R.id.cmvAHDotTV);

            holder.recStatusAHDotTV         = (TextView) convertView.findViewById(R.id.recStatusAHDotTV);
            holder.recOriginAHDotTV         = (TextView) convertView.findViewById(R.id.recOriginAHDotTV);
            holder.seqNoDotTV               = (TextView) convertView.findViewById(R.id.seqNoDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.addHrsDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

        String EventDateTime = itemsList.get(position).getDateTimeWithMins();
        try {
            if (position == 0) {
                holder.dateTimeDiffCycleTV.setVisibility(View.VISIBLE);
                holder.dateTimeDiffCycleTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime));
            } else {
                int dayDiff = constants.getDayDiff(itemsList.get(position-1).getDateTimeWithMins(), EventDateTime);
                if (dayDiff != 0){
                    holder.dateTimeDiffCycleTV.setVisibility(View.VISIBLE);
                    holder.dateTimeDiffCycleTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


       // holder.dateCycleDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getEventDate()));
        if(EventDateTime.length() > 16) {
            holder.dateCycleDotTV.setText(EventDateTime.substring(11, 16));
        }

        holder.eventCycleEventTV.setText( constants.getCycleOpZoneEventName(
                                                        itemsList.get(position).getEventType(),
                                                        itemsList.get(position).getEventCode()) );

        holder.geoLocCycleTV.setText(itemsList.get(position).getAnnotation());
        holder.latLongCycleTV.setText(constants.checkNullString(itemsList.get(position).getGPSLatitude()) + ", "+
                constants.checkNullString(itemsList.get(position).getGPSLongitude()));
        holder.distanceLastCoCycleTV.setText(constants.checkNullString(itemsList.get(position).getDistanceSinceLastValidCord()));

        holder.cmvAHDotTV.setText(itemsList.get(position).getTruckEquipmentNo());
        holder.recStatusAHDotTV.setText(""+itemsList.get(position).getRecordStatus());
        holder.recOriginAHDotTV.setText(itemsList.get(position).getRecordOrigin());
        holder.seqNoDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());


        // Set text style normal
        constants.setTextStyleNormal(holder.dateCycleDotTV);
        constants.setTextStyleNormal(holder.eventCycleEventTV);
        constants.setTextStyleNormal(holder.geoLocCycleTV);
        constants.setTextStyleNormal(holder.latLongCycleTV);
        constants.setTextStyleNormal(holder.distanceLastCoCycleTV);

        constants.setTextStyleNormal(holder.cmvAHDotTV);
        constants.setTextStyleNormal(holder.recStatusAHDotTV);
        constants.setTextStyleNormal(holder.recOriginAHDotTV);
        constants.setTextStyleNormal(holder.seqNoDotTV);


        // set Marque on view
        constants.setMarqueonView(holder.dateCycleDotTV);
        constants.setMarqueonView(holder.eventCycleEventTV);
        constants.setMarqueonView(holder.geoLocCycleTV);
        constants.setMarqueonView(holder.latLongCycleTV);
        constants.setMarqueonView(holder.distanceLastCoCycleTV);
        constants.setMarqueonView(holder.cmvAHDotTV);
        constants.setMarqueonView(holder.recStatusAHDotTV);
        constants.setMarqueonView(holder.recOriginAHDotTV);
        constants.setMarqueonView(holder.seqNoDotTV);



        return convertView;
    }


    public class ViewHolder {
        TextView dateCycleDotTV, eventCycleEventTV, geoLocCycleTV, latLongCycleTV, distanceLastCoCycleTV;
        TextView cmvAHDotTV, recStatusAHDotTV, recOriginAHDotTV, seqNoDotTV;
        TextView dateTimeDiffCycleTV;
        LinearLayout addHrsDotLay;

    }

}