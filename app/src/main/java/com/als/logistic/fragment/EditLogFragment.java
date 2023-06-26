package com.als.logistic.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.adapter.logistic.EditLogRecyclerViewAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.EditLogPreviewDialog;
import com.custom.dialogs.EditLogRemarksDialog;
import com.drag.slide.listview.Menu;
import com.drag.slide.listview.MenuItem;
import com.drag.slide.listview.Utils;
import com.driver.details.DriverConst;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.FailedApiTrackMethod;
import com.local.db.HelperMethods;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.UILApplication;
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

public class EditLogFragment extends Fragment implements View.OnClickListener, EditLogRecyclerViewAdapter.AdapterCallback{


    View rootView;
    RecyclerView driverLogRecyclerView;
    RecyclerView.Adapter mWrappedAdapter;
    EditLogRecyclerViewAdapter editLogRecyclerAdapter;
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
    public static List<DriverLogModel> oDriverLogDetail = new ArrayList<DriverLogModel>();
    List<DriverLogModel> tempDriverLogDetail = new ArrayList<DriverLogModel>();

    final int SaveDriverLog = 1;
    EditLogPreviewDialog previewDialog;
    EditLogRemarksDialog editLogRemarksDialog;
    public static boolean IsWrongDateEditLog = false, IsAllowToUpdate = true;
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
    String VersionCode = "";


    List<DriverLog> oDriverLogList = new ArrayList<DriverLog>();
    DriverDetail oDriverDetail;
    RulesResponseObject RulesObj;
    MainDriverEldPref MainDriverPref;
    CoDriverEldPref CoDriverPref;
    JSONArray finalEditedLogArray = new JSONArray();
    JSONArray offlineJobArray = new JSONArray();
    JSONArray previousDateJobs = new JSONArray();
    Globally global;
    FailedApiTrackMethod failedApiTrackMethod;
    boolean isHaulExcptn;
    boolean isAdverseExcptn;
    boolean isNorthCanada;
    boolean IsUnAssignedMileRecord = false;
    int savedItemPosition = 3;
    int scrollBottomPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }

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
        failedApiTrackMethod    = new FailedApiTrackMethod();

        driverLogRecyclerView   = (RecyclerView)view.findViewById(R.id.driverLogRecyclerView);

        eldMenuLay              = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);

        eldMenuBtn              = (ImageView)view.findViewById(R.id.eldMenuBtn);
        EldTitleTV              = (TextView)view.findViewById(R.id.EldTitleTV);

        addBtn                  = (FloatingActionButton)view.findViewById(R.id.addBtn);
        saveBtn                 = (FloatingActionButton)view.findViewById(R.id.saveBtn);
        editLogProgressBar      = (ProgressBar)view.findViewById(R.id.editLogProgressBar);

        if(SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            DriverName          = DriverConst.GetDriverDetails( DriverConst.DriverName, getActivity());
        }else{
            DriverName          = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, getActivity());
        }
        
        CompanyId     = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        VersionCode  = global.GetAppVersion(getActivity(), "VersionCode");

        EldTitleTV.setText("Edit Log");
        eldMenuBtn.setImageResource(R.drawable.back_btn);
        rightMenuBtn.setVisibility(View.GONE);

        IsAllowToUpdate = true;
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
                selectedDateTime = global.getDateTimeObj(getBundle.getString("selectedDate"), false);
                selectedUtcTime = global.getDateTimeObj(getBundle.getString("selectedUtcDate"), false);
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
            logArray                = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, selectedUtcTime,
                                        IsCurrentDate, offsetFromUTC, getActivity() );
            logArrayBeforeSelectedDate = hMethods.GetArrayBeforeSelectedDate(driverLogArray, selectedDateTime);

            currentDateTime         = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, getActivity()), false);    // Current Date Time
            currentUTCTime          = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), true);

            oDriverLogDetail        = hMethods.GetLogModelEditDriver( logArray, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC);
            IsOffDutyPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OffDutyKey);
            IsSleeperPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.SleeperKey);
            IsDrivingPermission     = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.DrivingKey);
            IsOnDutyPermission      = driverPermissionMethod.getPermissionStatus(logPermissionObj, ConstantsKeys.OnDutyKey);

            IsUnAssignedMileRecord = IsUnAssignedMileRecord();

            if(IsUnAssignedMileRecord){
                IsDrivingPermission = true;
            }else{
                if(SharedPref.IsCCMTACertified(getActivity()) ) {
                   /* IsOffDutyPermission = true;
                    IsSleeperPermission = true;
                    IsOnDutyPermission  = true;*/
                    IsDrivingPermission = false;
                }
            }

            setRecyclerAdapter(false);
           // enableSwipeToDeleteAndUndo();


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

        driverLogRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int itemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (itemPosition != RecyclerView.NO_POSITION){
                    int adapterCount = recyclerView.getAdapter().getItemCount();
                    if(adapterCount > 4) {

                        if (itemPosition == adapterCount - 1 || itemPosition <= 3) {
                            if (savedItemPosition != itemPosition) {
                                Logger.LogDebug("RecyclerView", "Scroll at Bottom: " + itemPosition);
                                savedItemPosition = itemPosition;

                                if(itemPosition == adapterCount - 1){
                                    notifyAdapterWithListUpdate(true);
                                }else{
                                    notifyAdapterWithListUpdate(false);
                                }
                            }
                        }/*else{
                            if (itemPosition <= 3 && savedItemPosition != itemPosition) {
                                Logger.LogDebug("RecyclerView", "Scroll to Top: " + itemPosition);
                                savedItemPosition = itemPosition;
                            }
                        }*/
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                scrollBottomPosition = dy;

            }
        });



    }




    private boolean IsUnAssignedMileRecord(){
        boolean IsUnAssignedMileRecord = false;
        try{
            for(int i = 0 ; i < logArray.length() ; i++){
                JSONObject obj = (JSONObject) logArray.get(i);
                if(obj.has(ConstantsKeys.IsUnAssignedMileRecord) && obj.getBoolean(ConstantsKeys.IsUnAssignedMileRecord)){
                    IsUnAssignedMileRecord = true;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return IsUnAssignedMileRecord;
    }

    private void setRecyclerAdapter(boolean isScroll){

        try {

            driverLogRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();


            editLogRecyclerAdapter = new EditLogRecyclerViewAdapter(getActivity(), driverLogRecyclerView, oDriverLogDetail, selectedDateFormat, offsetFromUTC,
                    logPermissionObj, driverPermissionMethod, hMethods, IsCurrentDate, IsUnAssignedMileRecord, EditLogFragment.this);

            mWrappedAdapter = dragMgr.createWrappedAdapter(editLogRecyclerAdapter);
            driverLogRecyclerView.setLayoutManager(mLayoutManager);
            driverLogRecyclerView.setAdapter(mWrappedAdapter);

            if(isScroll){
                driverLogRecyclerView.scrollToPosition(editLogRecyclerAdapter.getItemCount()-1);
            }

            dragMgr.attachRecyclerView(driverLogRecyclerView);

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


    private void enableSwipeToDeleteAndUndo(final int position) {

        isUndo = false;
        final DriverLogModel item = editLogRecyclerAdapter.getData().get(position);

        int jobStatus = item.getDriverStatusId();

        if((item.IsUnAssignedMileRecord() && jobStatus == Constants.DRIVING) || jobStatus == Constants.DRIVING) {
            global.EldScreenToast(eldMenuBtn, "You don't have permission to delete this log.", getResources().getColor(R.color.colorVoilation));
        }else{
            editLogRecyclerAdapter.removeItem(position);

            if (isEnabled(jobStatus)) {

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
            } else {
                isUndo = true;
                editLogRecyclerAdapter.restoreItem(item, position);
                driverLogRecyclerView.scrollToPosition(position);
                global.EldScreenToast(eldMenuBtn, "You don't have permission to delete this log.", getResources().getColor(R.color.colorVoilation));
            }
        }
    }





    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.eldMenuLay:
                eldMenuLay.setEnabled(false);
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
                    logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail, getActivity());

                    if(logArray.length() > 0) {
                        obj = (JSONObject) logArray.get(logArray.length() - 1);
                        logArray.put(obj);
                    }else{
                        DateTime currentUtcTime = global.getDateTimeObj(global.GetCurrentUTCTimeFormat(), false);
                        logArray     = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, currentUtcTime,
                                IsCurrentDate, offsetFromUTC, getActivity() );
                        oDriverLogDetail = new ArrayList<DriverLogModel>();

                        if(logArray.length() > 0) {
                            obj = (JSONObject) logArray.get(0);
                            logArray = new JSONArray();
                            logArray.put(obj);
                        }
                    }

                    currentDateTime = global.getDateTimeObj(global.GetDriverCurrentDateTime(global, getActivity()), false);    // Current Date Time
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


                        DriverLogModel addNewModel = hMethods.GetDriverLogModel(obj, startDateTime, startUtcDateTime,
                                endDateTime, endUtcDateTime, IsOffDutyPermission, IsSleeperPermission,
                                IsDrivingPermission , IsOnDutyPermission, IsNewLogAdded, VersionCode);
                        addNewModel.setDriverVehicleTypeId(Constants.Driver);
                        addNewModel.setDriverLogId(0);
                        oDriverLogDetail.add(addNewModel);

                    }else{
                        DateTime endTime , endUtc;
                        if(IsCurrentDate) {
                            String startDateFormat = global.GetCurrentDeviceDateDefault(global, getActivity()) + "T00:00:00"; //2018-07-26T04:24:44.547
                            startDateTime = global.getDateTimeObj(startDateFormat, false);
                            startUtcDateTime = global.getDateTimeObj(startDateTime.plusHours(Math.abs(offsetFromUTC)).toString(), false);
                            endTime = currentDateTime;
                            endUtc = currentUTCTime;

                        }else {
                            startDateTime = selectedDateTime;
                            startUtcDateTime = selectedUtcTime; //global.getDateTimeObj(startDateTime.plusHours(Math.abs(offsetFromUTC)).toString(), false);
                            String endTimeStr = startDateTime.toString().substring(0,11) + "23:59:59" ;

                            endTime = global.getDateTimeObj(endTimeStr, false);
                            endUtc =  global.getDateTimeObj(endTime.plusHours(Math.abs(offsetFromUTC)).toString(), false);
                           // endUtc = endUtc.plusSeconds(1);

                        }

                        DriverLogModel addNewModel = hMethods.GetDriverLogModel(obj, startDateTime, startUtcDateTime,
                                endTime, endUtc, IsOffDutyPermission, IsSleeperPermission,
                                IsDrivingPermission , IsOnDutyPermission, IsNewLogAdded, VersionCode);
                        addNewModel.setDriverVehicleTypeId(Constants.Driver);
                        addNewModel.setDriverLogId(0);
                        oDriverLogDetail.add(addNewModel);
                    }

                    logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail, getActivity());


                    setRecyclerAdapter(false);
                   // LoadAdapterOnListView();

                }catch (Exception e){
                    e.printStackTrace();
                }

                IsNewLogAdded = false;

                break ;


            case R.id.saveBtn:

                saveBtn.setEnabled(false);
                IsAllowToUpdate = false;
                IsWrongDateEditLog = false;
                IsNewLogAdded = false;

                notifyAdapterWithListUpdate(false);

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
                            IsAllowToUpdate = true;
                        }
                    }, 300);

                }catch (Exception e){
                e.printStackTrace();
                }


                break ;


        }

    }


    private void notifyAdapterWithListUpdate(boolean isScroll){
        try{

            JSONArray tempTotalArray    = hMethods.GetSameArray(logArrayBeforeSelectedDate);
            JSONArray tempLogArray      = hMethods.ConvertListToJsonArray(oDriverLogDetail, getActivity());
            finalEditingArray           = hMethods.GetSameArray(logArrayBeforeSelectedDate);
            oDriverLogDetail = new ArrayList<DriverLogModel>();
            int wrongPosCall = 0;
            violationMsg = "Incorrect Time. Please check your log time on RED highlighted area.";

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
                            wrongPosCall++;
                        }
                    }else{
                        if(!IsCurrentDate){
                            if(i == tempLogArray.length()-1){
                                String time = logObj.getString(ConstantsKeys.endDateTime).substring(11, 16);
                                if(!time.equals("23:59")){
                                    IsWrongDateEditLog = true;
                                    violationMsg = "Incorrect Time. Day end time should be 23:59";
                                    wrongPosCall++;
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


                        String LastEndTimeHHMM = lastRecordEndTime.toString().substring(11, 16);
                        String StartTimeHHMM   = currentLogStartTime.toString().substring(11, 16);

                      //  Logger.LogDebug("Time", "----LastEndTimeHHMM: " +LastEndTimeHHMM);
                     //   Logger.LogDebug("Time", "----StartTimeHHMM: " +StartTimeHHMM);

                        // some times sec appears in end time and it makes time validation wrong, because here we are using hh:mm only
                        if(!LastEndTimeHHMM.equals(StartTimeHHMM)){
                            if(currentLogEndTime.isBefore(currentLogStartTime) ){
                                IsWrongDateEditLog = true;
                                violationMsg = "Incorrect Time. Start time is greater then End time in " + pos + " position.";
                                wrongPosCall++;
                            }else{
                                IsWrongDateEditLog = true;
                                violationMsg = "Incorrect Time. Start time is not matching in " + pos + " position with previous log End Time.";
                                wrongPosCall++;
                            }
                        }
                    }

                    if(wrongPosCall > 1){
                        violationMsg = "Incorrect Time. Please check your log time on RED highlighted area.";
                    }

                    lastRecordEndTime = global.getDateTimeObj(logObj.getString(ConstantsKeys.endDateTime), false);



                    if((DRIVER_JOB_STATUS == Constants.DRIVING || DRIVER_JOB_STATUS == Constants.ON_DUTY) && !isScroll) {
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
                                obj.getString(ConstantsKeys.startDateTime).substring(0,19),
                                obj.getString(ConstantsKeys.utcStartDateTime).substring(0,19),
                                obj.getString(ConstantsKeys.endDateTime).substring(0,19),
                                obj.getString(ConstantsKeys.utcEndDateTime).substring(0,19), RulesObj , false);
                        model.setNewRecordStatus(isNewRecord(logObj));
                        oDriverLogDetail.add(model);

                        finalEditingArray.put(obj);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail, getActivity());
            setRecyclerAdapter(isScroll);


        }catch (Exception e){
            e.printStackTrace();
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
                rulesVersion, oDriverLogList, getActivity());

        if(CurrentCycleId.equals(Globally.CANADA_CYCLE_1) || CurrentCycleId.equals(Globally.CANADA_CYCLE_2) ) {
            oDriverDetail.setCanAdverseException(isAdverseExcptn);
        }
        RulesResponseObject RulesObj = hMethods.CheckDriverRule(Integer.valueOf(CurrentCycleId), Integer.valueOf(DRIVER_JOB_STATUS), oDriverDetail);

        return RulesObj;
    }



    void AddSplitLogInList(JSONArray tempLogArray, JSONObject logObj, int TotalMin, double elapsedTime, int pos){
        int leftMin = TotalMin - (int) elapsedTime;

        try {
            String startDateStr = logObj.getString(ConstantsKeys.startDateTime).substring(0, 19);
            String startUtcDateStr = logObj.getString(ConstantsKeys.utcStartDateTime).substring(0, 19);
            String endDateStr = logObj.getString(ConstantsKeys.endDateTime).substring(0, 19);
            String endUtcDateStr = logObj.getString(ConstantsKeys.utcEndDateTime).substring(0, 19);


            DateTime startDateTime = Globally.getDateTimeObj(startDateStr, false);
            DateTime startUtcDateTime = Globally.getDateTimeObj(startUtcDateStr, false);
            DateTime endDateTime = Globally.getDateTimeObj(endDateStr, false);
            DateTime endUtcDateTime = Globally.getDateTimeObj(endUtcDateStr, false);


            String endDateTimeStr = startDateTime.plusMinutes((int) elapsedTime).toString().substring(0, 19);
            String endUtcDateTimeStr = startUtcDateTime.plusMinutes((int) elapsedTime).toString().substring(0, 19);

            DateTime tempEndDateTime = global.getDateTimeObj(endDateTimeStr, false);
            DateTime tempEndUtcDateTime = global.getDateTimeObj(endUtcDateTimeStr, false);


            JSONArray jArray = new JSONArray();
            jArray.put(tempLogArray.get(pos));
            JSONObject splitPart1 = hMethods.SplitJsonFromArray(jArray, logObj.getString(ConstantsKeys.startDateTime),
                    logObj.getString(ConstantsKeys.utcStartDateTime),
                    endDateTimeStr, endUtcDateTimeStr, elapsedTime, true, RulesObj );

            int elapsedTimeInt = (int) Constants.getDateTimeDuration(startDateTime, tempEndDateTime).getStandardMinutes();

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
                IsOffDutyPermission, IsSleeperPermission, IsDrivingPermission,
                IsOnDutyPermission, IsNewLogAdded, VersionCode);
       // logModel1.setDriverLogId(0);

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

    @Override
    public void onItemClicked(int position) {
        enableSwipeToDeleteAndUndo(position);
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
                    if(getActivity() != null && !getActivity().isFinishing()) {
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveEditLogWithReason(String reason){
        try {
            tempDriverLogDetail = oDriverLogDetail;

    // Add current Day log with previous day
            logArray = hMethods.ConvertListToJsonArray(oDriverLogDetail, getActivity());
            finalEditedLogArray = GetEditDataAsJson(logArray, lastDaySavedLocation, reason);

            Globally.EldScreenToast(saveBtn, "Saved data successfully.", getResources().getColor(R.color.colorPrimary));

            // set edited log status true
            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                SharedPref.SetMainDriverEditedLogStatus(true, getActivity());
            }else{
                SharedPref.SetCoDriverEditedLogStatus(true, getActivity());
            }

            // reset api call count
            failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.SAVE_DRIVER_EDIT_LOG_NEW, true, global, getActivity());

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








    //*================== Get Driver Status Permissions ===================*//*
    void GetDriverStatusPermission(final String DriverId, final String DeviceId, final String VehicleId ){

        String Country = "";
        //String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
        String  CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());
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

                String IsStatusAutomatic = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsStatusAutomatic);
                String OBDSpeed = constants.checkIntInJsonObj(obj, ConstantsKeys.OBDSpeed);
                String GPSSpeed = constants.checkIntInJsonObj(obj, ConstantsKeys.GPSSpeed);
                String PlateNumber = constants.checkStringInJsonObj(obj, ConstantsKeys.PlateNumber);
                String isHaulException = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsShortHaulException);
                String IsShortHaulUpdate = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsShortHaulUpdate);
                String isAdverseException = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsAdverseException);
                String adverseExceptionRemark = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.AdverseExceptionRemarks);
                String TruckNumber = constants.checkStringInJsonObj(obj, ConstantsKeys.Truck);
                String LocationType = constants.checkStringInJsonObj(obj, ConstantsKeys.LocationType);
                String IsNorthCanada = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsNorthCanada);
                String isNewRecord = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.isNewRecord);
                String IsCycleChanged = constants.checkStringBoolInJsonObj(obj, ConstantsKeys.IsCycleChanged);
                String CoDriverId = constants.checkStringInJsonObj(obj, ConstantsKeys.CoDriverId);
                String CoDriverName = constants.checkStringInJsonObj(obj, ConstantsKeys.CoDriverName);
                String UnAssignedVehicleMilesId = constants.checkIntInJsonObj(obj, ConstantsKeys.UnAssignedVehicleMilesId);
                String Remarks = constants.checkStringInJsonObj(obj, ConstantsKeys.Remarks);

                String DecesionSource = "edit";
                if(obj.has(ConstantsKeys.DecesionSource)){
                    DecesionSource = obj.getString(ConstantsKeys.DecesionSource);
                }

                int LocationSource = -1;
                if (obj.has(ConstantsKeys.LocationSource)) {
                    LocationSource = obj.getInt(ConstantsKeys.LocationSource);
                }

                String EngHour = "";
                if (obj.has(ConstantsKeys.EngineHours) && !obj.getString(ConstantsKeys.EngineHours).equals("null")) {
                    EngHour = obj.getString(ConstantsKeys.EngineHours);
                }

                String odometer = "";
                if (obj.has(ConstantsKeys.Odometer) && !obj.getString(ConstantsKeys.Odometer).equals("null")) {
                    odometer = obj.getString(ConstantsKeys.Odometer);
                }

                String DriverVehicleTypeId = Constants.Driver;
                if (obj.has(ConstantsKeys.DriverVehicleTypeId) && !obj.getString(ConstantsKeys.DriverVehicleTypeId).equals("null")) {
                    DriverVehicleTypeId = obj.getString(ConstantsKeys.DriverVehicleTypeId);
                }

                String IsHosLoggingRule = "false";
                if (obj.has(ConstantsKeys.IsHosLoggingRule) && !obj.getString(ConstantsKeys.IsHosLoggingRule).equals("null")) {
                    IsHosLoggingRule = obj.getString(ConstantsKeys.IsHosLoggingRule);
                }

                EldDataModelNew eldModel = new EldDataModelNew(

                        obj.getString(ConstantsKeys.ProjectId),
                        obj.getString(ConstantsKeys.DriverId),
                        obj.getString(ConstantsKeys.DriverStatusId),
                        obj.getString(ConstantsKeys.DriverLogId),

                        obj.getString(ConstantsKeys.YardMove),
                        obj.getString(ConstantsKeys.Personal),
                        DeviceID,
                        Remarks,
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
                        isNewRecord,
                        IsCycleChanged,
                        UnAssignedVehicleMilesId,
                        CoDriverId,
                        CoDriverName,
                        "false",
                        LocationSource,
                        EngHour,
                        odometer,
                        DriverVehicleTypeId,
                        IsHosLoggingRule
                        );

                    if(eldModel != null) {
                        constants.SaveEldJsonToList(eldModel, DriverJsonArray, getActivity());  /* Put data as JSON to List */
                    }


                    editLogReason = "";


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

      //  Logger.LogDebug("DriverJsonArray", "DriverJsonArray: " + DriverJsonArray);
        return DriverJsonArray;

    }


    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {
            // SaveDriverLog
            editLogProgressBar.setVisibility(View.GONE);

                try {
                   JSONObject obj = new JSONObject(response);
                    String Message = obj.getString("Message");

                    if (obj.getString("Status").equals("true")) {
                        Globally.EldScreenToast(saveBtn, Message , getResources().getColor(R.color.colorPrimary));

                        // set edited log status false
                        if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
                            SharedPref.SetMainDriverEditedLogStatus(false, getActivity());
                        }else{
                            SharedPref.SetCoDriverEditedLogStatus(false, getActivity());
                        }

                        UpdateLocalLogWithBackStack(true);

                    }else{
                        UpdateLocalLogWithBackStack(false);

                        Message = constants.getErrorMsg(Message);
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
            Logger.LogDebug("errorrr ", ">>>error dialog: " );

            try {
                if (getActivity() != null) {
                    Globally.EldScreenToast(saveBtn, "Saved data successfully.", getResources().getColor(R.color.colorPrimary));
                    editLogProgressBar.setVisibility(View.GONE);
                    oDriverLogDetail = tempDriverLogDetail;

                   // SharedPref.SetEditedLogStatus(true, getActivity());
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

                            if(IsUnAssignedMileRecord){
                                IsDrivingPermission = true;
                            }else{
                                if(SharedPref.IsCCMTACertified(getActivity()) ) {
                                   /* IsOffDutyPermission = true;
                                    IsSleeperPermission = true;
                                    IsOnDutyPermission  = true;*/
                                    IsDrivingPermission = false;
                                }
                            }

                            setRecyclerAdapter(false);

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
                        constants.SaveEldJsonToList(listModel, driverJsonArray, getActivity());  /* Put data as JSON to List */
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return driverJsonArray;

        // Logger.LogDebug("Arraay", "Arraay: " + DriverJsonArray.toString());
    }




    void RefreshTempAdapter(){
        driverLogArray          = hMethods.getSavedLogArray(Integer.valueOf(DRIVER_ID), dbHelper);
        logArray                = hMethods.GetSingleDateArray( driverLogArray, selectedDateTime, selectedDateTime, selectedUtcTime,
                                    IsCurrentDate , offsetFromUTC, getActivity());
        oDriverLogDetail        = hMethods.GetLogModelEditDriver( logArray, currentDateTime, currentUTCTime, IsCurrentDate, offsetFromUTC);

    }


    // Save data locally
    void SaveDataLocally(){
        if(IsSingleDriver){
            // clear data before adding
            MainDriverPref.ClearLogFromList(getActivity());

            // Save data for Main Driver
            List<EldDataModelNew> editLogList = constants.getLogInList(finalEditedLogArray);
            MainDriverPref.SaveDriverLoc(getActivity(), editLogList);

        }else{

            if (SharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) { // If Current driver is Main Driver
                // clear data before adding
                MainDriverPref.ClearLogFromList(getActivity());

                // Save data for Main Driver
                List<EldDataModelNew> editLogList = constants.getLogInList(finalEditedLogArray);
                MainDriverPref.SaveDriverLoc(getActivity(), editLogList);
            }else{

                // clear data before adding
                CoDriverPref.ClearCoDrLogFromList(getActivity());

                // Save data for Co Driver
                List<EldDataModelNew> editLogList = constants.getLogInList(finalEditedLogArray);
                CoDriverPref.SaveDriverLoc(getActivity(), editLogList);
            }
        }
    }

    void updateLocalLog(){
        try {
            //  Logger.LogDebug("finalEditedLogArray", "finalEditedLogArray: " + finalEditedLogArray);
            JSONArray editableLogArray = hMethods.GetSameArray(logArrayBeforeSelectedDate);

            // Add prev edited log in array
            for (int i = 0; i < logArray.length(); i++) {
                JSONObject jsonObj = (JSONObject) logArray.get(i);
                editableLogArray.put(jsonObj);
            }

            // this loop was call only when previous day log is edited.
            // In logArrayBeforeSelectedDate logs were before selected date. In upper loop we are adding previous edited log in array and in this loop we are adding current day non-edited log in array
            if(!IsCurrentDate){
                JSONArray currentLogArray   = hMethods.GetSingleDateArray( driverLogArray, currentDateTime, currentDateTime, currentUTCTime,
                        IsCurrentDate, offsetFromUTC, getActivity() );
                for (int i = 0; i < currentLogArray.length(); i++) {
                    JSONObject jsonObj1 = (JSONObject) currentLogArray.get(i);
                    editableLogArray.put(jsonObj1);
                }
            }

           // Constants.isLogEdited = true;
            // ------------ Update log array in local DB ---------
            hMethods.DriverLogHelper(Integer.valueOf(DRIVER_ID), dbHelper, editableLogArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void UpdateLocalLogWithBackStack(final boolean isBackStack){

        updateLocalLog();
        EldFragment.isUpdateDriverLog = isBackStack;

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
            }, 500);



        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
