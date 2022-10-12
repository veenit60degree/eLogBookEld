package com.adapter.logistic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.custom.dialogs.DriverLocationDialog;
import com.custom.dialogs.TrailorDialog;
import com.driver.details.DriverConst;
import com.local.db.MalfunctionDiagnosticMethod;
import com.models.EldDriverLogModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.UpdateLogRecordMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.CertifyViewLogFragment;
import com.models.DriverLocationModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverLogInfoAdapter extends BaseAdapter {

    Context context;
    LayoutInflater mInflater;
    List<EldDriverLogModel> LogList;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;
    DriverLocationDialog driverLocationDialog;
    TrailorDialog remarksDialog;
    String City = "", State = "", Country = "", DeviceId = "";
    Globally Global;
    int DriverType = 0, DriverId = 0, DaysDiff = 0;
    final int SaveDriverRecordLog   = 1;
    final int ClearDiagnosticEvents = 2;
    boolean IsEditView, isExceptionEnabled = false;
    ProgressDialog progressDialog;
    SaveDriverLogPost saveDriverLogPost;
    //TextView certifyNoTV;
    DBHelper dbHelper;
    HelperMethods hMethods;
    UpdateLogRecordMethod logRecordMethod;
    JSONArray finalUpdatedArray, driverLog18DaysArray, selectedArray;
    String RecordType = "";
    String selectedDate = "";
    String selectedRemarks = "";
    boolean IsPersonalRecord = false, IsDrivingAllowForSwap = false;
    int selectedStatus = 0;
    String currentCycle = "";
    boolean IsCurrentDate;
    DateTime currentDateTime, currentUTCTime;
    int offsetFromUTC;
    MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;


    public DriverLogInfoAdapter(Context cxt, List<EldDriverLogModel> logList,  List<String> stateArrayList, List<DriverLocationModel> stateList,
                                int driverType, boolean isEditView, int daysDiff, int driverId,
                                boolean isCurrentDate, boolean isExcptnEnabled, JSONArray driverLog18DaysArray,
                                DateTime currentDateTime, DateTime currentUTCTime, int offsetFromUTC, boolean isDrivingAllowForSwap,
                                DBHelper db_helper, HelperMethods h_methods ){

        this.context        = cxt;
        this.mInflater      = LayoutInflater.from(context);
        this.LogList        = logList;
        this.StateArrayList = stateArrayList;
        this.StateList      = stateList;
        this.DriverType     = driverType;
        this.IsEditView     = isEditView;
        this.DaysDiff       = daysDiff;
        this.DriverId       = driverId;
        this.IsCurrentDate  = isCurrentDate;
        this.isExceptionEnabled = isExcptnEnabled;
        this.driverLog18DaysArray = driverLog18DaysArray;
        this.currentDateTime = currentDateTime;
        this.currentUTCTime = currentUTCTime;
        this.offsetFromUTC = offsetFromUTC;
        this.IsDrivingAllowForSwap = isDrivingAllowForSwap;
        this.dbHelper       = db_helper;
        this.hMethods       = h_methods;
        Global = new Globally();
        //currentCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context);
        currentCycle      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(context), context);

        malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();

        saveDriverLogPost   = new SaveDriverLogPost(context, saveLogRequestResponse);
        DeviceId            = SharedPref.GetSavedSystemToken(context);
        logRecordMethod     = new UpdateLogRecordMethod();
        progressDialog      = new ProgressDialog(context);
        progressDialog.setMessage("Loading ...");


        selectedArray = hMethods.GetSelectedDateArray(driverLog18DaysArray, ""+DriverId, currentDateTime, currentDateTime,
                currentUTCTime, offsetFromUTC, 2, dbHelper);


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
        return arg0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final EldDriverLogModel LogItem = (EldDriverLogModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.include_drive_daily_info, null);

            holder.certifyNoTV          = (TextView)convertView.findViewById(R.id.certifyNoTV);
            holder.certifyStatusTV      = (TextView)convertView.findViewById(R.id.certifyStatusTV);
            holder.certifyStartTimeTV   = (TextView)convertView.findViewById(R.id.certifyStartTimeTV);
            holder.certifyDurationTV    = (TextView)convertView.findViewById(R.id.certifyDurationTV);
            holder.certifyLocationTV    = (TextView)convertView.findViewById(R.id.certifyLocationTV);
            holder.certifyRemarksTV     = (TextView)convertView.findViewById(R.id.certifyRemarksTV);
            holder.certifyExcptnTV      = (TextView)convertView.findViewById(R.id.certifyExcptnTV);

            holder.certifyRemarksIV     = (ImageView) convertView.findViewById(R.id.certifyRemarksIV);
            holder.certifyLocationIV    = (ImageView)convertView.findViewById(R.id.certifyLocationIV);

            holder.LogInfoLay           = (LinearLayout)convertView.findViewById(R.id.LogInfoLay);

            holder.certifyLocationLay   = (RelativeLayout)convertView.findViewById(R.id.certifyLocationLay);
            holder.certifyRemarksLay    = (RelativeLayout)convertView.findViewById(R.id.certifyRemarksLay);

            holder.drivingSwapCheckBox  = (CheckBox)convertView.findViewById(R.id.drivingSwapCheckBox);

            SetViewFontColor(holder);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        setMarqueOnView(holder.certifyLocationTV);

        final int JobStatus = LogItem.getDriverStatusId();
        SetTextDataInView(holder,  LogItem, Global.JobStatus(JobStatus, LogItem.isPersonal(), ""+JobStatus),  String.valueOf(position + 1)+ "." );

        if(SharedPref.IsCCMTACertified(context) && !IsCurrentDate) {
            if (JobStatus == Constants.DRIVING && IsDrivingAllowForSwap &&  //IsEditView &&
                    !LogItem.getDuration().equals("00:00")) {
                holder.drivingSwapCheckBox.setVisibility(View.VISIBLE);
            }
        }


        if(isExceptionEnabled) {
            setMarqueOnView(holder.certifyExcptnTV);
            holder.certifyExcptnTV.setVisibility(View.VISIBLE);
            if (LogItem.isAdverseException()) {
                holder.certifyExcptnTV.setText(context.getResources().getString(R.string.adverse));
            } else if (LogItem.isShortHaulException()) {
                holder.certifyExcptnTV.setText(context.getResources().getString(R.string.short_haul));
            } else {
                holder.certifyExcptnTV.setText("--");
            }
        }

        if(SharedPref.IsAOBRD(context) || IsEditView) {
            holder.certifyLocationIV.setVisibility(View.VISIBLE);
        }

        holder.certifyLocationLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(IsEditView || holder.certifyLocationIV.getVisibility() == View.VISIBLE) {
                    String city = holder.certifyLocationTV.getText().toString();
                    RecordType = Constants.Location;
                    selectedDate = LogItem.getStartDateTime();
                    selectedStatus = LogItem.getDriverStatusId();
                    selectedRemarks = LogItem.getRemarks();
                    IsPersonalRecord = LogItem.isPersonal();

                    OpenLocationDialog(city, position, Constants.EditLocation, view);
                }
            }
        });


        holder.drivingSwapCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    if(CertifyViewLogFragment.SwapDrivingArray.size() == 0){
                        addRemoveLogId(isChecked, LogItem.getDriverStatusLogId());
                        CertifyViewLogFragment.SelectedCoDriverId = LogItem.getCoDriverId();
                        CertifyViewLogFragment.SelectedCoDriverName = LogItem.getCoDriverName();
                    }else{
                        holder.drivingSwapCheckBox.setChecked(false);
                        Global.EldScreenToast(buttonView, "You can Swap one Driving status at a time", context.getResources().getColor(R.color.colorVoilation));
                    }
                }else {
                    addRemoveLogId(isChecked, LogItem.getDriverStatusLogId());
                    CertifyViewLogFragment.SelectedCoDriverId = "";
                    CertifyViewLogFragment.SelectedCoDriverName = "";
                }
            }
        });

        if(IsEditView ){    //&& DaysDiff < 2

            holder.certifyRemarksIV.setVisibility(View.VISIBLE);
            holder.certifyRemarksLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String remarks = holder.certifyRemarksTV.getText().toString();
                    if (remarks.equals("--")) {
                        remarks = "";
                    }
                    RecordType = Constants.Remarks;
                    selectedDate = LogItem.getStartDateTime();
                    selectedStatus = LogItem.getDriverStatusId();
                    selectedRemarks = LogItem.getRemarks();
                    IsPersonalRecord = LogItem.isPersonal();

                    OpenRemarksDialog(remarks, position, JobStatus, LogItem.isPersonal());  // isPersonal is used for yard move here
                }
            });

        }

        return convertView;
    }



    private void addRemoveLogId(boolean isChecked, String logId){

        if(isChecked){
            CertifyViewLogFragment.SwapDrivingArray.add(logId);
        }else {
            for (int i = 0; i < CertifyViewLogFragment.SwapDrivingArray.size(); i++) {
                if (logId.equals(CertifyViewLogFragment.SwapDrivingArray.get(i))) {
                    CertifyViewLogFragment.SwapDrivingArray.remove(i);
                    break;
                }
            }
        }
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
        TextView certifyNoTV, certifyStatusTV, certifyStartTimeTV, certifyDurationTV, certifyLocationTV, certifyRemarksTV, certifyExcptnTV;
        ImageView certifyLocationIV, certifyRemarksIV;
        LinearLayout LogInfoLay;
        RelativeLayout certifyLocationLay, certifyRemarksLay;
        CheckBox drivingSwapCheckBox;

    }

    private void setMarqueOnView(TextView view){
       view.setHorizontallyScrolling(true);
       view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
       view.setSingleLine(true);
       view.setMarqueeRepeatLimit(-1);
       view.setSelected(true);

    }

    private void SetViewFontColor(ViewHolder holder){
        holder.certifyNoTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyStatusTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyStartTimeTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyDurationTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyLocationTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyRemarksTV.setTypeface(null, Typeface.NORMAL);
        holder.certifyExcptnTV.setTypeface(null, Typeface.NORMAL);

        holder.certifyNoTV.setTextColor(context.getResources().getColor(R.color.black));
        holder.certifyStatusTV.setTextColor(context.getResources().getColor(R.color.black));
        holder.certifyStartTimeTV.setTextColor(context.getResources().getColor(R.color.black) );
        holder.certifyDurationTV.setTextColor(context.getResources().getColor(R.color.black));
        holder.certifyLocationTV.setTextColor(context.getResources().getColor(R.color.black));
        holder.certifyRemarksTV.setTextColor(context.getResources().getColor(R.color.black));
        holder.certifyExcptnTV.setTextColor(context.getResources().getColor(R.color.black));

      //  holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
    }


    private void SetTextDataInView(ViewHolder holder,  EldDriverLogModel LogItem, String status, String position){

        String StartTime = Global.ConvertToTimeFormat(LogItem.getStartDateTime(), Global.DateFormatWithMillSec);
        holder.certifyNoTV.setText(position);
        holder.certifyStatusTV.setText(status);
        holder.certifyStartTimeTV.setText(StartTime);
        holder.certifyDurationTV.setText(LogItem.getDuration());


       /* if(currentCycle.equals(Globally.USA_WORKING_6_DAYS) || currentCycle.equals(Globally.USA_WORKING_7_DAYS)) {
            String location = LogItem.getLocation().trim();
            if (location.contains("null") || location.equals(",") || location.equals("")) {
                if(LogItem.getStartLatitude().length() > 4){
                    holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
                    holder.certifyLocationTV.setText(LogItem.getStartLatitude() + "," + LogItem.getStartLongitude());
                   holder.certifyLocationIV.setVisibility(View.GONE);
                }else {
                    holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.blue_background_light));
                    holder.certifyLocationTV.setText(context.getResources().getString(R.string.no_location));
                    holder.certifyLocationIV.setVisibility(View.VISIBLE);
                }
            } else {
                holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
                holder.certifyLocationTV.setText(location);
                holder.certifyLocationIV.setVisibility(View.GONE);
            }
        }else{*/

            String locationKm = LogItem.getLocationKm().trim();
            String location = LogItem.getLocation().trim();
            if (locationKm.equals("null") || locationKm.equals(",") || locationKm.equals("") || locationKm.equals(context.getString(R.string.no_location_found))) {
                if(location.equals("null") || location.equals(",") || location.equals("") || locationKm.equals(context.getString(R.string.no_location_found)) ){
                     if(LogItem.getStartLatitude().length() > 4 && SharedPref.IsAOBRD(context) == false && IsEditView == false ){
                        holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
                        holder.certifyLocationTV.setText(LogItem.getStartLatitude() + "," + LogItem.getStartLongitude());
                        holder.certifyLocationIV.setVisibility(View.GONE);
                    }else {
                         holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.blue_background_light));
                         holder.certifyLocationTV.setText(context.getResources().getString(R.string.no_location));
                         holder.certifyLocationIV.setVisibility(View.VISIBLE);
                     }
                }else{
                    holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
                    holder.certifyLocationTV.setText(location);
                    if(SharedPref.IsAOBRD(context) == false && IsEditView == false ) {
                        holder.certifyLocationIV.setVisibility(View.GONE);
                    }
                }

            } else {
                holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
                holder.certifyLocationTV.setText(LogItem.getLocationKm());
                if(SharedPref.IsAOBRD(context) == false && IsEditView == false ) {
                    holder.certifyLocationIV.setVisibility(View.GONE);
                }
            }
       // }

        if(!LogItem.getRemarks().trim().equalsIgnoreCase("null") && !LogItem.getRemarks().trim().equalsIgnoreCase(""))
            holder.certifyRemarksTV.setText(LogItem.getRemarks());
        else
            holder.certifyRemarksTV.setText("--");

    }




    void OpenLocationDialog(String city, int oldSelectedPosition, int JobType, View view) {

        try {
            if (StateArrayList.size() > 0) {

                if (driverLocationDialog != null && driverLocationDialog.isShowing()) {
                    driverLocationDialog.dismiss();
                }

                driverLocationDialog = new DriverLocationDialog(context, city, "", oldSelectedPosition, JobType, false, view,
                        StateArrayList, new DriverLocationListener());

                driverLocationDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    void OpenRemarksDialog(String remarks,  int ItemPosition, int jobStatus, boolean isYardMove) {

        try {
            if (StateArrayList.size() > 0) {

                if (remarksDialog != null && remarksDialog.isShowing()) {
                    remarksDialog.dismiss();
                }

                remarksDialog = new TrailorDialog(context, Constants.Remarks, isYardMove, remarks, ItemPosition, true,
                        Global.onDutyRemarks, jobStatus, dbHelper, new RemarksListener());
                remarksDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }




    private class DriverLocationListener implements DriverLocationDialog.LocationListener {

        @Override
        public void CancelLocReady(boolean isMalfunction, int JobType) {

            try {
                if (driverLocationDialog != null && driverLocationDialog.isShowing())
                    driverLocationDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void SaveLocReady(int position, int spinnerItemPos,  int JobType, String city,
                                 EditText CityNameEditText, View view, boolean isMalfunction) {

            City = city;

            if (spinnerItemPos < StateList.size() && JobType != Constants.EditRemarks) {
                State = StateList.get(spinnerItemPos).getStateCode();
                Country = StateList.get(spinnerItemPos).getCountry();
            }

            if (City.length() > 0) {
                HideKeyboard(CityNameEditText);
                EldDriverLogModel logModel = LogList.get(position);

                if (JobType == Constants.EditLocation){

                    logModel.setLocation(City + "; " + State + "; " + Country);
                    logModel.setLocationKm(City + "; " + State + "; " + Country);
                    SaveAndUploadData(logModel, RecordType, position, "","");

                    DateTime eventDate = Globally.getDateTimeObj(LogList.get(position).getUTCStartDateTime(), false);
                    int offset = Math.abs((int) Global.GetTimeZoneOffSet());
                    String eventStartUtcDate = eventDate.plusHours(offset).toString();

                    boolean isMissingEventToClear =  malfunctionDiagnosticMethod.isMissingEventToClear(eventStartUtcDate, dbHelper);
                    if(isMissingEventToClear){
                        int JobStatusId = LogList.get(position).getDriverStatusId();
                        boolean isPersonalYardMove = LogList.get(position).isPersonal();
                        Constants.ClearMissingEventTime = eventStartUtcDate;
                        Constants.ClearMissingEventStatus = Global.JobStatus(JobStatusId, isPersonalYardMove, ""+JobStatusId);
                        Constants.isClearMissingEvent = true;
                        SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, context);

                        Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent);
                        }
                        context.startService(serviceIntent);

	                }

                }


                try {
                    if (driverLocationDialog != null && driverLocationDialog.isShowing())
                        driverLocationDialog.dismiss();
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }

            } else {
                if(JobType == Constants.EditRemarks) {
                    Global.EldScreenToast(CityNameEditText, "Please enter remarks", context.getResources().getColor(R.color.colorVoilation));
                }else {
                    Global.EldScreenToast(CityNameEditText, "Please enter city name", context.getResources().getColor(R.color.colorVoilation));
                }
            }
        }
    }




    private class RemarksListener implements TrailorDialog.TrailorListener {

        @Override
        public void CancelReady() {

            try {
                if (remarksDialog != null && remarksDialog.isShowing())
                    remarksDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void JobBtnReady(String TrailorNo, String remarks, String type, boolean isUpdatedTrailer,
                                int ItemPosition, EditText TrailorNoEditText, EditText ReasonEditText) {

                if (remarks.length() > 0) {
                    HideKeyboard(ReasonEditText);
                    EldDriverLogModel logModel = LogList.get(ItemPosition);
                    logModel.setRemarks(remarks);

                    if (RecordType.equals(Constants.Remarks)){
                        if(remarks.equals("Yard Move")){
                            logModel.setRemarks(remarks + " - " + ReasonEditText.getText().toString() );
                        }
                    }

                    LogList.set(ItemPosition, logModel);
                    notifyDataSetChanged();
                  //  Global.EldScreenToast(certifyNoTV, "Remarks updated.", context.getResources().getColor(R.color.colorPrimary));

                    SaveAndUploadData(logModel, RecordType, ItemPosition, remarks, ReasonEditText.getText().toString());

                    try {
                        if (remarksDialog != null && remarksDialog.isShowing())
                            remarksDialog.dismiss();
                    } catch (final IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ReasonEditText.requestFocus();
                    Global.EldScreenToast(TrailorNoEditText, "Please enter remarks.", context.getResources().getColor(R.color.colorVoilation));
                }
        }
    }


    void HideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }


    /*================== Save And Upload Log Record Data ===================*/
    void SaveAndUploadData( EldDriverLogModel logModel, String RecordType, int position, String onDutyRemarks, String ReasonDesc){
        String currentUtcDate = Global.GetCurrentUTCTimeFormat();
        String currentDriverZoneDate = Global.GetCurrentDateTime();

        JSONObject logObj = logRecordMethod.GetUpdateLogRecordJson(logModel, String.valueOf(DriverId), DeviceId, RecordType,
                currentUtcDate, currentDriverZoneDate, Globally.LATITUDE, Globally.LONGITUDE, selectedRemarks, ""+IsPersonalRecord );


        finalUpdatedArray = logRecordMethod.getSavedLogRecordArray(DriverId, dbHelper);

       boolean IsAlreadyExistEntry = false;
        for(int i = 0 ; i < finalUpdatedArray.length() ; i++){

            try {
                JSONObject existingObj = (JSONObject) finalUpdatedArray.get(i);
                String existingDate = existingObj.getString(ConstantsKeys.startDateTime);
                String selectedDatee = logObj.optString(ConstantsKeys.startDateTime);

                String existingStatus = existingObj.getString(ConstantsKeys.DriverStatusId);
                String selectedStatus = logObj.optString(ConstantsKeys.DriverStatusId);

                if(existingDate.equals(selectedDatee) && existingStatus.equals(selectedStatus)){
                    IsAlreadyExistEntry = true;
                    finalUpdatedArray.put(i, logObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if(!IsAlreadyExistEntry) {
            finalUpdatedArray.put(logObj);
        }

        // ------------ Update Log Record File locally ------------
        logRecordMethod.UpdateLogRecordHelper( DriverId, dbHelper, finalUpdatedArray);


        JSONArray driverLogArray          = hMethods.getSavedLogArray(DriverId, dbHelper);

        String location = "", locationKm = "";
        for(int i = driverLogArray.length()-1 ; i >=0  ; i--){

            try {   //2021-04-26T00:09:03
                JSONObject obj = (JSONObject)driverLogArray.get(i);
                String compareStartDate = obj.getString(ConstantsKeys.startDateTime);
                int status = obj.getInt(ConstantsKeys.DriverStatusId);
                String selectedDateSec = selectedDate.substring(17, selectedDate.length());
                String selectedRem = obj.getString(ConstantsKeys.Remarks);

                if(selectedDateSec.equals("00")) {
                    if (compareStartDate.length() > 17) {
                        compareStartDate = compareStartDate.substring(0, 17) + "00";
                    }
                }
                if(selectedDate.equals(compareStartDate) && selectedStatus == status){

                    if(selectedStatus == Constants.ON_DUTY){
                        if (selectedRem.equals(selectedRemarks)) {
                            if (RecordType.equals(Constants.Location)) {
                                String[] locArray = logObj.getString(ConstantsKeys.RecordValue).split(";");
                                if (locArray.length > 1) {
                                    location = locArray[1] + " " + locArray[0];
                                    locationKm = locArray[0] + " " + locArray[1];
                                }
                                obj.put(ConstantsKeys.StartLocation, location);
                                obj.put(ConstantsKeys.StartLocationKm, locationKm);
                            } else if (RecordType.equals(Constants.Remarks)) {
                                if (onDutyRemarks.equals("Yard Move")) {
                                    obj.put(ConstantsKeys.YardMove, true);
                                    onDutyRemarks = ReasonDesc;
                                }
                                obj.put(ConstantsKeys.Remarks, ReasonDesc);
                            }

                            driverLogArray.put(i, obj);
                            break;

                        }
                    }else {
                        if (RecordType.equals(Constants.Location)) {
                            String[] locArray = logObj.getString(ConstantsKeys.RecordValue).split(";");

                                if (locArray.length > 1) {
                                    location = locArray[1] + " " + locArray[0];
                                    locationKm = locArray[0] + " " + locArray[1];
                                }


                            obj.put(ConstantsKeys.StartLocation, location);
                            obj.put(ConstantsKeys.StartLocationKm, locationKm);

                        } else if (RecordType.equals(Constants.Remarks)) {
                            if (onDutyRemarks.equals("Yard Move")) {
                                obj.put(ConstantsKeys.YardMove, true);
                                onDutyRemarks = ReasonDesc;
                            }
                            obj.put(ConstantsKeys.Remarks, ReasonDesc);
                        }

                        driverLogArray.put(i, obj);
                        break;
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        // Update 18 Days Array.....
        hMethods.DriverLogHelper( DriverId, dbHelper, driverLogArray);

        if(RecordType.equals(Constants.Location)) {
            logModel.setLocation(location);
            logModel.setLocationKm(locationKm);
            LogList.set(position, logModel);
        }else{
            logModel.setRemarks(onDutyRemarks);
            LogList.set(position, logModel);
        }
        notifyDataSetChanged();

        // -------------- Upload data on server --------------
        if(Global.isConnected(context)) {
            SAVE_DRIVER_RECORD_LOG(finalUpdatedArray, false, false, Constants.SocketTimeout20Sec);
        }else{
            Global.EldToastWithDuration(CertifyViewLogFragment.saveSignatureBtn, "Connection unavailable! Your edited " + RecordType + " will be posted to server automatically when your device will be connected with working internet connection.", context.getResources().getColor(R.color.colorSleeper));
        }
    }


    void checkDiagnosticEventsForClear(String compareStartDate){

      /*  try {
            ArrayList<String> eventList = new ArrayList<>();
            int minDiffPlusMinus = 5;
            DateTime selectedLogDate = Global.getDateTimeObj(compareStartDate, false);
            malfnJsonArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArrayEvents(DriverId, dbHelper);
            for(int i = malfnJsonArray.length()-1 ; i >= 0 ; i--){
                JSONObject obj = (JSONObject)malfnJsonArray.get(i);
                DateTime eventDateTime = Global.getDateTimeObj(obj.getString(ConstantsKeys.EventDateTime), false);
                if(selectedLogDate.equals(eventDateTime) || selectedLogDate.isAfter(eventDateTime.minusMinutes(minDiffPlusMinus)) ||
                        selectedLogDate.isBefore(eventDateTime.plusMinutes(minDiffPlusMinus)) ){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        malfnJsonArray.remove(i);
                    }

                    eventList.add(obj.getString(ConstantsKeys.DiagnosticType));

                }
            }

            if(eventList.size() > 0){

                JSONArray eventsArray = new JSONArray();
                eventsArray.put(malfunctionDiagnosticMethod.GetJsonForClearDiagnostic(""+DriverId, DeviceId, eventList.toString(),
                        "Auto clear from android"));
                if(Global.isConnected(context)) {
                    ClearDiagnosticEvents(eventsArray, false, false, Constants.SocketTimeout10Sec);

                    malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(DriverId, dbHelper, malfnJsonArray);

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }


    /*================== Clear missing location diagnostic events data  ===================*/
    void ClearDiagnosticEvents(final JSONArray eventData, final boolean isLoad, final boolean IsRecap, int socketTimeout){
        progressDialog.show();
        saveDriverLogPost.PostDriverLogData(eventData, APIs.CLEAR_MALFNCN_DIAGSTC_EVENT, socketTimeout, isLoad, IsRecap, 1, ClearDiagnosticEvents);

    }



    /*================== Upload Driver Updated Log Record Data ===================*/
    void SAVE_DRIVER_RECORD_LOG(final JSONArray geoData, final boolean isLoad, final boolean IsRecap, int socketTimeout){
        progressDialog.show();
        saveDriverLogPost.PostDriverLogData(geoData, APIs.UPDATE_DRIVER_LOG_RECORD, socketTimeout, isLoad, IsRecap, 1, SaveDriverRecordLog);

    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {

            // SaveDriverLog
            progressDialog.dismiss();

            try {
                JSONObject obj = new JSONObject(response);
                String Message = obj.getString("Message");

                if (obj.getString("Status").equals("true")) {

                    if(flag == ClearDiagnosticEvents){
                        // update event array
                      //  malfunctionDiagnosticMethod.MalfnDiagnstcLogHelperEvents(DriverId, dbHelper, malfnJsonArray);
                    }else {
                        Global.EldScreenToast(CertifyViewLogFragment.saveSignatureBtn, Message, context.getResources().getColor(R.color.colorPrimary));

                        // ------------ Clear Log Record File locally ------------
                        logRecordMethod.UpdateLogRecordHelper(DriverId, dbHelper, new JSONArray());
                    }
                }else{
                    // ------------ Clear Log Record File locally ------------
                    logRecordMethod.UpdateLogRecordHelper( DriverId, dbHelper, new JSONArray());
                    Global.EldScreenToast(CertifyViewLogFragment.saveSignatureBtn, Message , context.getResources().getColor(R.color.colorVoilation));
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        }
        @SuppressLint("NewApi")
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: " );
            Global.EldScreenToast(CertifyViewLogFragment.saveSignatureBtn, "Data updated successfully." , context.getResources().getColor(R.color.colorPrimary));
            progressDialog.dismiss();


        }
    };




}
