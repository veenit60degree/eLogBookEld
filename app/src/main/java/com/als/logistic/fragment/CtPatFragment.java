package com.als.logistic.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adapter.logistic.CtPatAdapter;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.VolleyRequest;
import com.custom.dialogs.CtPatDialog;
import com.custom.dialogs.DatePickerDialog;
import com.driver.details.DriverConst;
import com.local.db.CTPatInspectionMethod;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.FailedApiTrackMethod;
import com.local.db.ShipmentHelperMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.models.PrePostModel;
import com.simplify.ink.InkView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtPatFragment extends Fragment implements View.OnClickListener {


    View rootView,agricultureReasonCtPatView;
    RelativeLayout rightMenuBtn, eldMenuLay;
    LinearLayout scrollChildMainLay,agricutureReasonLay;
    TextView EldTitleTV, actionBarRightBtn, truckCtPatTV, trailerCtPatTV, ctPatDateTimeTv, ctPatDateTimeTitle;
    public static TextView ctPatInspctTV;
    EditText arrivalContNoEditTxt, departureContNoEditTxt, conductedSecInspEditTxt, followSecLayEditTxt, affixedSealEditTxt,
            verifiedSealEditTxt,agricultureReasonEditText, containerIdenEditText;
    ImageView conductedSecIV, followSecLayIV, affixedSealIV, verifiedSealIV;
    GridView ctPatTruckGridVw, ctPatTrailerGridVw,ctPatAgricultureGridVw;
    Button ctPatInspectionBtn;
    ScrollView ctPatScrollView;
    CtPatDialog signDialog;
    CtPatAdapter truckAdapter, trailerAdapter,agricultureAdapter;
    List<PrePostModel> TruckInspList = new ArrayList<PrePostModel>();
    List<PrePostModel> TrailerInspList = new ArrayList<PrePostModel>();
    List<PrePostModel> AgricultureInspList = new ArrayList<PrePostModel>();
    ArrayList<String> TruckList = new ArrayList<String>();
    ArrayList<String> TrailerList = new ArrayList<String>();
    ArrayList<String> AgricultureList = new ArrayList<String>();
    ArrayList<Integer> TruckIdList = new ArrayList<Integer>();
    ArrayList<Integer> TrailerIdList = new ArrayList<Integer>();
    ArrayList<Integer> AgricultureIdList = new ArrayList<Integer>();
    FailedApiTrackMethod failedApiTrackMethod;

    ProgressDialog pDialog;
    VolleyRequest GetCtPatInspRequest, ctPatInsp18DaysRequest;
    Map<String, String> params;
    Constants constant;
    String  DRIVER_ID = "", VIN_NUMBER = "", DeviceId = "", CreatedDate = "", TruckIssueType = "", TraiorIssueType = "",
            CurrentCycleId = "", SelectedDatee = "";
    String AgricultureIssueType = "101,103,104,105,106";

    String ArrivalSealNumber = "", DepartureSealNumber = "";
    String SecurityInspectionPersonName = "", ByteInspectionConductorSign = "";
    String FollowUpInspectionPersonName = "", ByteFollowUpConductorSign = "";
    String AffixedSealPersonName        = "", ByteSealFixerSign = "";
    String VerificationPersonName       = "", ByteSealVerifierSign = "";
    String AgricultureReason            = "",  ContainerIdentification = "";

    String DriverId = "", CoDriverId = "", SelectedMain = "", DriverName = "", CompanyId = "";


    int SecurityConducted                   = 1;
    int FollowUpConducted                   = 2;
    int AffixedSeal                         = 3;
    int PhysicalVerify                      = 4;
    final int GetCtPatInspection            = 101;
    final int GetCtPat18DaysMainDriverLog   = 102;
    final int GetCtPat18DaysCoDriverLog     = 103;


    JSONArray truckArray = new JSONArray();
    JSONArray trailerArray = new JSONArray();
    List<PrePostModel> agricultureArray;
    JSONArray inspection18DaysArray = new JSONArray();

    Slidingmenufunctions slideMenu;
    DBHelper dbHelper;
    CTPatInspectionMethod ctPatInspectionMethod;
    ShipmentHelperMethod shipmentHelperMethod;
    SaveDriverLogPost saveInspectionPost;
    DatePickerDialog dateDialog;


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
            rootView = inflater.inflate(R.layout.fragment_ct_pat, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View view) {

        slideMenu               = new Slidingmenufunctions();
        dbHelper                = new DBHelper(getActivity());
        ctPatInspectionMethod   = new CTPatInspectionMethod();
        shipmentHelperMethod    = new ShipmentHelperMethod();
        saveInspectionPost      = new SaveDriverLogPost(getActivity(), saveInspectionResponse);

        GetCtPatInspRequest     = new VolleyRequest(getActivity());
        ctPatInsp18DaysRequest  = new VolleyRequest(getActivity());
        constant                = new Constants();
        failedApiTrackMethod    = new FailedApiTrackMethod();

        eldMenuLay              = (RelativeLayout) view.findViewById(R.id.eldMenuLay);

        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        scrollChildMainLay      = (LinearLayout) view.findViewById(R.id.scrollChildMainLay);
        agricutureReasonLay      = (LinearLayout) view.findViewById(R.id.agricutureReasonLay);

        EldTitleTV              = (TextView) view.findViewById(R.id.EldTitleTV);
        actionBarRightBtn       = (TextView) view.findViewById(R.id.dateActionBarTV);
        truckCtPatTV            = (TextView) view.findViewById(R.id.truckCtPatTV);
        trailerCtPatTV          = (TextView) view.findViewById(R.id.trailerCtPatTV);
        ctPatDateTimeTv         = (TextView) view.findViewById(R.id.ctPatDateTimeTv);
        ctPatDateTimeTitle     = (TextView) view.findViewById(R.id.ctPatDateTimeTtitle);
        ctPatInspctTV           = (TextView) view.findViewById(R.id.ctPatInspctTV);

        arrivalContNoEditTxt    = (EditText) view.findViewById(R.id.arrivalContNoEditTxt);
        departureContNoEditTxt  = (EditText) view.findViewById(R.id.departureContNoEditTxt);
        agricultureReasonEditText = (EditText) view.findViewById(R.id.agricultureReasonEditText);
        conductedSecInspEditTxt = (EditText) view.findViewById(R.id.conductedSecInspEditTxt);
        followSecLayEditTxt     = (EditText) view.findViewById(R.id.followSecLayEditTxt);
        affixedSealEditTxt      = (EditText) view.findViewById(R.id.affixedSealEditTxt);
        verifiedSealEditTxt     = (EditText) view.findViewById(R.id.verifiedSealEditTxt);
        containerIdenEditText   = (EditText) view.findViewById(R.id.containerIdenEditText);

        ctPatTruckGridVw        = (GridView) view.findViewById(R.id.ctPatTruckGridVw);
        ctPatTrailerGridVw      = (GridView) view.findViewById(R.id.ctPatTrailerGridVw);
        ctPatAgricultureGridVw  = (GridView) view.findViewById(R.id.ctPatAgricultureGridVw);

        conductedSecIV          = (ImageView) view.findViewById(R.id.conductedSecIV);
        followSecLayIV          = (ImageView) view.findViewById(R.id.followSecLayIV);
        affixedSealIV           = (ImageView) view.findViewById(R.id.affixedSealIV);
        verifiedSealIV          = (ImageView) view.findViewById(R.id.verifiedSealIV);

        ctPatInspectionBtn      = (Button) view.findViewById(R.id.ctPatInspectionBtn);

        ctPatScrollView         = (ScrollView)view.findViewById(R.id.ctPatScrollView);
        agricultureReasonCtPatView         = (View) view.findViewById(R.id.agricultureReasonCtPatView);


        pDialog                 = new ProgressDialog(getActivity());
        pDialog.setMessage("Saving ...");

        rightMenuBtn.setVisibility(View.GONE);
        actionBarRightBtn.setVisibility(View.VISIBLE);
        ctPatDateTimeTitle.setText(getResources().getString(R.string.date));
        SelectedDatee = Globally.GetCurrentDeviceDate(null, new Globally(), getActivity());


        actionBarRightBtn.setText(Html.fromHtml("<b><u>" + getResources().getString(R.string.view_ct_pat) + "</u></b>"));
        actionBarRightBtn.setBackgroundResource(R.drawable.transparent);
        actionBarRightBtn.setTextColor(getResources().getColor(R.color.whiteee));

        try{

            truckArray = new JSONArray(SharedPref.getCtPatInspectionIssues(ConstantsKeys.TruckCtPatIssues, getActivity()) );
            trailerArray = new JSONArray(SharedPref.getCtPatInspectionIssues(ConstantsKeys.TrailerCtPatIssues, getActivity()) );
//            agricultureArray =  constant.CtPatAgricultureList();   //new(JSONArray();
//            ParseInspectionIssues( truckArray, trailerArray ,agricultureArray);

        }catch (Exception e){
            e.printStackTrace();
        }

        if(TruckInspList.size() == 0) {
            TruckInspList = constant.CtPatTruckList();
            TrailerInspList = constant.CtPatTrailerList();
            AgricultureInspList = constant.CtPatAgricultureList();
        }

     /*   new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ctPatScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 500);*/


        agricultureAdapter = new CtPatAdapter(getActivity(), true, true, AgricultureInspList, AgricultureList, AgricultureIdList);
        ctPatAgricultureGridVw.setAdapter(agricultureAdapter);

        ctPatInspectionBtn.setOnClickListener(this);
        conductedSecIV.setOnClickListener(this);
        followSecLayIV.setOnClickListener(this);
        affixedSealIV.setOnClickListener(this);
        verifiedSealIV.setOnClickListener(this);
        scrollChildMainLay.setOnClickListener(this);
        actionBarRightBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);
        ctPatInspctTV.setOnClickListener(this);
        arrivalContNoEditTxt.setOnClickListener(this);


    }


    @Override
    public void onResume() {
        super.onResume();

        DRIVER_ID           = SharedPref.getDriverId( getActivity());
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        VIN_NUMBER          = SharedPref.getVINNumber(getActivity());
        DriverName          = slideMenu.usernameTV.getText().toString();
        CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getActivity());
        CurrentCycleId      = DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(getActivity()), getActivity());


        EldTitleTV.setText(getResources().getString(R.string.ctPat));
        CreatedDate = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetDriverCurrentDateTime(new Globally(), getActivity()));
        ctPatDateTimeTv.setText(CreatedDate.substring(0, 11));
        trailerCtPatTV.setText(SharedPref.getTrailorNumber(getActivity()));
        truckCtPatTV.setText(SharedPref.getTruckNumber(getActivity()));

        if(truckArray.length() == 0 || trailerArray.length() == 0)
            GetCtPatInspectionDetail(DRIVER_ID, DeviceId, Globally.PROJECT_ID, VIN_NUMBER);

        constant.IsCtPatUploading = false;
        ctPatInspectionBtn.setEnabled(true);
        inspection18DaysArray = ctPatInspectionMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);

        JSONArray ctPatInsp18DaysArray = ctPatInspectionMethod.getCtPat18DaysInspectionArray(Integer.valueOf(DRIVER_ID), dbHelper);
        if(ctPatInsp18DaysArray.length() == 0) {
            String SelectedDate = Globally.GetCurrentDeviceDate(null, new Globally(), getActivity());
            if (SharedPref.getDriverType(getActivity()).equals(DriverConst.TeamDriver)) {
                DriverId = DriverConst.GetDriverDetails(DriverConst.DriverID, getActivity());
                CoDriverId = DriverConst.GetCoDriverDetails(DriverConst.CoDriverID, getActivity());
                SelectedMain = DriverId;
                GetCtPatInspection18Days(DriverId, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
                GetCtPatInspection18Days(CoDriverId, DeviceId, SelectedDate, GetCtPat18DaysCoDriverLog);
            } else {
                SelectedMain = DRIVER_ID;
                GetCtPatInspection18Days(DRIVER_ID, DeviceId, SelectedDate, GetCtPat18DaysMainDriverLog);
            }
        }

        agricultureArray =  constant.CtPatAgricultureList();   //new(JSONArray();
        ParseInspectionIssues( truckArray, trailerArray ,agricultureArray);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.arrivalContNoEditTxt:
               // Logger.LogDebug("onTouch", "editText on Touch");
                arrivalContNoEditTxt.setEnabled(true);
                arrivalContNoEditTxt.requestFocus();
                arrivalContNoEditTxt.setFocusableInTouchMode(true);
                arrivalContNoEditTxt.setSelection(arrivalContNoEditTxt.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(arrivalContNoEditTxt, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(arrivalContNoEditTxt.getWindowToken(), 0);
                // imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
                imm.showSoftInput(arrivalContNoEditTxt, InputMethodManager.SHOW_IMPLICIT);
                // imm.showSoftInput(pass, InputMethodManager.SHOW_IMPLICIT);


                break;


            case R.id.eldMenuLay:
                TabAct.sliderLay.performClick();
                break;


            case R.id.ctPatInspectionBtn:

                ArrivalSealNumber = arrivalContNoEditTxt.getText().toString().trim();
                DepartureSealNumber = departureContNoEditTxt.getText().toString().trim();
                SecurityInspectionPersonName = conductedSecInspEditTxt.getText().toString().trim();
                FollowUpInspectionPersonName = followSecLayEditTxt.getText().toString().trim();
                AffixedSealPersonName = affixedSealEditTxt.getText().toString().trim();
                VerificationPersonName = verifiedSealEditTxt.getText().toString().trim();
                AgricultureReason      = agricultureReasonEditText.getText().toString().trim();
                ContainerIdentification = containerIdenEditText.getText().toString().trim();

                if (SharedPref.getDriverStatusId(getActivity()).equals(Globally.ON_DUTY )) {
                    if(constant.isActionAllowed(getContext())) {

                        if(AgricultureIssueType.contains("101")) {
                            if (ArrivalSealNumber.length() > 0 || DepartureSealNumber.length() > 0) {
                                AgricultureReason = "null";
                                SaveInspectionOfflineWithAPI();
                            } else {
                                //  inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                                // cityEditText.requestFocus();
                                Globally.EldScreenToast(ctPatInspectionBtn, getResources().getString(R.string.arrival_departure_seal_number),
                                        getResources().getColor(R.color.colorVoilation));
                            }
                        }else{
                            if(AgricultureReason.length()> 0){
                                if (ArrivalSealNumber.length() > 0 || DepartureSealNumber.length() > 0) {
                                    SaveInspectionOfflineWithAPI();
                                } else {
                                    //  inspectionScrollView.fullScroll(ScrollView.FOCUS_UP);
                                    // cityEditText.requestFocus();
                                    Globally.EldScreenToast(ctPatInspectionBtn, getResources().getString(R.string.arrival_departure_seal_number),
                                            getResources().getColor(R.color.colorVoilation));
                                }
                            }else{
                                Globally.EldScreenToast(ctPatInspectionBtn, getResources().getString(R.string.agriculture_unacceptable_reason),
                                        getResources().getColor(R.color.colorVoilation));
                            }
                        }
                    }else{
                        Globally.EldScreenToast(ctPatInspectionBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
                                        getString(R.string.stop_vehicle_alert),
                                getResources().getColor(R.color.colorVoilation));
                    }
                }else{
                    Globally.EldScreenToast(ctPatInspectionBtn, ConstantsEnum.CTPAT_SAVE_ONDUTY_ONLY, getResources().getColor(R.color.colorVoilation));
                }


                break;


            case R.id.conductedSecIV:
                openSignatureDialog(conductedSecIV, SecurityConducted);
                break;


            case R.id.followSecLayIV:
                openSignatureDialog(followSecLayIV, FollowUpConducted);
                break;


            case R.id.affixedSealIV:
                openSignatureDialog(affixedSealIV, AffixedSeal);
                break;


            case R.id.verifiedSealIV:
                openSignatureDialog(verifiedSealIV, PhysicalVerify);
                break;

            case R.id.scrollChildMainLay:
                Globally.hideKeyboardView(getActivity(), verifiedSealEditTxt);
                break;

            case R.id.dateActionBarTV:
                //ShowDateDialog();
                MoveFragment(Globally.GetCurrentDeviceDate(null, new Globally(), getActivity()));
                break;


            case R.id.ctPatInspctTV:

                agricultureAdapter.IsClicked = false;
                TruckIssueType = "";      TraiorIssueType = ""; AgricultureIssueType = "";

                TruckIssueType = GetItemsId(TruckList, TruckIdList);
                TraiorIssueType = GetItemsId(TrailerList, TrailerIdList);
                AgricultureIssueType = GetItemsId(AgricultureList,AgricultureIdList);
                Logger.LogError("AgricultureIssueType",AgricultureIssueType);

                if(!AgricultureIssueType.contains("101")){
                    agricutureReasonLay.setVisibility(View.VISIBLE);
                    agricultureReasonCtPatView.setVisibility(View.VISIBLE);
                }else{
                    agricutureReasonLay.setVisibility(View.GONE);
                    agricultureReasonCtPatView.setVisibility(View.GONE);
                    AgricultureReason = "";
                }

                break;

        }

    }


    private void MoveFragment(String date ){
        try {
            if(getActivity() != null && !getActivity().isFinishing()) {
                Constants.IsInspectionDetailViewBack = false;

                InspectionsHistoryFragment savedInspectionFragment = new InspectionsHistoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("date", date);
                bundle.putString("inspection_type", "ct_pat");
                Constants.SelectedDatePti = date;

                savedInspectionFragment.setArguments(bundle);

                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTran = fragManager.beginTransaction();
                fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTran.add(R.id.job_fragment, savedInspectionFragment);
                fragmentTran.addToBackStack("ctpat_inspection");
                fragmentTran.commitAllowingStateLoss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


/*
    void ShowDateDialog(){
        try {
            if (dateDialog != null && dateDialog.isShowing())
                dateDialog.dismiss();

            dateDialog = new DatePickerDialog(getActivity(), CurrentCycleId, SelectedDatee, new DateListener(), false);
            dateDialog.show();
        }catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


   private class DateListener implements DatePickerDialog.DatePickerListener{
        @Override
        public void JobBtnReady(String SelectedDate, String dayOfTheWeek, String MonthFullName, String MonthShortName, int dayOfMonth) {

            try {
                if (dateDialog != null && dateDialog.isShowing())
                    dateDialog.dismiss();
            }catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            SelectedDatee = SelectedDate;
            MoveFragment(SelectedDate);
        }
    }*/




    void openSignatureDialog(ImageView imgView, int flag) {
        if (signDialog != null && signDialog.isShowing())
            signDialog.dismiss();
        signDialog = new CtPatDialog(getActivity(), imgView, flag, new SignListener());
        signDialog.show();
    }

    /*================== Signature Listener ====================*/
    private class SignListener implements CtPatDialog.SignListener {

        @Override
        public void SignOkBtn(InkView inkView, ImageView imageView, int viewFlag, boolean IsSigned) {

            if (IsSigned) {
                String imagePath = GetSignatureBitmap(inkView, imageView, viewFlag);
                getImagePathToString(viewFlag, imagePath);
            } else {
                imageView.setBackgroundDrawable(null);
                getImagePathToString(viewFlag, "");
            }

            signDialog.dismiss();
        }
    }


    private void getImagePathToString(int flag, String img) {

        switch (flag) {

            case 1:
                ByteInspectionConductorSign = ConvertImageToBytes(img);

                break;

            case 2:
                ByteFollowUpConductorSign = ConvertImageToBytes(img);
                break;

            case 3:
                ByteSealFixerSign = ConvertImageToBytes(img);
                break;

            case 4:
                ByteSealVerifierSign = ConvertImageToBytes(img);
                break;

        }

    }

    // Convert image file into bytes
    private String ConvertImageToBytes(String path){

        String getByteImage = "";

        if(path.length() > 0) {
            File selectedFile = new File(path);
            if (selectedFile.exists()) {
                Logger.LogInfo("", "---File: " + selectedFile.toString());
                getByteImage = Globally.ConvertImageToByteAsString(path);

                // delete file after Convert To Bytes
                selectedFile.delete();
            }
        }
        return getByteImage;


     /*   File file = new File(ImgPathSecurityConducted);
        File file2 = new File(ImgPathFollowUpConducted);
        File file3 = new File(ImgPathAffixedSeal);
        File file4 = new File(ImgPathPhysicalVerify);

        if (file.exists()) {
            Logger.LogInfo("", "---File1: " + file.toString());
            ByteInspectionConductorSign = Globally.ConvertImageToByteAsString(ImgPathSecurityConducted);
        }

        if (file2.exists()) {
            Logger.LogInfo("", "---File2: " + file2.toString());
            ByteFollowUpConductorSign = Globally.ConvertImageToByteAsString(ImgPathFollowUpConducted);
        }

        if (file3.exists()) {
            Logger.LogInfo("", "---File3: " + file3.toString());
            ByteSealFixerSign = Globally.ConvertImageToByteAsString(ImgPathAffixedSeal);
        }

        if (file4.exists()) {
            Logger.LogInfo("", "---File4: " + file4.toString());
            ByteSealVerifierSign = Globally.ConvertImageToByteAsString(ImgPathPhysicalVerify);
        }*/

    }
    /*================== Get Signature Bitmap ====================*/
    String GetSignatureBitmap(View targetView, ImageView canvasView, int flag) {
        String img = "";
        Bitmap b = Bitmap.createBitmap(targetView.getWidth(),
                targetView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        targetView.draw(c);
        BitmapDrawable d = new BitmapDrawable(getResources(), b);
        canvasView.setBackgroundDrawable(d);

        img = Globally.SaveBitmapToFile(b, "ctPat" + flag, 100, getActivity());

        return img;

    }



    private void SetDataInList(List<PrePostModel> array, ArrayList<String> list, ArrayList<Integer> idList){
        for(int i = 0 ; i < array.size() ; i++){
            list.add("");
            idList.add(-1);
        }
    }

    private String GetItemsId(ArrayList<String> array, ArrayList<Integer> idArray) {
        String data = "";
        try{
            for (int i = 0; i < array.size(); i++) {
                if (!array.get(i).equals("")) {
                    data = data + "," + idArray.get(i);
                }
            }

            if (data.length() > 0 && data.substring(0, 1).equals(",")) {
                data = data.substring(1, data.length());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
      //  Logger.LogDebug("data", "---data: " + data);
        return data;

    }


    void ClearFields(){
        TruckList = new ArrayList<>();
        TruckIdList = new ArrayList<>();
        TrailerList = new ArrayList<>();
        TrailerIdList = new ArrayList<>();
        AgricultureList = new ArrayList<>();
        AgricultureIdList = new ArrayList<>();
        AgricultureInspList = new ArrayList<>();
        // reset lists
        TruckInspList = constant.CtPatTruckList();
        TrailerInspList = constant.CtPatTrailerList();
        AgricultureInspList = constant.CtPatAgricultureList();

        SetDataInList(TruckInspList, TruckList, TruckIdList);
        SetDataInList(TrailerInspList, TrailerList, TrailerIdList);
        SetDataInList(AgricultureInspList, AgricultureList, AgricultureIdList);



        truckAdapter = new CtPatAdapter(getActivity(), false, false, TruckInspList, TruckList, TruckIdList);
        ctPatTruckGridVw.setAdapter(truckAdapter);

        trailerAdapter = new CtPatAdapter(getActivity(), false, false, TrailerInspList, TrailerList, TrailerIdList);
        ctPatTrailerGridVw.setAdapter(trailerAdapter);

        agricultureAdapter = new CtPatAdapter(getActivity(), true, true, AgricultureInspList, AgricultureList, AgricultureIdList);
        ctPatAgricultureGridVw.setAdapter(agricultureAdapter);

        arrivalContNoEditTxt.setText("");
        departureContNoEditTxt.setText("");
        conductedSecInspEditTxt.setText("");
        followSecLayEditTxt.setText("");
        affixedSealEditTxt.setText("");
        verifiedSealEditTxt.setText("");
        agricultureReasonEditText.setText("");
        containerIdenEditText.setText("");

        TruckIssueType       = "";
        TraiorIssueType      = "";
        AgricultureIssueType = "";
        ContainerIdentification = "";
       // AgricultureIssueType = "101,103,104,105,106";

        conductedSecIV.setBackgroundDrawable(null);
        followSecLayIV.setBackgroundDrawable(null);
        affixedSealIV.setBackgroundDrawable(null);
        verifiedSealIV.setBackgroundDrawable(null);

        ArrivalSealNumber            = "";  DepartureSealNumber         = "";
        SecurityInspectionPersonName = "";  ByteInspectionConductorSign = "";
        FollowUpInspectionPersonName = "";  ByteFollowUpConductorSign   = "";
        AffixedSealPersonName        = "";  ByteSealFixerSign           = "";
        VerificationPersonName       = "";  ByteSealVerifierSign        = "";
        AgricultureReason = "";

        agricutureReasonLay.setVisibility(View.GONE);
        agricultureReasonCtPatView.setVisibility(View.GONE);
        AgricultureReason = "";

        ScrollUpView();


    }

    void ScrollUpView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ctPatScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }, 200);
    }


    private void ParseInspectionIssues(JSONArray TruckArray, JSONArray TrailerArray,List<PrePostModel> agricultureArray){
        TruckInspList = new ArrayList<>();
        TrailerInspList = new ArrayList<>();
        AgricultureInspList = new ArrayList<>();

        TruckInspList   = parseListData(TruckArray);
        TrailerInspList = parseListData(TrailerArray);
        AgricultureInspList =  agricultureArray;

        SetDataInList(TruckInspList, TruckList, TruckIdList);
        SetDataInList(TrailerInspList, TrailerList, TrailerIdList);
        SetDataInList(AgricultureInspList, AgricultureList, AgricultureIdList);

        truckAdapter = new CtPatAdapter(getActivity(), false, false, TruckInspList, TruckList, TruckIdList);
        ctPatTruckGridVw.setAdapter(truckAdapter);

        trailerAdapter = new CtPatAdapter(getActivity(), false, false, TrailerInspList, TrailerList, TrailerIdList);
        ctPatTrailerGridVw.setAdapter(trailerAdapter);

        final int truckViewCount      = TruckInspList.size() / 2 + TruckInspList.size() % 2;
        final int trailerViewCount    = TrailerInspList.size() / 2 + TrailerInspList.size() % 2;
        final int agricultureViewCount    = AgricultureInspList.size() / 2 + AgricultureInspList.size() % 2;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (constant.inspectionLayHeight * truckViewCount) );
                    ctPatTruckGridVw.setLayoutParams(mParam);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (constant.inspectionLayHeight * trailerViewCount) );
                    ctPatTrailerGridVw.setLayoutParams(mParam);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (constant.inspectionLayHeight * agricultureViewCount) );
                    ctPatAgricultureGridVw.setLayoutParams(mParam);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 300);


        ScrollUpView();

    }


    private List<PrePostModel> parseListData(JSONArray array){
        List<PrePostModel> list = new ArrayList<>();
        for(int i = 0 ; i < array.length() ; i++){
            try {
                JSONObject truckObj = (JSONObject)array.get(i);
                PrePostModel model = new PrePostModel(truckObj.getString("SeventeenPointInspectionIssueTypeId"), truckObj.getString("IssueName"));
                list.add(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return list;
    }





    //*================== Save Driver CT-PAT Inspection ===================*//*
    private void SaveInspectionOfflineWithAPI(){

        pDialog.show();

        // disable temperory button click to avoid multiple clicks on button at the same time
        ctPatInspectionBtn.setEnabled(false);

        CreatedDate = Globally.ConvertDateFormatMMddyyyyHHmm(Globally.GetDriverCurrentDateTime(new Globally(), getActivity()));
        ctPatDateTimeTv.setText(CreatedDate.substring(0, 11));

        JSONObject inspectionData = ctPatInspectionMethod.AddUnPostedCtPatInspObj(DRIVER_ID, DeviceId, Globally.PROJECT_ID,
                DriverName, CompanyId, SharedPref.getVehicleId(getActivity()), VIN_NUMBER,
                SharedPref.getTruckNumber(getActivity()), SharedPref.getTrailorNumber(getActivity()), CreatedDate,
                ArrivalSealNumber , DepartureSealNumber, SecurityInspectionPersonName , FollowUpInspectionPersonName,
                AffixedSealPersonName , VerificationPersonName, Globally.LATITUDE, Globally.LONGITUDE, TruckIssueType,
                TraiorIssueType, ByteInspectionConductorSign, ByteFollowUpConductorSign, ByteSealFixerSign,
                ByteSealVerifierSign, AgricultureIssueType,  AgricultureReason, ContainerIdentification );

        // Add inspection JSON obj in 18 Days Array
        JSONArray reverseArray = shipmentHelperMethod.ReverseArray(inspection18DaysArray);
        JSONObject inspectionFor18DaysObj = ctPatInspectionMethod.AddCtPat18DaysObj(inspectionData, TruckList, TruckIdList,
                TrailerList, TrailerIdList, AgricultureList, AgricultureIdList);
        reverseArray.put(inspectionFor18DaysObj);

        // again reverse Array to show last item at top
        inspection18DaysArray = new JSONArray();
        inspection18DaysArray = shipmentHelperMethod.ReverseArray(reverseArray);
        ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, inspection18DaysArray);


        // Add inspection JSON obj in Offline Array
        JSONArray ctPatInspectionArray = ctPatInspectionMethod.getCtPatUnPostedInspArray(Integer.valueOf(DRIVER_ID), dbHelper);
        ctPatInspectionArray.put(inspectionData);
        ctPatInspectionMethod.DriverCtPatUnPostedInspHelper(Integer.valueOf(DRIVER_ID), dbHelper, ctPatInspectionArray);

        if(Globally.isConnected(getActivity()) ){
            // reset api call count
            failedApiTrackMethod.isAllowToCallOrReset(dbHelper, APIs.SAVE_17_INSPECTION_OFFLINE, true, new Globally(), getActivity());

            constant.IsCtPatUploading = true;
            saveInspectionPost.PostDriverLogData(ctPatInspectionArray, APIs.SAVE_17_INSPECTION_OFFLINE, Constants.SocketTimeout30Sec, true, false, 1, 101);
        }else{
            pDialog.dismiss();
            Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.ct_pat_inspection_willbe_saved ), getResources().getColor(R.color.colorSleeper) );
            ClearFields();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TabAct.host.setCurrentTab(0);
                }
            },1200);

        }


    }




    //*================== Get Inspection Details ===================*//*
    void GetCtPatInspectionDetail(final String DriverId, final String DeviceId, final String ProjectId, final String VIN) {

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId);
         params.put(ConstantsKeys.ProjectId, ProjectId);
         params.put(ConstantsKeys.VIN, VIN);

        GetCtPatInspRequest.executeRequest(Request.Method.POST, APIs.GET_INSPECTION_DETAIL, params, GetCtPatInspection,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }



    //*================== Get Driver Status Permissions ===================*//*
    void GetCtPatInspection18Days(final String DriverId, final String DeviceId, final String SearchedDate, final int GetInspectionFlag ){

        params = new HashMap<String, String>();
        params.put(ConstantsKeys.DriverId, DriverId);
         params.put(ConstantsKeys.DeviceId, DeviceId );
        params.put(ConstantsKeys.SearchedDate, SearchedDate );

        ctPatInsp18DaysRequest.executeRequest(Request.Method.POST, APIs.GET_OFFLINE_17_INSPECTION_LIST, params, GetInspectionFlag,
                Constants.SocketTimeout10Sec, ResponseCallBack, ErrorCallBack);

    }




    VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

        @Override
        public void getResponse(String response, int flag) {

            Logger.LogDebug("response", "Driver response: " + response);
            String status = "";
            JSONObject obj = null;
            try {
                obj = new JSONObject(response);
                status = obj.getString("Status");

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (status.equalsIgnoreCase("true")) {

                    switch (flag) {

                        case GetCtPatInspection:

                            JSONObject dataObj = new JSONObject(obj.getString("Data"));

                            // Save Inspections issues list in local
                            SharedPref.setInspectionIssues(dataObj.getString("TruckIssueList"),dataObj.getString("TrailorIssueList"), getActivity());

                            // Save CT-PAT Inspections issues list in local
                            if(dataObj.has("SeventeenTruckList")){
                                truckArray = new JSONArray();
                                trailerArray = new JSONArray();

                                truckArray = new JSONArray(dataObj.getString("SeventeenTruckList"));
                                trailerArray = new JSONArray(dataObj.getString("SeventeenTrailorList"));

                                SharedPref.setCtPatInspectionIssues( truckArray.toString() , trailerArray.toString(), getActivity());

                                ParseInspectionIssues( truckArray, trailerArray ,agricultureArray);
                            }


                            break;

                        case GetCtPat18DaysMainDriverLog:
                            if (!obj.isNull("Data")) {
                                try {
                                    JSONArray inspectionData = new JSONArray(obj.getString("Data"));

                                    ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(DRIVER_ID), dbHelper, inspectionData);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            break;

                        case GetCtPat18DaysCoDriverLog:
                            if (!obj.isNull("Data")) {
                                try {
                                    JSONArray inspectionData = new JSONArray(obj.getString("Data"));

                                    ctPatInspectionMethod.DriverCtPatInsp18DaysHelper(Integer.valueOf(CoDriverId), dbHelper, inspectionData);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall(){

        @Override
        public void getError(VolleyError error, int flag) {

        }
    };




    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveInspectionResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {
            Logger.LogDebug("InspectionLog", "---Response CT-PAT Inspection: " + response);
            pDialog.dismiss();
            Globally.hideSoftKeyboard(getActivity());
            constant.IsCtPatUploading = false;

            try {
                JSONObject obj = new JSONObject(response);
                String Message = obj.getString("Message");

                if (obj.getBoolean("Status")) {
                    if(Message.equals(getResources().getString(R.string.data_saved_successfully))){
                        // Clear all unposted Array from list......
                        ctPatInspectionMethod.DriverCtPatUnPostedInspHelper(Integer.valueOf(DRIVER_ID), dbHelper, new JSONArray());
                        ClearFields();

                        if(flag != 102) {
                            Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.ct_pat_inspection_saved_successfully), UILApplication.getInstance().getThemeColor());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    TabAct.host.setCurrentTab(0);
                                }
                            }, 1200);
                        }
                    }
                }else{
                    if(flag != 102) {
                        Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.ct_pat_inspection_willbe_saved), getResources().getColor(R.color.colorSleeper));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                TabAct.host.setCurrentTab(0);
                            }
                        }, 1200);
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: ");
            Globally.hideSoftKeyboard(getActivity());
            pDialog.dismiss();
            ClearFields();
            constant.IsCtPatUploading = false;

            if(flag != 102) {
                Globally.EldToastWithDuration(TabAct.sliderLay, getResources().getString(R.string.ct_pat_inspection_willbe_saved), getResources().getColor(R.color.colorSleeper));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TabAct.host.setCurrentTab(0);
                    }
                }, 1200);
            }
        }
    };



}
