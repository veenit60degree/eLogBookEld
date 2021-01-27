package com.adapter.logistic;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.DriverLocationDialog;
import com.custom.dialogs.ShareDriverLogDialog;
import com.custom.dialogs.TrailorDialog;
import com.driver.details.EldDriverLogModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.UpdateLogRecordMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.DriverLogDetailFragment;
import com.messaging.logistic.fragment.EldFragment;
import com.models.DriverLocationModel;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    int DriverType = 0, DriverId = 0, DaysDiff = 0, socketTimeout = 30000;
    final int SaveDriverRecordLog = 1;
    boolean IsEditView, isExceptionEnabled = false;
    ProgressDialog progressDialog;
    SaveDriverLogPost saveDriverLogPost;
    //TextView certifyNoTV;
    DBHelper dbHelper;
    HelperMethods hMethods;
    UpdateLogRecordMethod logRecordMethod;
    JSONArray finalUpdatedArray;
    String RecordType = "";
    String selectedDate = "";
    boolean IsCurrentDate;

    public DriverLogInfoAdapter(Context cxt, List<EldDriverLogModel> logList,  List<String> stateArrayList, List<DriverLocationModel> stateList,
                                int driverType, boolean isEditView, int daysDiff, int driverId,
                                boolean isCurrentDate, boolean isExcptnEnabled,
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
        this.dbHelper       = db_helper;
        this.hMethods       = h_methods;
        Global = new Globally();

        saveDriverLogPost   = new SaveDriverLogPost(context, saveLogRequestResponse);
        DeviceId            = SharedPref.GetSavedSystemToken(context);
        logRecordMethod     = new UpdateLogRecordMethod();
        progressDialog      = new ProgressDialog(context);
        progressDialog.setMessage("Loading ...");


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

            holder.certifyNoTV                 = (TextView)convertView.findViewById(R.id.certifyNoTV);
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

            SetViewFontColor(holder);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        setMarqueOnView(holder.certifyLocationTV);

        final int JobStatus = LogItem.getDriverStatusId();
        boolean IsPersonal = LogItem.isPersonal();
        SetTextDataInView(holder,  LogItem, Globally.JobStatus(JobStatus, LogItem.isPersonal()),  String.valueOf(position + 1)+ "." );

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

        if(IsEditView && DaysDiff < 2){

          //  if(JobStatus == Constants.ON_DUTY ) {   //|| (JobStatus == Constants.OFF_DUTY && IsPersonal)
           // }

            holder.certifyRemarksIV.setVisibility(View.VISIBLE);
            holder.certifyLocationIV.setVisibility(View.VISIBLE);

            holder.certifyLocationLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String city = holder.certifyLocationTV.getText().toString();
                    RecordType = Constants.Location;
                    selectedDate = LogItem.getStartDateTime();
                    OpenLocationDialog( city , position, Constants.EditLocation, view);
                }
            });

          //  if(JobStatus == Constants.ON_DUTY || (JobStatus == Constants.OFF_DUTY || IsPersonal )) {

                holder.certifyRemarksLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String remarks = holder.certifyRemarksTV.getText().toString();
                        if (remarks.equals("--")) {
                            remarks = "";
                        }
                        RecordType = Constants.Remarks;
                        selectedDate = LogItem.getStartDateTime();
                        OpenRemarksDialog(remarks, position, JobStatus, LogItem.isPersonal());  // isPersonal is used for yard move here
                    }
                });
          //  }
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
        TextView certifyNoTV, certifyStatusTV, certifyStartTimeTV, certifyDurationTV, certifyLocationTV, certifyRemarksTV, certifyExcptnTV;
        ImageView certifyLocationIV, certifyRemarksIV;
        LinearLayout LogInfoLay;
        RelativeLayout certifyLocationLay, certifyRemarksLay;

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

        holder.LogInfoLay.setBackgroundColor(context.getResources().getColor(R.color.white_theme));
    }


    private void SetTextDataInView(ViewHolder holder,  EldDriverLogModel LogItem, String status, String position){

        String StartTime = "00:00";
        StartTime = Globally.ConvertToTimeFormat(LogItem.getStartDateTime(), Globally.DateFormatWithMillSec);
        holder.certifyNoTV.setText(position);
        holder.certifyStatusTV.setText(status);
        holder.certifyStartTimeTV.setText(StartTime);
        holder.certifyDurationTV.setText(LogItem.getDuration());
        holder.certifyLocationTV.setText(LogItem.getLocation());

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

                driverLocationDialog = new DriverLocationDialog(context, city, "", oldSelectedPosition, JobType, view,
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

                remarksDialog = new TrailorDialog(context, Constants.Remarks, isYardMove, remarks, ItemPosition, true, Global.onDutyRemarks, jobStatus, dbHelper, new RemarksListener());
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
        public void CancelLocReady() {

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
        public void SaveLocReady(int position, int spinnerItemPos,  int JobType, String city, EditText CityNameEditText, View view) {

            City = city;

            if (spinnerItemPos < StateList.size() && JobType != Constants.EditRemarks) {
                State = StateList.get(spinnerItemPos).getState();
                Country = StateList.get(spinnerItemPos).getCountry();
            }

            if (City.length() > 0) {
                HideKeyboard(CityNameEditText);
                EldDriverLogModel logModel = LogList.get(position);

                if(JobType == Constants.EditRemarks) {

                }else if (JobType == Constants.EditLocation){

                    logModel.setLocation(City + ", " + State + ", " + Country);
                    LogList.set(position, logModel);
                    notifyDataSetChanged();

                    SaveAndUploadData(logModel, RecordType, position);

                }else {
                    Global.SaveCurrentCycle(DriverType, Country, "edit_log", context);
                }

                if(position == LogList.size()-1 && IsCurrentDate){
                    Global.SaveCurrentCycle(DriverType, Country, "edit_log", context);
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
        public void JobBtnReady(String TrailorNo, String remarks, String type, boolean isUpdatedTrailer, int ItemPosition, EditText TrailorNoEditText, EditText ReasonEditText) {

                if (remarks.length() > 0) {
                    HideKeyboard(ReasonEditText);
                    EldDriverLogModel logModel = LogList.get(ItemPosition);
                    logModel.setRemarks(remarks);
                    LogList.set(ItemPosition, logModel);
                    notifyDataSetChanged();
                  //  Global.EldScreenToast(certifyNoTV, "Remarks updated.", context.getResources().getColor(R.color.colorPrimary));

                    SaveAndUploadData(logModel, RecordType, ItemPosition);

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
    void SaveAndUploadData( EldDriverLogModel logModel, String RecordType, int ItemPosition){
        JSONObject logObj = logRecordMethod.GetUpdateLogRecordJson(logModel, String.valueOf(DriverId), DeviceId, RecordType);

        finalUpdatedArray = logRecordMethod.getSavedLogRecordArray(DriverId, dbHelper);

       boolean IsAlreadyExistEntry = false;
        for(int i = 0 ; i < finalUpdatedArray.length() ; i++){

            try {
                JSONObject existingObj = (JSONObject) finalUpdatedArray.get(i);
                String existingDate = existingObj.getString(ConstantsKeys.startDateTime);
                String selectedDate = logObj.optString(ConstantsKeys.startDateTime);

                String existingStatus = existingObj.getString(ConstantsKeys.DriverStatusId);
                String selectedStatus = logObj.optString(ConstantsKeys.DriverStatusId);


                if(existingDate.equals(selectedDate) && existingStatus.equals(selectedStatus)){
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

        for(int i = driverLogArray.length()-1 ; i >=0  ; i--){

            try {
                JSONObject obj = (JSONObject)driverLogArray.get(i);
                String compareStartDate = obj.getString(ConstantsKeys.startDateTime);
                if(selectedDate.equals(compareStartDate)){
                    if(RecordType.equals(Constants.Location)) {
                        obj.put(ConstantsKeys.StartLocation, logObj.getString(ConstantsKeys.RecordValue));
                    }else if (RecordType.equals(Constants.Remarks)){
                        obj.put(ConstantsKeys.Remarks, logObj.getString(ConstantsKeys.RecordValue));
                    }
                    driverLogArray.put(i, obj);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

      //  Log.d("driverLogArray", "driverLogArray: " + driverLogArray);

        // Update 18 Days Array.....
        hMethods.DriverLogHelper( DriverId, dbHelper, driverLogArray);

        // -------------- Upload data on server --------------
        if(Global.isConnected(context)) {
            SAVE_DRIVER_RECORD_LOG(finalUpdatedArray, false, false, socketTimeout);
        }else{
            Global.EldToastWithDuration(DriverLogDetailFragment.saveSignatureBtn, "Connection unavailable! Your edited " + RecordType + " will be posted to server automatically when your device will be connected with working internet connection.", context.getResources().getColor(R.color.colorVoilation));
        }
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
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

            // SaveDriverLog
            progressDialog.dismiss();

            try {
                JSONObject obj = new JSONObject(response);
                String Message = obj.getString("Message");

                if (obj.getString("Status").equals("true")) {

                    Globally.EldScreenToast(DriverLogDetailFragment.saveSignatureBtn, Message , context.getResources().getColor(R.color.colorPrimary));

                    // ------------ Clear Log Record File locally ------------
                    logRecordMethod.UpdateLogRecordHelper( DriverId, dbHelper, new JSONArray());

                }else{
                    // ------------ Clear Log Record File locally ------------
                    logRecordMethod.UpdateLogRecordHelper( DriverId, dbHelper, new JSONArray());
                    Globally.EldScreenToast(DriverLogDetailFragment.saveSignatureBtn, Message , context.getResources().getColor(R.color.colorVoilation));
                }
            }catch (Exception e){
                e.printStackTrace();
            }



        }
        @SuppressLint("NewApi")
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: " );
            Globally.EldScreenToast(DriverLogDetailFragment.saveSignatureBtn, "Data updated successfully." , context.getResources().getColor(R.color.colorPrimary));
            progressDialog.dismiss();


        }
    };




}
