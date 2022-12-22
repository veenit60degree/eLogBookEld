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
import com.models.UnAssignedVehicleModel;

import java.util.List;

public class CanDotUnAssignedVehAdapter extends RecyclerView.Adapter<CanDotUnAssignedVehAdapter.CustomViewHolder> {
    private Context mContext;
    private final List<UnAssignedVehicleModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotUnAssignedVehAdapter(Context c, List<UnAssignedVehicleModel> list) {
        mContext = c;
        itemsList = list;
        mInflater = LayoutInflater.from(mContext);
        constants = new Constants();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_unidentified_veh_dot, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.unidentifiedVehDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.layout_color_dot));
        }else{
            holder.unidentifiedVehDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
        }

        // truckNoUnTV, statusUnTV, startTimeUnTV, startLocUnTV, startOdoUnTV, seqNoUnTV

        holder.truckNoUnTV.setText( itemsList.get(position).getEquipmentNumber());


        holder.statusUnTV.setText( itemsList.get(position).getDutyStatus());
        //holder.startTimeUnTV.setText( itemsList.get(position).getDriverZoneStartDateTime());

        String date = itemsList.get(position).getDriverZoneStartDateTime();
        if(date.length() >= 19) {
            holder.startTimeUnTV.setText(Globally.ConvertDateFormatMMddyyyy(date) + " " + date.substring(11, 19));//Globally.ConvertTo12HTimeFormat(date, Globally.DateFormat));
        }

        String StartLatitude = itemsList.get(position).getStartLatitude();
        String StartLongitude = itemsList.get(position).getStartLongitude();

        if(!StartLatitude.equals("null") && StartLatitude.length() > 0){
            holder.lstLongUnidenTV.setText(StartLatitude + "," + StartLongitude);
        }else{
            holder.lstLongUnidenTV.setText("--");
        }

        holder.startLocUnTV.setText( itemsList.get(position).getStartLocation());

        holder.startOdoUnTV.setText( itemsList.get(position).getStartOdometer());
        holder.seqNoUnTV.setText( itemsList.get(position).getHexaSeqNumber());


     /*//   holder.vinUnLay.setText( itemsList.get(position).getVIN());
        holder.startOdoUnTV.setText( itemsList.get(position).getStartOdometer());
     //   holder.endOdoUnTV.setText( itemsList.get(position).getEndOdometer());

        holder.startLocUnTV.setText( itemsList.get(position).getStartLocation());
      //  holder.endLocUnTV.setText( itemsList.get(position).getEndLocation());

      //  holder.totalKmUnTV.setText( itemsList.get(position).getTotalKm());
       // holder.totalMilesUnTV.setText( itemsList.get(position).getTotalMiles());

        //holder.statusUnTV.setText( itemsList.get(position).getStatusId());
        holder.statusUnTV.setText( itemsList.get(position).getDutyStatus());
        holder.seqNoUnTV.setText( itemsList.get(position).getHexaSeqNumber());

        holder.startTimeUnTV.setText(Globally.ConvertTo12HTimeFormat(itemsList.get(position).getDriverZoneStartDateTime(), Globally.DateFormat));
*/
        //  holder.endTimeUnTV.setText(Globally.ConvertTo12HTimeFormat(itemsList.get(position).getDriverZoneEndDateTime(), Globally.DateFormat));

        // Set text style normal
        constants.setTextStyleNormal(holder.truckNoUnTV);
        //  constants.setTextStyleNormal(holder.vinUnLay);
        constants.setTextStyleNormal(holder.startOdoUnTV);

        // constants.setTextStyleNormal(holder.endOdoUnTV);
        // constants.setTextStyleNormal(holder.totalKmUnTV);
        // constants.setTextStyleNormal(holder.totalMilesUnTV);

        constants.setTextStyleNormal(holder.startTimeUnTV);
        // constants.setTextStyleNormal(holder.endTimeUnTV);
        constants.setTextStyleNormal(holder.statusUnTV);
        constants.setTextStyleNormal(holder.seqNoUnTV);

        constants.setTextStyleNormal(holder.startLocUnTV);
        constants.setTextStyleNormal(holder.lstLongUnidenTV);


        // set Marque on view
        constants.setMarqueonView(holder.truckNoUnTV);
        //  constants.setMarqueonView(holder.vinUnLay);
        constants.setMarqueonView(holder.startOdoUnTV);
        //  constants.setMarqueonView(holder.endOdoUnTV);
        //  constants.setMarqueonView(holder.totalKmUnTV);
        //  constants.setMarqueonView(holder.totalMilesUnTV);
        constants.setMarqueonView(holder.startTimeUnTV);
        // constants.setMarqueonView(holder.endTimeUnTV);
        constants.setMarqueonView(holder.statusUnTV);
        constants.setMarqueonView(holder.seqNoUnTV);
        constants.setMarqueonView(holder.startLocUnTV);

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView truckNoUnTV, statusUnTV, startTimeUnTV, startLocUnTV, lstLongUnidenTV, startOdoUnTV, seqNoUnTV;
        // TextView vinUnLay, endOdoUnTV, endTimeUnTV, totalKmUnTV, totalMilesUnTV,endLocUnTV;
        LinearLayout unidentifiedVehDotLay;

        public CustomViewHolder(View itemView) {
            super(itemView);

            unidentifiedVehDotLay     = (LinearLayout) itemView.findViewById(R.id.unidentifiedVehDotLay);

            truckNoUnTV      = (TextView) itemView.findViewById(R.id.truckNoUnTV);
            //  holder.vinUnLay         = (TextView) convertView.findViewById(R.id.vinUnLay);
            statusUnTV       = (TextView) itemView.findViewById(R.id.statusUnTV);

            //  holder.endOdoUnTV       = (TextView) convertView.findViewById(R.id.endOdoUnTV);

            startLocUnTV     = (TextView) itemView.findViewById(R.id.startLocUnTV);
            lstLongUnidenTV  = (TextView) itemView.findViewById(R.id.lstLongUnidenTV);

            //  holder.totalKmUnTV      = (TextView) convertView.findViewById(R.id.totalKmUnTV);
            //  holder.totalMilesUnTV   = (TextView) convertView.findViewById(R.id.totalMilesUnTV);
            startTimeUnTV    = (TextView) itemView.findViewById(R.id.startTimeUnTV);
            // holder.endTimeUnTV      = (TextView) convertView.findViewById(R.id.endTimeUnTV);

            startOdoUnTV     = (TextView) itemView.findViewById(R.id.startOdoUnTV);
            seqNoUnTV        = (TextView) itemView.findViewById(R.id.seqNoUnTV);



        }
    }

}
