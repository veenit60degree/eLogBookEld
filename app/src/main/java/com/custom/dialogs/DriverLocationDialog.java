package com.custom.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.constants.Constants;
import com.constants.SharedPref;
import com.google.android.material.textfield.TextInputLayout;
import com.messaging.logistic.R;
import com.messaging.logistic.fragment.EldFragment;

import java.util.List;

public class DriverLocationDialog extends Dialog {


    public interface LocationListener {

        public void CancelLocReady(boolean isMalfunction, int JobType);

        public void SaveLocReady(int position, int spinnerItemPos, int JobType, String city, EditText CityNameEditText, View view, boolean isMalfunction);

    }

    boolean isMalfunction;
    int Position = -1, spinnerItemPos = 0, JobType;
    private String City = "", State = "", location = "";
    List<String> locationList;
    private LocationListener locListener;
    TextInputLayout cityInputLayout;
    EditText CityNameEditText;
    Button btnLoadingJob, btnCancelLoadingJob;
    Spinner locationSpinner;
    TextView TitleTV, SpinnerTitleTV, dismissTxtView, malfunctionEventTV;
    public static TextView updateViewTV;
    View view;
    Animation malfunctionAnim;


    public DriverLocationDialog(Context context, String loc, String state, int position, int jobType, boolean isMalfunction,
                                View view, List<String> locList, LocationListener readyListener) {
        super(context);
        this.location        = loc;
        this.State           = state;
        this.Position        = position;
        this.JobType         = jobType;
        this.isMalfunction  = isMalfunction;
        this.view            = view;
        this.locationList    = locList;
        this.locListener     = readyListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.popup_trailor_fields);
        setCancelable(false);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        CityNameEditText    = (EditText) findViewById(R.id.TrailorNoEditText);
        cityInputLayout     = (TextInputLayout)findViewById(R.id.trailorNoInputType);

        btnLoadingJob       = (Button) findViewById(R.id.btnLoadingJob);
        btnCancelLoadingJob = (Button) findViewById(R.id.btnCancelLoadingJob);

        TitleTV             = (TextView) findViewById(R.id.TitleTV);
        SpinnerTitleTV      = (TextView) findViewById(R.id.SpinnerTitleTV);
        updateViewTV        = (TextView) findViewById(R.id.sText);
        dismissTxtView      = (TextView) findViewById(R.id.logoutTruckPopupTV);
        malfunctionEventTV  = (TextView) findViewById(R.id.malfunctionEventTV);

        locationSpinner     = (Spinner) findViewById(R.id.remarkSpinner);


        malfunctionAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        malfunctionAnim.setDuration(1500);

        if(JobType == Constants.EditRemarks){
            cityInputLayout.setHint("Enter Remarks");
            TitleTV.setText("Remarks");
            CityNameEditText.setHint("Remarks");
            CityNameEditText.setText(City);
            CityNameEditText.setSelection(City.length());


        }else {
            cityInputLayout.setHint("Enter City");
            CityNameEditText.setHint("City");
            SpinnerTitleTV.setText("Select State");
            SpinnerTitleTV.setVisibility(View.VISIBLE);

            if(State.equals(getContext().getResources().getString(R.string.update_loc))){
                TitleTV.setText(getContext().getResources().getString(R.string.update_loc));
                btnLoadingJob.setText(getContext().getResources().getString(R.string.update));
            }else{
                TitleTV.setText("Driver Location");
            }

            if (locationList.size() > 0) {
                locationSpinner.setVisibility(View.VISIBLE);

                String[] loc = location.split(",");
                int locLength = loc.length-1;
                if(locLength > 1) {
                    State = loc[locLength - 1].trim();
                }

                City = "";
                for(int i = 0 ; i < locLength-1 ; i++){
                    City = City + " " + loc[i].trim();
                }


                CityNameEditText.setText(City);
                CityNameEditText.setSelection(City.length());

                // Creating adapter for spinner
                ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, locationList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locationSpinner.setAdapter(dataAdapter);

                if(Position != -1) {
                    locationSpinner.setSelection(Position);
                }else{
                    City = location;
                    CityNameEditText.setText(City);
                    CityNameEditText.setSelection(City.length());
                }

                for(int i = 0 ; i<locationList.size() ; i++){
                    if(State.equalsIgnoreCase(locationList.get(i))){
                        spinnerItemPos = i;
                        locationSpinner.setSelection(i);
                        break;
                    }
                }

            }


            // Spinner click listener
            locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    //String item = parent.getItemAtPosition(pos).toString();
                    spinnerItemPos = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            if(isMalfunction){

                malfunctionAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        malfunctionEventTV.startAnimation(malfunctionAnim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });


             /*   String text1 = getContext().getResources().getString(R.string.enter_manual_loc) + " ("+
                        getContext().getResources().getString(R.string.loc_mal_occur) + ")";

              //  TitleTV.setTextAppearance(getContext(), R.style.fontForLocMalTitleMobile);
                SpannableString spanString =  new SpannableString(text1);
                final StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
               // spanString.setSpan(new RelativeSizeSpan(1.10f), 0,21, 0); // set size
                spanString.setSpan(new RelativeSizeSpan(0.80f), 23,51, 0); // set size
                spanString.setSpan(boldSpan, 0, 21, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // set text style
                spanString.setSpan(new ForegroundColorSpan(Color.RED), 23, 51, 0);// set color
                TitleTV.setText(spanString, TextView.BufferType.SPANNABLE);
*/

                TitleTV.setText(getContext().getResources().getString(R.string.enter_manual_loc) );
                btnCancelLoadingJob.setText(getContext().getResources().getString(R.string.ignore_location));
                btnLoadingJob.setText(getContext().getResources().getString(R.string.save_location));
                dismissTxtView.setText(Html.fromHtml("<u>" + getContext().getResources().getString(R.string.cancel) +"</u>"));
                dismissTxtView.setVisibility(View.VISIBLE);
                malfunctionEventTV.setVisibility(View.VISIBLE);

                malfunctionEventTV.startAnimation(malfunctionAnim);

            }

        }


        updateViewTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  City = location;
                CityNameEditText.setText(EldFragment.City);
                CityNameEditText.setSelection(EldFragment.City.length());

                for(int i = 0 ; i<locationList.size() ; i++){
                    if(EldFragment.AobrdState.equalsIgnoreCase(locationList.get(i))){
                        locationSpinner.setSelection(i);
                        break;
                    }
                }



            }
        });

        dismissTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard();
                dismiss();
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
            // Log.d("State", "State: " + locationList.get(Position));

            if(Position == -1){
                Position = 0;
            }
            if(JobType == Constants.EditRemarks && City.equalsIgnoreCase(updatedCityName)){
                dismiss();
            } else {
                locListener.SaveLocReady(
                        Position,
                        spinnerItemPos,
                        JobType,
                        updatedCityName,
                        CityNameEditText,
                        view,
                        isMalfunction);
            }
        }
    }


    private class CancelBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            HideKeyboard();
            locListener.CancelLocReady(isMalfunction, JobType);
        }
    }

}