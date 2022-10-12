package com.custom.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.adapter.logistic.RecapRecordSignAdapter;
import com.constants.APIs;
import com.constants.Constants;
import com.constants.DriverLogResponse;
import com.constants.Logger;
import com.constants.SaveDriverLogPost;
import com.constants.SharedPref;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.driver.details.DriverConst;
import com.local.db.CertifyLogMethod;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.UILApplication;
import com.messaging.logistic.fragment.CertifyViewLogFragment;
import com.models.RecapSignModel;
import com.simplify.ink.InkView;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignRecordDialog extends Dialog {

    public interface DateSelectListener {
        public void SignOkBtn(DateTime dateTime, boolean IsSigned);
    }

    private DateSelectListener readyListener;
    ListView signRecordListView;
    List<RecapSignModel> recapRecordsList;
    TextView fromToDateTv;
    public static TextView certifyRecordBtn, recapRecordInvisibleTv;
    public static CheckBox selectAllRecordsCheckBox;
    ImageView invisbleSignImgView;

    private RecapRecordSignAdapter recapSignAdapter;
    Context context;
    Constants constants;
    public static int recapSelectedPosition = 0;
    public  static boolean isSignItemClicked = false;
    ArrayList<String> recordSelectedList = new ArrayList<>();
    JSONArray CertifyLogArray = new JSONArray();
    JSONArray recap18DaysArray = new JSONArray();
    ArrayList<String> selectedDateList = new ArrayList<>();

    String DriverId, DeviceId, imagePath = "", LogSignImageInByte = "", SignCopyDate = "", CompanyId;
    boolean isCertifySignExist;
    AlertDialog alertDialog;
    SignDialog signDialog;
    PtiSignDialog ptiSignDialog;

    DriverPermissionMethod driverPermissionMethod;
    RecapViewMethod recapViewMethod;
    CertifyLogMethod certifyLogMethod;
    SaveDriverLogPost saveCertifyLogPost;
    Globally globally;
    DBHelper dbHelper;
    int DriverType;
    ProgressDialog progressDialog;
    VolleyRequest notReadyRequest;
    CertifyConfirmationDialog certifyConfirmationDialog;
    Utils obdUtil;

    public SignRecordDialog(Context context, int DriverType, boolean isCertifySignExist, JSONArray recap18DaysArray, List<RecapSignModel> recapList,
                            DateSelectListener readyListener,
                            Constants constants,
                            RecapViewMethod recapViewMethod,
                            CertifyLogMethod certifyLogMethod,
                            DBHelper dbHelper, Utils utils) {
        super(context);
        this.context = context;
        this.DriverType = DriverType;
        this.isCertifySignExist = isCertifySignExist;
        recapRecordsList = recapList;

        this.readyListener = readyListener;

        this.constants =  constants;
        this.recapViewMethod =  recapViewMethod;
        this.certifyLogMethod =  certifyLogMethod;
        this.dbHelper =  dbHelper;
        obdUtil = utils;

        driverPermissionMethod      = new DriverPermissionMethod();
        saveCertifyLogPost          = new SaveDriverLogPost(context, saveCertifyResponse);

        notReadyRequest = new VolleyRequest(context);
        globally        = new Globally();
        progressDialog  = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");

        DeviceId           = SharedPref.GetSavedSystemToken(context);
        DriverId           = SharedPref.getDriverId(context);
        CompanyId          = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);

        this.recap18DaysArray = recap18DaysArray;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.sign_record_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(globally.isTablet(context)){
            getWindow().setLayout(constants.intToPixel(context, 730), ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            getWindow().setLayout(constants.intToPixel(context, 550), ViewGroup.LayoutParams.WRAP_CONTENT);
        }


        signRecordListView      = (ListView) findViewById(R.id.signRecordListView);
        selectAllRecordsCheckBox= (CheckBox) findViewById(R.id.selectAllRecordsCheckBox);
        invisbleSignImgView     = (ImageView)findViewById(R.id.invisbleSignImgView);

        fromToDateTv            = (TextView)findViewById(R.id.fromToDateTv);
        certifyRecordBtn        = (TextView)findViewById(R.id.certifyRecordTv);
        recapRecordInvisibleTv  = (TextView)findViewById(R.id.recapRecordInvisibleTv);

        if(recapRecordsList.size() > 0) {
            String fromDate = globally.dateConversionMonthNameWithDay(recapRecordsList.get(0).getDate().toString());
            String toDate   = globally.dateConversionMonthNameWithDay(recapRecordsList.get(recapRecordsList.size()-1).getDate().toString());

            fromToDateTv.setText(fromDate + " - " + toDate);
        }

        setListSelectionRecord(false);
        try {
            recapSignAdapter = new RecapRecordSignAdapter(context, recapRecordsList, recordSelectedList, false, false);
            signRecordListView.setAdapter(recapSignAdapter);
        }catch (Exception e){}

        selectAllRecordsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(!isSignItemClicked) {
                    boolean isAllSelected = compoundButton.isChecked();
                    setListSelectionRecord(isAllSelected);
                    Parcelable state = signRecordListView.onSaveInstanceState();
                    signRecordListView.onRestoreInstanceState(state);

                    try{
                        recapSignAdapter = new RecapRecordSignAdapter(context, recapRecordsList, recordSelectedList, isAllSelected, true);
                        signRecordListView.setAdapter(recapSignAdapter);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                isSignItemClicked = false;
            }
        });


        recapRecordInvisibleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                readyListener.SignOkBtn(
                        recapRecordsList.get(recapSelectedPosition).getDate(),
                        recapRecordsList.get(recapSelectedPosition).isCertified());

            }
        });

        certifyRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedDateList = getSelectedItemDate();   //09/19/2022

                if(selectedDateList.size() > 0){
                    boolean isReCertify = isRecertify(selectedDateList);

                    certifyConfirmationDialog = new CertifyConfirmationDialog(getContext(), false, isReCertify,
                                                "", new CertificationListener() );
                    certifyConfirmationDialog.show();

                }else{
                    globally.EldScreenToast(certifyRecordBtn, context.getResources().getString(R.string.no_date_for_certify),
                            context.getResources().getColor(R.color.colorVoilation));
                }
            }
        });
    }


    private boolean isRecertify(ArrayList<String> selectedDateList){
        boolean isReCertify = false;
        for(int i = 0 ; i< selectedDateList.size() ; i++) {
            String selectedDate = Globally.ConvertDateFormat(selectedDateList.get(i));
            isReCertify = constants.isReCertifyRequired(getContext(), null, selectedDate);

            if(isReCertify){
                break;
            }

        }

        return isReCertify;

    }

    private class CertificationListener implements CertifyConfirmationDialog.CertifyConfirmationListener{

        @Override
        public void CertifyBtnReady(boolean isSwapConfirmation, boolean isReCertify) {

            if(isReCertify){
                SignCopyDate =  constants.getLastSignDate(recapViewMethod, DriverId, dbHelper);
                LogSignImageInByte = constants.getLastSignature(recapViewMethod, DriverId, dbHelper);
                SaveDriverSignArray(true);
            }else {
                CertifyLogArray = certifyLogMethod.getSavedCertifyLogArray(Integer.valueOf(DriverId), dbHelper);
                if (isCertifySignExist) {
                    // ContinueWithoutSignDialog();
                    if (ptiSignDialog != null && ptiSignDialog.isShowing()) {
                        ptiSignDialog.dismiss();
                    }
                    String lastSignature = constants.getLastSignature(recapViewMethod, DriverId, dbHelper);
                    SignCopyDate = constants.getLastSignDate(recapViewMethod, DriverId, dbHelper);
                    ptiSignDialog = new PtiSignDialog(getContext(), "Certify", lastSignature,
                            null, new PtiConfirmationListener());
                    ptiSignDialog.show();
                } else {
                    openSignDialog();
                }
            }
        }

        @Override
        public void CancelBtnReady() {
            dismiss();
        }
    }


    private class PtiConfirmationListener implements PtiSignDialog.PtiConfirmationListener{

        @Override
        public void PtiBtnReady(String ByteSign, String SignDate) {
            if(SignDate.length() > 0) {
                SignCopyDate = SignDate;
            }

            LogSignImageInByte = ByteSign;
            SaveDriverSignArray(true);
        }

        @Override
        public void CancelBtnReady() {
            openSignDialog();
        }
    }



   /* public void ContinueWithoutSignDialog(){
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
            alertDialogBuilder.setTitle("Certify log alert !!");
            alertDialogBuilder.setMessage(context.getResources().getString(R.string.continue_sign_desc));
            alertDialogBuilder.setCancelable(false);


            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {

                            SaveDriverSignArray(true);
                            dialog.dismiss();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    openSignDialog();
                    dialog.dismiss();
                }
            });


            if (alertDialog != null && alertDialog.isShowing())
                alertDialog.dismiss();

            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            if(UILApplication.getInstance().isNightModeEnabled()) {
                alertDialog.getWindow().setBackgroundDrawableResource(R.color.layout_color_dot);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.white));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.white));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    void openSignDialog(){
        if(context != null  ) {
            if (signDialog != null && signDialog.isShowing())
                signDialog.dismiss();
            signDialog = new SignDialog(context, new SignListener());
            signDialog.show();
        }
    }



    /*================== Signature Listener ====================*/
    private class SignListener implements SignDialog.SignListener{

        @Override
        public void SignOkBtn(InkView inkView, boolean IsSigned) {

            try {
                if (signDialog != null) {
                    if (IsSigned) {
                        imagePath = constants.GetSignatureBitmap(inkView, invisbleSignImgView, context);
                        signDialog.dismiss();
                        SaveDriverSignArray(false);

                    } else {
                        globally.EldScreenToast(TabAct.sliderLay, "Error", context.getResources().getColor(R.color.colorVoilation) );
                        imagePath = "";
                    }


                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }



    private void SaveDriverSignArray(boolean IsCarryForward){

        if(IsCarryForward){

            String lastSignature = constants.getLastSignature(recapViewMethod, DriverId, dbHelper);
            //boolean isReCertifyRequired = constants.isReCertifyRequired(context, dataObj, "");
            saveByteSignLocally(lastSignature, true, false);
            LogSignImageInByte = lastSignature;

            if(globally.isConnected(context) ){
                progressDialog.show();
                saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec,
                        true, false, DriverType, 101);
            }else{
                globally.EldToastWithDuration(CertifyViewLogFragment.saveSignatureBtn, context.getResources().getString(R.string.certify_log_offline_saved), context.getResources().getColor(R.color.colorSleeper) );

                // refresh view with button click
                CertifyViewLogFragment.invisibleRfreshBtn.performClick();

                dismiss();

            }

        }else{
            File f = new File(imagePath);
            if (f.exists() ) {

                // Convert image file into bytes
                LogSignImageInByte = globally.ConvertImageToByteAsString(imagePath);
                saveByteSignLocally(LogSignImageInByte, false, true);

                if(globally.isConnected(context) ){
                    progressDialog.show();
                    saveCertifyLogPost.PostDriverLogData(CertifyLogArray, APIs.CERTIFY_LOG_OFFLINE, constants.SocketTimeout10Sec, true, false, DriverType, 101);
                }else{
                    globally.EldToastWithDuration(CertifyViewLogFragment.saveSignatureBtn, context.getResources().getString(R.string.certify_log_offline_saved), context.getResources().getColor(R.color.colorSleeper) );

                    // refresh view with button click
                    CertifyViewLogFragment.invisibleRfreshBtn.performClick();

                    dismiss();

                }

            }else{
                globally.EldScreenToast(certifyRecordBtn , context.getResources().getString(R.string.sign_not_valid), context.getResources().getColor(R.color.colorVoilation));
            }
        }

    }


    private void saveByteSignLocally(String SignImageInBytes, boolean IsContinueWithSign, boolean isNewFile){
        // Add signed parameters with values into the json object and put into the json Array.
        String signCopyDatee = "";
        for(int i = 0 ; i < selectedDateList.size() ; i++){
            JSONObject CertifyLogObj;
            String dateStr = selectedDateList.get(i);

            if(IsContinueWithSign){
                signCopyDatee = SignCopyDate;
                if(SignCopyDate.length() == 0){
                    signCopyDatee = dateStr;
                }
            }else{
                if(signCopyDatee.length() == 0) {
                    signCopyDatee = dateStr;
                }
            }

            boolean isReCertifyRequired = constants.isReCertifyRequired(getContext(), null, globally.ConvertDateFormat(dateStr));
            String locationType = SharedPref.getLocationEventType(context);
            if(i == 0) {
                CertifyLogObj = certifyLogMethod.AddCertifyLogArray(DriverId, DeviceId, globally.PROJECT_ID, dateStr,
                        SignImageInBytes, IsContinueWithSign, isReCertifyRequired, CompanyId, locationType, signCopyDatee);
            }else{
                CertifyLogObj = certifyLogMethod.AddCertifyLogArray(DriverId, DeviceId, globally.PROJECT_ID, dateStr,
                        SignImageInBytes, true, isReCertifyRequired, CompanyId, locationType, signCopyDatee);
            }
            CertifyLogArray.put(CertifyLogObj);

            // Insert/Update Certify Log table
            certifyLogMethod.CertifyLogHelper(Integer.valueOf(DriverId), dbHelper, CertifyLogArray);

            // Update recap array with byte image
            recap18DaysArray = recapViewMethod.UpdateSelectedDateRecapArray(recap18DaysArray, selectedDateList.get(i), SignImageInBytes);

         /*   constants.saveObdData("Certify",
                    "CertifyLogArray: " + CertifyLogArray.toString(),
                    "", "","", "","", "",
                    "","", "", "", "",
                    DriverId, dbHelper, driverPermissionMethod, obdUtil);
*/

        }

        recapViewMethod.RecapView18DaysHelper(Integer.valueOf(DriverId), dbHelper, recap18DaysArray);
        SignCopyDate = "";
    }


    private void setListSelectionRecord(boolean isSelected){
      //  Logger.LogDebug("recordSelectedList", "recordSelectedList: " + recordSelectedList);

        if(recordSelectedList.size() > 0) {
            boolean isItemChecked = false;
            for (int i = 0; i < recapRecordsList.size(); i++) {
                if (isSelected && !recapRecordsList.get(i).isCertified()) {
                    recordSelectedList.set(i, "selected");
                    isItemChecked = true;
                } else {
                    recordSelectedList.set(i, "");
                }
            }

            if(isItemChecked){
                certifyRecordBtn.setVisibility(View.VISIBLE);
            }else{
                certifyRecordBtn.setVisibility(View.GONE);
            }
        }else{
            for (int i = 0; i < recapRecordsList.size(); i++) {
                 recordSelectedList.add("");
            }
        }
    }

    private ArrayList<String> getSelectedItemDate(){
        ArrayList<String> selectedDateList = new ArrayList<>();
        if(recordSelectedList.size() > 0) {
            for (int i = 0; i < recordSelectedList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    String date = globally.ConvertDateFormatMMddyyyy(recapRecordsList.get(i).getDate().toString());
                    selectedDateList.add(date);
                }
            }
        }

        return selectedDateList;
    }

    void dismissDialog(){
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }catch (Exception e){ e.printStackTrace();}
    }



    /* ---------------------- Save Log Request Response ---------------- */
    DriverLogResponse saveCertifyResponse = new DriverLogResponse() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, int inputDataLength) {
            Logger.LogDebug("signatureLog", "---Response: " + response);
            dismissDialog();

            try {
                JSONObject obj = new JSONObject(response);

                if (obj.getString("Status").equals("true")) {
                    // Clear unsent Shipping Log from db
                    CertifyLogArray = new JSONArray();
                    certifyLogMethod.CertifyLogHelper(Integer.valueOf(DriverId), dbHelper, CertifyLogArray );

                    globally.EldScreenToast(CertifyViewLogFragment.saveSignatureBtn, context.getString(R.string.has_been_certified),
                            context.getResources().getColor(R.color.colorPrimary));

                    // refresh view with button click
                    CertifyViewLogFragment.invisibleRfreshBtn.performClick();

                    dismiss();

                }else{
                    globally.EldToastWithDuration(CertifyViewLogFragment.saveSignatureBtn, context.getResources().getString(R.string.certify_log_offline_saved),
                            context.getResources().getColor(R.color.colorSleeper) );

                    // refresh view with button click
                    CertifyViewLogFragment.invisibleRfreshBtn.performClick();

                    dismiss();

                }



            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
            Logger.LogDebug("errorrr ", ">>>error dialog: ");

            if(context != null) {
                try {

                    dismissDialog();

                    globally.EldToastWithDuration(TabAct.sliderLay, context.getResources().getString(R.string.certify_log_offline_saved),
                                context.getResources().getColor(R.color.colorSleeper));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    };


}
