package com.adapter.logistic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.constants.RecycleViewClickListener;
import com.constants.RecyclerViewItemClickListener;
import com.driver.details.DriverConst;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.NotificationHistoryFragment;
import com.models.Notification18DaysModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationHistoryRecylerAdapter extends RecyclerView.Adapter<NotificationHistoryRecylerAdapter.NotificationViewHolder> {

    private  List<Notification18DaysModel> notificationList;
    private Globally global;
    Context context;
    boolean isItemSelected;
    public ArrayList<Integer> selectedPosArray ;

    public NotificationHistoryRecylerAdapter(Context context, Globally global, List<Notification18DaysModel> data) {
        this.context = context;
        this.global = global;
        this.notificationList = data;
        isItemSelected = false;
        selectedPosArray = new ArrayList<Integer>();
    }



    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView historyTitleTV, historyDescTV, historyDurationTV;
        private RelativeLayout notiRecycleMainView;
        private ImageView deleteHistoryBtn;


        public NotificationViewHolder(View itemView) {
            super(itemView);

            historyTitleTV       = itemView.findViewById(R.id.historyTitleTV);
            historyDescTV        = itemView.findViewById(R.id.historyDescTV);
            historyDurationTV    = itemView.findViewById(R.id.historyDurationTV);

            notiRecycleMainView  = itemView.findViewById(R.id.notiRecycleMainView);
            deleteHistoryBtn     = itemView.findViewById(R.id.deleteHistoryBtn);

        }
    }


    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti_history, parent, false);
        return new NotificationViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final NotificationViewHolder holder, final int position) {

        final Notification18DaysModel itemModel = notificationList.get(position);
        String title = itemModel.getTitle();

        holder.historyTitleTV.setText(title);

        String CurrentDate  = "";

        if(itemModel.getType().equals("Requested")){
            CurrentDate = global.GetCurrentUTCTimeFormat();
            holder.historyTitleTV.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.historyTitleTV.setText(context.getResources().getString(R.string.cycle_change_request));

            String changedCycleName = "";
            String currentCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, context);
            String currentCycleId = itemModel.getNotificationTypeName();
            String changedCycleId = itemModel.getTitle();
            if(currentCycleId.equals(Globally.CANADA_CYCLE_1) || currentCycleId.equals(Globally.CANADA_CYCLE_2)){
                if(changedCycleId.equals(Globally.USA_WORKING_6_DAYS)){
                    changedCycleName = Globally.USA_WORKING_6_DAYS_NAME;
                }else{
                    changedCycleName = Globally.USA_WORKING_7_DAYS_NAME;
                }
            }else{
                if(changedCycleId.equals(Globally.CANADA_CYCLE_1)){
                    changedCycleName = Globally.CANADA_CYCLE_1_NAME;
                }else{
                    changedCycleName = Globally.CANADA_CYCLE_2_NAME;
                }
            }


            holder.historyDescTV.setText(Html.fromHtml(context.getResources().getString(R.string.change_cycle_request) + "<font color='#1A3561'> <b>"+ currentCycle
                    +"</b></font> to<font color='#1A3561'> <b>"+ changedCycleName +"</b></font>.") );

            holder.deleteHistoryBtn.setImageResource(R.drawable.change_cycle);

        }else {
            CurrentDate  =  global.getCurrentDate();
            if (title.equals(context.getResources().getString(R.string.violation))) {
                holder.historyTitleTV.setTextColor(context.getResources().getColor(R.color.colorVoilation));
            } else if (title.equals(context.getResources().getString(R.string.warning))) {
                holder.historyTitleTV.setTextColor(context.getResources().getColor(R.color.warning));
            }
            holder.historyDescTV.setText(itemModel.getMessage());
        }

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

        holder.historyDurationTV.setText(Constants.DateDifference(DateItem, DateCurrent));





        holder.notiRecycleMainView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Log.d("onClick", "onClick" );
                if(isItemSelected) {
                    if(selectedPosArray.contains(position)){
                        view.setBackgroundResource(R.drawable.gray_eld_default);
                        for(int i = 0 ; i < selectedPosArray.size() ; i++){
                            if(selectedPosArray.get(i) == position){
                                selectedPosArray.remove(i);
                                break;
                            }
                        }
                    }else{
                        if(!itemModel.getType().equals("Requested")) {
                            view.setBackgroundResource(R.drawable.gray_eld_hover);
                            selectedPosArray.add(position);
                        }else{
                            Globally.showToast(holder.notiRecycleMainView, context.getResources().getString(R.string.not_able_to_select));
                        }
                    }

                    if(selectedPosArray.size() > 0){
                        NotificationHistoryFragment.deleteNotificationBtn.setVisibility(View.VISIBLE);
                    }else{
                        isItemSelected = false;
                        NotificationHistoryFragment.deleteNotificationBtn.setVisibility(View.GONE);
                    }
                    Log.d("selectedPosArray", "selectedPosArray array: " + selectedPosArray);

                }
            }
        });

        holder.notiRecycleMainView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onLongClick(View view) {
                Log.d("onLongClick", "onLongClick" );
                if(!isItemSelected) {
                    if(!itemModel.getType().equals("Requested")) {
                        view.setBackgroundResource(R.drawable.gray_eld_hover);
                        isItemSelected = true;
                        selectedPosArray.add(position);

                        NotificationHistoryFragment.deleteNotificationBtn.setVisibility(View.VISIBLE);
                    }else{
                        Globally.showToast(holder.notiRecycleMainView, context.getResources().getString(R.string.not_able_to_select));
                    }
                }

                return true;
            }
        }) ;


    holder.deleteHistoryBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!isItemSelected) {
                if(itemModel.getType().equals("Requested")){
                    NotificationHistoryFragment.invisibleNotiBtn.performClick();
                }else {
                    selectedPosArray.add(position);
                    NotificationHistoryFragment.deleteNotificationBtn.performClick();
                }
            }else{
                if(itemModel.getType().equals("Requested")){
                    NotificationHistoryFragment.invisibleNotiBtn.performClick();
                }
            }
        }
    });


    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }




    public void removeItem(int position) {
        notificationList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Notification18DaysModel item, int position) {
        notificationList.add(position, item);
        notifyItemInserted(position);
    }



    public List<Notification18DaysModel>  getData() {
        return notificationList;
    }



}

