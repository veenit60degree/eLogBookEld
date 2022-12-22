package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.models.CanadaDutyStatusModel;
import com.models.PrePostModel;

import java.util.List;

public class CanDotDutyStatusAdapter extends RecyclerView.Adapter<CanDotDutyStatusAdapter.CustomViewHolder> {

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
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_duty_status_change_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.dutyStatusDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.trailer_dialog_background));
        }else{
            holder.dutyStatusDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
        }

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

        holder.distanceAccDotTV.setText(constants.checkNullString(itemsList.get(position).getAccumulatedVehicleKm()));
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
        // constants.setMarqueonView(holder.dateTimeDotTV);
        constants.setMarqueonView(holder.eventDotTV);
        constants.setMarqueonView(holder.distanceAccDotTV);
        constants.setMarqueonView(holder.hrsAccDotTV);
        //  constants.setMarqueonView(holder.distanceTotalDotTV);
        constants.setMarqueonView(holder.recStatusDotTV);
        constants.setMarqueonView(holder.recOriginDotTV);
        constants.setMarqueonView(holder.seqNoDotTV);
        constants.setMarqueonView(holder.distanceCordDotTV);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView dateTimeDotTV, eventDotTV, cmvDotTV, distanceAccDotTV, hrsAccDotTV, distanceTotalDotTV, recStatusDotTV, recOriginDotTV, seqNoDotTV;
        TextView dateTimeDiffTV, distanceCordDotTV, latLongDotTV, geoLocDotTV;
        LinearLayout dutyStatusDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

//            final ViewHolder holder;
//
//            if (convertView == null) {
//                holder = new ViewHolder();
//                convertView = mInflater.inflate(R.layout.item_duty_status_change_dot, null);

                dutyStatusDotLay     = (LinearLayout) itemView.findViewById(R.id.dutyStatusDotLay);

                dateTimeDiffTV       = (TextView) itemView.findViewById(R.id.dateTimeDiffTV);
                distanceCordDotTV       = (TextView) itemView.findViewById(R.id.distanceCordDotTV);
                latLongDotTV       = (TextView) itemView.findViewById(R.id.latLongDotTV);
                geoLocDotTV       = (TextView) itemView.findViewById(R.id.geoLocDotTV);

                dateTimeDotTV        = (TextView) itemView.findViewById(R.id.dateTimeDotTV);
                eventDotTV           = (TextView) itemView.findViewById(R.id.eventDotTV);
                cmvDotTV             = (TextView) itemView.findViewById(R.id.cmvDotTV);

                distanceAccDotTV     = (TextView) itemView.findViewById(R.id.distanceAccDotTV);
                hrsAccDotTV          = (TextView) itemView.findViewById(R.id.hrsAccDotTV);
                distanceTotalDotTV   = (TextView) itemView.findViewById(R.id.distanceTotalDotTV);

                recStatusDotTV       = (TextView) itemView.findViewById(R.id.recStatusDotTV);
                recOriginDotTV       = (TextView) itemView.findViewById(R.id.recOriginDotTV);
                seqNoDotTV           = (TextView) itemView.findViewById(R.id.seqNoDotTV);



        }
    }

}