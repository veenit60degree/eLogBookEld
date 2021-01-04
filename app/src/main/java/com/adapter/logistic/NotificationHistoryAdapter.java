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
import com.models.Notification18DaysModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationHistoryAdapter extends BaseAdapter {

    Context context;
    Globally global;
    LayoutInflater mInflater;
    LayoutInflater inflater;
    List<Notification18DaysModel> notificationList;

    public NotificationHistoryAdapter(Context context, List<Notification18DaysModel> notiList){
        global = new Globally();
        this.context = context;
        this.notificationList = notiList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        Notification18DaysModel itemModel = notificationList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_noti_history, null);

            holder.historyTitleTV       = (TextView)convertView.findViewById(R.id.historyTitleTV);
            holder.historyDescTV        = (TextView)convertView.findViewById(R.id.historyDescTV);
            holder.historyDurationTV    = (TextView)convertView.findViewById(R.id.historyDurationTV);

            holder.deleteHistoryBtn     = (ImageView)convertView.findViewById(R.id.deleteHistoryBtn);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        String title = itemModel.getTitle();
      /*  String[] detail = itemModel.getNotificationDetails().split(";");
        if(detail.length > 0){
            title = detail[0];
        }
*/
        holder.historyTitleTV.setText(title);
        holder.historyDescTV.setText(itemModel.getMessage());

      //  holder.historyDurationTV.setText();
        String CurrentDate  =  global.getCurrentDate();
        String selectedDate =  String.valueOf(itemModel.getSendDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Globally.DateFormat);  //:SSSZ
        Date DateCurrent = new Date();
        Date DateItem = new Date();

        try {
            DateCurrent = simpleDateFormat.parse(CurrentDate);
            DateItem = simpleDateFormat.parse(selectedDate);
        }catch (Exception e){
            e.printStackTrace();
        }

        DateDifference(DateItem, DateCurrent, holder.historyDurationTV);

        if(title.equals(context.getResources().getString(R.string.violation))){
            holder.historyTitleTV.setTextColor(context.getResources().getColor(R.color.colorVoilation));
        }else  if(title.equals(context.getResources().getString(R.string.warning))){
            holder.historyTitleTV.setTextColor(context.getResources().getColor(R.color.warning));
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
        TextView historyTitleTV, historyDescTV, historyDurationTV;
        ImageView deleteHistoryBtn;
    }



     void DateDifference(Date startDate, Date endDate, TextView view) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

         long elapsedHours = different / hoursInMilli;
         different = different % hoursInMilli;

         long elapsedMinutes = different / minutesInMilli;
        // different = different % minutesInMilli;


         String time = "";
         if(elapsedDays > 0)
             time = elapsedDays + " days ago";
         else if(elapsedHours > 0)
             time = elapsedHours + " hours ago";
         else
             time = elapsedMinutes + " mins ago";

         view.setText(time);

    }



}
