package com.custom.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.Constants;
import com.constants.Logger;
import com.constants.VolleyRequest;
import com.als.logistic.Globally;
import com.als.logistic.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DriverAddressDialog extends Dialog implements AdapterView.OnItemClickListener {


    public interface LocationListener {

        public void CancelLocReady();

        public void SaveLocReady(String Address);

    }


    private String City = "";
    private LocationListener locListener;
    AutoCompleteTextView CityNameEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    Spinner locationSpinner;
    TextView TitleTV;
    Map<String, String> params;
    View view;
    VolleyRequest AddressRequest;
    ArrayList formattedAddress = new ArrayList();
    ArrayAdapter<String> adapter;
    String address = "";
    boolean isInProgress = false;

    public DriverAddressDialog(Context context,
                                View view,  LocationListener readyListener) {
        super(context);

        this.view            = view;
        locListener          = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_address);
        setCancelable(false);
        AddressRequest   = new VolleyRequest(getContext());
         adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, formattedAddress);
        //Getting the instance of AutoCompleteTextView
        CityNameEditText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        CityNameEditText.setThreshold(0);//will start working from first character
        CityNameEditText.setAdapter(adapter);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        btnLoadingJob       = (Button) findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button) findViewById(R.id.btnCancelLoadingJob);

        TitleTV             = (TextView) findViewById(R.id.TitleTV);

        locationSpinner     = (Spinner) findViewById(R.id.remarkSpinner);

            TitleTV.setText("Enter Source Address");
            CityNameEditText.setHint("Address");
            CityNameEditText.setText(City);
            CityNameEditText.setSelection(City.length());
        CityNameEditText.setOnItemClickListener(this);







        CityNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String updatedCityName = CityNameEditText.getText().toString().trim();
                if(updatedCityName.length() > 2) {
                    if(!isInProgress) {
                        AddressUser(updatedCityName);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btnLoadingJob.setOnClickListener(new LocationFieldListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());

        HideKeyboard();
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }


    private class LocationFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            String updatedCityName = CityNameEditText.getText().toString().trim();
            locListener.SaveLocReady(updatedCityName);

        }
    }

    void AddressUser(final String Address){

        isInProgress = true;
        params = new HashMap<String, String>();
        AddressRequest.executeRequest(Request.Method.GET, "https://dev.virtualearth.net/REST/v1/Autosuggest?query="+Address+"&key=Aovjh460r6Z3o2jcmOdWUCfYBTKQpZaNDOBvkX9LK78OB9wvbF3WnxDPKyyFJmYr", null, 1,
                Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);

    }

    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {


            Logger.LogDebug("response", "DriverAddDia logout response: " + response);
            //String status = "";
            isInProgress = false;
            formattedAddress = new ArrayList();

            try {
                JSONObject obj = new JSONObject(response);
                String getObject = obj.getString("resourceSets");
//                JSONObject getResources = new JSONObject(getObject);
                JSONArray getResources = new JSONArray(getObject);
//                getResources
                String getIndex = getResources.getString(0);

                JSONObject lastObject = new JSONObject(getIndex);
                String lastResources = lastObject.getString("resources");
                JSONArray getLastResources = new JSONArray(lastResources);
                String getLastIndex = getLastResources.getString(0);
                JSONObject getValues = new JSONObject(getLastIndex);
                String getValue = getValues.getString("value");
                JSONArray getAddress = new JSONArray(getValue);

                for(int i = 0 ; i< getAddress.length() ; i++){
                    String getLastAddress = getAddress.getString(i);
                    JSONObject addressArray = new JSONObject(getLastAddress);
                    String addressObject = addressArray.getString("address");
                    JSONObject formattedAddresss = new JSONObject(addressObject);
                    String getAddresss = formattedAddresss.getString("formattedAddress");
                    Logger.LogDebug("",getAddresss);
                    formattedAddress.add(getAddresss);

                }
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, formattedAddress);
                CityNameEditText.setAdapter(adapter);
                Logger.LogDebug("", String.valueOf(formattedAddress));



            }catch(Exception e){
                e.printStackTrace();
            }

        }
    };

    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

        @Override
        public void getError(VolleyError error, int flag) {
            isInProgress = false;
            Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());
        }
    };


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            dismiss();
            locListener.CancelLocReady();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // fetch the user selected value
        String item = parent.getItemAtPosition(position).toString();
        CityNameEditText.setText(item);
        Globally.hideKeyboardView(getContext(), CityNameEditText);

    }



}