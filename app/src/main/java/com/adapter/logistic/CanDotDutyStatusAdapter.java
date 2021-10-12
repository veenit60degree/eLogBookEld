package com.adapter.logistic;

import android.content.Context;
import android.util.Log;
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
import com.models.PrePostModel;

import java.util.List;

public class CanDotDutyStatusAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotDutyStatusAdapter(Context c, List<CanadaDutyStatusModel> list) {
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
            convertView = mInflater.inflate(R.layout.item_duty_status_change_dot, null);

            holder.dutyStatusDotLay     = (LinearLayout) convertView.findViewById(R.id.dutyStatusDotLay);

            holder.dateTimeDiffTV       = (TextView) convertView.findViewById(R.id.dateTimeDiffTV);
            holder.distanceCordDotTV       = (TextView) convertView.findViewById(R.id.distanceCordDotTV);
            holder.latLongDotTV       = (TextView) convertView.findViewById(R.id.latLongDotTV);
            holder.geoLocDotTV       = (TextView) convertView.findViewById(R.id.geoLocDotTV);

            holder.dateTimeDotTV        = (TextView) convertView.findViewById(R.id.dateTimeDotTV);
            holder.eventDotTV           = (TextView) convertView.findViewById(R.id.eventDotTV);
            holder.cmvDotTV             = (TextView) convertView.findViewById(R.id.cmvDotTV);

            holder.distanceAccDotTV     = (TextView) convertView.findViewById(R.id.distanceAccDotTV);
            holder.hrsAccDotTV          = (TextView) convertView.findViewById(R.id.hrsAccDotTV);
            holder.distanceTotalDotTV   = (TextView) convertView.findViewById(R.id.distanceTotalDotTV);

            holder.recStatusDotTV       = (TextView) convertView.findViewById(R.id.recStatusDotTV);
            holder.recOriginDotTV       = (TextView) convertView.findViewById(R.id.recOriginDotTV);
            holder.seqNoDotTV           = (TextView) convertView.findViewById(R.id.seqNoDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.dutyStatusDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

        String EventDateTime = itemsList.get(position).getDateTimeWithMins();
        try {
            if (position == 0) {
                holder.dateTimeDiffTV.setVisibility(View.VISIBLE);
                holder.dateTimeDiffTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));

                /*if(EventDateTime.length() >= 19) {
                    holder.dateTimeDiffTV.setText(EventDateTime.substring(11, 19));
                }*/
            } else {
                int dayDiff = constants.getDayDiff(itemsList.get(position-1).getDateTimeWithMins(), EventDateTime);
                if (dayDiff != 0){
                    holder.dateTimeDiffTV.setVisibility(View.VISIBLE);
                    holder.dateTimeDiffTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));

                    /*if(EventDateTime.length() >= 19) {
                        holder.dateTimeDiffTV.setText(EventDateTime.substring(11, 19));
                    }*/

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(EventDateTime.length() >= 19) {
            holder.dateTimeDotTV.setText(EventDateTime.substring(11, 19));
        }

        holder.eventDotTV.setText(constants.getDutyChangeEventName( itemsList.get(position).getEventType(),
                                                                    itemsList.get(position).getEventCode(),
                                                                    itemsList.get(position).isPersonal(),
                                                                    itemsList.get(position).isYard() ));

        holder.cmvDotTV.setText(constants.checkNullString(itemsList.get(position).getTruckEquipmentNo()));

        holder.distanceAccDotTV.setText(constants.checkNullString(itemsList.get(position).getAccumulatedVehicleMiles()));
        holder.hrsAccDotTV.setText(itemsList.get(position).getAccumulatedEngineHours());
        holder.distanceTotalDotTV.setText(constants.checkNullString(itemsList.get(position).getTotalVehicleKM()) );

        holder.recStatusDotTV.setText(""+itemsList.get(position).getRecordStatus());
        holder.recOriginDotTV.setText(itemsList.get(position).getRecordOrigin());
        holder.seqNoDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());

        holder.distanceCordDotTV.setText(""+itemsList.get(position).getDistanceSinceLastValidCord());
        holder.latLongDotTV.setText(""+itemsList.get(position).getGPSLatitude() + ", " + itemsList.get(position).getGPSLongitude());
        holder.geoLocDotTV.setText(""+itemsList.get(position).getAnnotation());



        // Set text style normal
        constants.setTextStyleNormal(holder.dateTimeDotTV);
        constants.setTextStyleNormal(holder.eventDotTV);
        constants.setTextStyleNormal(holder.cmvDotTV);

        constants.setTextStyleNormal(holder.distanceAccDotTV);
        constants.setTextStyleNormal(holder.hrsAccDotTV);
        constants.setTextStyleNormal(holder.distanceTotalDotTV);

        constants.setTextStyleNormal(holder.recStatusDotTV);
        constants.setTextStyleNormal(holder.recOriginDotTV);
        constants.setTextStyleNormal(holder.seqNoDotTV);

        constants.setTextStyleNormal(holder.distanceCordDotTV);
        constants.setTextStyleNormal(holder.latLongDotTV);
        constants.setTextStyleNormal(holder.geoLocDotTV);


        // set Marque on view
        constants.setMarqueonView(holder.geoLocDotTV);
        constants.setMarqueonView(holder.latLongDotTV);
        constants.setMarqueonView(holder.cmvDotTV);
        constants.setMarqueonView(holder.dateTimeDotTV);
        constants.setMarqueonView(holder.eventDotTV);
        constants.setMarqueonView(holder.distanceAccDotTV);
        constants.setMarqueonView(holder.hrsAccDotTV);
        constants.setMarqueonView(holder.distanceTotalDotTV);
        constants.setMarqueonView(holder.recStatusDotTV);
        constants.setMarqueonView(holder.recOriginDotTV);
        constants.setMarqueonView(holder.seqNoDotTV);
        constants.setMarqueonView(holder.distanceCordDotTV);


        return convertView;
    }


    public class ViewHolder {
        TextView dateTimeDotTV, eventDotTV, cmvDotTV, distanceAccDotTV, hrsAccDotTV, distanceTotalDotTV, recStatusDotTV, recOriginDotTV, seqNoDotTV;
        TextView dateTimeDiffTV, distanceCordDotTV, latLongDotTV, geoLocDotTV;
        LinearLayout dutyStatusDotLay;

    }

}