package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.als.logistic.R;
import com.models.VehicleModel;

import java.util.ArrayList;
import java.util.List;

public class VehicleDialogOld extends Dialog {


    public interface VehicleListener {
        public void ChangeVehicleReady( String Title, int position, boolean isOldDialog);
    }

    List<VehicleModel> truckList;
    private VehicleListener readyListener;
    EditText TrailorNoEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    Spinner remarkSpinner;
    String Truck, Title = "";
    TextView TitleTV, SpinnerTitleTV;
    int SelectedPosition = 0, SetSpinnerPosition = 0;
    boolean isOldDialog;


    public VehicleDialogOld(Context context, String truck, boolean isOldDialog, List<VehicleModel> remarkList, VehicleListener readyListener) {
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

        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        TrailorNoEditText = (EditText) findViewById(R.id.TrailorNoEditText);
        TrailorNoEditText.setVisibility(View.GONE);

        btnLoadingJob = (Button) findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button) findViewById(R.id.btnCancelLoadingJob);

        TitleTV = (TextView) findViewById(R.id.TitleTV);
        SpinnerTitleTV = (TextView) findViewById(R.id.SpinnerTitleTV);
        remarkSpinner = (Spinner) findViewById(R.id.remarkSpinner);

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
                ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, EquipmentList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                remarkSpinner.setAdapter(dataAdapter);
                remarkSpinner.setSelection(SetSpinnerPosition);
            }else {
                SpinnerTitleTV.setVisibility(View.INVISIBLE);
                remarkSpinner.setVisibility(View.INVISIBLE);
                if(Truck.trim().length() == 0) {
                    Title = "We can't see any truck attached with you. Please contact with your support team.";
                }

                btnCancelLoadingJob.setVisibility(View.INVISIBLE);
                btnLoadingJob.setText("Ok");
            }
        }


        remarkSpinner.setVisibility(View.VISIBLE);
        SpinnerTitleTV.setVisibility(View.VISIBLE);


        btnLoadingJob.setText("Save");
        TitleTV.setText(Title);
        SpinnerTitleTV.setText("Select truck from list to change.");



        // Spinner click listener
        remarkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // String item = parent.getItemAtPosition(position).toString();
                SelectedPosition = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnLoadingJob.setOnClickListener(new VehicleFieldListener());
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


    private class VehicleFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //if(SelectedPosition > 0) {
                readyListener.ChangeVehicleReady(Title, SelectedPosition, isOldDialog);
            /*}else{
                Globally.EldScreenToast(btnLoadingJob, "Please select truck to change.", getContext().getResources().getColor(R.color.colorVoilation));
            }*/
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            dismiss();
        }
    }

}
