                                                                                                                                                                    package com.messaging.logistic.fragment;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.adapter.logistic.DownloadLogsAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DownloadPdf;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.WebViewActvity;
import com.models.DownloadLogsModel;
import com.models.HelpDocModel;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.kingja.switchbutton.SwitchMultiButton;

import static com.messaging.logistic.Globally.GetStartDate;


public class DownloadRodsFragment extends Fragment implements View.OnClickListener{

    View rootView;
    TextView actionBarTitle,rodsFromDateTxtView,noDataRODSTV;
    DownloadLogsAdapter downloadLogsAdapter;
    Map<String, String> params;
    VolleyRequest GetDownloadLogRequest;
    ProgressBar dotProgressBar;
    List<DownloadLogsModel> downloadLogsModelList;
    Constants constants;
    ListView downloadLogListView;
    SwitchMultiButton usDotSwitchBtn;
    JSONArray dotUsLogArray = new JSONArray();
    JSONArray dotCanLogArray = new JSONArray();
    String CurrentCycle = "";
    RelativeLayout eldMenuLay,rightMenuBtn;
    ImageView eldMenuBtn;
    SwitchCompat dotSwitchButton;
    private WebView webView;
    SwitchMultiButton switchMultiButton;
    DBHelper dbHelper;
    HelperMethods hMethods;
    int DriverId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        if(UILApplication.getInstance().isNightModeEnabled()){
            getActivity().setTheme(R.style.DarkTheme);
        } else {
            getActivity().setTheme(R.style.LightTheme);
        }

        try {
            rootView = inflater.inflate(R.layout.fragment_rods_logs, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;
    }


    void initView(View v){

        GetDownloadLogRequest = new VolleyRequest(getActivity());
        dotProgressBar        = new ProgressBar(getActivity());
        constants             = new Constants();
        webView               = new WebView(getActivity());
        dbHelper              = new DBHelper(getActivity());
        hMethods              = new HelperMethods();
        actionBarTitle        = (TextView)v.findViewById(R.id.EldTitleTV);
        downloadLogListView   = (ListView) v.findViewById(R.id.downloadDataListView);
        rodsFromDateTxtView   = (TextView)v.findViewById(R.id.rodsFromDateTxtView);
        noDataRODSTV          = (TextView)v.findViewById(R.id.noDataRODSTV);
        dotProgressBar        = (ProgressBar) v.findViewById(R.id.downloadProgressBar);
        usDotSwitchBtn        = (SwitchMultiButton) v.findViewById(R.id.usDotSwitchBtn);
        eldMenuLay            = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        rightMenuBtn          = (RelativeLayout)v.findViewById(R.id.rightMenuBtn);
        eldMenuBtn            = (ImageView) v.findViewById(R.id.eldMenuBtn);
        dotSwitchButton       = (SwitchCompat) v.findViewById(R.id.dotSwitchButton);
        switchMultiButton     = (SwitchMultiButton) v.findViewById(R.id.switchButton);

        DriverId           = Integer.parseInt(SharedPref.getDriverId(getActivity()));

        eldMenuBtn.setImageResource(R.drawable.back_btn);

            Bundle getBundle = this.getArguments();
            if (getBundle != null) {
                CurrentCycle = getBundle.getString("cycle");
            }

        if(CurrentCycle.equals(Globally.CANADA_CYCLE)){
            switchMultiButton.setSelectedTab(1);
            setCanadaView();
        }else{
            switchMultiButton.setSelectedTab(0);
            setUsaView();
        }

        eldMenuLay.setOnClickListener(this);
        rightMenuBtn.setOnClickListener(this);
        switchMultiButton.setOnSwitchListener(onSwitchListener);
    }


    SwitchMultiButton.OnSwitchListener onSwitchListener = new SwitchMultiButton.OnSwitchListener() {
        @Override
        public void onSwitch(int position, String tabText) {
            downloadLogListView.setAdapter(null);
            setView();
        }
    };

   void setView(){
        if(CurrentCycle.equals(Globally.USA_CYCLE)){
            CurrentCycle = Globally.CANADA_CYCLE;
        }else{
            CurrentCycle = Globally.USA_CYCLE;
        }


        if(CurrentCycle.equals(Globally.USA_CYCLE)){
            setUsaView();
        }else{
            setCanadaView();
        }
    }



  void setCanadaView(){
      CurrentCycle = "CAN";
      dotSwitchButton.setText("CAN");
      dotCanLogArray = hMethods.getSavedDownlodedLogCanadaArray(DriverId,dbHelper);
      if(dotCanLogArray.length()>0) {
          setDataOnList(dotCanLogArray);
          noDataRODSTV.setVisibility(View.GONE);
      }else{
          checkInternetAvailability();
      }

    }

    void setUsaView(){
        CurrentCycle = "USA";
        dotSwitchButton.setText("USA");
        dotUsLogArray = hMethods.getSavedDownlodedLogUsaArray(DriverId,dbHelper);
        if(dotUsLogArray.length()>0) {
            setDataOnList(dotUsLogArray);
            noDataRODSTV.setVisibility(View.GONE);
        }else {
            checkInternetAvailability();
        }
    }




   void checkInternetAvailability(){
        if (Globally.isConnected(getActivity())) {
            CallApiDownload();
        }else{
            Globally.EldScreenToast(getActivity().findViewById(android.R.id.content), Globally.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
        }
    }

    void CallApiDownload(){
        String currentDatetime = Globally.GetCurrentUTCDate();
        String currentParseDatetime = Globally.ConvertDateFormatyyyy_MM_dd(currentDatetime);
        DateTime lastSevenDays = GetStartDate(Globally.GetCurrentJodaDateTime(),30);
        String lastParseDatetime = lastSevenDays.toString().substring(0,10);
        GetDownloadedPdfLog(CurrentCycle,lastParseDatetime,currentParseDatetime);
    }

    void GetDownloadedPdfLog(String country,String fromDate,String toDate) {

        dotProgressBar.setVisibility(View.VISIBLE);

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity()));
        params.put(ConstantsKeys.FromDate, fromDate);
        params.put(ConstantsKeys.ToDate, toDate);
        params.put(ConstantsKeys.Country, country);

        GetDownloadLogRequest.executeRequest(Request.Method.POST, APIs.GetListOfPdfCanadaLogs, params, 1,
                Constants.SocketTimeout50Sec, ResponseCallBack, ErrorCallBack);
    }

    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback(){

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void getResponse(String response, int flag) {

            dotProgressBar.setVisibility(View.GONE);

            String status = "";
            JSONObject dataObj = null;
            try {
                dataObj = new JSONObject(response);

                status = dataObj.getString("Status");


            } catch (JSONException e) {
            }

            switch (flag) {
                case 1:

                    if (status.equalsIgnoreCase("true")) {

                        try {


                            if(CurrentCycle.equals("CAN")){
                                dotCanLogArray = new JSONArray(dataObj.getString("Data"));
                                setDataOnList(dotCanLogArray);

                                if(dotCanLogArray.length()<=0) {
                                    noDataRODSTV.setVisibility(View.VISIBLE);
                                }

                                //////////////// Save Canada Log in Db ///////////////

                                hMethods.DownloadedCanadaRecordLogHelper(DriverId,dbHelper,dotCanLogArray);
                            }else{
                                dotUsLogArray = new JSONArray(dataObj.getString("Data"));
                                setDataOnList(dotUsLogArray);
                                if(dotUsLogArray.length()<=0) {
                                    noDataRODSTV.setVisibility(View.VISIBLE);
                                }

                                //////////////// Save Usa Log in Db ///////////////

                                hMethods.DownloadedUsaRecordLogHelper(DriverId,dbHelper,dotUsLogArray);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{

                    }

            }

        }


    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("error", ">>error: " + error);

        }
    };

    void setDataOnList(JSONArray array){
        try{
            downloadLogsModelList = new ArrayList<>();


            if(array.length() > 0) {
                noDataRODSTV.setVisibility(View.GONE);

                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = (JSONObject) array.get(i);
                        DownloadLogsModel model = new DownloadLogsModel(obj.getString("DriverId"),
                                obj.getString("EldInspectionLogId"), obj.getString("PdfFilePath"),
                                obj.getString("FileNameUniqueNumber"), obj.getString("LogGenratedDateTime"),
                                obj.getString("ToDate"),obj.getString("FromDate"),obj.getString("LogType"),obj.getString("ShareId"),obj.getString("Country"));

//                        obj.getString("Country")
                        downloadLogsModelList.add(model);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{

            }
            downloadLogsAdapter = new DownloadLogsAdapter(getActivity(), constants, downloadLogsModelList);
            downloadLogListView.setAdapter(downloadLogsAdapter);

            //   if(inspectionLayHeight == 0) {

        }catch (Exception e){
            e.printStackTrace();
        }



    }






    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.eldMenuLay:
                Log.d("count", "stack_count: " + getParentFragmentManager().getBackStackEntryCount());

                    getParentFragmentManager().popBackStack();
                break;

            case R.id.rightMenuBtn:

                CallApiDownload();

                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        actionBarTitle.setText(getResources().getString(R.string.download_logs));
        getActivity().registerReceiver(onComplete, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(onComplete);

    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Prevents the occasional unintentional call. I needed this.
            try {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Log.d("TAG", "onReceive: " + intent);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cur = Globally.downloadManager.query(query);
                if (cur.moveToFirst()) {
                    int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                        String path = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        String[] pathArray = path.split("///");
                        if (pathArray.length > 1) {
                            path = pathArray[1];
                        }
                        File getFileDownloaded = new File(path);
                        File pathOriginal = new File(Globally.getAlsGenerateRodsPath(getActivity()).toString());
                        Log.d("Files", "FileName:" + pathOriginal + "/" + getFileDownloaded.getName());
                        getFileDownloaded.renameTo(new File(pathOriginal + "/" + getFileDownloaded.getName()));

                    } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                        String path = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        String[] pathArray = path.split("///");
                        if (pathArray.length > 1) {
                            path = pathArray[1];
                        }
                        File getFileDownloaded = new File(path);
                        getFileDownloaded.delete();
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
