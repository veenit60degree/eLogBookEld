package com.als.logistic.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adapter.logistic.ViewInspectionGridAdapter;
import com.constants.Constants;
import com.local.db.ConstantsKeys;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.models.CtPatInspectionModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CtPatDetailInspection extends Fragment {


    View rootView, truckCtPatView, trailerCtPatView,agricultureCtPatView,agricultureReasonCtPatView;
    RelativeLayout eldMenuLay;
    LinearLayout scrollChildMainLay,agricutureReasonLay;
    RelativeLayout rightMenuBtn;
    TextView trailerCtPatGridTitle, truckCtPatGridTitle,agricultureCtPatGridTitle;
    TextView EldTitleTV, actionBarRightBtn, truckCtPatTV, trailerCtPatTV, ctPatDateTimeTv;
    EditText arrivalContNoEditTxt, departureContNoEditTxt, conductedSecInspEditTxt, followSecLayEditTxt, affixedSealEditTxt, verifiedSealEditTxt,
            agricultureReasonEditText, containerIdenEditText;
    ImageView conductedSecIV, followSecLayIV, affixedSealIV, verifiedSealIV, eldMenuBtn;
    GridView ctPatTruckGridVw, ctPatTrailerGridVw,ctPatAgricultureGridVw;
    Button ctPatInspectionBtn;

    ArrayList<String> TruckList, TrailerList,AgricultureList;
    ViewInspectionGridAdapter truckAdapter, trailerAdapter,agricultureAdapter;

    boolean Clicked = false;

    String ArrivalSealNumber = "", DepartureSealNumber = "";
    String SecurityInspectionPersonName = "", ByteInspectionConductorSign = "";
    String FollowUpInspectionPersonName = "", ByteFollowUpConductorSign = "";
    String AffixedSealPersonName        = "", ByteSealFixerSign = "";
    String VerificationPersonName       = "", ByteSealVerifierSign = "";
    String AgricultureReason       = "",    ContainerIdentificationReason = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
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

        eldMenuLay              = (RelativeLayout) view.findViewById(R.id.eldMenuLay);

        rightMenuBtn            = (RelativeLayout) view.findViewById(R.id.rightMenuBtn);
        scrollChildMainLay      = (LinearLayout) view.findViewById(R.id.scrollChildMainLay);
        agricutureReasonLay      = (LinearLayout) view.findViewById(R.id.agricutureReasonLay);

        truckCtPatView          = (View) view.findViewById(R.id.truckCtPatView);
        trailerCtPatView        = (View) view.findViewById(R.id.trailerCtPatView);
        agricultureCtPatView        = (View) view.findViewById(R.id.agricultureCtPatView);
        agricultureReasonCtPatView         = (View) view.findViewById(R.id.agricultureReasonCtPatView);

        trailerCtPatGridTitle   = (TextView) view.findViewById(R.id.trailerCtPatGridTitle);
        truckCtPatGridTitle   = (TextView) view.findViewById(R.id.truckCtPatGridTitle);
        agricultureCtPatGridTitle   = (TextView) view.findViewById(R.id.agricultureCtPatGridTitle);
        EldTitleTV              = (TextView) view.findViewById(R.id.EldTitleTV);
        actionBarRightBtn       = (TextView) view.findViewById(R.id.dateActionBarTV);
        truckCtPatTV            = (TextView) view.findViewById(R.id.truckCtPatTV);
        trailerCtPatTV          = (TextView) view.findViewById(R.id.trailerCtPatTV);
        ctPatDateTimeTv         = (TextView) view.findViewById(R.id.ctPatDateTimeTv);

        arrivalContNoEditTxt    = (EditText) view.findViewById(R.id.arrivalContNoEditTxt);
        departureContNoEditTxt  = (EditText) view.findViewById(R.id.departureContNoEditTxt);
        conductedSecInspEditTxt = (EditText) view.findViewById(R.id.conductedSecInspEditTxt);
        followSecLayEditTxt     = (EditText) view.findViewById(R.id.followSecLayEditTxt);
        affixedSealEditTxt      = (EditText) view.findViewById(R.id.affixedSealEditTxt);
        verifiedSealEditTxt     = (EditText) view.findViewById(R.id.verifiedSealEditTxt);
        agricultureReasonEditText     = (EditText) view.findViewById(R.id.agricultureReasonEditText);
        containerIdenEditText    = (EditText) view.findViewById(R.id.containerIdenEditText);

        ctPatTruckGridVw        = (GridView) view.findViewById(R.id.ctPatTruckGridVw);
        ctPatTrailerGridVw      = (GridView) view.findViewById(R.id.ctPatTrailerGridVw);
        ctPatAgricultureGridVw      = (GridView) view.findViewById(R.id.ctPatAgricultureGridVw);

        conductedSecIV          = (ImageView) view.findViewById(R.id.conductedSecIV);
        followSecLayIV          = (ImageView) view.findViewById(R.id.followSecLayIV);
        affixedSealIV           = (ImageView) view.findViewById(R.id.affixedSealIV);
        verifiedSealIV          = (ImageView) view.findViewById(R.id.verifiedSealIV);
        eldMenuBtn              = (ImageView) view.findViewById(R.id.eldMenuBtn);

        ctPatInspectionBtn      = (Button) view.findViewById(R.id.ctPatInspectionBtn);

        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(getResources().getString(R.string.detaildCtPatIns));

        disableView();
        getDataOnView();

        eldMenuLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eldMenuLay.setEnabled(false);
                getParentFragmentManager().popBackStack();
            }
        });

    }


    private void disableView(){

        arrivalContNoEditTxt.setEnabled(false);
        departureContNoEditTxt.setEnabled(false);
        conductedSecInspEditTxt.setEnabled(false);
        followSecLayEditTxt.setEnabled(false);
        affixedSealEditTxt.setEnabled(false);
        verifiedSealEditTxt.setEnabled(false);
        agricultureReasonEditText.setEnabled(false);

        arrivalContNoEditTxt.setHint("--");
        departureContNoEditTxt.setHint("--");
        conductedSecInspEditTxt.setHint("--");
        followSecLayEditTxt.setHint("--");
        affixedSealEditTxt.setHint("--");
        verifiedSealEditTxt.setHint("--");
        agricultureReasonEditText.setHint("--");


        ctPatInspectionBtn.setVisibility(View.GONE);
        actionBarRightBtn.setVisibility(View.GONE);
        rightMenuBtn.setVisibility(View.INVISIBLE);

    }


    private void getDataOnView(){
        Bundle getBundle  = this.getArguments();
        int position  = getBundle.getInt("position");
        getBundle.clear();

        CtPatInspectionModel savedInspectionModel = InspectionsHistoryFragment.savedCtPatInspectionList.get(position);

        ArrivalSealNumber = savedInspectionModel.getArrivalSealNumber();
        DepartureSealNumber = savedInspectionModel.getDepartureSealNumber();

        SecurityInspectionPersonName = savedInspectionModel.getSecurityInspectionPersonName();
        FollowUpInspectionPersonName = savedInspectionModel.getFollowUpInspectionPersonName();
        AffixedSealPersonName = savedInspectionModel.getAffixedSealPersonName();
        VerificationPersonName = savedInspectionModel.getVerificationPersonName();
        AgricultureReason      = savedInspectionModel.getAgricultureReason();
        ContainerIdentificationReason  = savedInspectionModel.getContainerIdentificationReason();

        ByteInspectionConductorSign = savedInspectionModel.getByteInspectionConductorSign().trim();
        ByteFollowUpConductorSign = savedInspectionModel.getByteFollowUpConductorSign().trim();
        ByteSealFixerSign = savedInspectionModel.getByteSealFixerSign().trim();
        ByteSealVerifierSign = savedInspectionModel.getByteSealVerifierSign().trim();

        setTextOnView(arrivalContNoEditTxt, ArrivalSealNumber);
        setTextOnView(departureContNoEditTxt, DepartureSealNumber);
        setTextOnView(conductedSecInspEditTxt, SecurityInspectionPersonName);
        setTextOnView(followSecLayEditTxt, FollowUpInspectionPersonName);
        setTextOnView(affixedSealEditTxt, AffixedSealPersonName);
        setTextOnView(verifiedSealEditTxt, VerificationPersonName);
        setTextOnView(agricultureReasonEditText, AgricultureReason);
        setTextOnView(containerIdenEditText , ContainerIdentificationReason);


        truckCtPatTV.setText(savedInspectionModel.getVehicleEquNumber());
        trailerCtPatTV.setText(savedInspectionModel.getTrailorEquNumber());

        //2019-06-14T02:42:30
        String savedDate = savedInspectionModel.getInspectionDateTime();
        String date = "", time = "";
        if(savedDate != null && savedDate.length() > 11){
            time = Globally.ConvertTo12HTimeFormat(savedDate, Globally.DateFormatWithMillSec);
            date =  Globally.ConvertDateFormatMMddyyyy(savedDate);

        }
        ctPatDateTimeTv.setText(date ); //+ " " +time

        LoadByteImage(conductedSecIV, ByteInspectionConductorSign);
        LoadByteImage(followSecLayIV, ByteFollowUpConductorSign);
        LoadByteImage(affixedSealIV,  ByteSealFixerSign);
        LoadByteImage(verifiedSealIV, ByteSealVerifierSign);


        TruckList   = ParseListData(savedInspectionModel.getTruckIssueType());
        TrailerList = ParseListData(savedInspectionModel.getTraiorIssueType());
        AgricultureList = ParseListData(savedInspectionModel.getAgricultureIssueType());


        if(TruckList.size() == 0){
            truckCtPatGridTitle.setVisibility(View.GONE);
            truckCtPatView.setVisibility(View.GONE);
        }

        if(TrailerList.size() == 0){
            trailerCtPatGridTitle.setVisibility(View.GONE);
            trailerCtPatView.setVisibility(View.GONE);
        }

        if(AgricultureList.size() == 0){
            agricultureCtPatGridTitle.setVisibility(View.GONE);
            agricultureCtPatView.setVisibility(View.GONE);

        }

        if(!AgricultureReason.equals("null") && AgricultureReason.length() > 0) {
            agricutureReasonLay.setVisibility(View.VISIBLE);
            agricultureReasonCtPatView.setVisibility(View.VISIBLE);
            AgricultureList.add(0,"Area of Inspection");
            Clicked = true;
        }


        try{
            truckAdapter = new ViewInspectionGridAdapter(getActivity(),  TruckList,false);
            ctPatTruckGridVw.setAdapter(truckAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            trailerAdapter = new ViewInspectionGridAdapter(getActivity(), TrailerList,false);
            ctPatTrailerGridVw.setAdapter(trailerAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            agricultureAdapter = new ViewInspectionGridAdapter(getActivity(), AgricultureList,Clicked);
            ctPatAgricultureGridVw.setAdapter(agricultureAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }


        final int truckViewCount      = TruckList.size() / 2 + TruckList.size() % 2;
        final int trailerViewCount    = TrailerList.size() / 2 + TrailerList.size() % 2;
        final int agricultureViewCount    = AgricultureList.size() / 2 + AgricultureList.size() % 2;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int truckViewHeight = Constants.inspectionViewHeight * truckViewCount;
                int trailerViewHeight = Constants.inspectionViewHeight * trailerViewCount;
                int agricultureViewHeight = Constants.inspectionViewHeight * agricultureViewCount;

                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, truckViewHeight) ;  //SavedInspectionsFragment.inspectionLayHeight
                    ctPatTruckGridVw.setLayoutParams(mParam);
                }catch (Exception e){}

                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, trailerViewHeight ) ; //SavedInspectionsFragment.inspectionLayHeight
                    ctPatTrailerGridVw.setLayoutParams(mParam);
                }catch (Exception e){}

                try {
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, agricultureViewHeight ) ; //SavedInspectionsFragment.inspectionLayHeight
                    ctPatAgricultureGridVw.setLayoutParams(mParam);
                }catch (Exception e){}
            }
        }, 500);


    }



    private ArrayList<String> ParseListData(String data){

        ArrayList<String> list = new ArrayList<String>();

        try{
            JSONArray array = new JSONArray(data);
            for(int i = 0 ; i < array.length() ; i++){
                try {
                    JSONObject truckObj = (JSONObject)array.get(i);
                    if(truckObj.has(ConstantsKeys.IssueName)){
                        list.add(truckObj.getString(ConstantsKeys.IssueName));
                    }else {
                        if (truckObj.getBoolean(ConstantsKeys.Selected))
                            list.add(truckObj.getString(ConstantsKeys.Text));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }




    private void LoadByteImage(final ImageView signImageView, String SignImageInByte){
        if(!SignImageInByte.equals("null") && SignImageInByte.length() > 5 ) {
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



    private void setTextOnView(EditText view, String data){
        if(!data.equals("null")){
            view.setText(data);
        }
    }
}
