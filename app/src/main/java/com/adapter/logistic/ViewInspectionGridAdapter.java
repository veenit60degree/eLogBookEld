package com.adapter.logistic;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.R;

import java.util.ArrayList;

/**
 * Created by kumar on 12/22/2017.
 */

public class ViewInspectionGridAdapter extends BaseAdapter {

    private Context mContext;
    LayoutInflater mInflater;
    ArrayList<String> SelectedItemArray;
    boolean clicked;

    public ViewInspectionGridAdapter(Context c, ArrayList<String> selectedItemArray,boolean checked) {
        mContext = c;
        SelectedItemArray = selectedItemArray;
        clicked = checked;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return SelectedItemArray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return SelectedItemArray.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_inspection, null);

            holder.inspectionItemLay       = (LinearLayout)convertView.findViewById(R.id.inspectionItemLay);
            holder.inspectionTruckTV       = (TextView)convertView.findViewById(R.id.inspectionTruckTV);
            holder.checkboxInspection      = (CheckBox) convertView.findViewById(R.id.checkboxInspection);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.inspectionTruckTV.setText(SelectedItemArray.get(position));

// holder.inspectionItemLay.getLayoutParams().height;  //holder.inspectionItemLay.getHeight()
        if(clicked == true && position == 0){
            holder.checkboxInspection.setChecked(false);
        }else {
            holder.checkboxInspection.setChecked(true);
        }
        holder.checkboxInspection.setClickable(false);

        if(Constants.inspectionViewHeight == 0) {

            ViewTreeObserver vto = holder.inspectionItemLay.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        holder.inspectionItemLay.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        holder.inspectionItemLay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    //int width = holder.itemParentLay.getMeasuredWidth();
                    Constants.inspectionViewHeight =  holder.inspectionItemLay.getMeasuredHeight();

                }
            });

        }


        return convertView;
    }




    public class ViewHolder {
        TextView inspectionTruckTV;
        CheckBox checkboxInspection;
        LinearLayout inspectionItemLay;
    }

}
