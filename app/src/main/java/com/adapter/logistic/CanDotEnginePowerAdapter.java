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
import com.models.PrePostModel;

import java.util.List;

public class CanDotEnginePowerAdapter extends BaseAdapter {

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
            convertView = mInflater.inflate(R.layout.item_engine_power_dot, null);

            holder.enginePwrDotLay     = (LinearLayout) convertView.findViewById(R.id.enginePwrDotLay);

            holder.dateTimeEDotTV      = (TextView) convertView.findViewById(R.id.dateTimeEDotTV);
            holder.eventDotETV         = (TextView) convertView.findViewById(R.id.eventDotETV);
            holder.jeoLocEDotTV        = (TextView) convertView.findViewById(R.id.jeoLocEDotTV);

            holder.distanceAccDotTV    = (TextView) convertView.findViewById(R.id.distanceAccDotTV);
            holder.cmvEDotTV           = (TextView) convertView.findViewById(R.id.cmvEDotTV);
            holder.distanceTotalEDotTV = (TextView) convertView.findViewById(R.id.distanceTotalEDotTV);

            holder.hoursTotalEDotTV    = (TextView) convertView.findViewById(R.id.hoursTotalEDotTV);
            holder.seqNoEDotTV         = (TextView) convertView.findViewById(R.id.seqNoEDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.enginePwrDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.dateTimeEDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getEventDate() ));
        holder.eventDotETV.setText(""+itemsList.get(position).getEventCode());
        holder.jeoLocEDotTV.setText(itemsList.get(position).getGPSLatitude() + ", " + itemsList.get(position).getGPSLongitude());

        holder.distanceAccDotTV.setText(itemsList.get(position).getDistanceInKM());
        holder.cmvEDotTV.setText(itemsList.get(position).getCMVVIN());
        holder.distanceTotalEDotTV.setText(itemsList.get(position).getTotalEngineHours());

        holder.hoursTotalEDotTV.setText(itemsList.get(position).getTotalEngineHours());
        holder.seqNoEDotTV.setText(""+itemsList.get(position).getSequenceNumber());


        // Set text style normal
        constants.setTextStyleNormal(holder.dateTimeEDotTV);
        constants.setTextStyleNormal(holder.eventDotETV);
        constants.setTextStyleNormal(holder.jeoLocEDotTV);

        constants.setTextStyleNormal(holder.distanceAccDotTV);
        constants.setTextStyleNormal(holder.cmvEDotTV);
        constants.setTextStyleNormal(holder.distanceTotalEDotTV);

        constants.setTextStyleNormal(holder.hoursTotalEDotTV);
        constants.setTextStyleNormal(holder.seqNoEDotTV);

        return convertView;
    }


    public class ViewHolder {
        TextView dateTimeEDotTV, eventDotETV, jeoLocEDotTV, distanceAccDotTV, cmvEDotTV, distanceTotalEDotTV, hoursTotalEDotTV, seqNoEDotTV ;
        LinearLayout enginePwrDotLay;

    }


}