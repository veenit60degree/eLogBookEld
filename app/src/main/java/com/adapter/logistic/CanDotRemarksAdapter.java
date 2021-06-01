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

public class CanDotRemarksAdapter extends BaseAdapter {

    private Context mContext;
    private final List<CanadaDutyStatusModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotRemarksAdapter(Context c, List<CanadaDutyStatusModel> list) {
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
            convertView = mInflater.inflate(R.layout.item_remarks_ano_dot, null);

            holder.remarksAnoDotLay     = (LinearLayout) convertView.findViewById(R.id.remarksAnoDotLay);

            holder.dateRemarksDotTV     = (TextView) convertView.findViewById(R.id.dateRemarksDotTV);
            holder.timeDotTV            = (TextView) convertView.findViewById(R.id.timeDotTV);
            holder.usernameDotTV        = (TextView) convertView.findViewById(R.id.usernameDotTV);
            holder.sqNoRemDotTV         = (TextView) convertView.findViewById(R.id.sqNoRemDotTV);
            holder.commAnotnDotTV       = (TextView) convertView.findViewById(R.id.commAnotnDotTV);
            holder.editDateCmntDotTV       = (TextView) convertView.findViewById(R.id.editDateCmntDotTV);
            holder.editTimeCmntDotTV       = (TextView) convertView.findViewById(R.id.editTimeCmntDotTV);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.remarksAnoDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

        String eventTime = itemsList.get(position).getDateTimeWithMins();
        holder.dateRemarksDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(eventTime));
        if(eventTime.length() > 16) {
            holder.timeDotTV.setText(eventTime.substring(11, 16));
        }

        //holder.timeDotTV.setText(Globally.ConvertDateFormatddMMMyyyy(itemsList.get(position).getDateTimeWithMins()));

        holder.usernameDotTV.setText(constants.checkNullString(itemsList.get(position).getUserName()));

        holder.sqNoRemDotTV.setText(""+itemsList.get(position).getHexaSeqNumber());
        holder.commAnotnDotTV.setText(constants.checkNullString(itemsList.get(position).getRemarks()));

        String editTime = itemsList.get(position).getEditDateTime();
        holder.editDateCmntDotTV.setText(Globally.ConvertDateFormatMMddyy(editTime));
        if(editTime.length() > 11){
            holder.editTimeCmntDotTV.setText(editTime.substring(11, editTime.length()));
        }
        // Set text style normal
        constants.setTextStyleNormal(holder.dateRemarksDotTV);
        constants.setTextStyleNormal(holder.timeDotTV);
        constants.setTextStyleNormal(holder.usernameDotTV);

        constants.setTextStyleNormal(holder.sqNoRemDotTV);
        constants.setTextStyleNormal(holder.commAnotnDotTV);
        constants.setTextStyleNormal(holder.editDateCmntDotTV);
        constants.setTextStyleNormal(holder.editTimeCmntDotTV);


        // set Marque on view
        constants.setMarqueonView(holder.dateRemarksDotTV);
        constants.setMarqueonView(holder.timeDotTV);
        constants.setMarqueonView(holder.usernameDotTV);
        constants.setMarqueonView(holder.sqNoRemDotTV);
        constants.setMarqueonView(holder.commAnotnDotTV);
        constants.setMarqueonView(holder.editDateCmntDotTV);
        constants.setMarqueonView(holder.editTimeCmntDotTV);

        return convertView;
    }


    public class ViewHolder {
        TextView dateRemarksDotTV, timeDotTV, usernameDotTV, sqNoRemDotTV, commAnotnDotTV  ;
        TextView editDateCmntDotTV, editTimeCmntDotTV;
        LinearLayout remarksAnoDotLay;
    }


}