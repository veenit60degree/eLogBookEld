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
import com.models.UnAssignedVehicleModel;

import java.util.List;

public class CanDotUnAssignedVehAdapter extends BaseAdapter {
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
            convertView = mInflater.inflate(R.layout.item_unidentified_veh_dot, null);

            holder.unidentifiedVehDotLay     = (LinearLayout) convertView.findViewById(R.id.unidentifiedVehDotLay);

            holder.truckNoUnTV      = (TextView) convertView.findViewById(R.id.truckNoUnTV);
            holder.vinUnLay         = (TextView) convertView.findViewById(R.id.vinUnLay);
            holder.startOdoUnTV     = (TextView) convertView.findViewById(R.id.startOdoUnTV);
            holder.endOdoUnTV       = (TextView) convertView.findViewById(R.id.endOdoUnTV);

            holder.startLocUnTV     = (TextView) convertView.findViewById(R.id.startLocUnTV);
            holder.endLocUnTV       = (TextView) convertView.findViewById(R.id.endLocUnTV);

            holder.totalKmUnTV      = (TextView) convertView.findViewById(R.id.totalKmUnTV);
            holder.totalMilesUnTV   = (TextView) convertView.findViewById(R.id.totalMilesUnTV);
            holder.startTimeUnTV    = (TextView) convertView.findViewById(R.id.startTimeUnTV);
            holder.endTimeUnTV      = (TextView) convertView.findViewById(R.id.endTimeUnTV);

            holder.statusUnTV       = (TextView) convertView.findViewById(R.id.statusUnTV);
            holder.seqNoUnTV        = (TextView) convertView.findViewById(R.id.seqNoUnTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.unidentifiedVehDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.truckNoUnTV.setText( itemsList.get(position).getEquipmentNumber());
        holder.vinUnLay.setText( itemsList.get(position).getVIN());
        holder.startOdoUnTV.setText( itemsList.get(position).getStartOdometer());
        holder.endOdoUnTV.setText( itemsList.get(position).getEndOdometer());

        holder.startLocUnTV.setText( itemsList.get(position).getStartLocation());
        holder.endLocUnTV.setText( itemsList.get(position).getEndLocation());

        holder.totalKmUnTV.setText( itemsList.get(position).getTotalKm());
        holder.totalMilesUnTV.setText( itemsList.get(position).getTotalMiles());

        //holder.statusUnTV.setText( itemsList.get(position).getStatusId());
        holder.statusUnTV.setText( itemsList.get(position).getDutyStatus());
        holder.seqNoUnTV.setText( itemsList.get(position).getHexaSeqNumber());

        holder.startTimeUnTV.setText(Globally.ConvertTo12HTimeFormat(itemsList.get(position).getDriverZoneStartDateTime(), Globally.DateFormat));
        holder.endTimeUnTV.setText(Globally.ConvertTo12HTimeFormat(itemsList.get(position).getDriverZoneEndDateTime(), Globally.DateFormat));

        // Set text style normal
        constants.setTextStyleNormal(holder.truckNoUnTV);
        constants.setTextStyleNormal(holder.vinUnLay);
        constants.setTextStyleNormal(holder.startOdoUnTV);

        constants.setTextStyleNormal(holder.endOdoUnTV);
        constants.setTextStyleNormal(holder.totalKmUnTV);
        constants.setTextStyleNormal(holder.totalMilesUnTV);

        constants.setTextStyleNormal(holder.startTimeUnTV);
        constants.setTextStyleNormal(holder.endTimeUnTV);
        constants.setTextStyleNormal(holder.statusUnTV);
        constants.setTextStyleNormal(holder.seqNoUnTV);


        // set Marque on view
        constants.setMarqueonView(holder.truckNoUnTV);
        constants.setMarqueonView(holder.vinUnLay);
        constants.setMarqueonView(holder.startOdoUnTV);
        constants.setMarqueonView(holder.endOdoUnTV);
        constants.setMarqueonView(holder.totalKmUnTV);
        constants.setMarqueonView(holder.totalMilesUnTV);
        constants.setMarqueonView(holder.startTimeUnTV);
        constants.setMarqueonView(holder.endTimeUnTV);
        constants.setMarqueonView(holder.statusUnTV);
        constants.setMarqueonView(holder.seqNoUnTV);

        return convertView;
    }


    public class ViewHolder {
        TextView truckNoUnTV, vinUnLay, startOdoUnTV, endOdoUnTV, totalKmUnTV, totalMilesUnTV, startTimeUnTV, endTimeUnTV, statusUnTV, seqNoUnTV;
        TextView startLocUnTV, endLocUnTV;
        LinearLayout unidentifiedVehDotLay;

    }

}
