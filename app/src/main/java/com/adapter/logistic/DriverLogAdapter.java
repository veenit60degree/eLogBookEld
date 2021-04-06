package com.adapter.logistic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.custom.dialogs.TimerDialog;
import com.local.db.ConstantsKeys;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EditLogFragment;
import com.models.DriverLogModel;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

public class DriverLogAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    List<DriverLogModel> driverLogList;
    Context context;
    String[] list = {"Off Duty", "Sleeper", "Driving", "On Duty", "Personal"};
    String SelectedDateTime ;
    int offsetFromUTC,  parentPosition = 0;
    TimerDialog timerDialog;
    JSONObject logPermissionObj;
    DriverPermissionMethod permitMethod;
    boolean isTouch = false;
    HelperMethods hMethods;
    boolean IsOffDutyPermission ,IsSleeperPermission , IsDrivingPermission ,  IsOnDutyPermission;
    final int OFF_DUTY       = 1;
    final int SLEEPER        = 2;
    final int DRIVING        = 3;
    final int ON_DUTY        = 4;

    public DriverLogAdapter(Context context, List<DriverLogModel> oDriverLogDetail, String selectedDate, int offset,
                            JSONObject permitLog, DriverPermissionMethod pMethod, HelperMethods h_method){
        this.context = context;
        this.driverLogList = oDriverLogDetail;
        this.SelectedDateTime = selectedDate;
        this.offsetFromUTC = offset;
        this.logPermissionObj = permitLog;
        this.permitMethod = pMethod;
        this.hMethods     = h_method;
        mInflater = LayoutInflater.from(context);

        IsOffDutyPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OffDutyKey);
        IsSleeperPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.SleeperKey);
        IsDrivingPermission = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.DrivingKey);
        IsOnDutyPermission  = permitMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OnDutyKey);



    }


    @Override
    public int getCount() {
        return driverLogList.size();
    }

    @Override
    public Object getItem(int i)  {

        return driverLogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return driverLogList.get(i).hashCode();   //i
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder ;
        String startTime = "", endTime = "";
        int TotalHours = 0, status = 1;
        boolean isPersonal = false;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_edit_log, null);

            holder.editLogSerialNoTV    = (TextView)convertView.findViewById(R.id.editLogSerialNoTV);
            holder.startTimeTV          = (TextView)convertView.findViewById(R.id.startTimeTV);
            holder.endTimeTV            = (TextView)convertView.findViewById(R.id.endTimeTV);
            holder.editLogDurationTV    = (TextView)convertView.findViewById(R.id.editLogDurationTV);

            holder.startTimeBtn         = (RelativeLayout)convertView.findViewById(R.id.startTimeIV);
            holder.endTimeBtn           = (RelativeLayout)convertView.findViewById(R.id.endTimeIV);
            holder.startTimeLayout      = (RelativeLayout)convertView.findViewById(R.id.startTimeLayout);
            holder.endTimeLayout        = (RelativeLayout)convertView.findViewById(R.id.endTimeLayout);

            holder.editLogItemLay       = (LinearLayout)convertView.findViewById(R.id.editLogItemLay);

            holder.editLogStatusSpinner = (Spinner)convertView.findViewById(R.id.editLogStatusSpinner);


            holder.startTimeBtn.setTag(position);
            holder.endTimeBtn.setTag(position);


            holder.startTimeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    parentPosition = getParentViewPosition(holder.editLogSerialNoTV);
                    String time = holder.startTimeTV.getText().toString() ;
                    int Hour = Integer.valueOf(time.split(":")[0] );
                    int min = Integer.valueOf(time.split(":")[1] );

                    timerDialog = new TimerDialog(context, parentPosition, Hour, min, holder.startTimeTV, holder.endTimeTV, "start", new TimePickerListener());
                    timerDialog.show();
                }
            });


            holder.endTimeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentPosition = getParentViewPosition(holder.editLogSerialNoTV);
                    String time = holder.endTimeTV.getText().toString() ;
                    int Hour = Integer.valueOf(time.split(":")[0] );
                    int min = Integer.valueOf(time.split(":")[1] );
                    timerDialog = new TimerDialog(context, parentPosition, Hour, min, holder.startTimeTV, holder.endTimeTV, "end", new TimePickerListener());
                    timerDialog.show();

                }
            });




            holder.editLogStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {

                    if(isTouch) {

                        parentPosition = getParentViewPosition(holder.editLogSerialNoTV);

                        isTouch = false;
                        DriverLogModel logModel = EditLogFragment.oDriverLogDetail.get(parentPosition);
                        logModel.setDriverStatusId(pos + 1);
                        if (pos == 4) { // Check for Personal use
                            logModel.setPersonal(true);
                        } else {
                            logModel.setPersonal(false);
                        }

                        if(pos != 3) {    // If status is not On Duty
                            logModel.setRemarks("");
                        }

                        EditLogFragment.oDriverLogDetail.set(parentPosition, logModel);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // nothing
                }

            });

            holder.editLogStatusSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTouch = true;
                    return false;
                }
            });

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        try {
            DriverLogModel logModel = driverLogList.get(position);
            startTime       = logModel.getStartDateTime().toString();
            endTime         = logModel.getEndDateTime().toString();
            TotalHours      = (int)logModel.getTotalMinutes();
            status          = logModel.getDriverStatusId();
            isPersonal      = logModel.isPersonal();

            startTime = startTime.substring(11, 16);
            endTime   = endTime.substring(11, 16);

        } catch (Exception e) {
            e.printStackTrace();
        }

        DateTime currenDateTime = Globally.getDateTimeObj(Globally.GetCurrentDateTime(), false);
        DateTime startDateTime ,endDateTime ;
        if(position == 0){
            startDateTime   = Globally.getDateTimeObj(EditLogFragment.oDriverLogDetail.get(0).getStartDateTime().toString(), false);
            endDateTime     = Globally.getDateTimeObj(EditLogFragment.oDriverLogDetail.get(0).getEndDateTime().toString(), false);

            if(endDateTime.isAfter(startDateTime)){
                if(endTime.equals("00:00")){
                    endTime = "23:59";
                }
            }
            CheckTimeInLogEditing(startDateTime, endDateTime, holder.endTimeLayout, false, currenDateTime, false);

        }else{
            DateTime previousEndTime = Globally.getDateTimeObj(EditLogFragment.oDriverLogDetail.get(position - 1).getEndDateTime().toString(), false );
            startDateTime   = Globally.getDateTimeObj(EditLogFragment.oDriverLogDetail.get(position).getStartDateTime().toString(), false);
            endDateTime     = Globally.getDateTimeObj(EditLogFragment.oDriverLogDetail.get(position).getEndDateTime().toString(), false);

            boolean isLast = false;
            if(position == driverLogList.size() -1){
                isLast = true;
            }


            CheckTimeInLogEditing(previousEndTime, startDateTime, holder.startTimeLayout, true,currenDateTime, false);
            CheckTimeInLogEditing(startDateTime, endDateTime, holder.endTimeLayout, false, currenDateTime, isLast);

        }


        holder.editLogSerialNoTV.setText("" + (position+1));
        holder.startTimeTV.setText(startTime);
        holder.endTimeTV.setText(endTime);
        holder.editLogDurationTV.setText(FinalValue(TotalHours));


            // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                context,R.layout.item_editlog_spinner, R.id.editlogSpinTV, list){
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
                }else if(position == ON_DUTY - 1){
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
                }else if(position == ON_DUTY - 1){
                    setViewTextColor(tv, IsOnDutyPermission);
                }else{
                    setViewTextColor(tv, true);
                }

                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.item_editlog_spinner);
        holder.editLogStatusSpinner.setAdapter(spinnerArrayAdapter);


        int spinPos = status - 1;
        if(status == OFF_DUTY ){
            if(isPersonal) {
                holder.editLogStatusSpinner.setSelection(4);
                setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                        holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, true);
            }else {
                holder.editLogStatusSpinner.setSelection(0);
                setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                        holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, IsOffDutyPermission);
            }
        }else if(status == SLEEPER ){
            holder.editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                    holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, IsSleeperPermission);

        }else if(status == DRIVING ){
            holder.editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                    holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, IsDrivingPermission);

        }else if(status == ON_DUTY ){
            holder.editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                    holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, IsOnDutyPermission);

        }else{
            holder.editLogStatusSpinner.setSelection(spinPos);
            setSpinnerViewEnabledStatus(holder.editLogSerialNoTV, holder.editLogStatusSpinner, holder.startTimeLayout,
                    holder.startTimeTV, holder.endTimeLayout, holder.endTimeTV, holder.editLogDurationTV, holder.editLogItemLay, position, IsOffDutyPermission);
        }





        return convertView;

    }


    public class ViewHolder {
        TextView editLogSerialNoTV, startTimeTV, endTimeTV, editLogDurationTV;
        RelativeLayout startTimeLayout, endTimeLayout, endTimeBtn, startTimeBtn;
        LinearLayout editLogItemLay;
        Spinner editLogStatusSpinner;

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
            editLogItemLay.setBackgroundColor(context.getResources().getColor(R.color.eld_gray_bg));

        }

        if(position == driverLogList.size() - 1 && isPermit){
            spinner.setEnabled(true);
            startTimeBtn.setEnabled(true);
            endTimeBtn.setEnabled(true);
            countTV.setTextColor(Color.BLACK);
            durationTV.setTextColor(Color.BLACK);
            startTV.setTextColor(Color.BLACK);
            endTV.setTextColor(Color.BLACK);
            editLogItemLay.setBackgroundColor(context.getResources().getColor(R.color.white));

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

            DriverLogModel logModel = getDriverLog(EditLogFragment.oDriverLogDetail, position, startView.getText().toString(),  endView.getText().toString());
            EditLogFragment.oDriverLogDetail.set(position, logModel);

            if(position < EditLogFragment.oDriverLogDetail.size()-1){
                DriverLogModel logModelNextPos = EditLogFragment.oDriverLogDetail.get(position + 1);
                int Status = logModelNextPos.getDriverStatusId();
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

                if(isEnabled){
                    DriverLogModel nextPosLogModel = getDriverNextLog(EditLogFragment.oDriverLogDetail, position + 1, endView.getText().toString());
                    EditLogFragment.oDriverLogDetail.set(position + 1, nextPosLogModel);
                }
            }

            timerDialog.dismiss();
            notifyDataSetChanged();
        }
    }


    void CheckTimeInLogEditing(DateTime startDate, DateTime endDate, View view, boolean IsPreviousEndAndNewStart,   DateTime selectedDateTime, boolean isLast ){

        int LastJobTotalMin = endDate.getMinuteOfDay() - startDate.getMinuteOfDay();
        if(IsPreviousEndAndNewStart){

            if (LastJobTotalMin == 0) {             // || LastJobTotalMin == 1         //compareDate.equals(viewDate)
                view.setBackgroundResource(R.drawable.edit_log_drawable);
            } else {
                EditLogFragment.IsWrongDateEditLog = true;
                view.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }

            if (hMethods.DayDiff(startDate, endDate) > 0 ){
                EditLogFragment.IsWrongDateEditLog = true;
                view.setBackgroundResource(R.drawable.edit_log_red_drawable);
            }


        }else {

            if(isLast && LastJobTotalMin >= 0 ){
                view.setBackgroundResource(R.drawable.edit_log_drawable);
            }else{
                if (LastJobTotalMin >= 0) {              //compareDate.equals(viewDate) || viewDate.isBefore(compareDate)
                    view.setBackgroundResource(R.drawable.edit_log_drawable);
                } else {
                    EditLogFragment.IsWrongDateEditLog = true;
                    view.setBackgroundResource(R.drawable.edit_log_red_drawable);
                }
            }


            if(endDate.isAfter(selectedDateTime)){
                EditLogFragment.IsWrongDateEditLog = true;
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
