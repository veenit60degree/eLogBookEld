package com.adapter.logistic;

import android.content.Context;
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
import com.messaging.logistic.UILApplication;
import com.models.CanadaDutyStatusModel;
import com.models.PrePostModel;

import java.util.List;

public class CanDotEnginePowerAdapter extends RecyclerView.Adapter<CanDotEnginePowerAdapter.CustomViewHolder> {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotEnginePowerAdapter(Context c, List<CanadaDutyStatusModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_engine_power_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.enginePwrDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.layout_color_dot));
        }else{
            holder.enginePwrDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
        }

        String EventDateTime = itemsList.get(position).getDateTimeWithMins();
        try {
            if (position == 0) {
                holder.dateTimeDiffEngineTV.setVisibility(View.VISIBLE);
                holder.dateTimeDiffEngineTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));
                /*if(EventDateTime.length() >= 19) {
                    holder.dateTimeDiffEngineTV.setText(EventDateTime.substring(11, 19));
                }*/
            } else {
                int dayDiff = constants.getDayDiff(itemsList.get(position-1).getDateTimeWithMins(), EventDateTime);
                if (dayDiff != 0){
                    holder.dateTimeDiffEngineTV.setVisibility(View.VISIBLE);
                    holder.dateTimeDiffEngineTV.setText(Globally.ConvertDateFormatddMMMyyyy(EventDateTime, Globally.DateFormat_mm_dd_yy));

                    /*if(EventDateTime.length() >= 19) {
                        holder.dateTimeDiffEngineTV.setText(EventDateTime.substring(11, 19));
                    }*/

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

       /* if(EventDateTime.length() > 16) {
            holder.dateTimeEDotTV.setText(EventDateTime.substring(11, 16));
        }*/

        if(EventDateTime.length() >= 19) {
            holder.dateTimeEDotTV.setText(EventDateTime.substring(11, 19));
        }

        // holder.dateTimeEDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getDateTimeWithMins() ));
        holder.eventDotETV.setText(constants.getEnginePowerUpDownEventName(
                itemsList.get(position).getEventType(),
                itemsList.get(position).getEventCode()));
        holder.jeoLocEDotTV.setText(itemsList.get(position).getAnnotation());

        holder.distanceAccDotTV.setText(constants.checkNullString(itemsList.get(position).getDistanceSinceLastValidCord()));
        holder.cmvEDotTV.setText(constants.checkNullString(itemsList.get(position).getTruckEquipmentNo()));
        holder.distanceTotalEDotTV.setText(itemsList.get(position).getTotalVehicleKM());

        String EngineHour = itemsList.get(position).getTotalEngineHours();
        if(EngineHour.length() > 0 && !EngineHour.equals("--") && !EngineHour.equals("null")) {
            EngineHour = constants.Convert1DecimalPlacesDouble(Double.parseDouble(EngineHour));
        }

        holder.hoursTotalEDotTV.setText(EngineHour);
        holder.seqNoEDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());
        holder.latLonEDotTV.setText(""+itemsList.get(position).getGPSLatitude() + ", " + itemsList.get(position).getGPSLongitude());

        // Set text style normal
        constants.setTextStyleNormal(holder.dateTimeEDotTV);
        constants.setTextStyleNormal(holder.eventDotETV);
        constants.setTextStyleNormal(holder.jeoLocEDotTV);

        constants.setTextStyleNormal(holder.distanceAccDotTV);
        constants.setTextStyleNormal(holder.cmvEDotTV);
        constants.setTextStyleNormal(holder.distanceTotalEDotTV);

        constants.setTextStyleNormal(holder.hoursTotalEDotTV);
        constants.setTextStyleNormal(holder.seqNoEDotTV);
        constants.setTextStyleNormal(holder.latLonEDotTV);


        // set Marque on view
        constants.setMarqueonView(holder.dateTimeEDotTV);
        constants.setMarqueonView(holder.eventDotETV);
        constants.setMarqueonView(holder.jeoLocEDotTV);
        constants.setMarqueonView(holder.distanceAccDotTV);
        constants.setMarqueonView(holder.cmvEDotTV);
        constants.setMarqueonView(holder.distanceTotalEDotTV);
        constants.setMarqueonView(holder.hoursTotalEDotTV);
        constants.setMarqueonView(holder.seqNoEDotTV);
        constants.setMarqueonView(holder.latLonEDotTV);

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView dateTimeEDotTV, eventDotETV, jeoLocEDotTV, distanceAccDotTV, cmvEDotTV, distanceTotalEDotTV, hoursTotalEDotTV, seqNoEDotTV ;
        TextView dateTimeDiffEngineTV, latLonEDotTV;
        LinearLayout enginePwrDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

            enginePwrDotLay     = (LinearLayout) itemView.findViewById(R.id.enginePwrDotLay);

            dateTimeDiffEngineTV= (TextView) itemView.findViewById(R.id.dateTimeDiffEngineTV);
            latLonEDotTV        = (TextView) itemView.findViewById(R.id.latLonEDotTV);

            dateTimeEDotTV      = (TextView) itemView.findViewById(R.id.dateTimeEDotTV);
            eventDotETV         = (TextView) itemView.findViewById(R.id.eventDotETV);
            jeoLocEDotTV        = (TextView) itemView.findViewById(R.id.jeoLocEDotTV);

            distanceAccDotTV    = (TextView) itemView.findViewById(R.id.distanceAccDotTV);
            cmvEDotTV           = (TextView) itemView.findViewById(R.id.cmvEDotTV);
            distanceTotalEDotTV = (TextView) itemView.findViewById(R.id.distanceTotalEDotTV);

            hoursTotalEDotTV    = (TextView) itemView.findViewById(R.id.hoursTotalEDotTV);
            seqNoEDotTV         = (TextView) itemView.findViewById(R.id.seqNoEDotTV);



        }
    }


}