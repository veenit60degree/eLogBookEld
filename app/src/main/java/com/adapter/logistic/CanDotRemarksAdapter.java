package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.R;
import com.models.PrePostModel;

import java.util.List;

public class CanDotRemarksAdapter extends BaseAdapter {

    private Context mContext;
    private final List<PrePostModel> itemsList;
    LayoutInflater mInflater;
    Constants constants;

    public CanDotRemarksAdapter(Context c, List<PrePostModel> list) {
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

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }



        holder.remarksAnoDotLay.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        holder.dateRemarksDotTV.setText(itemsList.get(position).getName());
        holder.timeDotTV.setText(itemsList.get(position).getName());
        holder.usernameDotTV.setText(itemsList.get(position).getName());

        holder.sqNoRemDotTV.setText(itemsList.get(position).getName());
        holder.commAnotnDotTV.setText(itemsList.get(position).getName());

        // Set text style normal
        constants.setTextStyleNormal(holder.dateRemarksDotTV);
        constants.setTextStyleNormal(holder.timeDotTV);
        constants.setTextStyleNormal(holder.usernameDotTV);

        constants.setTextStyleNormal(holder.sqNoRemDotTV);
        constants.setTextStyleNormal(holder.commAnotnDotTV);

        return convertView;
    }


    public class ViewHolder {
        TextView dateRemarksDotTV, timeDotTV, usernameDotTV, sqNoRemDotTV, commAnotnDotTV  ;
        LinearLayout remarksAnoDotLay;
    }


}