package com.messaging.logistic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.fragment.SuggestedLogFragment;
import com.messaging.logistic.fragment.SuggestedLogListFragment;
import com.models.RecapModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestedFragmentActivity extends FragmentActivity {

    ImageView menuImageView;
    FragmentManager fragManager;
    public static JSONArray dataArray = new JSONArray();
    public static JSONArray editDataArray = new JSONArray();
    public static List<RecapModel> otherLogList   = new ArrayList<>();
    String editedData = "", DriverId, DeviceId;
    SharedPref sharedPref;
    ProgressDialog progressDialog;
    VolleyRequest GetEditedRecordRequest;
    Map<String, String> params;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
// AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.frame_layout_xml);

        GetEditedRecordRequest      = new VolleyRequest(this);

        progressDialog              = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");

        dataArray       = new JSONArray();
        editDataArray   = new JSONArray();
        otherLogList    = new ArrayList<>();
        menuImageView   = (ImageView)findViewById(R.id.menuImageView);
        sharedPref      = new SharedPref();

        Intent i        = getIntent();
        editedData      = i.getStringExtra(ConstantsKeys.suggested_data);

        DeviceId        = sharedPref.GetSavedSystemToken(this);
        DriverId        = sharedPref.getDriverId(this);

        if(editedData.length() > 0){
            try {
                editDataArray = new JSONArray(editedData);
            }catch (Exception e){
                e.printStackTrace();
            }

            parseData();

            if(editDataArray.length() > 1){
                // for multiple days record
                SuggestedLogListFragment logFragment = new SuggestedLogListFragment();
                moveFragment(logFragment);
            }else{
                // for single day record
                SuggestedLogFragment logFragment = new SuggestedLogFragment();
                moveFragment(logFragment);
            }

        }else{
            if (Globally.isConnected(this)) {
                GetSuggestedRecords(DriverId, DeviceId);
            } else {
                Globally.EldScreenToast(menuImageView, Globally.CHECK_INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
            }

        }




    }


    void moveFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsKeys.suggested_data, editedData);
        bundle.putString(ConstantsKeys.Date, "");
        fragment.setArguments(bundle);

        fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTran = fragManager.beginTransaction();
        fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTran.replace(R.id.job_fragment, fragment);
        fragmentTran.addToBackStack("SuggestedLog");
        fragmentTran.commit();

    }

    void parseData(){

        try{
              for(int dataCount = editDataArray.length()-1 ; dataCount >= 0 ; dataCount--){
                JSONObject dataObj = (JSONObject)editDataArray.get(dataCount);
                String selectedDate = dataObj.getString(ConstantsKeys.DriverLogDate);
                otherLogList.add(new RecapModel(Constants.parseDateWithName(selectedDate), selectedDate,""));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*================== Get suggested records edited from web ===================*/
    void GetSuggestedRecords(final String DriverId, final String DeviceId){

        if(progressDialog.isShowing() == false)
            progressDialog.show();

        params = new HashMap<String, String>();
        params.put("DriverId", DriverId);
        params.put("DeviceId", DeviceId );

        GetEditedRecordRequest.executeRequest(Request.Method.POST, APIs.GET_SUGGESTED_RECORDS , params, 101,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }


    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "edit response: " + response);
            JSONObject obj = null;
            String status = "";


            try {
                obj = new JSONObject(response);
                status = obj.getString(ConstantsKeys.Status);
            } catch (JSONException e) {
            }

            if (status.equalsIgnoreCase("true")) {

                try {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                }catch (Exception e){ e.printStackTrace();}

                try {
                    dataArray = new JSONArray(obj.getString(ConstantsKeys.Data));
                    editDataArray = new JSONArray(dataArray.toString());

                    parseData();

                    if(editDataArray.length() > 1){
                        // for multiple days record
                        SuggestedLogListFragment logFragment = new SuggestedLogListFragment();
                        moveFragment(logFragment);
                    }else{
                        // for single day record
                        SuggestedLogFragment logFragment = new SuggestedLogFragment();
                        moveFragment(logFragment);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };



    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {
        @Override
        public void getError(VolleyError error, int flag) {
            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }catch (Exception e){ e.printStackTrace();}

           Globally.EldScreenToast(menuImageView, error.toString(), getResources().getColor(R.color.colorVoilation));

        }
    };




        @Override
    public void onBackPressed() {
            fragManager = getSupportFragmentManager();
            if (fragManager.getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
    }

}