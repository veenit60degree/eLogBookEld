package com.custom.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidtrip.plugins.searchablespinner.SearchableSpinner;
import com.androidtrip.plugins.searchablespinner.interfaces.IStatusListener;
import com.androidtrip.plugins.searchablespinner.interfaces.OnItemSelectedListener;
import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.models.VehicleModel;
import com.searchable.spinner.SearchArrayListAdapter;

import java.util.ArrayList;
import java.util.List;

public class VehicleDialog extends Dialog {


    public interface VehicleListener {
        public void ChangeVehicleReady(String Title, int position, boolean isOldDialog);
    }

    Constants constants;
    List<VehicleModel> truckList;
    private VehicleListener readyListener;
    Button btnSaveVehList, btnCancelLoadingJob;
    SearchableSpinner updateVehSearchableSpinner;
    String Truck, Title = "";
    TextView TitleVehTV, updateVehTitleTV, logoutVehTV;
    int SelectedPosition = -1, SetSpinnerPosition = 0;
    private SearchArrayListAdapter mSimpleArrayListAdapter;
    boolean isOldDialog;

    public VehicleDialog(Context context, String truck, boolean isOldDialog, List<VehicleModel> remarkList, VehicleListener readyListener) {
        super(context);
        Truck = truck;
        this.isOldDialog = isOldDialog;
        this.truckList = remarkList;
        this.readyListener = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_vehicle_list);
         setCancelable(false);

        constants = new Constants();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        if(Globally.isTablet(getContext())) {
            lp.width = constants.intToPixel(getContext(), 650);
        }else{
            lp.width = constants.intToPixel(getContext(), 550);
        }
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);



        btnSaveVehList = (Button) findViewById(R.id.btnSaveVehList);
        btnCancelLoadingJob = (Button) findViewById(R.id.btnCancelChngeVehJob);

        TitleVehTV = (TextView) findViewById(R.id.TitleVehTV);
        updateVehTitleTV = (TextView) findViewById(R.id.updateVehTitleTV);
        logoutVehTV = (TextView) findViewById(R.id.logoutVehTV);
        updateVehSearchableSpinner = (SearchableSpinner) findViewById(R.id.searchableSpinner);

        btnCancelLoadingJob.setVisibility(View.VISIBLE);
        logoutVehTV.setVisibility(View.GONE);
        Title = "Vehicles";

        if (truckList.size() > 0) {
            ArrayList<String> EquipmentList = new ArrayList<String>();
            for(int i = 0 ; i < truckList.size() ; i++ ) {
                if(Truck.equals(truckList.get(i).getEquipmentNumber())){
                    SetSpinnerPosition = i;
                }
                EquipmentList.add(truckList.get(i).getEquipmentNumber());
            }

            // Creating adapter for spinner
            if(EquipmentList.size() > 0) {

                mSimpleArrayListAdapter = new SearchArrayListAdapter(getContext(), EquipmentList);
                updateVehSearchableSpinner.setAdapter(mSimpleArrayListAdapter);
                updateVehSearchableSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
                updateVehSearchableSpinner.setSelectedItem(SetSpinnerPosition + 1);

                updateVehSearchableSpinner.setStatusListener(new IStatusListener() {
                    @Override
                    public void spinnerIsOpening() {
                        //mSearchableSpinner1.hideEdit();
                        //mSearchableSpinner2.hideEdit();

                    }

                    @Override
                    public void spinnerIsClosing() {

                    }
                });

            }else {
                updateVehTitleTV.setVisibility(View.INVISIBLE);
                updateVehSearchableSpinner.setVisibility(View.INVISIBLE);
                if(Truck.trim().length() == 0) {
                    Title = "We can't see any truck attached with you. Please contact with your support team.";
                }

                btnCancelLoadingJob.setVisibility(View.INVISIBLE);
                btnSaveVehList.setText("Ok");
            }
        }


        updateVehSearchableSpinner.setVisibility(View.VISIBLE);
        updateVehTitleTV.setVisibility(View.VISIBLE);


        TitleVehTV.setText(Title);
        updateVehTitleTV.setText("Select truck from list to change.");



        LinearLayout loginTruckLay = (LinearLayout)findViewById(R.id.loginTruckLay);
        loginTruckLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard();
                updateVehSearchableSpinner.hideEdit();

            }
        });


        btnSaveVehList.setOnClickListener(new VehicleFieldListener());
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


    private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
           // Log.d("onItemSelected", "onItemSelected: " + position);
            SelectedPosition = position;
           // saveBtnJob.setBackgroundResource(R.drawable.green_selector);
            Object object = updateVehSearchableSpinner.getSelectedItem();
            if(object != null) {
                for (int i = 0; i < truckList.size(); i++) {
                    if (object.toString().equals(truckList.get(i).getEquipmentNumber())) {
                        SelectedPosition = i;
                        break;
                    }
                }
            }else{
                SelectedPosition = -1;
            }
        }

        @Override
        public void onNothingSelected() {
           // Log.d("onNothingSelected", "onNothingSelected" );
           // saveBtnJob.setBackgroundResource(R.drawable.gray_selector);
        }
    };


    private class VehicleFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(SelectedPosition >= 0) {
                readyListener.ChangeVehicleReady(Title, SelectedPosition, isOldDialog);
            }else{
                Globally.EldScreenToast(btnSaveVehList, "Please select truck to change.", getContext().getResources().getColor(R.color.colorVoilation));
            }
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        updateVehSearchableSpinner.hideEdit();

    }
}
