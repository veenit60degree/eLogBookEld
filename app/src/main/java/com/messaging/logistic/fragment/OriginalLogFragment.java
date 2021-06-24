package com.messaging.logistic.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.adapter.logistic.EditedLogAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OriginalLogFragment extends Fragment {

    View rootView;
    SuggestedLogFragment suggestedLogFragment;
    JSONArray selectedArray;
    String DriverId, DeviceId;
    int offsetFromUTC;
    TextView statusEditedTxtView , startTimeEditedTxtView, endTimeEditedTxtView, durationEditedTxtView;
    ListView editLogListView;
    WebView editLogWebView;
    LinearLayout editedItemMainLay, editedLogMainLay;
    VolleyRequest GetLogRequest;
    boolean isAPILoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.webview_log_preview, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        GetLogRequest           = new VolleyRequest(getActivity());
        editLogWebView           = (WebView)rootView.findViewById(R.id.previewLogWebView);
        editLogListView          = (ListView)rootView.findViewById(R.id.editLogListView);
        statusEditedTxtView      = (TextView)rootView.findViewById(R.id.statusEditedTxtView);
        startTimeEditedTxtView   = (TextView)rootView.findViewById(R.id.startTimeEditedTxtView);
        endTimeEditedTxtView     = (TextView)rootView.findViewById(R.id.endTimeEditedTxtView);
        durationEditedTxtView    = (TextView)rootView.findViewById(R.id.durationEditedTxtView);

        editedItemMainLay        = (LinearLayout)rootView.findViewById(R.id.editedItemMainLay);
        editedLogMainLay         = (LinearLayout)rootView.findViewById(R.id.editedLogMainLay);

        suggestedLogFragment = new SuggestedLogFragment();
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        DriverId            =  SharedPref.getDriverId( getActivity());
        offsetFromUTC       = (int) suggestedLogFragment.globally.GetTimeZoneOffSet();

        selectedArray       = suggestedLogFragment.hMethods.GetSingleDateArray( SuggestedLogFragment.driverLogArray, suggestedLogFragment.selectedDateTime, suggestedLogFragment.currentDateTime,
                                        suggestedLogFragment.selectedUtcTime, false, offsetFromUTC );

        if(selectedArray.length() > 0){
            loadData();
        }else{
            if(!isAPILoading) {
                GET_DRIVER_LOG(SuggestedLogFragment.LogDate);
            }
        }

        return rootView;
    }


    void loadData(){
        suggestedLogFragment.LoadDataOnWebView(editLogWebView, selectedArray, SuggestedLogFragment.LogDate, false);

        if(SuggestedLogFragment.originalLogList.size() > 0) {
            EditedLogAdapter adapter = new EditedLogAdapter(getActivity(), SuggestedLogFragment.originalLogList);
            editLogListView.setAdapter(adapter);

            SetCertifyListViewHeight();
        }
    }


    void SetCertifyListViewHeight(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int DividerHeigh = suggestedLogFragment.constants.intToPixel( getActivity(), editLogListView.getDividerHeight() );
                    int itemLayoutHeight = editedItemMainLay.getHeight();
                    int listSize     = SuggestedLogFragment.originalLogList.size() ;
                    int DriverLogListHeight      = itemLayoutHeight + ((itemLayoutHeight + DividerHeigh ) * listSize) + 50;
                    editLogListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DriverLogListHeight ));
                }catch (Exception e){}

            }
        }, 200);

    }



    /* ================== Get Driver Details =================== */
    void GET_DRIVER_LOG(final String date) {

        isAPILoading = true;
        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.ProjectId, Globally.PROJECT_ID);
         params.put(ConstantsKeys.DeviceId, DeviceId);
        params.put(ConstantsKeys.ELDSearchDate, date);
        params.put(ConstantsKeys.TeamDriverType, "1");

        GetLogRequest.executeRequest(Request.Method.POST, APIs.GET_DRIVER_STATUS, params, 101,
                Constants.SocketTimeout20Sec, ResponseCallBack, ErrorCallBack);
    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            JSONObject obj = null, dataObj = null;
            String status = "";

            if (getActivity() != null) {

                try {


                    try {
                        obj = new JSONObject(response);
                        status = obj.getString("Status");
                        if (!obj.isNull("Data")) {
                            dataObj = new JSONObject(obj.getString("Data"));

                            selectedArray = new JSONArray(dataObj.getString("DriverLogModel"));
                            loadData();
                        }

                    } catch (JSONException e) {
                    }

                    if (status.equalsIgnoreCase("true")) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("error", ">>error: " +error);
        }
    };


}
