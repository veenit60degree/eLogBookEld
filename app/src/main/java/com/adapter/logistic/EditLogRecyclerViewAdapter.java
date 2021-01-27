package com.adapter.logistic;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.constants.Constants;
import com.custom.dialogs.TimerDialog;
import com.local.db.ConstantsKeys;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EditGraphFragment;
import com.models.DriverLogModel;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EditLogRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {



    private List<DriverLogModel> driverLogList;

    OnItemClickListener mItemClickListener;
    private static final int TYPE_ITEM = 0;
    private final LayoutInflater mInflater;
    private final OnStartDragListener mDragStartListener;
    private Context mContext;

    String[] list = {"Off Duty", "Sleeper", "Driving", "On Duty", "Personal", "On Duty (YM)"};
    String SelectedDateTime ;
    int offsetFromUTC,  parentPosition = 0;
    TimerDialog timerDialog;
    JSONObject logPermissionObj;
    DriverPermissionMethod permitMethod;
    boolean isTouch = false, IsCurrentDate;
    HelperMethods hMethods;
    boolean IsOffDutyPermission ,IsSleeperPermission , IsDrivingPermission ,  IsOnDutyPermission;
    final int OFF_DUTY       = 1;
    final int SLEEPER        = 2;
    final int DRIVING        = 3;
    final int ON_DUTY        = 4;
    RecyclerView parentView ;


    public EditLogRecyclerViewAdapter(Context context, RecyclerView eldMenuBtn, List<DriverLogModel> oDriverLogDetail, String selectedDate, int offset,
                                      JSONObject permitLog, DriverPermissionMethod pMethod, HelperMethods h_method, boolean isCurrentDate, OnStartDragListener dragListner) {
        this.driverLogList = oDriverLogDetail;
        this.mInflater = LayoutInflater.from(context);
        this.mDragStartListener = dragListner;
        this.mContext = context;

        this.parentView = eldMenuBtn;
        this.SelectedDateTime = selectedDate;
        this.offsetFromUTC = offset;
        this.logPermissionObj = permitLog;
        this.permitMethod = pMethod;
        this.hMethods     = h_method;
        this.IsCurrentDate = isCurrentDate;

        IsOffDutyPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OffDutyKey);
        IsSleeperPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.SleeperKey);
        IsDrivingPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.DrivingKey);
        IsOnDutyPermission  = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OnDutyKey);



    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view viewHolder
            View v = mInflater.inflate(R.layout.item_edit_log, viewGroup, false);
            return new ViewHolderItem(v );
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {

        String startTime = "", endTime = "";
        int TotalHours = 0, status = 1;
        boolean isPersonal = false, isYardMove = false;

        ((ViewHolderItem) viewHolder).startTimeBtn.setTag(position);
        ((ViewHolderItem) viewHolder).endTimeBtn.setTag(position);


        ((ViewHolderItem) viewHolder).startTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentPosition = viewHolder.getAdapterPosition();  //getParentViewPosition(((ViewHolderItem) viewHolder).editLogSerialNoTV);
                String time = ((ViewHolderItem) viewHolder).startTimeTV.getText().toString() ;
                int Hour = Integer.valueOf(time.split(":")[0] );
                int min = Integer.valueOf(time.split(":")[1] );

                timerDialog = new TimerDialog(mContext, parentPosition, Hour, min, ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeTV, "start", new TimePickerListener());
                timerDialog.show();

            }
        });


        ((ViewHolderItem) viewHolder).endTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parentPosition = viewHolder.getAdapterPosition();  //getParentViewPosition(((ViewHolderItem) viewHolder).editLogSerialNoTV);
                String time = ((ViewHolderItem) viewHolder).endTimeTV.getText().toString() ;
                int Hour = Integer.valueOf(time.split(":")[0] );
                int min = Integer.valueOf(time.split(":")[1] );
                timerDialog = new TimerDialog(mContext, parentPosition, Hour, min, ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeTV, "end", new TimePickerListener());
                timerDialog.show();

            }
        });




        ((ViewHolderItem) viewHolder).editLogStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {

                if(isTouch) {

                    parentPosition = viewHolder.getAdapterPosition();  //getParentViewPosition(((ViewHolderItem) viewHolder).editLogSerialNoTV);

                    isTouch = false;
                    DriverLogModel logModel = EditGraphFragment.oDriverLogDetail.get(parentPosition);

                    logModel.setDriverStatusId(pos + 1);
                    if (pos == 4) { // Check for Personal use
                        logModel.setPersonal(true);
                    } else {
                        logModel.setPersonal(false);
                    }

                    if (pos == 5) { // Check for YardMove
                        logModel.setYardMove(true);
                        logModel.setDriverStatusId(Constants.ON_DUTY);
                    } else {
                        logModel.setYardMove(false);
                    }

                    if(pos != 3 && pos != 5 ) {    // If status is not On Duty
                        logModel.setRemarks("");
                    }

                    EditGraphFragment.oDriverLogDetail.set(parentPosition, logModel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing
            }

        });

        ((ViewHolderItem) viewHolder).editLogStatusSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouch = true;
                return false;
            }
        });





        ((ViewHolderItem) viewHolder).dragLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int touchPos = viewHolder.getAdapterPosition();
                if(position < EditGraphFragment.oDriverLogDetail.size()-1){
                    DriverLogModel logModelNextPos = EditGraphFragment.oDriverLogDetail.get(position );
                    int Status = logModelNextPos.getDriverStatusId();
                    boolean isEnabled = isEnabled(Status);

                   // Log.d("Touch permission", "Touch permission: " + isEnabled);
                    if(isEnabled){
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(viewHolder);
                            Log.d("TouchTest", "Touch down");
                        }

                    }
                }


                return true;
            }
        });

        try {
            DriverLogModel logModel = driverLogList.get(position);
            startTime       = logModel.getStartDateTime().toString();
            endTime         = logModel.getEndDateTime().toString();
            TotalHours      = (int)logModel.getTotalMinutes();
            status          = logModel.getDriverStatusId();
            isPersonal      = logModel.isPersonal();
            isYardMove      = logModel.isYardMove();

            startTime = startTime.substring(11, 16);
            endTime   = endTime.substring(11, 16);

        } catch (Exception e) {
            e.printStackTrace();
        }

        DateTime currenDateTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
        DateTime startDateTime ,endDateTime ;
        if(position == 0){
            startDateTime   = Globally.getDateTimeObj(EditGraphFragment.oDriverLogDetail.get(0).getStartDateTime().toString(), false);
            endDateTime     = Globally.getDateTimeObj(EditGraphFragment.oDriverLogDetail.get(0).getEndDateTime().toString(), false);

            if(endDateTime.isAfter(startDateTime)){
                if(endTime.equals("00:00")){
                    endTime = "23:59";
                }
            }
            CheckTimeInLogEditing(startDateTime, endDateTime, ((ViewHolderItem) viewHolder).endTimeLayout, false, currenDateTime, false);

            String time = startDateTime.toString().substring(11, 16);
            Log.d("time: ", "time: " + time);

            if(time.equals("00:00")){
                EditGraphFragment.IsWrongDateEditLog = false;
                ((ViewHolderItem) viewHolder).startTimeLayout.setBackgroundResource(R.drawable.edit_log_drawable);
            }else{
                EditGraphFragment.IsWrongDateEditLog = true;
                ((ViewHolderItem) viewHolder).startTimeLayout.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }

            if(driverLogList.size() == 1) {
                checkDayEndTime(true, ((ViewHolderItem) viewHolder).endTimeLayout, endDateTime.toString());
            }

        }else{
            DateTime previousEndTime = Globally.getDateTimeObj(EditGraphFragment.oDriverLogDetail.get(position - 1).getEndDateTime().toString(), false );
            startDateTime   = Globally.getDateTimeObj(EditGraphFragment.oDriverLogDetail.get(position).getStartDateTime().toString(), false);
            endDateTime     = Globally.getDateTimeObj(EditGraphFragment.oDriverLogDetail.get(position).getEndDateTime().toString(), false);

            boolean isLast = false;
            if(position == driverLogList.size() -1){
                isLast = true;
            }


            CheckTimeInLogEditing(previousEndTime, startDateTime, ((ViewHolderItem) viewHolder).startTimeLayout, true,currenDateTime, false);
            CheckTimeInLogEditing(startDateTime, endDateTime, ((ViewHolderItem) viewHolder).endTimeLayout, false, currenDateTime, isLast);

            checkDayEndTime(isLast, ((ViewHolderItem) viewHolder).endTimeLayout, endDateTime.toString());

        }


        ((ViewHolderItem) viewHolder).editLogSerialNoTV.setText("" + (position+1));
        ((ViewHolderItem) viewHolder).startTimeTV.setText(startTime);
        ((ViewHolderItem) viewHolder).endTimeTV.setText(endTime);
        ((ViewHolderItem) viewHolder).editLogDurationTV.setText(FinalValue(TotalHours));


        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                mContext,R.layout.item_editlog_spinner, R.id.editlogSpinTV, list){
            @Override
            public boolean isEnabled(int position){

                if(position == OFF_DUTY - 1){  // -1 is applied because position start from 0.
                    return IsOffDutyPermission;
                }else if(position == SLEEPER - 1){
                    return IsSleeperPermission;
                }else if(position == DRIVING - 1){
                    return IsDrivingPermission;
                }else if(position == ON_DUTY - 1){
                    return IsOnDutyPermission;
                }else if( position == ON_DUTY + 1){
                    return IsOnDutyPermission;
                }else{
                    return true;
                }

            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = ((TextView) v);

                if(position == OFF_DUTY - 1){     // -1 is applied because position start from 0.
                    setViewTextColor(tv, IsOffDutyPermission);
                }else if(position == SLEEPER - 1){
                    setViewTextColor(tv, IsSleeperPermission);
                }else if(position == DRIVING - 1){
                    setViewTextColor(tv, IsDrivingPermission);
                }else if(position == ON_DUTY - 1 || position == ON_DUTY + 1 ){
                    setViewTextColor(tv, IsOnDutyPermission);
                }else{
                    setViewTextColor(tv, true);
                }


                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == OFF_DUTY - 1){     // -1 is applied because position start from 0.
                    setViewTextColor(tv, IsOffDutyPermission);
                }else if(position == SLEEPER - 1){
                    setViewTextColor(tv, IsSleeperPermission);
                }else if(position == DRIVING - 1){
                    setViewTextColor(tv, IsDrivingPermission);
                }else if(position == ON_DUTY - 1 || position == ON_DUTY + 1){
                    setViewTextColor(tv, IsOnDutyPermission);
                }else{
                    setViewTextColor(tv, true);
                }

                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_editlog_spinner);
        ((ViewHolderItem) viewHolder).editLogStatusSpinner.setAdapter(spinnerArrayAdapter);


        int spinPos = status - 1;
        if(status == OFF_DUTY ){
            if(isPersonal) {
                ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(4);
                setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                        ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, true);
            }else {
                ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(0);
                setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                        ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, IsOffDutyPermission);
            }
        }else if(status == SLEEPER ){
            ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                    ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, IsSleeperPermission);

        }else if(status == DRIVING ){
            ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                    ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, IsDrivingPermission);

        }else if(status == ON_DUTY ){



            if(isYardMove){
                ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(5);
            }else {
                ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(spinPos);
            }
            setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                    ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, IsOnDutyPermission);
        }else{
            ((ViewHolderItem) viewHolder).editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(((ViewHolderItem) viewHolder).editLogSerialNoTV, ((ViewHolderItem) viewHolder).editLogStatusSpinner, ((ViewHolderItem) viewHolder).startTimeLayout,
                    ((ViewHolderItem) viewHolder).startTimeTV, ((ViewHolderItem) viewHolder).endTimeLayout, ((ViewHolderItem) viewHolder).endTimeTV, ((ViewHolderItem) viewHolder).editLogDurationTV, ((ViewHolderItem) viewHolder).editLogItemLay, position, IsOffDutyPermission);
        }






    }

    @Override
    public int getItemCount() {
        return driverLogList.size();
    }


    @Override
    public void onItemDismiss(int position) {
        driverLogList.remove(position);
        notifyItemRemoved(position);
    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }


    public class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener ,ItemTouchHelperViewHolder{

        private TextView editLogSerialNoTV, startTimeTV, endTimeTV, editLogDurationTV;
        private RelativeLayout startTimeLayout, endTimeLayout, endTimeBtn, startTimeBtn, dragLay;
        private LinearLayout editLogItemLay;
        private Spinner editLogStatusSpinner;


        public ViewHolderItem(View itemView) {
            super(itemView);

            editLogSerialNoTV    = itemView.findViewById(R.id.editLogSerialNoTV);
            startTimeTV          = itemView.findViewById(R.id.startTimeTV);
            endTimeTV            = itemView.findViewById(R.id.endTimeTV);
            editLogDurationTV    = itemView.findViewById(R.id.editLogDurationTV);

            startTimeBtn         = itemView.findViewById(R.id.startTimeIV);
            dragLay              = itemView.findViewById(R.id.dragLay);
            endTimeBtn           = itemView.findViewById(R.id.endTimeIV);
            startTimeLayout      = itemView.findViewById(R.id.startTimeLayout);
            endTimeLayout        = itemView.findViewById(R.id.endTimeLayout);

            editLogItemLay       = itemView.findViewById(R.id.editLogItemLay);

            editLogStatusSpinner = itemView.findViewById(R.id.editLogStatusSpinner);

            editLogItemLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));
           // itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }




    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < driverLogList.size() && toPosition < driverLogList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(driverLogList, i, i + 1);
                  //  Collections.swap(EditGraphFragment.oDriverLogDetail, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(driverLogList, i, i - 1);
                  //  Collections.swap(EditGraphFragment.oDriverLogDetail, i, i - 1);
                }
            }

            notifyItemMoved(fromPosition, toPosition);
         //   notifyDataSetChanged();

        }
        return true;
    }

    public void updateList(List<DriverLogModel> list) {
        driverLogList = list;
        notifyDataSetChanged();
    }


    public void removeItem(int position) {
        driverLogList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(DriverLogModel item, int position) {
        driverLogList.add(position, item);
        notifyItemInserted(position);
    }

    public List<DriverLogModel>  getData() {
        return driverLogList;
    }




    private void checkDayEndTime(boolean isLast, View view, String endDateTime){

        if(isLast){
            if(!IsCurrentDate){
                String time = endDateTime.substring(11, 16);
                if(!time.equals("23:59")){
                    EditGraphFragment.IsWrongDateEditLog = true;
                    view.setBackgroundResource(R.drawable.edit_log_red_drawable);
                }else{
                   // EditGraphFragment.IsWrongDateEditLog = false;
                    view.setBackgroundResource(R.drawable.edit_log_drawable);
                }
            }
        }
    }

    void setViewTextColor(TextView tView, boolean isPermit){
        if(isPermit){
            tView.setTextColor(Color.BLACK);
        }else{
            tView.setTextColor(Color.GRAY);
        }
    }

    void setSpinnerViewEnabledStatus(TextView countTV, Spinner spinner, RelativeLayout startTimeBtn, TextView startTV,
                                     RelativeLayout endTimeBtn, TextView endTV, TextView durationTV, LinearLayout editLogItemLay,
                                     int position, boolean isPermit){
        if(!isPermit){
            spinner.setEnabled(false);
            startTimeBtn.setEnabled(false);
            endTimeBtn.setEnabled(false);
            countTV.setTextColor(Color.GRAY);
            durationTV.setTextColor(Color.GRAY);
            startTV.setTextColor(Color.GRAY);
            endTV.setTextColor(Color.GRAY);
            editLogItemLay.setBackgroundColor(mContext.getResources().getColor(R.color.eld_gray_bg_theme));

        }

        if(position == driverLogList.size() - 1 && isPermit){
            spinner.setEnabled(true);
            startTimeBtn.setEnabled(true);
            endTimeBtn.setEnabled(true);
            countTV.setTextColor(Color.BLACK);
            durationTV.setTextColor(Color.BLACK);
            startTV.setTextColor(Color.BLACK);
            endTV.setTextColor(Color.BLACK);
            editLogItemLay.setBackgroundColor(mContext.getResources().getColor(R.color.whiteee));

        }
    }



    String FinalValue(int min){
        int hour = HourFromMin(min);
        int minut = MinFromHourOnly(min);

        String finalValue = TwoDecimalViewIntegerVal(hour, true) + ":" + TwoDecimalViewIntegerVal(minut, false);
        return finalValue;
    }

    int HourFromMin(int min){
        int hours = min / 60; //since both are ints, you get an int
        return hours;
    }

    int MinFromHourOnly(int min){
        int minutes = min % 60;
        return minutes;
    }




    String TwoDecimalViewIntegerVal(int value, boolean isHour){
        boolean isNegative = false;

        if(value < 0)
            isNegative = true;

        String val = String.valueOf(value);
        val 		= val.replaceAll("-","");
        if(val.trim().length() == 1)
            val = "0" + val;

        if(isNegative && isHour)
            val = "-" + val;

        return val;
    }

    int getParentViewPosition(TextView view){
        return Integer.valueOf(view.getText().toString())-1;
    }


    private class TimePickerListener implements TimerDialog.TimePickerListener{

        @Override
        public void TimePickerReady(int position, int SelectedHour, int SelectedMin, TextView startView, TextView endView, String viewType, TimePicker timePicker) {

            String time = TwoDecimalViewIntegerVal(SelectedHour, true) + ":" + TwoDecimalViewIntegerVal(SelectedMin, false);

            if(viewType.equals("start")){
                startView.setText(time);
            }else{
                endView.setText(time);
            }

            DriverLogModel logModel = getDriverLog(EditGraphFragment.oDriverLogDetail, position, startView.getText().toString(),  endView.getText().toString());
            EditGraphFragment.oDriverLogDetail.set(position, logModel);

            if(position < EditGraphFragment.oDriverLogDetail.size()-1){
                DriverLogModel logModelNextPos = EditGraphFragment.oDriverLogDetail.get(position + 1);
                int Status = logModelNextPos.getDriverStatusId();

                if(isEnabled(Status)){
                    DriverLogModel nextPosLogModel = getDriverNextLog(EditGraphFragment.oDriverLogDetail, position + 1, endView.getText().toString());
                    EditGraphFragment.oDriverLogDetail.set(position + 1, nextPosLogModel);
                }
            }

            timerDialog.dismiss();
            notifyDataSetChanged();
        }
    }


    boolean isEnabled(int Status){

        boolean isEnabled = false;
        switch (Status){
            case OFF_DUTY:
                isEnabled = IsOffDutyPermission;
                break;

            case SLEEPER:
                isEnabled = IsSleeperPermission;
                break;

            case DRIVING:
                isEnabled = IsDrivingPermission;
                break;

            case ON_DUTY:
                isEnabled = IsOnDutyPermission;
                break;
        }

        return isEnabled;

    }

    void CheckTimeInLogEditing(DateTime startDate, DateTime endDate, View view, boolean IsPreviousEndAndNewStart,   DateTime selectedDateTime, boolean isLast ){

        int LastJobTotalMin = endDate.getMinuteOfDay() - startDate.getMinuteOfDay();
        if(IsPreviousEndAndNewStart){

            if (LastJobTotalMin <= 1) {    //== 0         // || LastJobTotalMin == 1         //compareDate.equals(viewDate)
                view.setBackgroundResource(R.drawable.edit_log_drawable);
            } else {
                EditGraphFragment.IsWrongDateEditLog = true;
                view.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }

            int dayDiff = hMethods.DayDiff(startDate, endDate);
            if ( dayDiff > 0 ){
                EditGraphFragment.IsWrongDateEditLog = true;
                view.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }


        }else {

            if(isLast && LastJobTotalMin >= 0 ){
                view.setBackgroundResource(R.drawable.edit_log_drawable);
            }else{
                if (LastJobTotalMin >= 0) {              //compareDate.equals(viewDate) || viewDate.isBefore(compareDate)
                    view.setBackgroundResource(R.drawable.edit_log_drawable);
                } else {
                    EditGraphFragment.IsWrongDateEditLog = true;
                    view.setBackgroundResource(R.drawable.edit_log_red_drawable);
                }
            }


            if(endDate.isAfter(selectedDateTime)){
                EditGraphFragment.IsWrongDateEditLog = true;
                view.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }

        }



    }




    DriverLogModel getDriverLog(List<DriverLogModel> list, int position, String startTime, String endTime){
        String startDateFormat  = SelectedDateTime + "T" + startTime + ":00"; //2018-07-26T04:24:44.547
        String endDateFormat    = SelectedDateTime + "T" + endTime + ":00"; //2018-07-26T04:24:44.547

        DateTime startDateTime = Globally.getDateTimeObj(startDateFormat, false);
        DateTime endDateTime = Globally.getDateTimeObj(endDateFormat, false);

        DateTime utcStartDateTime = Globally.getDateTimeObj(startDateTime.minusHours(offsetFromUTC).toString(), false);
        DateTime utcEndDateTime = Globally.getDateTimeObj(endDateTime.minusHours(offsetFromUTC).toString(), false);


        DriverLogModel logModel = list.get(position);
        // logModel.setDriverStatusId(DriverStatus);

        logModel.setTotalMinutes(endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay());
        logModel.setStartDateTime(startDateTime);
        logModel.setEndDateTime(endDateTime);

        logModel.setUtcStartDateTime(utcStartDateTime);
        logModel.setUtcEndDateTime(utcEndDateTime);

        return logModel;
    }


    DriverLogModel getDriverNextLog(List<DriverLogModel> list, int position, String endTime){
        String startDateFormat  = SelectedDateTime + "T" + endTime + ":00"; //2018-07-26T04:24:44.547

        DateTime startDateTime = Globally.getDateTimeObj(startDateFormat, false);
        // DateTime endDateTime = Globally.getDateTimeObj(endDateFormat, false);

        DateTime utcStartDateTime = Globally.getDateTimeObj(startDateTime.minusHours(offsetFromUTC).toString(), false);
        // DateTime utcEndDateTime = Globally.getDateTimeObj(endDateTime.minusHours(offsetFromUTC).toString(), false);


        DriverLogModel logModel = list.get(position);
        DateTime endDateTime = Globally.getDateTimeObj(logModel.getEndDateTime().toString(), false);

        logModel.setTotalMinutes(endDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay());
        logModel.setStartDateTime(startDateTime);
        // logModel.setEndDateTime(endDateTime);

        logModel.setUtcStartDateTime(utcStartDateTime);
        // logModel.setUtcEndDateTime(utcEndDateTime);

        return logModel;
    }

}
