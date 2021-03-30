package com.messaging.logistic.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.logistic.NotificationHistoryRecylerAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.SwipeToDeleteCallback;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.NotificationMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.Notification18DaysModel;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.NotificationPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class NotificationHistoryFragment extends Fragment implements View.OnClickListener{

    View rootView;
    RecyclerView notiHistoryRecyclerView;
    ProgressBar notiProgressBar;
    ImageView eldMenuBtn;
    RelativeLayout eldMenuLay, shippingMainLay;
    RelativeLayout rightMenuBtn;
    TextView EldTitleTV, noDataEldTV, dateActionBarTV;
    public static FloatingActionButton deleteNotificationBtn;
    //NotificationHistoryAdapter historyAdapter;
    NotificationHistoryRecylerAdapter historyRecylerAdapter;
    List<Notification18DaysModel> notificationsList = new ArrayList<Notification18DaysModel>();
    JSONArray historyArray = new JSONArray();
    JSONArray saveToNotificationArray = new JSONArray();
    String DeviceId = "", DriverId = "", CoDriverId = "", DriverLoginType = "";

    SharedPref sharedPref;
    DBHelper dbHelper;
    NotificationPref notificationPref;
    CoNotificationPref coNotificationPref;
    NotificationMethod notificationMethod;
    SaveDriverLogPost saveNotificationHistoryReq;
    Constants constants;
    Globally global;
    AlertDialog saveJobAlertDialog;
    private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();


    VolleyRequest GetNotificationRequest, GetMessagesRequest;
    Map<String, String> params;
    final int GetNotifications  = 1;
    final int MainDriverLog     = 101;
    final int CoDriverLog       = 102;
    final int DeleteLog         = 103;
    int DriverType              = 0;
    boolean isUndo = false, isSwipe = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.noti_history_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    @SuppressLint("RestrictedApi")
    void initView(View view){

        notificationPref            = new NotificationPref();
        coNotificationPref          = new CoNotificationPref();
        constants                   = new Constants();
        global                      = new Globally();
        sharedPref                  = new SharedPref();
        dbHelper                    = new DBHelper(getActivity());

        notiProgressBar             = (ProgressBar)view.findViewById(R.id.notiProgressBar);
        dateActionBarTV             = (TextView) view.findViewById(R.id.dateActionBarTV);
        notiHistoryRecyclerView     = (RecyclerView)view.findViewById(R.id.notiHistoryRecyclerView);
        eldMenuBtn                  = (ImageView)view.findViewById(R.id.eldMenuBtn);
        eldMenuLay                  = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        shippingMainLay             = (RelativeLayout)view.findViewById(R.id.shippingMainLay);
        deleteNotificationBtn       = (FloatingActionButton)view.findViewById(R.id.deleteNotificationBtn);

        rightMenuBtn                = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        EldTitleTV                  = (TextView)view.findViewById(R.id.EldTitleTV);
        noDataEldTV                 = (TextView)view.findViewById(R.id.noDataEldTV);

        GetNotificationRequest      = new VolleyRequest(getActivity());
        GetMessagesRequest          = new VolleyRequest(getActivity());
        notificationMethod          = new NotificationMethod();
        saveNotificationHistoryReq  = new SaveDriverLogPost(getActivity(), saveNotificationReqResponse);

        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);
        EldTitleTV.setText(getResources().getString(R.string.NotificationsHistory));
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        dateActionBarTV.setText(Html.fromHtml("<b><u>" + getString(R.string.DeleteAll) + "</u></b>"));

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            shippingMainLay.setBackgroundColor(getContext().getResources().getColor(R.color.gray_background));
        }

        enableSwipeToDeleteAndUndo();



        eldMenuLay.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);
        deleteNotificationBtn.setOnClickListener(this);
        //  dateActionBarTV.setVisibility(View.GONE);

    }



    @Override
    public void onResume() {
        super.onResume();

       /* TextView obdTestDataTV = (TextView)rootView.findViewById(R.id.obdTestDataTV);
        try {
            JSONArray obdDataArray = new JSONArray( sharedPref.getObdSpeed(getActivity()) );
            JSONArray reverseArray = sharedPref.ReverseArray(obdDataArray);
            obdTestDataTV.setText(reverseArray.toString());

        }catch (Exception e){

        }*/


        DeviceId        = sharedPref.GetSavedSystemToken(getActivity());
        DriverLoginType = sharedPref.getDriverType(getContext());

        if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {
            DriverType  = 0;
            DriverId    = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
        }else {
            DriverType  = 1;
            DriverId    = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
        }

        // Clear unread notification badge count
        constants.ClearUnreadNotifications(DriverType, notificationPref, coNotificationPref,  getActivity());

        // notificationPref.GetNotificationsList();




        saveToNotificationArray = notificationMethod.getSaveToNotificationArray(Integer.valueOf(DriverId), dbHelper);


        historyArray = notificationMethod.getSavedNotificationArray(Integer.valueOf(DriverId), dbHelper);
        // Parse Notification details
        ParseJSON(false, historyArray, new JSONObject());

        if (global.isConnected(getActivity()) ) {
            boolean isDeleted = sharedPref.isNotificationDeleted(getActivity());

            if(notificationsList.size() == 0 && !isDeleted ) {
                GetNotificationLog(DriverId, DeviceId);
            }

            if(saveToNotificationArray.length() > 0){
                saveNotificationHistoryReq.PostDriverLogData(saveToNotificationArray, APIs.SAVE_NOTIFICATION, constants.SocketTimeout20Sec, false, false, DriverType, MainDriverLog);
            }

        }


    }




    /* ---------------------- Save Notification Request Response ---------------- */
    DriverLogResponse saveNotificationReqResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {


            notiProgressBar.setVisibility(View.GONE);
            JSONObject obj;

            try {
                obj = new JSONObject(response);
                String Message = obj.getString("Message");
                //    Global.EldScreenToast(OnDutyBtn, Message, getResources().getColor(R.color.colorPrimary));

                if (obj.getString("Status").equals("true")) {
                    switch (flag) {

                        case MainDriverLog:

                            // Clear un posted logs from array
                            notificationMethod.SaveToNotificationHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

                            if(DriverLoginType.equals(DriverConst.TeamDriver)){
                                if(DriverType == Constants.MAIN_DRIVER_TYPE){    // if Current driver is main driver
                                    CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                                }else{   // if Current driver is co driver
                                    CoDriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                                }

                                JSONArray saveToCoDriverSaveToArray = notificationMethod.getSaveToNotificationArray(Integer.valueOf(CoDriverId), dbHelper);

                                if(saveToCoDriverSaveToArray.length() > 0){
                                    saveNotificationHistoryReq.PostDriverLogData(saveToCoDriverSaveToArray, APIs.SAVE_NOTIFICATION, constants.SocketTimeout20Sec, false, false, DriverType, CoDriverLog);
                                }
                            }


                            break;


                        case CoDriverLog:
                            // Clear un posted logs from array
                            notificationMethod.SaveToNotificationHelper(Integer.valueOf(CoDriverId), dbHelper, new JSONArray());

                            break;


                        case DeleteLog:
                            if(!isSwipe) {
                                global.EldScreenToast(eldMenuLay, "Log successfully deleted", getResources().getColor(R.color.colorPrimary));
                            }
                            isSwipe = false;

                            // refresh adapter with Remaining logs with local data
                            finalSavedNotifications();

                            // call get notification API to sync with server
                            GetNotificationLog(DriverId, DeviceId);

                            break;

                    }
                }else{
                    if(flag == DeleteLog){
                        global.EldScreenToast(eldMenuLay, Message, getResources().getColor(R.color.colorVoilation));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Log.d("error", "error: " +error);
            notiProgressBar.setVisibility(View.GONE);
            if(!isSwipe) {
                try {
                    if (flag == DeleteLog && getResources() != null) {
                        global.EldScreenToast(eldMenuLay, "Error", getResources().getColor(R.color.colorVoilation));
                    }
                }catch (Exception e){}
            }
            isSwipe = false;
        }

    };


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.dateActionBarTV:

                if(notificationsList.size() > 0) {
                    deleteConfirmationDialog();
                }

                break;

            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

            case R.id.deleteNotificationBtn:

                if(historyRecylerAdapter.selectedPosArray.size() > 0){
                    getFinalSelectedArray(true, historyRecylerAdapter.selectedPosArray, true);
                }else{
                    Globally.EldScreenToast(deleteNotificationBtn, "Select atleast 1 item to delete", getResources().getColor(R.color.colorVoilation));
                }

                break;


        }
    }


    private  void finalSavedNotifications(){
        List<Notification18DaysModel> notiList = historyRecylerAdapter.getData();
        JSONArray  finalNotificationArray = new JSONArray();

        for(int i = 0 ; i < notiList.size() ; i++){
            if(!historyRecylerAdapter.selectedPosArray.contains(i)){
                Notification18DaysModel listModel = notiList.get(i);

                try {

                    JSONObject obj = new JSONObject();
                    obj.put(ConstantsKeys.NotificationLogId, listModel.getNotificationLogId());
                    obj.put(ConstantsKeys.DriverId, listModel.getDriverId());
                    obj.put(ConstantsKeys.DeviceId, DeviceId);
                    obj.put(ConstantsKeys.DriverName, listModel.getDriverName());
                    obj.put(ConstantsKeys.NotificationTypeId, listModel.getNotificationTypeId());
                    obj.put(ConstantsKeys.NotificationTypeName, listModel.getNotificationTypeName());
                    obj.put(ConstantsKeys.Title, listModel.getTitle());
                    obj.put(ConstantsKeys.Message, listModel.getMessage());
                    obj.put(ConstantsKeys.ImagePath, listModel.getImagePath());
                    obj.put(ConstantsKeys.SendDate, listModel.getSendDate());
                    obj.put(ConstantsKeys.CompanyId, listModel.getCompanyId());

                    finalNotificationArray.put(obj);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


        notificationMethod.SaveToNotificationHelper(Integer.valueOf(DriverId), dbHelper, finalNotificationArray );

        ParseJSON(false, finalNotificationArray, new JSONObject());


    }



    private void getFinalSelectedArray(boolean isSelected, ArrayList<Integer> selectedArray, boolean isLoaderShown ){


        JSONArray finalJsonArray = new JSONArray();

        try {
            if (global.isConnected(getActivity())) {
                try {
                    if (isSelected) {
                        for (int i = 0; i < selectedArray.size(); i++) {
                            JSONObject jsonObj = getNotificationModel(notificationsList.get(selectedArray.get(i)));
                            finalJsonArray.put(jsonObj);
                        }
                    } else {
                        List<Notification18DaysModel> notiList = historyRecylerAdapter.getData();
                        for (int i = 0; i < notiList.size(); i++) {
                            JSONObject jsonObj = getNotificationModel(notiList.get(i));
                            finalJsonArray.put(jsonObj);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //  Log.d("finalJsonArray", "finalJsonArray: " + finalJsonArray);


                if (finalJsonArray.length() > 0) {
                    if (isLoaderShown) {
                        notiProgressBar.setVisibility(View.VISIBLE);
                    }
                    saveNotificationHistoryReq.PostDriverLogData(finalJsonArray, APIs.CLEAR_NOTIFICATION_LOG,
                            constants.SocketTimeout20Sec, false, false, DriverType, DeleteLog);
                }

            } else {
                if(getActivity() != null)
                    global.EldScreenToast(eldMenuLay, global.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }




    public void deleteConfirmationDialog(){

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Delete Notifications !!");
            alertDialogBuilder.setMessage("Do you really want to delete all notifications history?");

            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            getFinalSelectedArray(false, new ArrayList<Integer>(), true );
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            saveJobAlertDialog = alertDialogBuilder.create();
            vectorDialogs.add(saveJobAlertDialog);
            saveJobAlertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private JSONObject getNotificationModel(Notification18DaysModel notificationModel){

        JSONObject jsonObj = new JSONObject();

        try {
            // 2020-06-09T04:34:23.000Z
            String date = notificationModel.getSendDate();
            if(date.length() > 19){
                date = date.substring(0, 19);
            }

            jsonObj.put(ConstantsKeys.DriverId, notificationModel.getDriverId());
            jsonObj.put(ConstantsKeys.DeviceId, DeviceId);
            jsonObj.put(ConstantsKeys.NotificationTypeId, notificationModel.getNotificationTypeId() );
            jsonObj.put(ConstantsKeys.SendDate, date );

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObj;
    }



    /*================== Get Driver Trip Details ===================*/
    void GetNotificationLog(final String DriverId, final String DeviceId){  /*, final String SearchDate*/

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
         params.put(ConstantsKeys.ProjectId, global.PROJECT_ID );
        GetNotificationRequest.executeRequest(Request.Method.POST, APIs.GET_NOTIFICATION_LOG , params, GetNotifications,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }




    @SuppressLint("RestrictedApi")
    private void ParseJSON(boolean isServerData, JSONArray jsonArray, JSONObject obj){
        try {

            notificationsList = new ArrayList<Notification18DaysModel>();

            JSONArray notification18DaysArray = new JSONArray();
            if(isServerData) {
                jsonArray = new JSONArray(obj.getString(ConstantsKeys.Data));
            }

            for(int i = 0 ; i < jsonArray.length();i++){
                JSONObject dataJson = (JSONObject)jsonArray.get(i);

                if(dataJson.getString(ConstantsKeys.Title).length() > 0) {
                    Notification18DaysModel model = new Notification18DaysModel(
                            dataJson.getInt(ConstantsKeys.NotificationLogId),
                            dataJson.getString(ConstantsKeys.DriverId),
                            dataJson.getString(ConstantsKeys.DriverName),
                            dataJson.getString(ConstantsKeys.NotificationTypeId),
                            dataJson.getString(ConstantsKeys.NotificationTypeName),
                            dataJson.getString(ConstantsKeys.Title),
                            dataJson.getString(ConstantsKeys.Message),
                            dataJson.getString(ConstantsKeys.ImagePath),
                            dataJson.getString(ConstantsKeys.SendDate),
                            dataJson.getString(ConstantsKeys.CompanyId)
                    );
                    notificationsList.add(model);
                    notification18DaysArray.put(dataJson);
                }
            }

            if(isServerData) {
                // Save Notifications in 18 days Array
                notificationMethod.NotificationHelper(Integer.valueOf(DriverId), dbHelper, notification18DaysArray);
            }

            // Load adapter on ListView
            //  historyAdapter = new NotificationHistoryAdapter(getActivity(), notificationsList);
            historyRecylerAdapter = new NotificationHistoryRecylerAdapter(getActivity(), global, notificationsList);
            notiHistoryRecyclerView.setAdapter(historyRecylerAdapter);

            deleteNotificationBtn.setVisibility(View.GONE);
            if(notificationsList.size() > 0)
                noDataEldTV.setVisibility(View.GONE);
            else
                noDataEldTV.setVisibility(View.VISIBLE);

        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                isSwipe = true;
                isUndo = false;
                sharedPref.notificationDeleted(true, getActivity());
                final int position = viewHolder.getAdapterPosition();
                final Notification18DaysModel item = historyRecylerAdapter.getData().get(position);

                historyRecylerAdapter.removeItem(position);
                // notificationsList.remove(position);

                Snackbar snackbar = Snackbar.make(eldMenuLay, getResources().getString(R.string.notification_deleted), Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isUndo = true;
                        historyRecylerAdapter.restoreItem(item, position);
                        notiHistoryRecyclerView.scrollToPosition(position);

                        //     notificationsList.add(position, item);

                    }
                });



                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

                // Update notification list after delete on Undo item
                updateNotificationList(position);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(notiHistoryRecyclerView);
    }


    private void updateNotificationList( final int position) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONArray notificationArray = notificationMethod.getNotification18DaysJSONArray(notificationsList);

                // Update Notifications 18 days Array
                notificationMethod.NotificationHelper(Integer.valueOf(DriverId), dbHelper, notificationArray);

                if(!isUndo) {
                    ArrayList<Integer> array = new ArrayList<>();
                    array.add(position);
                    getFinalSelectedArray(true, array, false);
                }

            }
        }, 3000);

    }





    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;  //, dataObj = null;
            String status = "";


            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");

               /* if (!obj.isNull("Data")) {
                    dataObj = new JSONObject(obj.getString("Data"));
                }*/
            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {
                switch (flag) {
                    case GetNotifications:
                        Log.d("response", "response: " + response);

                        // Parse Notification details
                        ParseJSON(true, new JSONArray(), obj);

                }
            }else{
                try {
                    if(obj.getString("Message").equals("Device Logout") && EldFragment.DriverJsonArray.length() == 0){
                        global.ClearAllFields(getActivity());
                        global.StopService(getActivity());
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(i);
                        getActivity().finish();
                    }else{
                        TabAct.host.setCurrentTab(0);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            switch (flag){

                default:
                    Log.d("Driver", "error" + error.toString());
                    break;
            }
        }
    };

}
