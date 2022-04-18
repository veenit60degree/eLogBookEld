package com.adapter.logistic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.ShipmentModel;

import java.util.List;

public class ShippingViewDetailAdapter  extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    List<ShipmentModel> shippingList;


    public ShippingViewDetailAdapter(Context c, List<ShipmentModel> supportLst) {
        mContext        = c;
        shippingList     = supportLst;
        mInflater       = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return shippingList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return shippingList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder ;
        ShipmentModel shippingModel = shippingList.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_shipping_options, null);

            holder.shipperNoItemTV       = (TextView)convertView.findViewById(R.id.shipperNoItemTV);
            holder.commodityItemTV       = (TextView)convertView.findViewById(R.id.commodityItemTV);
            holder.shipperNameItemTV     = (TextView)convertView.findViewById(R.id.shipperNameItemTV);
            holder.shipperFromItemTV     = (TextView)convertView.findViewById(R.id.shipperFromItemTV);
            holder.shipperToItemTV       = (TextView)convertView.findViewById(R.id.shipperToItemTV);


            holder.shipperNoItemTV.setText(checkNullData(shippingModel.getBlNoTripNo()));
            holder.commodityItemTV.setText(checkNullData(shippingModel.getCommodity()));
            holder.shipperNameItemTV.setText(checkNullData(shippingModel.getShipperName()));
            holder.shipperFromItemTV.setText(checkNullData(shippingModel.getFromAddress()));
            holder.shipperToItemTV.setText(checkNullData(shippingModel.getToAddress()));

           if(UILApplication.getInstance().isNightModeEnabled()){
               holder.shipperNoItemTV.setTextColor(mContext.getResources().getColor(R.color.white));
               holder.commodityItemTV.setTextColor(mContext.getResources().getColor(R.color.white));
               holder.shipperNameItemTV.setTextColor(mContext.getResources().getColor(R.color.white));
               holder.shipperFromItemTV.setTextColor(mContext.getResources().getColor(R.color.white));
               holder.shipperToItemTV.setTextColor(mContext.getResources().getColor(R.color.white));
            }else{
               holder.shipperNoItemTV.setTextColor(mContext.getResources().getColor(R.color.black_theme));
               holder.commodityItemTV.setTextColor(mContext.getResources().getColor(R.color.black_theme));
               holder.shipperNameItemTV.setTextColor(mContext.getResources().getColor(R.color.black_theme));
               holder.shipperFromItemTV.setTextColor(mContext.getResources().getColor(R.color.black_theme));
               holder.shipperToItemTV.setTextColor(mContext.getResources().getColor(R.color.black_theme));
           }

            holder.shipperNoItemTV.setTypeface(Typeface.DEFAULT);
            holder.commodityItemTV.setTypeface(Typeface.DEFAULT);
            holder.shipperNameItemTV.setTypeface(Typeface.DEFAULT);
            holder.shipperFromItemTV.setTypeface(Typeface.DEFAULT);
            holder.shipperToItemTV.setTypeface(Typeface.DEFAULT);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }


    public class ViewHolder {
        TextView shipperNoItemTV, commodityItemTV, shipperNameItemTV, shipperFromItemTV, shipperToItemTV;
    }


    private String checkNullData(String data){
        if(data.equals("null") || data.length() == 0){
            data = "--";
        }
        return data;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }





}
