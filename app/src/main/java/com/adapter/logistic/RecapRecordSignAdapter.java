package com.adapter.logistic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.custom.dialogs.SignRecordDialog;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.UnidentifiedFragment;
import com.models.RecapSignModel;
import com.models.TripHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class RecapRecordSignAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    List<RecapSignModel> transferList;
    ArrayList<String> SelectedRecordList;
    boolean IsAllSelectedClicked;
    boolean isChecked;

    public RecapRecordSignAdapter(Context context, List<RecapSignModel> transferList, ArrayList<String> recordSelectedArray, boolean IsAllSelected, boolean isChecked) {
        this.context = context;
        this.transferList = transferList;
        this.SelectedRecordList = recordSelectedArray;
        this.IsAllSelectedClicked = IsAllSelected;
        this.isChecked = isChecked;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final RecapSignModel eventItem = (RecapSignModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.popup_sign_record, null);

            holder.dateSignTxtView      = (TextView) convertView.findViewById(R.id.dateSignTxtView);
            holder.certifyStatusTxtView = (TextView) convertView.findViewById(R.id.certifyStatusTxtView);
            holder.noSignErrorImg       = (ImageView)convertView.findViewById(R.id.noSignErrorImg);
            holder.signRecordCheckBox   = (CheckBox)convertView.findViewById(R.id.signRecordCheckBox);
            holder.recapRecordItemLay   = (RelativeLayout)convertView.findViewById(R.id.recapRecordItemLay);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

       // holder.signRecordCheckBox.setVisibility(View.GONE);

        if(IsAllSelectedClicked) {
            if (isChecked && !eventItem.isCertified()) {
                SelectedRecordList.set(position, "selected");
                holder.signRecordCheckBox.setChecked(true);
            } else {
                SelectedRecordList.set(position, "");
                holder.signRecordCheckBox.setChecked(false);
            }
        }


        if(eventItem.isCertified()) {
            holder.signRecordCheckBox.setButtonDrawable(R.drawable.unchecked_mobile_disabled);
          //  holder.dateSignTxtView.setTextColor(context.getResources().getColor(R.color.gray_text));
        }

        holder.signRecordCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!eventItem.isCertified()) {
                    if(holder.signRecordCheckBox.isChecked()){
                        SelectedRecordList.set(position, "selected");
                    }else{
                        SelectedRecordList.set(position, "");
                    }

                    IsAllSelectedClicked = false;
                    SignRecordDialog.isSignItemClicked = true;
                    int count = getSelectedCount();
                    if (count < SelectedRecordList.size()) {
                        SignRecordDialog.selectAllRecordsCheckBox.setChecked(false);
                    } else {
                        SignRecordDialog.selectAllRecordsCheckBox.setChecked(true);
                    }

                    if (count > 0) {
                        SignRecordDialog.certifyRecordBtn.setVisibility(View.VISIBLE);
                    } else {
                        SignRecordDialog.certifyRecordBtn.setVisibility(View.GONE);
                    }
                    SignRecordDialog.isSignItemClicked = false;
                }else{
                    holder.signRecordCheckBox.setChecked(false);
                    Globally.EldScreenToast(holder.signRecordCheckBox, context.getResources().getString(R.string.already_certify_for_this_date),
                            context.getResources().getColor(R.color.active_status_bg));
                }
            }
        });


        holder.recapRecordItemLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignRecordDialog.recapSelectedPosition = position;
                SignRecordDialog.recapRecordInvisibleTv.performClick();
            }
        });


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


    int getSelectedCount(){
        int count = 0;
        for(int i = 0 ; i < SelectedRecordList.size() ; i++){
            if(SelectedRecordList.get(i).equals("selected")){
                count++;
            }
        }
        return count;
    }



    public class ViewHolder {
        TextView dateSignTxtView, certifyStatusTxtView;
        ImageView noSignErrorImg;
        CheckBox signRecordCheckBox;
        RelativeLayout recapRecordItemLay;

    }

}