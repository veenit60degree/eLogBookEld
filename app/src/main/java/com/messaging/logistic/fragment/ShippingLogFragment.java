package com.messaging.logistic.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.logistic.ShippingAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.DatePickerDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.models.ShipmentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShippingLogFragment extends Fragment implements View.OnClickListener {


    View rootView;
    DBHelper dbHelper;
    ShipmentHelperMethod shipmentHelper;
    Globally Global;
    SharedPref sharedPref;

    ListView hiddenListView, shippingListView;
    RecyclerView notiHistoryRecyclerView;
    final int MainDriver = 101;
    final int CoDriver   = 102;

    String IsSingleDriver = "", DriverId = "", MainDriverId = "", CoDriverId = "", DeviceId = "", SelectedDate = "";
    String CurrentCycle = "", CurrentCycleId = "";
    int DriverType = 0;
    DatePickerDialog dateDialog;
    JSONArray shipment18DaysJsonArray = new JSONArray();
    JSONArray MainDriver18DaysJsonArray = new JSONArray();
    JSONArray CoDriver18DaysJsonArray = new JSONArray();
    ImageView eldMenuBtn;
    RelativeLayout rightMenuBtn, eldMenuLay, shippingMainLay;
    Map<String, String> params;
    VolleyRequest GetShippingRequest;
    List<ShipmentModel> shipmentLogList = new ArrayList<ShipmentModel>();
    ShippingAdapter shippingAdapter;
    TextView EldTitleTV, noDataEldTV, dateActionBarTV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.noti_history_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;
    }



    void initView(View view) {

        sharedPref          = new SharedPref();
        dbHelper            = new DBHelper(getActivity());
        shipmentHelper      = new ShipmentHelperMethod();
        Global              = new Globally();
        GetShippingRequest  = new VolleyRequest(getActivity());

        hiddenListView      = (ListView)view.findViewById(R.id.notiHistoryListView);
        shippingListView    = (ListView)view.findViewById(R.id.shippingListView);
        notiHistoryRecyclerView = (RecyclerView)view.findViewById(R.id.notiHistoryRecyclerView);

        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        shippingMainLay     = (RelativeLayout)view.findViewById(R.id.shippingMainLay);
        rightMenuBtn        = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        dateActionBarTV     = (TextView) view.findViewById(R.id.dateActionBarTV);
        noDataEldTV         = (TextView)view.findViewById(R.id.noDataEldTV);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);

        IsSingleDriver  = sharedPref.getDriverType(getContext());
        MainDriverId    = DriverConst.GetDriverDetails(DriverConst.DriverID, getContext());
        CoDriverId      = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getContext());
        DeviceId        = sharedPref.GetSavedSystemToken(getActivity());
        SelectedDate    = Global.GetCurrentDeviceDate();

        hiddenListView.setVisibility(View.GONE);
        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setVisibility(View.VISIBLE);
        notiHistoryRecyclerView.setVisibility(View.GONE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));


        EldTitleTV.setText(getResources().getString(R.string.ShippingDetails));

        int mnth = Integer.valueOf(SelectedDate.substring(0, 2));
        String MonthFullName    =   Globally.MONTHS_FULL[mnth - 1];

        dateActionBarTV.setText(Html.fromHtml("<b><u>" + MonthFullName + " " + SelectedDate.substring(3, SelectedDate.length() ) + "</u></b>" ));

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
        if(UILApplication.getInstance().isNightModeEnabled()){
            shippingMainLay.setBackgroundColor(getContext().getResources().getColor(R.color.gray_background));
        }

        eldMenuLay.setOnClickListener(this);
        dateActionBarTV.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();



        DriverId       = sharedPref.getDriverId( getActivity());

        try {
            GetDriverCycle(DriverType);
            LoadDataOnList();
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    void LoadDataOnList(){

        if (sharedPref.getCurrentDriverType(getActivity()).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            DriverType = Constants.MAIN_DRIVER_TYPE;     // Single Driver Type and Position is 0
            shipment18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(DriverId), dbHelper);

        } else {
            DriverType = Constants.CO_DRIVER_TYPE;     // Co Driver Type and Position is 1
            MainDriver18DaysJsonArray   = shipmentHelper.getShipment18DaysArray(Integer.valueOf(MainDriverId), dbHelper);
            CoDriver18DaysJsonArray     = shipmentHelper.getShipment18DaysArray(Integer.valueOf(CoDriverId), dbHelper);
        }


        if (IsSingleDriver.equals(DriverConst.SingleDriver)) {
            if (shipment18DaysJsonArray.length() == 0) {
                GetShipment18Days(DriverId, DeviceId, SelectedDate, MainDriver);
            }
        } else {
            if (MainDriver18DaysJsonArray.length() == 0) {
                GetShipment18Days(MainDriverId, DeviceId, SelectedDate, MainDriver);
            }
            if (CoDriver18DaysJsonArray.length() == 0) {
                GetShipment18Days(CoDriverId, DeviceId, SelectedDate, CoDriver);
            }

            if(DriverType == Constants.MAIN_DRIVER_TYPE){
                shipment18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(MainDriverId), dbHelper);
            }else{
                shipment18DaysJsonArray = shipmentHelper.getShipment18DaysArray(Integer.valueOf(CoDriverId), dbHelper);
            }
        }

        try {
            shipmentLogList = new ArrayList<ShipmentModel>();
            for(int i = 0 ; i < shipment18DaysJsonArray.length() ; i++){
                JSONObject obj = (JSONObject)shipment18DaysJsonArray.get(i);
                String[] dateArray = obj.getString(ConstantsKeys.ShippingDocDate).split(" ");
                String date = "";
                if(dateArray.length > 0){
                    date = dateArray[0];
                }

                String savedDate = "", commodity = "";
                if(obj.has(ConstantsKeys.ShippingSavedDate)){
                    savedDate = obj.getString(ConstantsKeys.ShippingSavedDate);
                }else if(obj.has(ConstantsKeys.shippingdate)){
                    savedDate = obj.getString(ConstantsKeys.shippingdate);
                }

                if(obj.has(ConstantsKeys.Commodity)){
                    commodity = obj.getString(ConstantsKeys.Commodity);
                }

                if(date.equals(SelectedDate)) {
                    boolean IsShippingCleared = false;
                    if(obj.has(ConstantsKeys.IsShippingCleared) ){
                        if(!obj.getString(ConstantsKeys.IsShippingCleared).equalsIgnoreCase("null"))
                            IsShippingCleared = obj.getBoolean(ConstantsKeys.IsShippingCleared);
                    }
                    String blNumber = obj.getString(ConstantsKeys.ShippingDocumentNumber);
                    String ShipperName =  obj.getString(ConstantsKeys.ShipperName);
                    String ShipperState =  obj.getString(ConstantsKeys.ShipperState);
                    String ShipperPostalCode =  obj.getString(ConstantsKeys.ShipperPostalCode);

                    if(!IsShippingCleared ) {
                        ShipmentModel shipModel = new ShipmentModel(
                                i,
                                DriverId,
                                CoDriverId,
                                DeviceId,
                                date,
                                blNumber,
                                commodity,
                                ShipperName,
                                ShipperState,
                                ShipperPostalCode,
                                savedDate,
                                false,
                                false
                        );

                        shipmentLogList.add(shipModel);
                    }
                }
            }

            if(shipmentLogList.size() > 0){
                noDataEldTV.setVisibility(View.GONE);
                shippingListView.setVisibility(View.VISIBLE);
            }else{
                noDataEldTV.setVisibility(View.VISIBLE);
                shippingListView.setVisibility(View.GONE);
            }

            //   Collections.sort(shipmentLogList);
            shippingAdapter = new ShippingAdapter(getActivity(), DriverId, IsSingleDriver, DeviceId,
                    DriverType, shipmentLogList);
            shippingListView.setAdapter(shippingAdapter);

        }catch (Exception e){
            e.printStackTrace();
        }




    }


    /*================== Get Shipping Documents 18 Days ===================*/
    void GetShipment18Days(final String DriverId, final String DeviceId, final String ShippingDocDate, int flag){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.ShippingDocDate, ShippingDocDate);

        GetShippingRequest.executeRequest(Request.Method.POST, APIs.GET_SHIPPING_INFO_OFFLINE , params, flag,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }



    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {
        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null;
            String status = "";

            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");
                if (status.equalsIgnoreCase("true")) {

                    switch (flag) {

                        case MainDriver:

                            try {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                shipmentHelper.Shipment18DaysHelper(Integer.valueOf(DriverId), dbHelper, resultArray);

                                LoadDataOnList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            break;


                        case CoDriver:

                            try {
                                JSONArray resultArray = new JSONArray(obj.getString("Data"));
                                shipmentHelper.Shipment18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, resultArray);

                                LoadDataOnList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

            Log.d("error", ">>error: " + error);
            switch (flag) {

            }


        }
    };


    void GetDriverCycle(int DriverType){

        try{
            if (DriverType == Constants.MAIN_DRIVER_TYPE) {
                CurrentCycle = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycle, getActivity());
                CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getActivity());
            }else{
                CurrentCycle = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycle, getActivity());
                CurrentCycleId = DriverConst.GetCoDriverCurrentCycle(DriverConst.CoCurrentCycleId, getActivity());
            }

            if (CurrentCycle.equalsIgnoreCase("null") || CurrentCycle.length() == 0) {
                CurrentCycle = Global.CANADA_CYCLE_1_NAME;
                CurrentCycleId = Global.CANADA_CYCLE_1;

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }




    void ShowDateDialog() {
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, SelectedDate, new DateListener());
            dateDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }


    private class DateListener implements DatePickerDialog.DatePickerListener {
        @Override
        public void JobBtnReady(String selectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            SelectedDate = selectedDate;
            try {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            dateActionBarTV.setText(MonthFullName + " " + selectedDate.substring(3, SelectedDate.length()));

            LoadDataOnList();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.dateActionBarTV:
                ShowDateDialog();
                break;

            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;

        }
    }
}
