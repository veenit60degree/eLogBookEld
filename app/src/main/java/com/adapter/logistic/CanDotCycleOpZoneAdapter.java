package com.adapter.logistic;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.CanadaDutyStatusModel;

import java.util.List;

public class CanDotCycleOpZoneAdapter extends RecyclerView.Adapter<CanDotCycleOpZoneAdapter.CustomViewHolder> {

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
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_cycle_op_zone_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.addHrsDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

        String EventDateTime = itemsList.get(position).getDateTimeWithMins();
        try {
            if (position == 0) {
                holder.dateTimeDiffCycleTV.setVisibility(View.VISIBLE);
                holder.dateTimeDiffCycleTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));
               /* if(EventDateTime.length() >= 19) {
                    holder.dateTimeDiffCycleTV.setText(EventDateTime.substring(11, 19));
                }*/
            } else {
                int dayDiff = constants.getDayDiff(itemsList.get(position-1).getDateTimeWithMins(), EventDateTime);
                if (dayDiff != 0){
                    holder.dateTimeDiffCycleTV.setVisibility(View.VISIBLE);
                    holder.dateTimeDiffCycleTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));
                    /*if(EventDateTime.length() >= 19) {
                        holder.dateTimeDiffCycleTV.setText(EventDateTime.substring(11, 19));
                    }*/
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        // holder.dateCycleDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getEventDate()));
       /* if(EventDateTime.length() > 16) {
            holder.dateCycleDotTV.setText(EventDateTime.substring(11, 16));
        }*/

        if(EventDateTime.length() >= 19) {
            holder.dateCycleDotTV.setText(EventDateTime.substring(11, 19));
        }

        holder.eventCycleEventTV.setText(Html.fromHtml(itemsList.get(position).getRemarks()) ); // constants.getCycleOpZoneEventName( itemsList.get(position).getEventType(), itemsList.get(position).getEventCode())

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
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView dateCycleDotTV, eventCycleEventTV, geoLocCycleTV, latLongCycleTV, distanceLastCoCycleTV;
        TextView cmvAHDotTV, recStatusAHDotTV, recOriginAHDotTV, seqNoDotTV;
        TextView dateTimeDiffCycleTV;
        LinearLayout addHrsDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

            addHrsDotLay             = (LinearLayout) itemView.findViewById(R.id.addHrsDotLay);

            dateTimeDiffCycleTV      = (TextView) itemView.findViewById(R.id.dateTimeDiffCycleTV);

            dateCycleDotTV           = (TextView) itemView.findViewById(R.id.dateCycleDotTV);
            eventCycleEventTV        = (TextView) itemView.findViewById(R.id.eventCycleEventTV);
            geoLocCycleTV            = (TextView) itemView.findViewById(R.id.geoLocCycleTV);

            latLongCycleTV           = (TextView) itemView.findViewById(R.id.latLongCycleTV);
            distanceLastCoCycleTV    = (TextView) itemView.findViewById(R.id.distanceLastCoCycleTV);
            cmvAHDotTV               = (TextView) itemView.findViewById(R.id.cmvAHDotTV);

            recStatusAHDotTV         = (TextView) itemView.findViewById(R.id.recStatusAHDotTV);
            recOriginAHDotTV         = (TextView) itemView.findViewById(R.id.recOriginAHDotTV);
            seqNoDotTV               = (TextView) itemView.findViewById(R.id.seqNoDotTV);



        }
    }

}