package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.driver.details.EldDriverLogModel;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import java.util.List;

public class EditedLogAdapter extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    List<EldDriverLogModel> LogList;

    public EditedLogAdapter(Context context, List<EldDriverLogModel> logList) {
        this.context = context;
        this.mInflater      = LayoutInflater.from(context);
        LogList = logList;
    }


    @Override
    public int getCount() {
        return LogList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return LogList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder ;
        final EldDriverLogModel LogItem = (EldDriverLogModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_edited_log, null);

            holder.editedItemMainLay        = (LinearLayout) convertView.findViewById(R.id.editedItemMainLay);
            holder.editedIcoImgVw           = (ImageView) convertView.findViewById(R.id.editedIcoImgVw);

            holder.statusEditedTxtView      = (TextView)convertView.findViewById(R.id.statusEditedTxtView);
            holder.startTimeEditedTxtView   = (TextView)convertView.findViewById(R.id.startTimeEditedTxtView);
            holder.endTimeEditedTxtView     = (TextView)convertView.findViewById(R.id.endTimeEditedTxtView);
            holder.durationEditedTxtView    = (TextView)convertView.findViewById(R.id.durationEditedTxtView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.editedItemMainLay.setBackgroundColor(context.getResources().getColor(R.color.whiteee));

        String StartTime    = Globally.ConvertToTimeFormat(LogItem.getStartDateTime(), Globally.DateFormatWithMillSec);
        String EndTime      = Globally.ConvertToTimeFormat(LogItem.getEndDateTime(), Globally.DateFormatWithMillSec);
        String Duration     = LogItem.getDuration();

        if(EndTime.equals("23:59")){
            EndTime = "24:00";
           // Duration    = String.valueOf(Integer.valueOf(Duration) + 1);
        }

        setStatusWiseView(LogItem.getDriverStatusId(), holder.statusEditedTxtView, LogItem.isPersonal(), LogItem.isShortHaulException());   // isShortHaulException is used for isYardMove
        holder.startTimeEditedTxtView.setText(StartTime);
        holder.endTimeEditedTxtView.setText(EndTime);
        holder.durationEditedTxtView.setText(Duration);

        boolean isEdited = LogItem.isAdverseException();    // isAdverseException is used here as isEdited value
        if(isEdited){
            holder.startTimeEditedTxtView.setTextColor(context.getResources().getColor(R.color.blue_button));
            holder.endTimeEditedTxtView.setTextColor(context.getResources().getColor(R.color.blue_button));
            holder.durationEditedTxtView.setTextColor(context.getResources().getColor(R.color.blue_button));
            holder.editedIcoImgVw.setVisibility(View.VISIBLE);
        }else{
            holder.startTimeEditedTxtView.setTextColor(context.getResources().getColor(R.color.sleeper_edit));
            holder.endTimeEditedTxtView.setTextColor(context.getResources().getColor(R.color.sleeper_edit));
            holder.durationEditedTxtView.setTextColor(context.getResources().getColor(R.color.sleeper_edit));
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
        TextView statusEditedTxtView, startTimeEditedTxtView, endTimeEditedTxtView, durationEditedTxtView;
        LinearLayout editedItemMainLay;
        ImageView editedIcoImgVw;

    }

    void setStatusWiseView(int status, TextView view, boolean isPersonal, boolean isYardMove){
        switch (status){

            case 1: // OffDuty
                if(isPersonal) {
                    view.setText("PC");
                }else{
                    view.setText("OFF");
                }
                view.setBackgroundResource(R.drawable.off_drawable);
                break;

            case 2: // Sleeper
                view.setText("SB");
                view.setBackgroundResource(R.drawable.sb_drawable);
                break;


            case 3: // Driving
                view.setText("D");
                view.setBackgroundResource(R.drawable.d_drawable);
                break;

            case 4: // OnDuty
                if(isYardMove){
                    view.setText("YM");
                }else {
                    view.setText("ON");
                }
                view.setBackgroundResource(R.drawable.on_drawable);
                break;

        }
    }
}
