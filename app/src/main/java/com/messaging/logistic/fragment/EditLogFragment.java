package com.messaging.logistic.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.logistic.EditItemTouchHelperCallback;
import com.adapter.logistic.EditLogRecyclerViewAdapter;
import com.adapter.logistic.OnStartDragListener;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.SwipeToDeleteCallback;
import com.constants.VolleyRequest;
import com.custom.dialogs.EditLogPreviewDialog;
import com.custom.dialogs.EditLogRemarksDialog;
import com.drag.slide.listview.Menu;
import com.drag.slide.listview.MenuItem;
import com.drag.slide.listview.Utils;
import com.driver.details.DriverConst;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.DriverLogModel;
import com.models.EldDataModelNew;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.DriverDetail;
import models.DriverLog;
import models.RulesResponseObject;

public class EditLogFragment extends Fragment implements View.OnClickListener, OnStartDragListener{


    View rootView;
    RecyclerView driverLogRecyclerView;
    ItemTouchHelper mItemTouchHelper;
    boolean isUndo = false;

    RelativeLayout eldMenuLay;
    RelativeLayout rightMenuBtn;
    ImageView eldMenuBtn;
    TextView EldTitleTV;
    FloatingActionButton addBtn, saveBtn;
    JSONArray logArray, driverLogArray, logArrayBeforeSelectedDate, finalEditingArray;
    DBHelper dbHelper;
    HelperMethods hMethods;
    VolleyRequest GetPermissions;
    String DRIVER_ID = "", DeviceId = "", VehicleId = "";
    final int GetDriverPermission   = 8;

    DateTime selectedDateTime, selectedUtcTime;
    DateTime currentDateTime,currentUTCTime;
    String selectedDateFormat = "";
    private Menu mMenu;
    EditLogRecyclerViewAdapter editLogRecyclerAdapter;
    public static List<DriverLogModel> oDriverLogDetail = new ArrayList<DriverLogModel>();
    List<DriverLogModel> tempDriverLogDetail = new ArrayList<DriverLogModel>();

    final int SaveDriverLog = 1;
    DriverLogModel mDraggedEntity;
    EditLogPreviewDialog previewDialog;
    EditLogRemarksDialog editLogRemarksDialog;
    public static boolean IsWrongDateEditLog = false;
    JSONObject logPermissionObj;
    DriverPermissionMethod driverPermissionMethod;
    SaveDriverLogPost saveDriverLogPost;
    Constants constants;
    boolean IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission , IsOnDutyPermission, isPermissionResponse = false;
    ProgressBar editLogProgressBar;
    String CompanyId = "",DriverName = "",   CurrentCycleId = "0", lastDaySavedLocation = "";
    int DriverType = 0, offsetFromUTC = 0;
    boolean IsSingleDriver = true, IsCurrentDate = false, IsNewLogAdded = false;
    final boolean IsOldRecord = true;
    String violationMsg = "";
    List<DriverLog> oDriverLogList = new ArrayList<DriverLog>();
    DriverDetail oDriverDetail;
    RulesResponseObject RulesObj;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    JSONArray finalEditedLogArray = new JSONArray();
    JSONArray offlineJobArray = new JSONArray();
    JSONArray previousDateJobs = new JSONArray();
    Globally global;
    boolean isHaulExcptn;
    boolean isAdverseExcptn;
    boolean isNorthCanada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_edit_log, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }


    void initView(View view){

        global                  = new Globally();
        constants               = new Constants();
        dbHelper                = new DBHelper(getActivity());
        hMethods                = new HelperMethods();
        driverPermissionMethod  = new DriverPermissionMethod();
        saveDriverLogPost       = new SaveDriverLogPost(getActivity(), saveLogRequestResponse);
        GetPermissions          = new VolleyRequest(getActivity());
        MainDriverPref          = new MainDriverEldPref();
        CoDriverPref            = new CoDriverEldPref();

        driverLogRecyclerView   = (RecyclerView)view.findViewById(R.id.driverLogRecyclerView);

        eldMenuLay              = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);

        eldMenuBtn              = (ImageView)view.findViewById(R.id.eldMenuBtn);
        EldTitleTV              = (TextView)view.findViewById(R.id.EldTitleTV);

        addBtn                  = (FloatingActionButton)view.findViewById(R.id.addBtn);
        saveBtn                 = (FloatingActionButton)view.findViewById(R.id.saveBtn);
        editLogProgressBar      = (ProgressBar)view.findViewById(R.id.editLogProgressBar);

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
            DriverName          = DriverConst.GetDriverDetails( DriverConst.DriverName, getActivity());
        }else{
            CompanyId           = DriverConst.GetCoDriverDetails(DriverConst.CoCompanyId, getActivity());
            DriverName          = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, getActivity());
        }

        EldTitleTV.setText("Edit Log");
        eldMenuBtn.setImageResource(R.drawable.back_btn);
        rightMenuBtn.setVisibility(View.GONE);

        initListControls();
        initMenu();
        initUiAndListener();



    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    private void initListControls() {

        try {
            Bundle getBundle        = this.getArguments();
            if(getBundle != null) {
                selectedDateTime = new DateTime(global.getDateTimeObj(getBundle.getString("selectedDate"), false));
                selectedUtcTime = new DateTime(global.getDateTimeObj(getBundle.getString("selectedUtcDate"), false));
                DeviceId = getBundle.getString("device_id");
                VehicleId = getBundle.getString("vehicle_id");

                offsetFromUTC = getBundle.getInt("offsetFromUTC");
                isPermissionResponse = getBundle.getBoolean("permission_response");
                IsCurrentDate = getBundle.getBoolean("isCurrentDate");
                logPermissionObj = new JSONObject(getBundle.getString("permissions"));
                CurrentCycleId = getBundle.getString("cycleId");
                DriverType = getBundle.getInt("driver_type");
               // getBundle.clear();
            }

            selectedDateFormat      = selectedDateTime.toString().substring(0,10);
            DRIVER_ID               = SharedPref.getDriverId( getActivity());
            driverLogArray          = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
            logArray                = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, selectedUtcTime, IsCurrentDate, offsetFromUTC );
            logArrayBeforeSelectedDate = hMethods.GetArrayBeforeSelectedDate(driverLogArray, selectedDateTime);

            currentDateTime         = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
            currentUTCTime          = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);

            oDriverLogDetail        = hMethods.GetLogModelEditDriver( logArray, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC);
            IsOffDutyPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OffDutyKey);
            IsSleeperPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.SleeperKey);
            IsDrivingPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.DrivingKey);
            IsOnDutyPermission      = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OnDutyKey);

            setRecyclerAdapter();
            enableSwipeToDeleteAndUndo();


            offlineJobArray         = GetDriversSavedData(DriverType);
            previousDateJobs        = hMethods.offlineSavedJob(offlineJobArray, selectedUtcTime);


            if(!isPermissionResponse){
               // editLogProgressBar.setVisibility(View.VISIBLE);
                GetDriverStatusPermission(DRIVER_ID, DeviceId, VehicleId);
            }
            isNorthCanada   =  SharedPref.IsNorthCanada(getActivity());

            if(DriverType == Constants.MAIN_DRIVER_TYPE){
                IsSingleDriver = true;
                isHaulExcptn    = SharedPref.get16hrHaulExcptn(getActivity());
                isAdverseExcptn = SharedPref.getAdverseExcptn(getActivity());

            }else{
                IsSingleDriver = false;
                isHaulExcptn    = SharedPref.get16hrHaulExcptnCo(getActivity());
                isAdverseExcptn = SharedPref.getAdverseExcptnCo(getActivity());
            }



            JSONObject knownLocObj  = (JSONObject)logArrayBeforeSelectedDate.get(logArrayBeforeSelectedDate.length()-1);
            lastDaySavedLocation    = knownLocObj.getString(ConstantsKeys.EndLocation);

        } catch (Exception e) {
            e.printStackTrace();
        }

/*
    // ----------------  Delete Item click event -----------------
        logArray.remove(position);
        oDriverLogDetail.remove(position);
        global.EldScreenToast(eldMenuBtn, "Deleted", getResources().getColor(R.color.colorSleeper));

        LoadAdapterOnListView();


        */

    }



    public void initMenu() {
        mMenu = new Menu(true);

        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(Utils.getDrawable(getActivity(), R.drawable.edit_log_bg_selector))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.drawable.trash_log))
                .build());


    }


    public void initUiAndListener() {

        eldMenuLay.setOnClickListener(this);
       // cancelBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }







    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    private void setRecyclerAdapter(){

        try {
            driverLogRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            driverLogRecyclerView.setLayoutManager(mLayoutManager);

            editLogRecyclerAdapter = new EditLogRecyclerViewAdapter(getActivity(), driverLogRecyclerView, oDriverLogDetail, selectedDateFormat, offsetFromUTC,
                    logPermissionObj, driverPermissionMethod, hMethods, IsCurrentDate, this);

            ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(editLogRecyclerAdapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(driverLogRecyclerView);

            driverLogRecyclerView.setAdapter(editLogRecyclerAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    boolean isEnabled(int Status){

        boolean isEnabled = false;
        switch (Status){

            case 1: //OFF_DUTY
                isEnabled = IsOffDutyPermission;
                break;

            case 2: //SLEEPER
                isEnabled = IsSleeperPermission;
                break;

            case 3: //DRIVING
                isEnabled = IsDrivingPermission;
                break;

            case 4: //ON_DUTY
                isEnabled = IsOnDutyPermission;
                break;
        }

        return isEnabled;

    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                isUndo = false;
                final int position = viewHolder.getAdapterPosition();
                final DriverLogModel item = editLogRecyclerAdapter.getData().get(position);

                int jobStatus = item.getDriverStatusId();
                editLogRecyclerAdapter.removeItem(position);

                if(isEnabled(jobStatus)) {

                    Snackbar snackbar = Snackbar.make(driverLogRecyclerView, getResources().getString(R.string.action_deleted), Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isUndo = true;
                            editLogRecyclerAdapter.restoreItem(item, position);
                            driverLogRecyclerView.scrollToPosition(position);

                        }
                    });

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }else{
                    isUndo = true;
                    editLogRecyclerAdapter.restoreItem(item, position);
                    driverLogRecyclerView.scrollToPosition(position);
                    global.EldScreenToast(eldMenuBtn, "You don't have permission to delete this log.", getResources().getColor(R.color.colorVoilation));
                }
            }
        };

      //  editLogRecyclerAdapter.notifyDataSetChanged();
       // Log.d("array_length", "array length: " + editLogRecyclerAdapter.getData().size());
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(driverLogRecyclerView);
    }





    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.eldMenuLay:
                Constants.IsEdiLogBackStack = true;
                getParentFragmentManager().popBackStack();
                break ;

        /*    case R.id.cancelBtn:
                getParentFragmentManager().popBackStack();
                break ;*/

            case R.id.addBtn:
                IsNewLogAdded = true;

                try {
                    JSONObject obj = new JSONObject();
                    logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail);

                    if(logArray.length() > 0) {
                        obj = (JSONObject) logArray.get(logArray.length() - 1);
                        logArray.put(obj);
                    }else{
                        DateTime currentUtcTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), false);
                        logArray     = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, currentUtcTime, IsCurrentDate, offsetFromUTC );
                        oDriverLogDetail = new ArrayList<DriverLogModel>();

                        if(logArray.length() > 0) {
                            obj = (JSONObject) logArray.get(0);
                            logArray = new JSONArray();
                            logArray.put(obj);
                        }
                    }

                    currentDateTime = global.getDateTimeObj(global.GetCurrentDateTime(), false);    // Current Date Time
                    currentUTCTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);

                    DateTime startDateTime ,startUtcDateTime;
                    DateTime endDateTime ;
                    DateTime endUtcDateTime;

                    if (oDriverLogDetail.size() > 0) {
                        startDateTime = global.getDateTimeObj(oDriverLogDetail.get(oDriverLogDetail.size() - 1).getEndDateTime().toString(), false);
                        startUtcDateTime = global.getDateTimeObj(oDriverLogDetail.get(oDriverLogDetail.size() - 1).getUtcEndDateTime().toString(), false);

                        if(IsCurrentDate){
                            endDateTime     = currentDateTime;
                            endUtcDateTime  = currentUTCTime;
                        }else{
                            endDateTime     = global.getDateTimeObj(oDriverLogDetail.get(oDriverLogDetail.size() - 1).getEndDateTime().toString(), false);
                            endUtcDateTime  = global.getDateTimeObj(oDriverLogDetail.get(oDriverLogDetail.size() - 1).getUtcEndDateTime().toString(), false);

                        }


                        DriverLogModel addNewModel = hMethods.GetDriverLogModel(obj, startDateTime, startUtcDateTime, endDateTime, endUtcDateTime,
                                IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission , IsOnDutyPermission, IsNewLogAdded);
                        oDriverLogDetail.add(addNewModel);

                    }else{
                        String startDateFormat = global.GetCurrentDeviceDateDefault() + "T00:00:00"; //2018-07-26T04:24:44.547
                        startDateTime = global.getDateTimeObj(startDateFormat, false);
                        startUtcDateTime = global.getDateTimeObj(startDateTime.plusHours(Math.abs(offsetFromUTC)).toString(), false);

                        DriverLogModel addNewModel = hMethods.GetDriverLogModel(obj, startDateTime, startUtcDateTime, currentDateTime, currentUTCTime,
                                IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission , IsOnDutyPermission, IsNewLogAdded);
                        oDriverLogDetail.add(addNewModel);
                    }

                    logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail);


                    setRecyclerAdapter();
                   // LoadAdapterOnListView();

                }catch (Exception e){
                    e.printStackTrace();
                }
                break ;


            case R.id.saveBtn:

                saveBtn.setEnabled(false);
                IsWrongDateEditLog = false;
                IsNewLogAdded = false;
                violationMsg = "Incorrect Time. Please check your log time on RED highlighted area.";

                JSONArray tempTotalArray    = hMethods.GetSameArray(logArrayBeforeSelectedDate);
                JSONArray tempLogArray      = hMethods.ConvertListToJsonArray(oDriverLogDetail);
                finalEditingArray           = hMethods.GetSameArray(logArrayBeforeSelectedDate);

                oDriverLogDetail = new ArrayList<DriverLogModel>();

                DateTime lastRecordEndTime = null;
                for(int i = 0 ; i < tempLogArray.length() ; i++) {
                    try {
                        tempTotalArray.put(tempLogArray.get(i));

                        JSONObject logObj = (JSONObject) tempLogArray.get(i);
                        int DRIVER_JOB_STATUS = logObj.getInt("DriverStatusId");
                        int TotalMin  = logObj.getInt("TotalHours");

                        if(i == 0){
                            String time = logObj.getString(ConstantsKeys.startDateTime).substring(11, 16);
                            if(!time.equals("00:00")){
                                IsWrongDateEditLog = true;
                                violationMsg = "Incorrect Time. Day start time should be 00:00";
                            }
                        }else{
                             if(!IsCurrentDate){
                                if(i == tempLogArray.length()-1){
                                    String time = logObj.getString(ConstantsKeys.endDateTime).substring(11, 16);
                                    if(!time.equals("23:59")){
                                        IsWrongDateEditLog = true;
                                        violationMsg = "Incorrect Time. Day end time should be 23:59";
                                    }
                                }
                            }

                            DateTime currentLogStartTime = global.getDateTimeObj(logObj.getString(ConstantsKeys.startDateTime), false);
                            DateTime currentLogEndTime = global.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);

                            String pos = "";
                            if(i == 1){
                                pos = "2nd";
                            }else if(i == 2){
                                pos = "3rd";
                            }else{
                                pos = "" + (i+1) + "th";
                            }
                             if(!lastRecordEndTime.equals(currentLogStartTime)){
                                 IsWrongDateEditLog = true;
                                 violationMsg = "Incorrect Time. Start time is not matching in " + pos + " position with previous log End Time.";
                             }else if(currentLogEndTime.isBefore(currentLogStartTime)){
                                 IsWrongDateEditLog = true;
                                 violationMsg = "Incorrect Time. Start time is greater then End time in "+ pos + " position.";
                             }
                        }


                        lastRecordEndTime = global.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);



                        if(DRIVER_JOB_STATUS == Constants.DRIVING || DRIVER_JOB_STATUS == Constants.ON_DUTY) {
                            CurrentCycleId = logObj.getString(ConstantsKeys.CurrentCycleId);
                            RulesObj =  InitilizeRulesObj(tempTotalArray, DRIVER_JOB_STATUS, CurrentCycleId );

                            if ( RulesObj.isViolation() ) {

                                double elapsedTime = RulesObj.getElapsedMinutesBeforeViolation();

                                if(elapsedTime == -1 ) {
                                    JSONObject obj = (JSONObject) tempLogArray.get(i);
                                    // ------------- Add Model in the list -------------
                                    DriverLogModel model = AddLogModelToList(obj,
                                            obj.getString(ConstantsKeys.startDateTime).substring(0,19),
                                            obj.getString(ConstantsKeys.utcStartDateTime).substring(0,19),
                                            obj.getString(ConstantsKeys.endDateTime).substring(0,19),
                                            obj.getString(ConstantsKeys.utcEndDateTime).substring(0,19), RulesObj, false );
                                    model.setNewRecordStatus(isNewRecord(logObj));

                                    oDriverLogDetail.add(model);

                                    finalEditingArray.put(obj);

                                }else{
                                    AddSplitLogInList(tempLogArray, logObj, TotalMin, elapsedTime, i);
                                }

                            } else {
                                JSONObject obj = (JSONObject) tempLogArray.get(i);
                                // ------------- Add Model in the list -------------
                                DriverLogModel model = AddLogModelToList(obj,
                                        obj.getString(ConstantsKeys.startDateTime).substring(0,19),
                                        obj.getString(ConstantsKeys.utcStartDateTime).substring(0,19),
                                        obj.getString(ConstantsKeys.endDateTime).substring(0,19),
                                        obj.getString(ConstantsKeys.utcEndDateTime).substring(0,19), RulesObj , false);
                                model.setNewRecordStatus(isNewRecord(logObj));
                                oDriverLogDetail.add(model);

                                finalEditingArray.put(obj);
                            }
                        }else{
                            CurrentCycleId = logObj.getString(ConstantsKeys.CurrentCycleId);
                            RulesObj =  InitilizeRulesObj(tempTotalArray, DRIVER_JOB_STATUS, CurrentCycleId );
                            JSONObject obj = (JSONObject) tempLogArray.get(i);
                            // ------------- Add Model in the list -------------
                            RulesObj.setViolation(false);
                            RulesObj.setViolationReason("");

                            obj.put(ConstantsKeys.ViolationReason, "");
                            obj.put(ConstantsKeys.IsViolation, false);

                            DriverLogModel model = AddLogModelToList(obj,
                                    obj.getString(ConstantsKeys.startDateTime).toString().substring(0,19),
                                    obj.getString(ConstantsKeys.utcStartDateTime).toString().substring(0,19),
                                    obj.getString(ConstantsKeys.endDateTime).toString().substring(0,19),
                                    obj.getString(ConstantsKeys.utcEndDateTime).toString().substring(0,19), RulesObj , false);
                            model.setNewRecordStatus(isNewRecord(logObj));
                            oDriverLogDetail.add(model);

                            finalEditingArray.put(obj);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail);

              //  if(IsWrongDateEditLog == false) {
                    //LoadAdapterOnListView();
                    setRecyclerAdapter();
               // }

                try {
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (IsWrongDateEditLog) {
                                    global.EldScreenToast(eldMenuBtn, violationMsg, getResources().getColor(R.color.colorVoilation));
                                    IsWrongDateEditLog = false;
                                } else {
                                    if (logArray.length() > 0) {
                                        previewDialog = new EditLogPreviewDialog(getActivity(), logArray, new EditLogPreviewListener());
                                        previewDialog.show();
                                    } else {
                                        global.EldScreenToast(eldMenuBtn, "You don't have any log to preview.", getResources().getColor(R.color.colorVoilation));
                                    }
                                }
                            }

                            saveBtn.setEnabled(true);

                        }
                    }, 300);

                }catch (Exception e){
                e.printStackTrace();
                }


                break ;


        }

    }


    private boolean isNewRecord(JSONObject obj){
        boolean isNewRecord = false;
        try {
            if (obj.has(ConstantsKeys.isNewRecord) && obj.getString(ConstantsKeys.isNewRecord).length() > 0) {
                isNewRecord = obj.getBoolean(ConstantsKeys.isNewRecord);
            }
        }catch (Exception e){}
        return isNewRecord;
    }


    RulesResponseObject InitilizeRulesObj(JSONArray tempTotalArray, int DRIVER_JOB_STATUS , String CurrentCycleId){
        oDriverDetail = new DriverDetail();
        oDriverLogList = hMethods.GetLogAsList(tempTotalArray);
        int rulesVersion = SharedPref.GetRulesVersion(getActivity());

        oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DRIVER_ID),
                offsetFromUTC, Integer.valueOf(CurrentCycleId), IsSingleDriver, DRIVER_JOB_STATUS, IsOldRecord,
                isHaulExcptn, isAdverseExcptn, isNorthCanada,
                rulesVersion, oDriverLogList);
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);

        return RulesObj;
    }



    void AddSplitLogInList(JSONArray tempLogArray, JSONObject logObj, int TotalMin, double elapsedTime, int pos){
        int leftMin = TotalMin - (int) elapsedTime;

        try {
            String startDateStr = logObj.getString(ConstantsKeys.startDateTime).substring(0, 19);
            String startUtcDateStr = logObj.getString(ConstantsKeys.utcStartDateTime).substring(0, 19);
            String endDateStr = global.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false).toString().substring(0, 19);
            String endUtcDateStr = global.getDateTimeObj(logObj.getString(ConstantsKeys.utcEndDateTime), false).toString().substring(0, 19);


            DateTime startDateTime = new DateTime(startDateStr);
            DateTime startUtcDateTime = new DateTime(startUtcDateStr);
            DateTime endDateTime = new DateTime(endDateStr);
            DateTime endUtcDateTime = new DateTime(endUtcDateStr);


            String endDateTimeStr = startDateTime.plusMinutes((int) elapsedTime).toString().substring(0, 19);
            String endUtcDateTimeStr = startUtcDateTime.plusMinutes((int) elapsedTime).toString().substring(0, 19);

            DateTime tempEndDateTime = global.getDateTimeObj(endDateTimeStr, false);
            DateTime tempEndUtcDateTime = global.getDateTimeObj(endUtcDateTimeStr, false);


            JSONArray jArray = new JSONArray();
            jArray.put(tempLogArray.get(pos));
            JSONObject splitPart1 = hMethods.SplitJsonFromArray(jArray, logObj.getString(ConstantsKeys.startDateTime),
                    logObj.getString(ConstantsKeys.utcStartDateTime),
                    endDateTimeStr, endUtcDateTimeStr, elapsedTime, true, RulesObj );

            int elapsedTimeInt = tempEndDateTime.getMinuteOfDay() - startDateTime.getMinuteOfDay();

            // ------------- Add Split Model 1st part in array -------------

            if(elapsedTimeInt > 0) {
                oDriverLogDetail.add(AddLogModelToList(splitPart1,
                        startDateTime.toString().substring(0, 19),
                        startUtcDateTime.toString().substring(0, 19),
                        tempEndDateTime.toString(),
                        tempEndUtcDateTime.toString(), RulesObj, true));
                finalEditingArray.put(splitPart1);
            }


            // ------------- Split JSON Object  -------------
            JSONArray j2Array = new JSONArray();
            j2Array.put(tempLogArray.get(pos));
            JSONObject splitPart2 = hMethods.SplitJsonFromArray(j2Array, tempEndDateTime.toString(), tempEndUtcDateTime.toString(),
                    logObj.getString(ConstantsKeys.endDateTime), logObj.getString(ConstantsKeys.utcEndDateTime), leftMin, false, RulesObj );

            // ------------- Add Split Model 2nd part in array -------------
            oDriverLogDetail.add(AddLogModelToList(splitPart2,
                    tempEndDateTime.toString().substring(0, 19),
                    tempEndUtcDateTime.toString().substring(0, 19),
                    endDateTime.toString().substring(0, 19),
                    endUtcDateTime.toString().substring(0, 19), RulesObj, false));

            finalEditingArray.put(splitPart2);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // ------------------- Make Driver Log type Model from Json Object -----------------
    DriverLogModel AddLogModelToList(JSONObject objJson, String startTime, String utcStartTime, String endTime, String utcEndTime,
                           RulesResponseObject RulesObj, boolean IsFirstPart){   //, boolean IsAdd, boolean AddWithoutPosMentioned

        DriverLogModel logModel1 = hMethods.GetDriverLogModel(objJson,
                global.getDateTimeObj(startTime, false),
                global.getDateTimeObj(utcStartTime, false),
                global.getDateTimeObj(endTime, false),
                global.getDateTimeObj(utcEndTime, false),
                IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission, IsOnDutyPermission, IsNewLogAdded);

        if(IsFirstPart){
            logModel1.setViolation(false);
            logModel1.setViolationReason("");
        }else {
            logModel1.setViolation(RulesObj.isViolation());
            logModel1.setViolationReason(RulesObj.getViolationReason());
        }

        try {
            if(objJson.getString(ConstantsKeys.StartLatitude).equalsIgnoreCase("null")){
                logModel1.setStartLatitude("");
                logModel1.setStartLongitude("");
            }

            if(objJson.getString(ConstantsKeys.EndLatitude).equalsIgnoreCase("null")){
                logModel1.setEndLatitude("");
                logModel1.setEndLongitude("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return logModel1;

    }


    /*================== Signature Listener ====================*/
    public class EditLogPreviewListener implements EditLogPreviewDialog.EditLogPreviewListener {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void EditPreviewReady() {

            try {
                previewDialog.dismiss();

                editLogRemarksDialog = new EditLogRemarksDialog(getActivity(), new RemarksListener());
                editLogRemarksDialog.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }



    private class RemarksListener implements EditLogRemarksDialog.RemarksListener{


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void CancelReady() {

            try {
                if (editLogRemarksDialog != null && editLogRemarksDialog.isShowing())
                    editLogRemarksDialog.dismiss();

                if(logArray.length() > 0) {
                    if(getActivity() != null) {
                        previewDialog = new EditLogPreviewDialog(getActivity(), logArray, new EditLogPreviewListener());
                        previewDialog.show();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void JobBtnReady(String AdverseExceptionRemarks) {

            try {
                if (editLogRemarksDialog != null && editLogRemarksDialog.isShowing())
                    editLogRemarksDialog.dismiss();


                saveEditLogWithReason(AdverseExceptionRemarks);

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    private void addCurrentDateLogWithPrevDay(){
        try{
             if(!IsCurrentDate) {

                JSONArray currentLogArray   = hMethods.GetSingleDateArray( driverLogArray, currentDateTime, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC );

                for(int i = 0 ; i < currentLogArray.length() ; i++){
                    JSONObject jsonObj          = (JSONObject) currentLogArray.get(i);
                    DateTime startDateTime      = new DateTime(global.getDateTimeObj(jsonObj.getString(ConstantsKeys.startDateTime), false));
                    DateTime startUtcDateTime   = new DateTime(global.getDateTimeObj(jsonObj.getString(ConstantsKeys.utcStartDateTime), false));
                    DateTime endDateTime        = new DateTime(global.getDateTimeObj(jsonObj.getString(ConstantsKeys.endDateTime), false));
                    DateTime endUtcDateTime     = new DateTime(global.getDateTimeObj(jsonObj.getString(ConstantsKeys.utcEndDateTime), false));
                    CurrentCycleId              = jsonObj.getString(ConstantsKeys.CurrentCycleId);

                    finalEditingArray.put(jsonObj);

                    int DRIVER_JOB_STATUS   = jsonObj.getInt("DriverStatusId");
                    int TotalMin            = jsonObj.getInt("TotalHours");

                    if(DRIVER_JOB_STATUS == Constants.DRIVING || DRIVER_JOB_STATUS == Constants.ON_DUTY) {
                        oDriverDetail = new DriverDetail();
                        oDriverLogList = hMethods.GetLogAsList(finalEditingArray);
                        int rulesVersion = SharedPref.GetRulesVersion(getActivity());

                        oDriverDetail = hMethods.getDriverList(currentDateTime, currentUTCTime, Integer.valueOf(DRIVER_ID),
                                offsetFromUTC, Integer.valueOf(CurrentCycleId), IsSingleDriver, DRIVER_JOB_STATUS, IsOldRecord,
                                isHaulExcptn, isAdverseExcptn, isNorthCanada,
                                rulesVersion, oDriverLogList);
                        RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);

                        if ( RulesObj.isViolation() ) {

                            double elapsedTime = RulesObj.getElapsedMinutesBeforeViolation();
                            if (elapsedTime == -1) {

                                oDriverLogDetail.add(AddLogModelToList(jsonObj,
                                        jsonObj.getString(ConstantsKeys.startDateTime).substring(0, 19),
                                        jsonObj.getString(ConstantsKeys.utcStartDateTime).substring(0, 19),
                                        jsonObj.getString(ConstantsKeys.endDateTime).substring(0, 19),
                                        jsonObj.getString(ConstantsKeys.utcEndDateTime).substring(0, 19), RulesObj, false));

                                finalEditingArray.put(jsonObj);

                            } else {
                                AddSplitLogInList(currentLogArray, jsonObj, TotalMin, elapsedTime, i);
                            }
                        }else{
                            oDriverLogDetail.add(AddLogModelToList(jsonObj,
                                    jsonObj.getString(ConstantsKeys.startDateTime).substring(0, 19),
                                    jsonObj.getString(ConstantsKeys.utcStartDateTime).substring(0, 19),
                                    jsonObj.getString(ConstantsKeys.endDateTime).substring(0, 19),
                                    jsonObj.getString(ConstantsKeys.utcEndDateTime).substring(0, 19), RulesObj, false));

                            finalEditingArray.put(jsonObj);

                        }


                    }else{
                        DriverLogModel currentLogModel  = hMethods.GetDriverLogModel(jsonObj, startDateTime, startUtcDateTime, endDateTime, endUtcDateTime,
                                IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission , IsOnDutyPermission, IsNewLogAdded);
                        oDriverLogDetail.add(currentLogModel);
                    }


                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveEditLogWithReason(String reason){
        try {
            tempDriverLogDetail = oDriverLogDetail;

    // Add current Day log with previous day
         //   addCurrentDateLogWithPrevDay();


            logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail);
            finalEditedLogArray = GetEditDataAsJson(logArray, lastDaySavedLocation, reason);

            Globally.EldScreenToast(saveBtn, "Saved data successfully.", getResources().getColor(R.color.colorPrimary));
            SharedPref.SetEditedLogStatus(true, getActivity());

            SaveDataLocally();
            UpdateLocalLogWithBackStack(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private JSONArray finalPostedArray(JSONArray previousDateJobs, JSONArray finalEditedLogArray, String reason){

        JSONArray array = new JSONArray();
        // Add previous dates jobs in final array
        for(int p = 0 ; p < previousDateJobs.length() ; p++){
            try {
                JSONObject objPrev      = (JSONObject)previousDateJobs.get(p);
                if(p == 0){
                    objPrev.put(ConstantsKeys.EditedReason, reason);
                }else{
                    objPrev.put(ConstantsKeys.EditedReason, "");
                }
                array.put(objPrev);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // Add in edited job in final array
        for(int p = 0 ; p < finalEditedLogArray.length() ; p++){
            try {
                JSONObject objEdit      = (JSONObject)finalEditedLogArray.get(p);
                if(p == 0 ){
                    if(previousDateJobs.length() == 0) {
                        objEdit.put(ConstantsKeys.EditedReason, reason);
                    }else{
                        objEdit.put(ConstantsKeys.EditedReason, "");
                    }
                }else{
                    objEdit.put(ConstantsKeys.EditedReason, "");
                }
                array.put(objEdit);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return array;
    }


    // Save Driver Cycle according to driver last location address
/*    private void SaveDriverCycle(JSONArray finalEditedLogArray){

        try {
            if(finalEditedLogArray.length() > 0){
                JSONObject lastItemJson = hMethods.GetLastJsonFromArray(finalEditedLogArray);
                String country = lastItemJson.getString(ConstantsKeys.Country).trim();

                global.SaveCurrentCycle(country, "edit_log", getActivity());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/


   private List<EldDataModelNew> getEditLogList(JSONArray logArray){

       List <EldDataModelNew> logList = new ArrayList<EldDataModelNew>();

        for(int i = 0; i < logArray.length() ; i++){
            try {
                String editLogReason = "", LocationType = "";
                JSONObject obj = (JSONObject)logArray.get(i);
                String IsStatusAutomatic = "false", OBDSpeed = "0", GPSSpeed = "0", TruckNumber = "",
                        DecesionSource = "", PlateNumber = "", isHaulException = "false", IsShortHaulUpdate = "false";
                String isAdverseException = "false", adverseExceptionRemark = "", IsNorthCanada = "false";

                if(obj.has(ConstantsKeys.IsStatusAutomatic)){
                    IsStatusAutomatic = obj.getString(ConstantsKeys.IsStatusAutomatic);
                }

                if(obj.has(ConstantsKeys.OBDSpeed)){
                    OBDSpeed = obj.getString(ConstantsKeys.OBDSpeed);
                }

                if(obj.has(ConstantsKeys.GPSSpeed)){
                    GPSSpeed = obj.getString(ConstantsKeys.GPSSpeed);
                }

                if(obj.has(ConstantsKeys.PlateNumber)){
                    PlateNumber = obj.getString(ConstantsKeys.PlateNumber);
                }

                if(obj.has(ConstantsKeys.IsShortHaulException)){
                    isHaulException = obj.getString(ConstantsKeys.IsShortHaulException);
                }

                if(obj.has(ConstantsKeys.IsShortHaulUpdate)){
                    IsShortHaulUpdate = obj.getString(ConstantsKeys.IsShortHaulUpdate);
                }


                if(obj.has(ConstantsKeys.DecesionSource)){
                    DecesionSource = obj.getString(ConstantsKeys.DecesionSource);
                }

                if(obj.has(ConstantsKeys.Truck)){
                    TruckNumber = obj.getString(ConstantsKeys.Truck);
                }

                if (obj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = obj.getString(ConstantsKeys.IsAdverseException );
                }
                if (obj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = obj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                if (obj.has(ConstantsKeys.EditedReason)) {
                    editLogReason = obj.getString(ConstantsKeys.EditedReason);
                }

                if (obj.has(ConstantsKeys.LocationType)) {
                    LocationType = obj.getString(ConstantsKeys.LocationType);
                }

                if (obj.has(ConstantsKeys.IsNorthCanada)) {
                    IsNorthCanada = obj.getString(ConstantsKeys.IsNorthCanada);
                }


                String DrivingStartTime = "", IsAOBRD = "false", CurrentCycleId = "", isDeferral = "false";
                String isNewRecord = "false";
                if (obj.has(ConstantsKeys.DrivingStartTime)) {
                    DrivingStartTime = obj.getString(ConstantsKeys.DrivingStartTime);
                }

                if (obj.has(ConstantsKeys.IsAOBRD)) {
                    IsAOBRD = obj.getString(ConstantsKeys.IsAOBRD);
                }
                if (obj.has(ConstantsKeys.CurrentCycleId)) {
                    CurrentCycleId = obj.getString(ConstantsKeys.CurrentCycleId);
                }

                if (obj.has(ConstantsKeys.isDeferral)) {
                    isDeferral = obj.getString(ConstantsKeys.isDeferral);
                }

                if (obj.has(ConstantsKeys.isNewRecord)) {
                    isNewRecord = obj.getString(ConstantsKeys.isNewRecord);
                }


                EldDataModelNew logModel = new EldDataModelNew(
                        obj.getString(ConstantsKeys.ProjectId),
                        obj.getString(ConstantsKeys.DriverId),
                        obj.getString(ConstantsKeys.DriverStatusId),

                        obj.getString(ConstantsKeys.IsYardMove),
                        obj.getString(ConstantsKeys.IsPersonal),
                        obj.getString(ConstantsKeys.DeviceID),

                        obj.getString(ConstantsKeys.Remarks),
                        obj.getString(ConstantsKeys.UTCDateTime),
                        TruckNumber,
                        obj.getString(ConstantsKeys.TrailorNumber),
                        obj.getString(ConstantsKeys.CompanyId),
                        obj.getString(ConstantsKeys.DriverName),

                        obj.getString(ConstantsKeys.City),
                        obj.getString(ConstantsKeys.State),
                        obj.getString(ConstantsKeys.Country),
                        obj.getString(ConstantsKeys.IsViolation),
                        obj.getString(ConstantsKeys.ViolationReason),
                        obj.getString(ConstantsKeys.Latitude),
                        obj.getString(ConstantsKeys.Longitude),
                        IsStatusAutomatic,
                        OBDSpeed,
                        GPSSpeed,
                        PlateNumber,

                        isHaulException,
                        IsShortHaulUpdate,

                        DecesionSource,
                        isAdverseException,
                        adverseExceptionRemark,
                        editLogReason,
                        LocationType,
                        IsNorthCanada,
                        DrivingStartTime,
                        IsAOBRD,
                        CurrentCycleId,
                        isDeferral,
                        "",
                        isNewRecord
                );

                logList.add(logModel);
            }catch (Exception e){
                e.printStackTrace();
            }
        }



        return logList;
    }




    //*================== Get Driver Status Permissions ===================*//*
    void GetDriverStatusPermission(final String DriverId, final String DeviceId, final String VehicleId ){

        String Country = "";
        String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
        if (CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2)) {
            Country = "CANADA";
        } else {
            Country = "USA";
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
        params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.VehicleId, VehicleId );
        params.put(ConstantsKeys.VIN, SharedPref.getVINNumber(getActivity()) );
        params.put(ConstantsKeys.CompanyId, DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity()) );
        params.put(ConstantsKeys.Country, Country );

        GetPermissions.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS_PERMISSION, params, GetDriverPermission,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    /* ================== Save Driver Details =================== */
    void SAVE_DRIVER_EDITED_LOG(final JSONArray geoData, final boolean isLoad, final boolean IsRecap, int socketTimeout){
        editLogProgressBar.setVisibility(View.VISIBLE);
        saveDriverLogPost.PostDriverLogData(geoData, APIs.SAVE_DRIVER_EDIT_LOG_NEW, socketTimeout, isLoad, IsRecap, 1, SaveDriverLog);

    }


    JSONArray GetEditDataAsJson( JSONArray geoData, String oldLocAddress, String editLogReason){
        String DeviceID = SharedPref.GetSavedSystemToken(getActivity());
        String City = "";
        String State = "";
        String Country = "";

        JSONArray DriverJsonArray = new JSONArray();

        // Add selected dates edited jobs in  array
        for(int i = 0 ; i<geoData.length() ; i++){
            try {
                JSONObject obj      = (JSONObject)geoData.get(i);
                String utcDate      = obj.getString(ConstantsKeys.UTCStartDateTime);
                String DrivingStartTime = obj.getString(ConstantsKeys.StartDateTime);
                utcDate             = Globally.ConvertDateFormatMMddyyyyHHmm(utcDate);
                String location     = obj.getString(ConstantsKeys.StartLocation);
                String[] loc        = location.split(",");
                String[] oldLocArr  = oldLocAddress.split(",");
                String IsStatusAutomatic = "false";
                String OBDSpeed = "0";
                String GPSSpeed = "0";
                String PlateNumber = "";
                String isHaulException = "false";
                String IsShortHaulUpdate = "false";
                String DecesionSource = "edit";
                String isAdverseException = "";
                String adverseExceptionRemark = "";

                String TruckNumber = "";
                String remarks = "";
                String LocationType = "";
                String IsNorthCanada = "false";
                String isNewRecord = "false";

                int locLength = loc.length - 1;
                City = "";
                State = "";
                Country = "";

                if (i == 0 && location.contains("Midnight")) {
                    City    = "Midnight";
                    State   = "Event";
                    if(oldLocArr.length > 2) {
                        Country = oldLocArr[locLength];
                    }
                }else {
                    if( location.contains("Midnight")){
                        if(oldLocArr.length > 2) {
                            if (locLength > 1) {
                                Country = oldLocArr[locLength];
                                State = oldLocArr[locLength - 1];
                            }

                            for (int j = 0; j < locLength - 1; j++) {
                                City = City + oldLocArr[j];
                            }
                        }
                    }else{
                        if(loc.length > 2){

                            if(locLength > 1) {
                                Country = loc[locLength];
                                State = loc[locLength - 1];
                            }

                            for(int j = 0 ; j < locLength-1 ; j++){
                                City = City + loc[j];
                            }

                        }
                    }
                }

                if(City.length() > 50){
                    City = City.substring(0, 49);
                }

                if(obj.has(ConstantsKeys.IsStatusAutomatic)){
                    IsStatusAutomatic = obj.getString(ConstantsKeys.IsStatusAutomatic);
                }

                if(obj.has(ConstantsKeys.OBDSpeed)){
                    OBDSpeed = obj.getString(ConstantsKeys.OBDSpeed);
                }

                if(obj.has(ConstantsKeys.GPSSpeed)){
                    GPSSpeed = obj.getString(ConstantsKeys.GPSSpeed);
                }

                if(obj.has(ConstantsKeys.PlateNumber)){
                    PlateNumber = obj.getString(ConstantsKeys.PlateNumber);
                }

                if(obj.has(ConstantsKeys.IsShortHaulException)){
                    isHaulException = obj.getString(ConstantsKeys.IsShortHaulException);
                }


                if(obj.has(ConstantsKeys.IsShortHaulUpdate)){
                    IsShortHaulUpdate = obj.getString(ConstantsKeys.IsShortHaulUpdate);
                }

                if(obj.has(ConstantsKeys.DecesionSource)){
                    DecesionSource = obj.getString(ConstantsKeys.DecesionSource);
                }

                if (obj.has(ConstantsKeys.IsAdverseException )) {
                    isAdverseException = obj.getString(ConstantsKeys.IsAdverseException );
                }
                if (obj.has(ConstantsKeys.AdverseExceptionRemarks)) {
                    adverseExceptionRemark = obj.getString(ConstantsKeys.AdverseExceptionRemarks);
                }

                if(obj.has(ConstantsKeys.Truck)){
                    TruckNumber = obj.getString(ConstantsKeys.Truck);
                }

                if(obj.has(ConstantsKeys.LocationType)){
                    LocationType = obj.getString(ConstantsKeys.LocationType);
                }
                if(obj.has(ConstantsKeys.IsNorthCanada)){
                    IsNorthCanada = obj.getString(ConstantsKeys.IsNorthCanada);
                }

                if(obj.has(ConstantsKeys.isNewRecord)){
                    isNewRecord = obj.getString(ConstantsKeys.isNewRecord);
                }

                remarks = obj.getString(ConstantsKeys.Remarks);

                EldDataModelNew eldModel = new EldDataModelNew(
                        obj.getString(ConstantsKeys.ProjectId),
                        obj.getString(ConstantsKeys.DriverId),
                        obj.getString(ConstantsKeys.DriverStatusId),
                        obj.getString(ConstantsKeys.YardMove),
                        obj.getString(ConstantsKeys.Personal),
                        DeviceID,
                        remarks,
                        utcDate,
                        TruckNumber,
                        obj.getString(ConstantsKeys.Trailor),

                        CompanyId,
                        DriverName,
                        City.trim(),
                        State.trim(),
                        Country.trim(),

                        obj.getString(ConstantsKeys.IsViolation),
                        obj.getString(ConstantsKeys.ViolationReason),
                        obj.getString(ConstantsKeys.StartLatitude),  //Latitude
                        obj.getString(ConstantsKeys.StartLongitude),  //Longitude
                        IsStatusAutomatic,
                        OBDSpeed,
                        GPSSpeed,
                        PlateNumber,

                        isHaulException,
                        IsShortHaulUpdate,

                        DecesionSource,
                        isAdverseException,
                        adverseExceptionRemark,
                        editLogReason,
                        LocationType,
                        IsNorthCanada,
                        DrivingStartTime,
                        ""+SharedPref.IsAOBRD(getActivity()),
                        CurrentCycleId,
                        "false",
                        "",
                        isNewRecord
                        );

                    if(eldModel != null) {
                        constants.SaveEldJsonToList(eldModel, DriverJsonArray);  /* Put data as JSON to List */
                    }


                    editLogReason = "";


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

      //  Log.d("DriverJsonArray", "DriverJsonArray: " + DriverJsonArray);
        return DriverJsonArray;

    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {
            // SaveDriverLog
            editLogProgressBar.setVisibility(View.GONE);

                try {
                   JSONObject obj = new JSONObject(response);
                    String Message = obj.getString("Message");

                    if (obj.getString("Status").equals("true")) {
                        Globally.EldScreenToast(saveBtn, Message , getResources().getColor(R.color.colorPrimary));
                        SharedPref.SetEditedLogStatus(false, getActivity());
                        UpdateLocalLogWithBackStack(true);

                    }else{
                        UpdateLocalLogWithBackStack(false);

                        if(Message.contains("ServerError")){
                            Message = "ALS server not responding";
                        }else if(Message.contains("Network")){
                            Message = "Internet connection problem";
                        }else if(Message.contains("NoConnectionError")){
                            Message = "Internet connection error";
                        }

                        Globally.EldScreenToast(saveBtn, Message , getResources().getColor(R.color.colorVoilation));
                    }
                }catch (Exception e){
                    UpdateLocalLogWithBackStack(false);
                    e.printStackTrace();
                }
            }
        @SuppressLint("NewApi")
        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("errorrr ", ">>>error dialog: " );

            try {
                if (getActivity() != null) {
                    Globally.EldScreenToast(saveBtn, "Saved data successfully.", getResources().getColor(R.color.colorPrimary));
                    editLogProgressBar.setVisibility(View.GONE);
                    oDriverLogDetail = tempDriverLogDetail;

                    SharedPref.SetEditedLogStatus(true, getActivity());
                }


                SaveDataLocally();
                UpdateLocalLogWithBackStack(true);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {
            editLogProgressBar.setVisibility(View.GONE);

            String status = "";

                try {
                    JSONObject obj = new JSONObject(response);
                    status = obj.getString("Status");

                    if(status.equalsIgnoreCase("true")) {

                        if (!obj.isNull("Data")) {
                            JSONObject dataJObject = new JSONObject(obj.getString("Data"));
                            driverPermissionMethod.DriverPermissionHelper(Integer.valueOf(DRIVER_ID), dbHelper, dataJObject);

                            IsOffDutyPermission  = driverPermissionMethod.getPermissionStatus(dataJObject, ConstantsKeys.OffDutyKey);
                            IsSleeperPermission  = driverPermissionMethod.getPermissionStatus(dataJObject, ConstantsKeys.SleeperKey);
                            IsDrivingPermission  = driverPermissionMethod.getPermissionStatus(dataJObject, ConstantsKeys.DrivingKey);
                            IsOnDutyPermission   = driverPermissionMethod.getPermissionStatus(dataJObject, ConstantsKeys.OnDutyKey);

                            isPermissionResponse = true;

                            setRecyclerAdapter();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            editLogProgressBar.setVisibility(View.GONE);

        }
    };

    /*===== Get Driver Jobs in Array List======= */
    private JSONArray GetDriversSavedData( int DriverType) {
        int listSize = 0;
        JSONArray driverJsonArray = new JSONArray();
        List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

        if (DriverType == Constants.MAIN_DRIVER_TYPE) {
            try {
                listSize = MainDriverPref.LoadSavedLoc(getActivity()).size();
                tempList = MainDriverPref.LoadSavedLoc(getActivity());
            } catch (Exception e) {
                listSize = 0;
            }
        } else {
            try {
                listSize = CoDriverPref.LoadSavedLoc(getActivity()).size();
                tempList = CoDriverPref.LoadSavedLoc(getActivity());
            } catch (Exception e) {
                listSize = 0;
            }
        }

        try {
            if (listSize > 0) {
                for (int i = 0; i < tempList.size(); i++) {
                    EldDataModelNew listModel = tempList.get(i);

                    if (listModel != null) {
                        constants.SaveEldJsonToList(listModel, driverJsonArray);  /* Put data as JSON to List */
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return driverJsonArray;

        // Log.d("Arraay", "Arraay: " + DriverJsonArray.toString());
    }




    void RefreshTempAdapter(){
        driverLogArray          = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        logArray                = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, selectedUtcTime, IsCurrentDate , offsetFromUTC);
        oDriverLogDetail        = hMethods.GetLogModelEditDriver( logArray, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC);

    }


    // Save data locally
    void SaveDataLocally(){
        if(IsSingleDriver){
            // clear data before adding
            MainDriverPref.ClearLocFromList(getActivity());

            // Save data for Main Driver
            List<EldDataModelNew> editLogList = getEditLogList(finalEditedLogArray);
            MainDriverPref.SaveDriverLoc(getActivity(), editLogList);

        }else{
            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) { // If Current driver is Main Driver
                // clear data before adding
                MainDriverPref.ClearLocFromList(getActivity());

                // Save data for Main Driver
                List<EldDataModelNew> editLogList = getEditLogList(finalEditedLogArray);
                MainDriverPref.SaveDriverLoc(getActivity(), editLogList);
            }else{

                // clear data before adding
                CoDriverPref.ClearLocFromList(getActivity());

                // Save data for Co Driver
                List<EldDataModelNew> editLogList = getEditLogList(finalEditedLogArray);
                CoDriverPref.SaveDriverLoc(getActivity(), editLogList);
            }
        }
    }

    void updateLocalLog(){
        try {
            //  Log.d("finalEditedLogArray", "finalEditedLogArray: " + finalEditedLogArray);
            JSONArray editableLogArray = hMethods.GetSameArray(logArrayBeforeSelectedDate);

            // Add prev edited log in array
            for (int i = 0; i < logArray.length(); i++) {
                JSONObject jsonObj = (JSONObject) logArray.get(i);
                editableLogArray.put(jsonObj);
            }

            // this loop was call only when previous day log is edited.
            // In logArrayBeforeSelectedDate logs were before selected date. In upper loop we are adding previous edited log in array and in this loop we are adding current day non-edited log in array
            if(!IsCurrentDate){
                JSONArray currentLogArray   = hMethods.GetSingleDateArray( driverLogArray, currentDateTime, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC );
                for (int i = 0; i < currentLogArray.length(); i++) {
                    JSONObject jsonObj1 = (JSONObject) currentLogArray.get(i);
                    editableLogArray.put(jsonObj1);
                }
            }
            // ------------ Update log array in local DB ---------
            hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, editableLogArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void UpdateLocalLogWithBackStack(final boolean isBackStack){

        updateLocalLog();
        EldFragment.isUpdateDriverLog = true;

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isBackStack) {
                        getParentFragmentManager().popBackStack();
                    }else {
                        RefreshTempAdapter();
                    }
                }
            }, 300);



        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
