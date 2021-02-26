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

public class CanDotAddHrsAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotAddHrsAdapter(Context c, List<CanadaDutyStatusModel> list) {
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
            convertView = mInflater.inflate(R.layout.item_additional_hr_dot, null);

            holder.addHrsDotLay         = (LinearLayout) convertView.findViewById(R.id.addHrsDotLay);

            holder.dateDotTV            = (TextView) convertView.findViewById(R.id.dateDotTV);
            holder.shiftStartDotTV      = (TextView) convertView.findViewById(R.id.shiftStartDotTV);
            holder.shiftEndDotTV        = (TextView) convertView.findViewById(R.id.shiftEndDotTV);

            holder.totalHrOnDutyDotTV   = (TextView) convertView.findViewById(R.id.totalHrOnDutyDotTV);
            holder.totalHrOffDutyDotTV  = (TextView) convertView.findViewById(R.id.totalHrOffDutyDotTV);
            holder.cmvAHDotTV           = (TextView) convertView.findViewById(R.id.cmvAHDotTV);

            holder.recStatusAHDotTV     = (TextView) convertView.findViewById(R.id.recStatusAHDotTV);
            holder.recOriginAHDotTV     = (TextView) convertView.findViewById(R.id.recOriginAHDotTV);
            holder.seqNoDotTV           = (TextView) convertView.findViewById(R.id.seqNoDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.addHrsDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.dateDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getEventDate()));
        holder.shiftStartDotTV.setText(itemsList.get(position).getWorkShiftStart());
        holder.shiftEndDotTV.setText(itemsList.get(position).getWorkShiftEnd());
        holder.totalHrOnDutyDotTV.setText(itemsList.get(position).getOnDutyHours());
        holder.totalHrOffDutyDotTV.setText(itemsList.get(position).getOffDutyHours());

        holder.cmvAHDotTV.setText(itemsList.get(position).getCMVVIN());
        holder.recStatusAHDotTV.setText(""+itemsList.get(position).getRecordStatus());
        holder.recOriginAHDotTV.setText(itemsList.get(position).getRecordOrigin());
        holder.seqNoDotTV.setText(""+itemsList.get(position).getSequenceNumber());


        // Set text style normal
        constants.setTextStyleNormal(holder.dateDotTV);
        constants.setTextStyleNormal(holder.shiftStartDotTV);
        constants.setTextStyleNormal(holder.shiftEndDotTV);

        constants.setTextStyleNormal(holder.totalHrOnDutyDotTV);
        constants.setTextStyleNormal(holder.totalHrOffDutyDotTV);
        constants.setTextStyleNormal(holder.cmvAHDotTV);

        constants.setTextStyleNormal(holder.recStatusAHDotTV);
        constants.setTextStyleNormal(holder.recOriginAHDotTV);
        constants.setTextStyleNormal(holder.seqNoDotTV);


        return convertView;
    }


    public class ViewHolder {
        TextView dateDotTV, shiftStartDotTV, shiftEndDotTV, totalHrOnDutyDotTV, totalHrOffDutyDotTV, cmvAHDotTV, recStatusAHDotTV, recOriginAHDotTV, seqNoDotTV;
        LinearLayout addHrsDotLay;

    }

}