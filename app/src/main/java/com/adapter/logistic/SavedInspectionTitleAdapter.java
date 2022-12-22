package com.adapter.logistic;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.SharedPref;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
import com.models.SavedInspectionModel;

import java.util.List;

public class SavedInspectionTitleAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    List<SavedInspectionModel> TitleList;
    String inspectionType;

    public  SavedInspectionTitleAdapter(Context c, String type, List<SavedInspectionModel> titleList) {
        this.mContext      = c;
        this.TitleList     = titleList;
        this.mInflater     = LayoutInflater.from(mContext);
        inspectionType     = type;
    }

    @Override
    public int getCount() {
        return TitleList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return TitleList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder ;
        String headerTitle = TitleList.get(position).getHeaderTitle();

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_inspection, null);

            holder.checkboxInspection = (CheckBox)convertView.findViewById(R.id.checkboxInspection);
            holder.inspectionTruckTV = (TextView) convertView.findViewById(R.id.inspectionTruckTV);
            holder.inspectionTimeTV  = (TextView) convertView.findViewById(R.id.inspectionTimeTV);
            holder.inspectionItemLay = (LinearLayout)convertView.findViewById(R.id.inspectionItemLay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.checkboxInspection.setVisibility(View.INVISIBLE);
        holder.inspectionTimeTV.setVisibility(View.VISIBLE);

        holder.inspectionTruckTV.setTypeface(null, Typeface.BOLD);
        if(UILApplication.getInstance().isNightModeEnabled()){
            holder.inspectionItemLay.setBackgroundColor(mContext.getResources().getColor(R.color.unselect_button));
            holder.inspectionTruckTV.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.inspectionTimeTV.setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            holder.inspectionItemLay.setBackgroundColor(mContext.getResources().getColor(R.color.eld_gray_bg));
            holder.inspectionTruckTV.setTextColor(mContext.getResources().getColor(R.color.color_eld_bg));
            holder.inspectionTimeTV.setTextColor(mContext.getResources().getColor(R.color.color_eld_bg));
        }

        if(inspectionType.contains("pti")){
            holder.inspectionTruckTV.setText(headerTitle);
        }else{
            holder.inspectionTruckTV.setText(mContext.getResources().getString(R.string.ctPat));
        }

        if(SharedPref.isTimestampEnabled(mContext)) {
            String savedDate = TitleList.get(position).getInspectionDateTime();
            String shippingTime = "";
            if (savedDate != null && savedDate.length() > 11) {
                shippingTime = Globally.ConvertTo12HTimeFormat(savedDate, Globally.DateFormatWithMillSec);
            }
            holder.inspectionTimeTV.setText(shippingTime);
        }

        return convertView;
    }


    public class ViewHolder {
        CheckBox checkboxInspection;
        TextView inspectionTruckTV, inspectionTimeTV;
        LinearLayout inspectionItemLay;
    }


}
