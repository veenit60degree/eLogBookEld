package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.google.android.material.textfield.TextInputLayout;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class TrailorDialog extends Dialog {


    public interface TrailorListener {

        public void CancelReady();
        public void JobBtnReady(String TrailorNo, String Reason, String type, boolean isUpdatedTrailer, int ItemPosition,
                                EditText TrailorNoEditText, EditText ReasonEditText);

    }

    private String type , Trailor ;
    List<String> remarkList;
    private TrailorListener readyListener;
    TextInputLayout trailorNoInputType;
    EditText TrailorNoEditText , ReasonEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    LinearLayout noTrailerView;
    Spinner remarkSpinner;
    RadioButton radioNoTrailer, radioEnterTrailer;
    TextView TitleTV, SpinnerTitleTV, recordTitleTV;
    boolean isUpdatedTrailer = false, isEditRemarks , isStart = true, isYardMove;
    int ItemPosition, jobStatus;
    String spinnerSelection = "", DriverId ;
    ShipmentHelperMethod shipmentMethod;
    DBHelper dbHelper;
    HelperMethods hMethods;
    Constants constants;
    Globally Global;

    public TrailorDialog(Context context, String type, boolean isYardmove, String trailor, int position, boolean IsEdit,  List<String> remarkList,
                         int jobStatuss, DBHelper dbHelper, TrailorListener readyListener) {
        super(context);
        this.type = type;
        this.isYardMove = isYardmove;
        Trailor = trailor;
        this.ItemPosition = position;
        this.isEditRemarks = IsEdit;
        this.remarkList = remarkList;
        this.jobStatus = jobStatuss;
        this.readyListener = readyListener;
        this.dbHelper = dbHelper;
        shipmentMethod = new ShipmentHelperMethod();
        constants = new Constants();
        Global  = new Globally();
        hMethods = new HelperMethods();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.popup_trailor_fields);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        trailorNoInputType  = (TextInputLayout)findViewById(R.id.trailorNoInputType);
        TrailorNoEditText   = (EditText)findViewById(R.id.TrailorNoEditText);
        ReasonEditText      = (EditText)findViewById(R.id.ReasonEditText);

        btnLoadingJob       = (Button)findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button)findViewById(R.id.btnCancelLoadingJob);

        TitleTV             = (TextView)findViewById(R.id.TitleTV);
        SpinnerTitleTV      = (TextView)findViewById(R.id.SpinnerTitleTV);
        recordTitleTV       = (TextView)findViewById(R.id.recordTitleTV);

        radioNoTrailer      = (RadioButton) findViewById(R.id.radioNoTrailer);
        radioEnterTrailer   = (RadioButton) findViewById(R.id.radioEnterTrailer);

        noTrailerView       = (LinearLayout)findViewById(R.id.noTrailerView);

        remarkSpinner       = (Spinner)findViewById(R.id.remarkSpinner);

        DriverId = SharedPref.getDriverId(getContext());

        ReasonEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 60) {
                    ReasonEditText.setError("Allows 60 characters only");
                    ReasonEditText.setText(ReasonEditText.getText().toString().substring(0, 59));
                }else{
                    ReasonEditText.setError(null);
                }
            }
        });


        if(type.equals("trailor") || type.equals("trailor_driving")){
            if(type.equals("trailor_driving")){
                TitleTV.setText("Enter Trailer number before Driving");
            }else{
                TitleTV.setText("Enter Trailer number");
            }

            btnLoadingJob.setText("Update");

            noTrailerView.setVisibility(View.VISIBLE);
            ReasonEditText.setVisibility(View.GONE);

            if(Trailor.equals(Constants.NoTrailer)){
                Trailor = "";
                radioNoTrailer.setChecked(true);
                trailorNoInputType.setVisibility(View.GONE);
            }else{
                radioEnterTrailer.setChecked(true);
            }

        }else if(type.equals(Constants.Personal)){
            TitleTV.setText(getContext().getResources().getString(R.string.reason_for_personal_use));
            setCancelable(false);
            isStart = false;

            trailorNoInputType.setVisibility(View.GONE);
            noTrailerView.setVisibility(View.GONE);
            remarkSpinner.setVisibility(View.GONE);

            ReasonEditText.setVisibility(View.VISIBLE);
            ReasonEditText.setText("");
            ShowHideTrailerField(Trailor, isStart);



        }else{

             setCancelable(false);

             if(isYardMove){
                 TitleTV.setText("Enter reason for Yard Move");
                 spinnerSelection = getContext().getResources().getString(R.string.yard_move);
                 ReasonEditText.setVisibility(View.VISIBLE);
                 ReasonEditText.setText("");
                 ReasonEditText.setHint(getContext().getResources().getString(R.string.reason));
                 ShowHideTrailerField(Trailor, isStart);

                 trailorNoInputType.setVisibility(View.GONE);
                 noTrailerView.setVisibility(View.GONE);
                 remarkSpinner.setVisibility(View.GONE);




             }else{
                 TitleTV.setText("Enter Reason");
                 trailorNoInputType.setVisibility(View.GONE);
                 noTrailerView.setVisibility(View.GONE);

                 SpinnerTitleTV.setVisibility(View.VISIBLE);

                 if(!SharedPref.IsYardMoveAllowed(getContext())){
                     for(int i = remarkList.size()-1 ; i >=0 ; i-- ){
                         if(remarkList.get(i).equals(getContext().getResources().getString(R.string.YardMove))){
                            remarkList.remove(i);
                            break;
                         }
                     }
                 }

                 if(remarkList.size() > 0) {
                     remarkSpinner.setVisibility(View.VISIBLE);
                     // Creating adapter for spinner
                     ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, remarkList);
                     dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                     remarkSpinner.setAdapter(dataAdapter);
                 }

             }

        }

        TrailorNoEditText.setText(Trailor);
        TrailorNoEditText.setSelection(Trailor.length());

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(30);
        TrailorNoEditText.setFilters(filterArray);


        // Spinner click listener
        remarkSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(!isYardMove) {
                    String tempTrailerNo = TrailorNoEditText.getText().toString().trim();
                    spinnerSelection = parent.getItemAtPosition(position).toString();
                    ReasonEditText.setHint(getContext().getResources().getString(R.string.EnterReason));

                    if (position == remarkList.size() - 1) {
                        ReasonEditText.setVisibility(View.VISIBLE);
                        ReasonEditText.setText("");
                        ShowHideTrailerField(Trailor, isStart);

                    } else if (spinnerSelection.equals("Trailer Drop") && Trailor.length() == 0) {
                        ReasonEditText.setVisibility(View.GONE);
                        ReasonEditText.setText(spinnerSelection);
                        Global.EldScreenToast(btnLoadingJob, ConstantsEnum.No_TRAILER_ALERT, getContext().getResources().getColor(R.color.red_eld));
                        //  remarkSpinner.setSelection(0);

                    } else if (spinnerSelection.equals("Trailer Pickup")) {
                        ShowHideTrailerField("", isStart);
                        ReasonEditText.setVisibility(View.GONE);
                        ReasonEditText.setText(spinnerSelection);

                    } else if (spinnerSelection.equals("Trailer Switch")) {
                        ShowHideTrailerField("", isStart);
                        ReasonEditText.setVisibility(View.GONE);
                        ReasonEditText.setText(spinnerSelection);
                    } else if (spinnerSelection.equals(getContext().getResources().getString(R.string.YardMove))) {

                        ReasonEditText.setVisibility(View.VISIBLE);
                        ReasonEditText.setText("");
                        ReasonEditText.setHint(getContext().getResources().getString(R.string.yard_move_desc));
                        ShowHideTrailerField(Trailor, isStart);

                    } else {
                        ShowHideTrailerField(tempTrailerNo, isStart);
                        ReasonEditText.setVisibility(View.GONE);
                        ReasonEditText.setText(spinnerSelection);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if(isEditRemarks){
            trailorNoInputType.setVisibility(View.GONE);
            recordTitleTV.setVisibility(View.GONE);

            if(jobStatus == Constants.ON_DUTY){ // && isYardMove == false

                for(int i = 0 ; i<remarkList.size() ; i++){
                    if(Trailor.equalsIgnoreCase(remarkList.get(i))){
                        remarkSpinner.setSelection(i);
                        break;
                    }
                }



            }else{
                TitleTV.setText("Edit Remarks");
                ReasonEditText.setVisibility(View.VISIBLE);
                SpinnerTitleTV.setVisibility(View.GONE);
                ReasonEditText.setText("");
                ReasonEditText.setHint(getContext().getResources().getString(R.string.Remarks_simple));
                ShowHideTrailerField(Trailor, isStart);

                trailorNoInputType.setVisibility(View.GONE);
                noTrailerView.setVisibility(View.GONE);
                remarkSpinner.setVisibility(View.GONE);


            }

        }



        btnLoadingJob.setOnClickListener(new TrailorFieldListener());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());
        radioNoTrailer.setOnClickListener(new NoTrailerListener());
        radioEnterTrailer.setOnClickListener(new EnterTrailerListener());

        HideKeyboard();
    }

    void ShowHideTrailerField(String Trailor, boolean isStartUp){

      if(!isEditRemarks) {
          if (Trailor.equals("")) {
              noTrailerView.setVisibility(View.VISIBLE);
              if (isStartUp) {
                  trailorNoInputType.setVisibility(View.VISIBLE);
                  radioEnterTrailer.setChecked(true);
              }
          } else if (Trailor.equals(Constants.NoTrailer)) {
              Trailor = "";

              if (isStartUp) {
                  radioNoTrailer.setChecked(true);
                  trailorNoInputType.setVisibility(View.GONE);
                  noTrailerView.setVisibility(View.VISIBLE);
              }

          } else if (!Trailor.equals("")) {
              if(isStartUp) {
                  trailorNoInputType.setVisibility(View.GONE);
              }
          } else {
              isUpdatedTrailer = true;
          }
      }
        isStart = false;
        TrailorNoEditText.setText(Trailor);
    }

    void HideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {   }
    }



    private class TrailorFieldListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            boolean isAllowed = true;
            String alertMsg  = "";
            String Trailer = TrailorNoEditText.getText().toString().trim();

              if(noTrailerView.getVisibility() == View.VISIBLE){
                  if(Trailer.length() == 0 && !radioNoTrailer.isChecked() && !radioEnterTrailer.isChecked()){
                      alertMsg = ConstantsEnum.SELECT_TRAILER_TYPE;
                      isAllowed = false;
                  }else{
                      if(Trailer.length() == 0 && radioEnterTrailer.isChecked()){
                          alertMsg = ConstantsEnum.ENTER_TRAILER_NO;
                          isAllowed = false;
                      }else if(radioNoTrailer.isChecked()){
                          Trailer = getContext().getString(R.string.no_trailer);
                      }
                  }
              }

            if(isAllowed) {
                if (spinnerSelection.equals(getContext().getResources().getString(R.string.YardMove))) {
                 //   String CurrentCycleId = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, getContext());
                 //   if( CurrentCycleId.equals(Globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(Globally.USA_WORKING_7_DAYS)) {
                        if (jobStatus == EldFragment.ON_DUTY) {
                            yardMoveEvent(Trailer);
                        } else {
                            Globally.EldToastWithDuration4Sec(btnLoadingJob, getContext().getResources().getString(R.string.ym_alert_with_us),
                                    getContext().getResources().getColor(R.color.colorVoilation));
                        }
                   /* }else {
                        yardMoveEvent(Trailer);
                    }*/

                } else {
                    String updatedReason = ReasonEditText.getText().toString().trim();

                    if(updatedReason.equals(getContext().getResources().getString(R.string.yard_move))){
                        updatedReason = updatedReason + "  ";
                    }

                    if (type.equals(Constants.Personal) || (isEditRemarks && jobStatus != Constants.ON_DUTY)) {

                        if (updatedReason.length() >= 4 && updatedReason.length() <= 60) {

                            readyListener.JobBtnReady(
                                    Trailer,
                                    updatedReason,
                                    type,
                                    isUpdatedTrailer,
                                    ItemPosition,
                                    TrailorNoEditText,
                                    ReasonEditText);

                        } else {
                            if (updatedReason.length() < 60) {
                                if (type.equals(Constants.Personal)) {
                                    Global.EldScreenToast(btnLoadingJob, ConstantsEnum.PROPER_REASON_ALERT, getContext().getResources().getColor(R.color.red_eld));
                                } else {
                                    Global.EldScreenToast(btnLoadingJob, ConstantsEnum.EDIT_REMARKS_DESC, getContext().getResources().getColor(R.color.red_eld));
                                }
                            } else {
                                Global.EldScreenToast(btnLoadingJob, ConstantsEnum.MAX_CHAR_LIMIT, getContext().getResources().getColor(R.color.red_eld));
                            }

                        }

                    } else {
                        if (isEditRemarks && Trailor.equalsIgnoreCase(updatedReason)) {
                            dismiss();
                        } else {

                            if(updatedReason.equals("Select")){
                                Global.EldScreenToast(btnLoadingJob, ConstantsEnum.SELECT_ONDUTY_REASON, getContext().getResources().getColor(R.color.red_eld));
                            }else if (updatedReason.equals("Trailer Drop") && (Trailor.length() == 0 || Trailor.equals(getContext().getString(R.string.no_trailer)))) {
                                Global.EldScreenToast(btnLoadingJob, ConstantsEnum.NO_TRAILER_ALERT, getContext().getResources().getColor(R.color.red_eld));

                            } else if ((updatedReason.equals("Trailer Pickup") && (Trailer.length() == 0) )||
                                    (updatedReason.equals("Trailer Pickup") && Trailor.equals(Trailer))) {
                                String msg = "";
                                if (Trailer.length() == 0) {
                                    msg = "Please enter " + ConstantsEnum.PICK_TRAILER_ALERT;
                                } else {
                                    msg = "Please enter updated " + ConstantsEnum.PICK_TRAILER_ALERT;
                                }
                                Global.EldScreenToast(btnLoadingJob, msg, getContext().getResources().getColor(R.color.red_eld));

                                radioEnterTrailer.performClick();


                            } else if (updatedReason.equals(getContext().getResources().getString(R.string.loading)) && SharedPref.IsDrivingShippingAllowed(getContext())) {

                                if(Trailer.length() == 0 || Trailer.equals("No Trailer")){
                                    Global.EldScreenToast(btnLoadingJob, "Enter trailer number", getContext().getResources().getColor(R.color.colorVoilation));
                                }else{
                                    JSONArray shipment18DaysJsonArray = shipmentMethod.getShipment18DaysArray(Integer.valueOf(SharedPref.getDriverId(getContext())), dbHelper);
                                    String shippingDocumentNumber = "", toAddress = "";
                                    if (shipment18DaysJsonArray.length() > 0) {
                                        try {
                                            JSONObject obj = shipmentMethod.GetLastJsonObject(shipment18DaysJsonArray, 0);
                                            shippingDocumentNumber = obj.getString(ConstantsKeys.ShippingDocumentNumber);
                                            toAddress = obj.getString(ConstantsKeys.ShipperPostalCode);

                                            if (shippingDocumentNumber.equals(getContext().getResources().getString(R.string.Empty)) ||
                                                    shippingDocumentNumber.trim().length() == 0 ||
                                                    toAddress.trim().length() == 0) {
                                                Global.EldScreenToast(btnLoadingJob, "Update your shipping detail first", getContext().getResources().getColor(R.color.colorVoilation));
                                            } else {
                                                readyListener.JobBtnReady(
                                                        Trailer,
                                                        updatedReason,
                                                        type,
                                                        isUpdatedTrailer,
                                                        ItemPosition,
                                                        TrailorNoEditText,
                                                        ReasonEditText);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        readyListener.JobBtnReady(
                                                Trailer,
                                                updatedReason,
                                                type,
                                                isUpdatedTrailer,
                                                ItemPosition,
                                                TrailorNoEditText,
                                                ReasonEditText);
                                    }

                                }

                            } else {
                                if (radioNoTrailer.isChecked()) {
                                    Trailer = Constants.NoTrailer;
                                }

                                if ( (type.equals("trailor_driving") && Trailer.length() == 0) ||
                                        ( (updatedReason.equals("Loading") || updatedReason.equals("Unloading"))
                                                && (Trailer.equals("No Trailer") || Trailer.length() == 0 ) )  ) {
                                    Global.EldScreenToast(btnLoadingJob, "Enter trailer number", getContext().getResources().getColor(R.color.colorVoilation));
                                } else {
                                    readyListener.JobBtnReady(
                                            Trailer,
                                            updatedReason,
                                            type,
                                            isUpdatedTrailer,
                                            ItemPosition,
                                            TrailorNoEditText,
                                            ReasonEditText);
                                }
                            }
                        }
                    }
                }
            }else{
                noTrailerView.requestFocus();
                Global.EldScreenToast(btnLoadingJob, alertMsg,
                        getContext().getResources().getColor(R.color.colorVoilation));
            }

        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            readyListener.CancelReady();
        }
    }


    private class EnterTrailerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            trailorNoInputType.setVisibility(View.VISIBLE);
            //TrailorNoEditText.getText().toString().trim();
            Log.d("Trailer", "Trailor: " + Trailor);
        }
    }


    private class NoTrailerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            trailorNoInputType.setVisibility(View.GONE);
           // Trailor = getContext().getString(R.string.no_trailer);
            Log.d("Trailer", "Trailor: " + Trailor);
        }
    }

    private void yardMoveEvent(String Trailer){
        if (constants.isObdConnected(getContext())) {
            if (jobStatus != EldFragment.DRIVING) {
                if (hMethods.isCoDriverInDrYMPC(getContext(), Global, DriverId, dbHelper)) {
                    String coDriverStatus = hMethods.getCoDriverStatus(getContext(), DriverId, Global, dbHelper);
                    Global.EldScreenToast(btnLoadingJob, ConstantsEnum.CO_DRIVING_ALERT + coDriverStatus + ConstantsEnum.CO_DRIVING_ALERT1,
                            getContext().getResources().getColor(R.color.colorVoilation));
                } else {
                    if (ReasonEditText.getText().toString().trim().length() >= 4) {
                        readyListener.JobBtnReady(
                                Trailer,
                                spinnerSelection,
                                type,
                                isUpdatedTrailer,
                                ItemPosition,
                                TrailorNoEditText,
                                ReasonEditText);
                    } else {
                        Global.EldScreenToast(btnLoadingJob, ConstantsEnum.YARD_MOVE_DESC, getContext().getResources().getColor(R.color.red_eld));
                    }
                }
            } else {
                Global.EldToastWithDuration4Sec(btnLoadingJob, getContext().getResources().getString(R.string.pc_ym_alert_with_dr),
                        getContext().getResources().getColor(R.color.colorVoilation));
            }

        } else {
            Global.EldToastWithDuration4Sec(TrailorNoEditText, getContext().getResources().getString(R.string.connect_with_obd_first), getContext().getResources().getColor(R.color.colorVoilation));
        }
    }

}
