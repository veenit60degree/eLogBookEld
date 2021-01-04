package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.RecapSignModel;
import com.models.TripHistoryModel;

import java.util.List;

public class RecapRecordSignAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    List<RecapSignModel> transferList;

    public RecapRecordSignAdapter(Context context, List<RecapSignModel> transferList) {
        this.context = context;
        this.transferList = transferList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return transferList.size();
    }

    @Override
    public Object getItem(int position) {
        return transferList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final RecapSignModel eventItem = (RecapSignModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.popup_sign_record, null);

            holder.dateSignTxtView      = (TextView) convertView.findViewById(R.id.dateSignTxtView);
            holder.certifyStatusTxtView = (TextView) convertView.findViewById(R.id.certifyStatusTxtView);
            holder.noSignErrorImg       = (ImageView)convertView.findViewById(R.id.noSignErrorImg);


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        String date = Globally.dateConversionMonthNameWithDay(eventItem.getDate().toString());

        holder.dateSignTxtView.setText(date);

        if(eventItem.isCertified()){
            holder.certifyStatusTxtView.setText(context.getResources().getString(R.string.certified));
            holder.certifyStatusTxtView.setTextColor(context.getResources().getColor(R.color.blue_button));
            holder.certifyStatusTxtView.setBackgroundResource(R.drawable.certify_border);
            holder.noSignErrorImg.setVisibility(View.GONE);
        }else{
            holder.certifyStatusTxtView.setText(context.getResources().getString(R.string.uncertified));
            holder.certifyStatusTxtView.setTextColor(context.getResources().getColor(R.color.colorVoilation));
            holder.certifyStatusTxtView.setBackgroundResource(R.drawable.uncertify_border);
            holder.noSignErrorImg.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    public class ViewHolder {
        TextView dateSignTxtView, certifyStatusTxtView;
        ImageView noSignErrorImg;

    }

}