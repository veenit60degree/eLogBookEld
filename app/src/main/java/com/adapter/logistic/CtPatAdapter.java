package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.CtPatFragment;
import com.models.PrePostModel;

import java.util.ArrayList;
import java.util.List;

public class CtPatAdapter extends BaseAdapter {

    private Context mContext;
    private final List<PrePostModel> itemsArray;
    LayoutInflater mInflater;
    ArrayList<String> SelectedItemNameArray;
    ArrayList<Integer> SelectedItemIdArray;
    boolean IsChecked;
    public boolean IsClicked;

    public CtPatAdapter(Context c, boolean isChecked, boolean isSelectAllBtnClicked, List<PrePostModel> list,
                        ArrayList<String> selectedItemArray, ArrayList<Integer> selectedItemIdArray) {
        mContext = c;
        itemsArray = list;
        SelectedItemNameArray = selectedItemArray;
        SelectedItemIdArray = selectedItemIdArray;
        IsChecked = isChecked;
        IsClicked = isSelectAllBtnClicked;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return itemsArray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return itemsArray.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_inspection, null);

            holder.inspectionItemLay = (LinearLayout) convertView.findViewById(R.id.inspectionItemLay);
            holder.inspectionTruckTV = (TextView) convertView.findViewById(R.id.inspectionTruckTV);
            holder.checkboxInspection = (CheckBox) convertView.findViewById(R.id.checkboxInspection);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if (IsClicked) {
            if (IsChecked) {
                SelectedItemNameArray.set(position, itemsArray.get(position).getName().trim());
                SelectedItemIdArray.set(position, Integer.valueOf(itemsArray.get(position).getId()));
                holder.checkboxInspection.setChecked(true);
            } else {
                SelectedItemNameArray.set(position, "");
                SelectedItemIdArray.set(position, -1);
                holder.checkboxInspection.setChecked(false);
            }
        }

        holder.inspectionTruckTV.setText(itemsArray.get(position).getName());
        Constants.inspectionLayHeight = holder.inspectionItemLay.getLayoutParams().height;  //holder.inspectionItemLay.getHeight()


        holder.checkboxInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkboxInspection.isChecked()) {
                    SelectedItemNameArray.set(position, itemsArray.get(position).getName().trim());
                    SelectedItemIdArray.set(position, Integer.valueOf(itemsArray.get(position).getId()));
                } else {
                    SelectedItemNameArray.set(position, "");
                    SelectedItemIdArray.set(position, -1);
                }

                CtPatFragment.ctPatInspctTV.performClick();
            }
        });

        return convertView;
    }


    public class ViewHolder {
        TextView inspectionTruckTV;
        CheckBox checkboxInspection;
        LinearLayout inspectionItemLay;
    }

}