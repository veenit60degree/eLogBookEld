package com.als.logistic.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.adapter.logistic.SupportAdapter;
import com.constants.SharedPref;
import com.models.SupportModel;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.SupportMethod;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlsSupportFragment extends Fragment {


    View rootView;
    ListView supportListView;
    TextView actionBarTitle, noDataSupportTV, dateActionBarTV;
    RelativeLayout actionBarMenuLay, supporttMainLay;
    RelativeLayout rightMenuBtn;
    SupportMethod supportMethod;
    SupportAdapter supportAdapter;
    DBHelper dbHelper;

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
            rootView = inflater.inflate(R.layout.fragment_support, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }


    void initView(View v) {

        dbHelper            = new DBHelper(getActivity());
        supportMethod       = new SupportMethod();
        actionBarTitle      = (TextView)v.findViewById(R.id.EldTitleTV);
        noDataSupportTV     = (TextView)v.findViewById(R.id.noDataSupportTV);
        dateActionBarTV     = (TextView)v.findViewById(R.id.dateActionBarTV);

        supportListView     = (ListView)v.findViewById(R.id.supportListView);

        supporttMainLay     = (RelativeLayout)v.findViewById(R.id.supporttMainLay);
        actionBarMenuLay    = (RelativeLayout) v.findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)v.findViewById(R.id.rightMenuBtn);

        actionBarTitle.setText("ALS Support");
        dateActionBarTV.setVisibility(View.VISIBLE);
        rightMenuBtn.setVisibility(View.GONE);
        dateActionBarTV.setBackgroundResource(R.drawable.transparent);
        dateActionBarTV.setTextColor(getResources().getColor(R.color.whiteee));


        actionBarMenuLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabAct.sliderLay.performClick();
            }
        });

        dateActionBarTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabAct.host.setCurrentTab(0);
            }
        });

        // if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
//        if(UILApplication.getInstance().isNightModeEnabled()){
//            supporttMainLay.setBackgroundColor(getResources().getColor(R.color.gray_background));
//        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(SharedPref.IsAOBRD(getActivity())){
            dateActionBarTV.setText(Html.fromHtml("<b><u>AOBRD</u></b>"));
        }else{
            dateActionBarTV.setText(Html.fromHtml("<b><u>ELD</u></b>"));
        }

        JSONArray supportArray = supportMethod.getSavedSupportArray(dbHelper);

        if(supportArray.length() > 0) {
            noDataSupportTV.setVisibility(View.GONE);
            supportListView.setVisibility(View.VISIBLE);

            ParseSupportDetails(supportArray);
        }else{
            noDataSupportTV.setVisibility(View.VISIBLE);
            supportListView.setVisibility(View.GONE);
        }
    }


    private void ParseSupportDetails(JSONArray supportArray){

        List<SupportModel> supportList = new ArrayList<SupportModel>();
        try {
            for(int i = 0 ; i < supportArray.length() ; i++){
                JSONObject supportObj = (JSONObject)supportArray.get(i);
                SupportModel sModel = new SupportModel(
                        supportObj.getString(ConstantsKeys.SupportDetailId),
                        supportObj.getString(ConstantsKeys.SupportKey),
                        supportObj.getString(ConstantsKeys.SupportValue),
                        supportObj.getInt(ConstantsKeys.SupportKeyType),
                        supportObj.getBoolean(ConstantsKeys.SupportIsActive),
                        supportObj.getString(ConstantsKeys.SupportCreateDate)
                );
                supportList.add(sModel);

            }

            if(supportList.size() > 0) {
                supportAdapter = new SupportAdapter(getActivity(), supportList);
                supportListView.setAdapter(supportAdapter);
            }else{
                Toast.makeText(getActivity(), "No record found.", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
