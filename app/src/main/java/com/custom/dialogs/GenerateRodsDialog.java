package com.custom.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.searchable.spinner.SearchArrayListAdapter;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenerateRodsDialog extends Dialog implements View.OnClickListener {


    public interface RodsListner {
        public void ChangeRodsReady(String Title, int positionm, String checkMode, Date fromDate, Date toDate);
    }

    Constants constants;
    List<String> selectCountryList;
    private RodsListner readyListener;
    LinearLayout generateRodsLogBtn, btnCancelLoadingJob;
    String Title = "";
    TextView TitleVehTV, updateVehTitleTV,TitleCanadaTV,startDateTv,endDateTv;
    int SelectedPosition = -1,MaxDays;
    int UsaMaxDays          = 7;    // 7 days
    int CanMaxDays          = 14;   // 14 days
    private SearchArrayListAdapter mSimpleArrayListAdapter;
    CheckBox checkboxEld,checkBoxAbord;
    String checkedMode;
    LinearLayout selectCheckboxLay;
    Spinner countrySpinner;
    Globally globally;
    Date StartDate, EndDate;
    DateFormat format;
    String selectedDateView = "";
    DatePickerDialog dateDialog;
    int SelectedCountry = 0;
    int CountryCan      = 1;
    String CurrentCycleId   = "";
    ImageView countryFlagImgView;

    public GenerateRodsDialog(Context context, List<String> selectCountryList, RodsListner readyListener,String currentCycleId) {
        super(context);
        this.selectCountryList = selectCountryList;
        this.readyListener = readyListener;
        CurrentCycleId   = currentCycleId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.popup_generaterods);
        setCancelable(false);
        constants = new Constants();
        globally  = new Globally();
        checkboxEld                = (CheckBox) findViewById(R.id.checkboxEld);
        checkBoxAbord              = (CheckBox) findViewById(R.id.checkboxAbord);
        TitleVehTV                 = (TextView) findViewById(R.id.TitleVehTV);
        updateVehTitleTV           = (TextView) findViewById(R.id.updateVehTitleTV);
        startDateTv                = (TextView) findViewById(R.id.startDateTv);
        endDateTv                  = (TextView) findViewById(R.id.endDateTv);
        countrySpinner             = (Spinner) findViewById(R.id.countrySpinner);
        selectCheckboxLay          = (LinearLayout) findViewById(R.id.selectCheckboxLay);
        btnCancelLoadingJob        = (LinearLayout) findViewById(R.id.cancelDriverLogBtn);
        generateRodsLogBtn         = (LinearLayout) findViewById(R.id.shareDriverLogBtn);
        countryFlagImgView         = (ImageView)findViewById(R.id.countryFlagImgView);
        Title                      = Globally.GENERATE_RODS_TITLE;
        format                     = new SimpleDateFormat(Globally.DateFormatHalf);


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


        if (CurrentCycleId.equals(globally.USA_WORKING_6_DAYS) || CurrentCycleId.equals(globally.USA_WORKING_7_DAYS) ) {
            MaxDays = UsaMaxDays;
        }else{
            MaxDays = CanMaxDays;
        }

        ArrayList<String> selectCountry = new ArrayList<String>();
        for(int i = 0 ; i < selectCountryList.size() ; i++ ) {
            try {
                selectCountry.add(selectCountryList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        ArrayAdapter countryAdapter = new ArrayAdapter(getContext(), R.layout.item_editlog_spinner, R.id.editlogSpinTV,
                getContext().getResources().getStringArray(R.array.country_array));
                countrySpinner.setAdapter(countryAdapter);
                countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        SelectedPosition = position;
                            if(SelectedPosition == 0){
                                setMinMaxDateOnView(CanMaxDays, false);
                                countryFlagImgView.setImageResource(R.drawable.no_flag);
                                selectCheckboxLay.setVisibility(View.GONE);
                                checkboxEld.setVisibility(View.GONE);
                                checkBoxAbord.setVisibility(View.GONE);
                                checkBoxAbord.setChecked(false);
                                checkboxEld.setChecked(false);
                                checkedMode  = "";
                            }else if(SelectedPosition == 1){
                                countryFlagImgView.setImageResource(R.drawable.can_flag);
                                SelectedCountry = 1;
                                selectCheckboxLay.setVisibility(View.VISIBLE);
                                checkboxEld.setVisibility(View.VISIBLE);
                                checkBoxAbord.setVisibility(View.VISIBLE);
                                setMinMaxDateOnView(CanMaxDays, true);
                                checkboxEld.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                 if(checkboxEld.isChecked()){
                                     checkedMode = Globally.LOG_TYPE_ELD;
                                     checkBoxAbord.setChecked(false);
                                 }else{
                                     checkedMode = "";
                                     checkBoxAbord.setChecked(false);
                                     checkboxEld.setChecked(false);
                                 }
                                  }
                                });

                                checkBoxAbord.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                    if(checkBoxAbord.isChecked()){
                                        checkedMode = Globally.LOG_TYPE_AOBRD;
                                        checkboxEld.setChecked(false);
                                    }else{
                                        checkedMode = "";
                                        checkBoxAbord.setChecked(false);
                                        checkboxEld.setChecked(false);
                                    }
                                 }
                                });

                            }else if(SelectedPosition == 2){
                                countryFlagImgView.setImageResource(R.drawable.usa_flag);
                                SelectedCountry = 3;
                                setMinMaxDateOnView(UsaMaxDays, true);
                                selectCheckboxLay.setVisibility(View.GONE);
                                checkboxEld.setVisibility(View.GONE);
                                checkBoxAbord.setVisibility(View.GONE);
                                checkBoxAbord.setChecked(false);
                                checkboxEld.setChecked(false);
                                checkedMode  = "";
                            }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


        countrySpinner.setVisibility(View.VISIBLE);
        generateRodsLogBtn.setOnClickListener(new RodsFieldListner());
        btnCancelLoadingJob.setOnClickListener(new CancelBtnListener());
        startDateTv.setOnClickListener(this);
        endDateTv.setOnClickListener(this);
        btnCancelLoadingJob.setVisibility(View.VISIBLE);

        setMinMaxDateOnView(MaxDays, false);

        HideKeyboard();
    }

    void setMinMaxDateOnView(int MaxDays, boolean isOnCreate){
        String currentDate = globally.GetCurrentDeviceDate();
        String currentDateStr = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
        DateTime selectedDateTime = globally.getDateTimeObj(currentDateStr, false);
        selectedDateTime = selectedDateTime.minusDays(MaxDays);
        String fromDate = selectedDateTime.toString().substring(0, 10);
        String afterConvert =   globally.ConvertDateFormat(currentDate);
        DateTime startDate = globally.getDateTimeObj(afterConvert, false);
        startDate = startDate.minusDays(MaxDays);
        String startDateFinal =  globally.ConvertDateFormatMMddyyyy(startDate.toString());

        try {
            startDateTv.setText(startDateFinal);
            StartDate = format.parse(fromDate);

            if(isOnCreate) {
                endDateTv.setText(currentDate);
                String endDate = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                EndDate = format.parse(endDate);
            }else{
                endDateTv.setText(currentDate);
                String endDate = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                EndDate = format.parse(endDate);
                startDateTv.setText(currentDate);
                String endDateStarting = Globally.ConvertDateFormatyyyy_MM_dd(currentDate);
                StartDate = format.parse(endDateStarting);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    void HideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    private class RodsFieldListner implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if(SelectedPosition > 0) {
                if(selectCheckboxLay.getVisibility() == View.VISIBLE) {
                    if (checkBoxAbord.isChecked() || checkboxEld.isChecked()) {
                        DriverViewValidations();
                    } else {
                        globally.EldScreenToast(generateRodsLogBtn, "Please select log type", getContext().getResources().getColor(R.color.colorVoilation));
                    }
                } else{
                    DriverViewValidations();
                }

            }else{
                Globally.EldScreenToast(generateRodsLogBtn, "Please select country.", getContext().getResources().getColor(R.color.colorVoilation));
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.startDateTv:
                HideKeyboard();
                selectedDateView = "start";
                ShowDateDialog();

                break;

            case R.id.endDateTv:
                HideKeyboard();
                selectedDateView = "end";
                ShowDateDialog();

                break;


        }
    }

    void ShowDateDialog() {
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            String cycleId = "";
            if(SelectedCountry == CountryCan){
                cycleId = "1";
            }else{
                cycleId = "3";
            }

            dateDialog = new DatePickerDialog(getContext(), cycleId + ",sendLog", globally.GetCurrentDeviceDate(), new DateListener(),true);
            dateDialog.show();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private class DateListener implements DatePickerDialog.DatePickerListener {
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            try {   //01/27/2021
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }


            try {
                if(selectedDateView.equals("start")) {
                    startDateTv.setText(SelectedDate);
                    String startDate = Globally.ConvertDateFormatyyyy_MM_dd(SelectedDate);
                    StartDate = format.parse(startDate);
                }else{
                    endDateTv.setText(SelectedDate);
                    String endDate = Globally.ConvertDateFormatyyyy_MM_dd(SelectedDate);
                    EndDate = format.parse(endDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    void DriverViewValidations(){
        if (startDateTv.getText().toString().length() > 0) {
            if (endDateTv.getText().toString().length() > 0) {
                HideKeyboard();
                if (EndDate.after(StartDate) || EndDate.equals(StartDate)) {
                    readyListener.ChangeRodsReady(Title, SelectedPosition,checkedMode,StartDate,EndDate);

                } else {
                    globally.EldScreenToast(generateRodsLogBtn, "(To Date) should be greater then (From Date).", getContext().getResources().getColor(R.color.colorVoilation));
                }
            } else {
                globally.EldScreenToast(generateRodsLogBtn, "Select (To Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
            }
        } else {
            globally.EldScreenToast(generateRodsLogBtn, "Select (From Date) first.", getContext().getResources().getColor(R.color.colorVoilation));
        }
    }
}


