package com.messaging.logistic.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.ViewInspectionGridAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.custom.dialogs.DriverLocationDialog;
import com.driver.details.DriverConst;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.InspectionMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.UILApplication;
import com.models.DriverLocationModel;
import com.models.SavedInspectionModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shared.pref.StatePrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InspectionDetailView  extends Fragment {

    View rootView;
    TextView inspectionTypeTV, noDefectLabel, inspectionDateTv, powerInspectionTV, trailerInspectionTV;
    TextView invisibleTV, dateActionBarTV, EldTitleTV, currentOdometerTV;
    RadioGroup prePostRadioGroup, correctRadioGroup;
    Button changeLocBtn, saveInspectionBtn;
    RelativeLayout rightMenuBtn, truckTrailerTVLay, inspectTrailerTitleLay, truckTrailerLayout, superviserSignLay, eldMenuLay;
    LinearLayout supervisorNameLay;
    GridView truckGridView, trailerGridView ;
    RadioButton preTripButton, postTripButton, DefectsCorrectedBtn, DefectsNotCorrectedBtn;
    EditText remarksEditText, SupervisorNameTV;
    AutoCompleteTextView locInspectionTV;

    ImageView signDriverIV, signSuprvsrIV, imgTruck, imgTrailer, eldMenuBtn;
    ProgressBar inspectionProgressBar;
    private DisplayImageOptions options;
    ViewInspectionGridAdapter truckAdapter, trailerAdapter;
    String EldThemeColor = "#1A3561";
    String BlackColor    = "#7C7C7B";
    String WhiteColor    = "#ffffff";
    String OrangeColor   = "#ffff900d";
    VolleyRequest SaveLocationRequest;
    String DeviceId = "", DriverId = "", InspectionId = "0", InspectionDateTime = "", UpdatedLocation = "", oldLocation = "";
    String date = "", inspectionType = "";
    String City = "", State = "", Country = "";
    JSONArray savedInspectionArray;
    DBHelper dbHelper;
    InspectionMethod inspectionMethod;
    DriverLocationDialog driverLocationDialog;
    StatePrefManager statePrefManager;
    List<String> StateArrayList;
    List<DriverLocationModel> StateList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.inspection_fragment, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initView(rootView);

        return rootView;

    }


    void initView(View view) {

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.transparent)
                .showImageForEmptyUri(R.drawable.transparent)
                .showImageOnFail(R.drawable.transparent)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        SaveLocationRequest      = new VolleyRequest(getActivity());
        statePrefManager         = new StatePrefManager();

        dbHelper                 = new DBHelper(getActivity());
        inspectionMethod         = new InspectionMethod();

        inspectionTypeTV         = (TextView)view.findViewById(R.id.inspectionTypeTV);
        noDefectLabel            = (TextView)view.findViewById(R.id.noDefectLabel);
        inspectionDateTv         = (TextView)view.findViewById(R.id.inspectionDateTv);
        invisibleTV              = (TextView)view.findViewById(R.id.invisibleTV);
        powerInspectionTV        = (TextView)view.findViewById(R.id.powerInspectionTV);
        trailerInspectionTV      = (TextView)view.findViewById(R.id.trailerTextVw);
        dateActionBarTV          = (TextView)view.findViewById(R.id.dateActionBarTV);
        EldTitleTV               = (TextView)view.findViewById(R.id.EldTitleTV);
        currentOdometerTV        = (TextView)view.findViewById(R.id.currentOdometerTV);

        prePostRadioGroup        = (RadioGroup)view.findViewById(R.id.prePostRadioGroup);
        correctRadioGroup        = (RadioGroup)view.findViewById(R.id.correctRadioGroup);
        preTripButton            = (RadioButton) view.findViewById(R.id.preTripButton);
        postTripButton           = (RadioButton)view.findViewById(R.id.postTripButton);
        DefectsCorrectedBtn      = (RadioButton)view.findViewById(R.id.DefectsCorrectedBtn);
        DefectsNotCorrectedBtn   = (RadioButton)view.findViewById(R.id.DefectsNotCorrectedBtn);


        changeLocBtn             = (Button)view.findViewById(R.id.changeLocBtn);
        saveInspectionBtn        = (Button)view.findViewById(R.id.saveInspectionBtn);

        remarksEditText          = (EditText)view.findViewById(R.id.remarksEditText);
        SupervisorNameTV         = (EditText)view.findViewById(R.id.SupervisorNameTV);
        locInspectionTV          = (AutoCompleteTextView)view.findViewById(R.id.locInspectionTV);


        signDriverIV             = (ImageView) view.findViewById(R.id.signDriverIV);
        signSuprvsrIV            = (ImageView) view.findViewById(R.id.signSuprvsrIV);
        imgTruck                 = (ImageView) view.findViewById(R.id.imgTruck);
        imgTrailer               = (ImageView) view.findViewById(R.id.imgTrailer);
        eldMenuBtn               = (ImageView) view.findViewById(R.id.eldMenuBtn);

        supervisorNameLay        = (LinearLayout)view.findViewById(R.id.supervisorNameLay);
        rightMenuBtn             = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        truckTrailerTVLay        = (RelativeLayout)view.findViewById(R.id.truckTrailerTVLay);
        inspectTrailerTitleLay   = (RelativeLayout)view.findViewById(R.id.inspectTrailerTitleLay);
        truckTrailerLayout       = (RelativeLayout)view.findViewById(R.id.truckTrailerLayout);
        superviserSignLay        = (RelativeLayout)view.findViewById(R.id.superviserSignLay);
        eldMenuLay               = (RelativeLayout)view.findViewById(R.id.eldMenuLay);

        truckGridView            = (GridView)view.findViewById(R.id.truckGridView);
        trailerGridView          = (GridView)view.findViewById(R.id.trailerGridView);
        inspectionProgressBar    = (ProgressBar)view.findViewById(R.id.inspectionProgressBar);

        EditText odometerEditTxt = (EditText)view.findViewById(R.id.odometerEditTxt);
        Spinner selectDistanceSpinner = (Spinner)view.findViewById(R.id.selectDistanceSpinner);
        selectDistanceSpinner.setVisibility(View.GONE);
        odometerEditTxt.setVisibility(View.GONE);
        currentOdometerTV.setVisibility(View.VISIBLE);

        Bundle getBundle  = this.getArguments();
        int position  = getBundle.getInt("position");
        inspectionType = getBundle.getString("inspectionType");
        date = getBundle.getString("InspectionDateTime");

        // getBundle.clear();

        DriverId            = SharedPref.getDriverId( getActivity());
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        //InspectionId, InspectionDateTime

        if(inspectionType.contains("dot")){
            changeLocBtn.setVisibility(View.GONE);
        }

        try{
            JSONObject responseObj = new JSONObject(getBundle.getString("selectedObj") );
            JSONObject inspectionObj = new JSONObject(responseObj.getString(ConstantsKeys.Inspection));
            InspectionDateTime = inspectionObj.getString(ConstantsKeys.InspectionDateTime);
            oldLocation        = inspectionObj.getString(ConstantsKeys.Location).trim();

            if(inspectionObj.has(ConstantsKeys.InspectionId)){
                InspectionId = inspectionObj.getString(ConstantsKeys.InspectionId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        SavedInspectionModel savedInspectionModel = InspectionsHistoryFragment.savedInspectionList.get(position);
        if(savedInspectionModel.getInspectionTypeId() == Constants.Trailer){
            truckTrailerTVLay.setVisibility(View.GONE);
            truckTrailerLayout.setVisibility(View.GONE);
        }

        savedInspectionArray = inspectionMethod.getSavedInspectionArray(Integer.valueOf(DriverId), dbHelper);;


        invisibleTV.setVisibility(View.VISIBLE);
        dateActionBarTV.setVisibility(View.GONE);
        rightMenuBtn.setVisibility(View.GONE);
        prePostRadioGroup.setVisibility(View.GONE);
        inspectionTypeTV.setVisibility(View.GONE);
        saveInspectionBtn.setVisibility(View.GONE);
        imgTruck.setVisibility(View.GONE);
        imgTrailer.setVisibility(View.GONE);
        // locInspectionTV.setEnabled(false);
        remarksEditText.setEnabled(false);
        preTripButton.setEnabled(false);
        postTripButton.setEnabled(false);
        DefectsCorrectedBtn.setChecked(false);
        DefectsNotCorrectedBtn.setEnabled(false);
        remarksEditText.setEnabled(false);
        SupervisorNameTV.setEnabled(false);
        //  locInspectionTV.setEnabled(false);
        changeLocBtn.setText(getResources().getString(R.string.update));

        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(savedInspectionModel.getHeaderTitle());
        powerInspectionTV.setText(savedInspectionModel.getVehicleEquNumber());
        trailerInspectionTV.setText(savedInspectionModel.getTrailorEquNumber());
        locInspectionTV.setText(savedInspectionModel.getLocation());
        inspectionDateTv.setText(convertDateFormat(savedInspectionModel.getCreatedDate(), savedInspectionModel.getInspectionDateTime() ));

        String odometer = savedInspectionModel.getOdometer();

        String meterToKm = Constants.meterToKmWithObd(odometer);
        String meterToMiles = Constants.meterToMilesWith2DecPlaces(odometer);
        currentOdometerTV.setText(Constants.getUpTo2DecimalString(meterToKm) + " km (" + Constants.getUpTo2DecimalString(meterToMiles) + " miles)" );

        if(!savedInspectionModel.getRemarks().trim().equalsIgnoreCase("null")) {
            remarksEditText.setText(savedInspectionModel.getRemarks());
        }
        String DriverImage = "", DriverImageBytes = "", SupervisorImage = "",SupervisorImageBytes = "" ;

        String[] DriverImageArray       = savedInspectionModel.getDriverSignature().split("@@@");
        String[] SupervisorImageArray   = savedInspectionModel.getSupervisorMechanicsSignature().split("@@@");

        if(DriverImageArray.length > 0){
            DriverImage = DriverImageArray[0];
            if(DriverImageArray.length > 1){
                DriverImageBytes = DriverImageArray[1];
            }else{
                DriverImageBytes = DriverImageArray[0];
            }

        }

        if(SupervisorImageArray.length > 0){
            SupervisorImage = SupervisorImageArray[0];
            if(SupervisorImageArray.length > 1){
                SupervisorImageBytes = SupervisorImageArray[1];
            }else{
                SupervisorImageBytes = SupervisorImageArray[0];
            }

        }

        if(DriverImageBytes.length() == 0) {
            ImageLoader.getInstance().displayImage( DriverImage, signDriverIV , options);
        }else{
            if(DriverImageBytes.contains(".png")){
                ImageLoader.getInstance().displayImage( DriverImage, signDriverIV , options);
            }else{
                LoadByteImage(signDriverIV, DriverImageBytes);
            }
        }

        if(SupervisorImageBytes.length() == 0) {
            ImageLoader.getInstance().displayImage( SupervisorImage, signSuprvsrIV , options);
        }else{
            if(SupervisorImageBytes.contains(".png")){
                ImageLoader.getInstance().displayImage( SupervisorImage, signSuprvsrIV , options);
            }else{
                LoadByteImage(signSuprvsrIV, SupervisorImageBytes);
            }
        }

        if(SupervisorImage.length() > 0 && !SupervisorImage.equals("null") ) {
            SupervisorNameTV.setText(savedInspectionModel.getSupervisorMechanicsName());
            supervisorNameLay.setVisibility(View.VISIBLE);
            superviserSignLay.setVisibility(View.VISIBLE);
        }else{
            superviserSignLay.setVerticalGravity(View.GONE);
        }

        try{
            if(savedInspectionModel.IsPreTripInspectionSatisfactory()){
                setPrePostBackgroundGreen(1, preTripButton, postTripButton);
            }else if(savedInspectionModel.IsPostTripInspectionSatisfactory()){
                setPrePostBackgroundGreen(2, preTripButton, postTripButton);
            }


            if(savedInspectionModel.IsAboveDefectsCorrected()){
                setBackgroundGreen( 1, noDefectLabel, DefectsCorrectedBtn,  DefectsNotCorrectedBtn,
                        correctRadioGroup, superviserSignLay) ;

            }else if(savedInspectionModel.IsAboveDefectsNotCorrected()){
                setBackgroundGreen(2, noDefectLabel, DefectsCorrectedBtn,  DefectsNotCorrectedBtn,
                        correctRadioGroup, superviserSignLay);
            }else {
                noDefectLabel.setText("No Defects");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayList<String> TruckList = savedInspectionModel.getTruckList();
        ArrayList<String> TrailerList = savedInspectionModel.getTrailerList();

        Constants.inspectionViewHeight = 0;
        try{
            truckAdapter = new ViewInspectionGridAdapter(getActivity(),  TruckList, false);
            truckGridView.setAdapter(truckAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            trailerAdapter = new ViewInspectionGridAdapter(getActivity(), TrailerList, false);
            trailerGridView.setAdapter(trailerAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(TruckList.size() > 0){
            truckTrailerTVLay.setVisibility(View.VISIBLE);
        }else {
            truckTrailerTVLay.setVisibility(View.GONE);
        }

        if(TrailerList.size() > 0){
            inspectTrailerTitleLay.setVisibility(View.VISIBLE);
        }else {
            inspectTrailerTitleLay.setVisibility(View.GONE);
        }



        final int truckViewCount      = TruckList.size() / 2 + TruckList.size() % 2;
        final int trailerViewCount    = TrailerList.size() / 2 + TrailerList.size() % 2;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int truckViewHeight = Constants.inspectionViewHeight * truckViewCount;
                int trailerViewHeight = Constants.inspectionViewHeight * trailerViewCount;

                try {
                    RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, truckViewHeight) ;  //SavedInspectionsFragment.inspectionLayHeight
                    truckGridView.setLayoutParams(mParam);
                }catch (Exception e){}

                try {
                    RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, trailerViewHeight ) ; //SavedInspectionsFragment.inspectionLayHeight
                    trailerGridView.setLayoutParams(mParam);
                }catch (Exception e){}
            }
        }, 500);


        AddStatesInList();

        eldMenuLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Constants.IsInspectionDetailViewBack = true;
                getParentFragmentManager().popBackStack();
            }
        });

        changeLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Globally.isConnected(getActivity())) {
                    OpenLocationDialog();
                } else {
                    Globally.EldScreenToast(changeLocBtn, Globally.CONNECTION_ERROR, getResources().getColor(R.color.colorVoilation));
                }
            }
        });


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    private void AddStatesInList() {
        int stateListSize = 0;
        StateArrayList = new ArrayList<String>();
        StateList = new ArrayList<DriverLocationModel>();

        try {
            StateList = statePrefManager.GetState(getActivity());
            StateList.add(0, new DriverLocationModel("", "Select", ""));
            stateListSize = StateList.size();
        } catch (Exception e) {
            stateListSize = 0;
        }


        for (int i = 0; i < stateListSize; i++) {
            StateArrayList.add(StateList.get(i).getState());
        }

    }



    void OpenLocationDialog() {

        //   new GetLocAddressAsync().execute(JobType);

        try {

            if (StateArrayList.size() > 0) {
                UpdatedLocation = locInspectionTV.getText().toString().trim();

                if (driverLocationDialog != null && driverLocationDialog.isShowing()) {
                    driverLocationDialog.dismiss();
                }
                driverLocationDialog = new DriverLocationDialog(getActivity(), UpdatedLocation, getResources().getString(R.string.update_loc),
                        0, Constants.EditLocation, false, changeLocBtn, StateArrayList, new DriverLocationListener());
                driverLocationDialog.show();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }




    private class DriverLocationListener implements DriverLocationDialog.LocationListener {

        @Override
        public void CancelLocReady(boolean isMalfunction, int JobType) {

            try {
                if (driverLocationDialog != null && driverLocationDialog.isShowing())
                    driverLocationDialog.dismiss();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void SaveLocReady(int position, int spinnerItemPos, int JobType, String city,
                                 EditText CityNameEditText, View view, boolean isMalfunction) {

            City = city;
            if (spinnerItemPos < StateList.size()) {
                State = StateList.get(spinnerItemPos).getStateCode();
                Country = StateList.get(spinnerItemPos).getCountry();
            }

            if (City.length() > 0) {
                try {
                    if (driverLocationDialog != null && driverLocationDialog.isShowing())
                        driverLocationDialog.dismiss();
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                Globally.hideKeyboard(getActivity(), truckTrailerTVLay);
                String Location = City + ", " + State ; //+ ", " + Country
                locInspectionTV.setText(Location.trim());
                UpdatedLocation = locInspectionTV.getText().toString();

                if(oldLocation.equals(UpdatedLocation)) {
                    // No action performed
                }else{
                    // Call API to Update inspections location on server..
                    UpdateInspectionLocation(DriverId, DeviceId, InspectionId, InspectionDateTime, Location);
                }

            } else {
                Globally.EldScreenToast(CityNameEditText, "Please enter city name", getResources().getColor(R.color.colorVoilation));
            }
        }
    }





    private void LoadByteImage(final ImageView signImageView, String SignImageInByte){
        if(!SignImageInByte.equals("null")) {
            final Bitmap bitmap = Globally.ConvertStringBytesToBitmap(SignImageInByte);
            signImageView.post(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        signImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, signImageView.getWidth(),
                                signImageView.getHeight(), false));
                    }
                }
            });
        }
    }


    void setBackgroundGreen(int SelectedBtn, TextView noDefectLabel, RadioButton DefectsCorrectedBtn, RadioButton DefectsNotCorrectedBtn,
                            RadioGroup correctRadioGroup, RelativeLayout superviserSignLay){


        noDefectLabel.setVisibility(View.GONE);
        noDefectLabel.setText("Defects");
        switch (SelectedBtn){

            case 1:
                setBackgroundSelected(DefectsCorrectedBtn);
                setBackgroundUnselected(DefectsNotCorrectedBtn);

                correctRadioGroup.setVisibility(View.VISIBLE);
                superviserSignLay.setVisibility(View.VISIBLE);
                noDefectLabel.setVisibility(View.VISIBLE);
                break;

            case 2:
                setBackgroundSelected(DefectsNotCorrectedBtn);
                setBackgroundUnselected(DefectsCorrectedBtn);

                correctRadioGroup.setVisibility(View.VISIBLE);
                superviserSignLay.setVisibility(View.VISIBLE);
                noDefectLabel.setVisibility(View.VISIBLE);

                break;

        }

    }


    void setPrePostBackgroundGreen( int SelectedBtn, RadioButton preTripButton, RadioButton postTripButton) {

        switch (SelectedBtn) {

            case 1:
                setBackgroundSelected(preTripButton);
                setBackgroundUnselected(postTripButton);

                break;

            case 2:
                setBackgroundSelected(postTripButton);
                setBackgroundUnselected(preTripButton);

                break;

        }

    }


    void setBackgroundUnselected(RadioButton view){
        view.setEnabled(false);
        view.setChecked(false);
//        view.setTextColor(Color.parseColor(BlackColor));
        if(UILApplication.getInstance().isNightModeEnabled()){
            view.setTextColor(Color.parseColor(WhiteColor));
        }else{
            view.setTextColor(Color.parseColor(BlackColor));
        }
    }


    void setBackgroundSelected(RadioButton view){
        view.setEnabled(true);
        view.setChecked(true);
        if(UILApplication.getInstance().isNightModeEnabled()){
            view.setTextColor(Color.parseColor(OrangeColor));
        }else{
            view.setTextColor(Color.parseColor(EldThemeColor));
        }
    }


    String convertDateFormat(String CreatedDate, String InspectionDateTime){
        try {
            if(!CreatedDate.equals("N/A") && CreatedDate.length() > 0) {
                CreatedDate = Globally.ConvertInspectionsDateFormat(CreatedDate);
                if(CreatedDate.equals("")){
                    CreatedDate = InspectionDateTime;
                }
            }else {
                CreatedDate = InspectionDateTime;
            }
        }catch (Exception e){
            CreatedDate = InspectionDateTime;
        }

        return  CreatedDate;
    }



    /*================== Get Driver Trip Details ===================*/
    void UpdateInspectionLocation(final String DriverId, final String DeviceId, final String InspectionId, final String InspectionDateTime,
                                  final String Location  ){  /*, final String SearchDate*/

        inspectionProgressBar.setVisibility(View.VISIBLE);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.InspectionId, InspectionId);
        params.put(ConstantsKeys.InspectionDateTime, InspectionDateTime);
        params.put(ConstantsKeys.Location, Location);

        SaveLocationRequest.executeRequest(Request.Method.POST, APIs.UPDATE_DRIVER_INSPECTION_LOC , params, 1,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Log.d("response", "response: " + response);
            inspectionProgressBar.setVisibility(View.GONE);


            JSONObject obj;

            try {
                obj = new JSONObject(response);
                String Message = obj.getString("Message");

                if (obj.getString("Status").equals("true")) {
                    if(Message.equals("Sucess")) {
                        Globally.EldScreenToast(changeLocBtn, "Location updated successfully", getResources().getColor(R.color.colorPrimary));

                        // Update inspections array locally
                        String updatedLoc = locInspectionTV.getText().toString();
                        JSONArray updatedArray = inspectionMethod.updateInspectionArray(savedInspectionArray, updatedLoc, InspectionDateTime);

                        // Update Inspection table in DB...
                        inspectionMethod.DriverInspectionHelper( Integer.valueOf(DriverId), dbHelper, updatedArray);


                    }else{
                        Globally.EldScreenToast(changeLocBtn, Message, getResources().getColor(R.color.colorPrimary));
                    }

                }else{
                    Globally.EldScreenToast(changeLocBtn, Message, getResources().getColor(R.color.colorVoilation));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };


    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){
        @Override
        public void getError(VolleyError error, int flag) {
            Log.d("error", "error: " + error);
            inspectionProgressBar.setVisibility(View.GONE);

            Globally.EldScreenToast(changeLocBtn, Globally.DisplayErrorMessage(error.toString()), getResources().getColor(R.color.colorVoilation));
        }
    };


    @Override
    public void onStop() {
        Constants.IsInspectionDetailViewBack = true;
        super.onStop();
    }
}
