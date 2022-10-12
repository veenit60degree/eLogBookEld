package com.messaging.logistic.fragment;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.logistic.WIredObdAdapter;
import com.constants.Logger;
import com.constants.SharedPref;
import com.local.db.ShipmentHelperMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import org.json.JSONArray;

import java.util.Timer;
import java.util.TimerTask;

public class ObdConfigFragment  extends Fragment implements View.OnClickListener {


    View rootView;
    long MIN_TIME_BW_UPDATES = 10000;  // 2 Sec
    ListView obdDataListView;
    ImageView clearDataBtn, eldMenuBtn;
    TextView EldTitleTV;
    RelativeLayout rightMenuBtn;
    RelativeLayout eldMenuLay;


    MyTimerTask timerTask;
    private Timer mTimer;

    WIredObdAdapter adapter;
    ShipmentHelperMethod method;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_wired_obd, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }


        initView(rootView);

        return rootView;
    }



    void initView(View v) {

        method = new ShipmentHelperMethod();
        obdDataListView = (ListView) v.findViewById(R.id.obdDataListView);

        clearDataBtn    = (ImageView)v.findViewById(R.id.clearDataBtn);

        rightMenuBtn    = (RelativeLayout) v.findViewById(R.id.rightMenuBtn);
        eldMenuLay      = (RelativeLayout)v.findViewById(R.id.eldMenuLay);
        eldMenuBtn      = (ImageView)v.findViewById(R.id.eldMenuBtn);
        EldTitleTV      = (TextView) v.findViewById(R.id.EldTitleTV);


        rightMenuBtn.setVisibility(View.INVISIBLE);
        eldMenuBtn.setImageResource(R.drawable.back_white);
        EldTitleTV.setText(getResources().getString(R.string.wired_obd_details));



        clearDataBtn.setOnClickListener(this);
        eldMenuLay.setOnClickListener(this);

        setAdapter();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.clearDataBtn:
                Globally.OBD_DataArray = new JSONArray();
                setAdapter();
                Toast.makeText(getActivity(), "Data is cleared in list but still saved in als_obd_log.txt file inside storage.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.eldMenuLay:
                getParentFragmentManager().popBackStack();
                break;


        }

    }


    @Override
    public void onResume() {
        super.onResume();
        RestartTimer();
    }


    @Override
    public void onPause() {
        super.onPause();
        clearTimer();
    }






    private class MyTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            Logger.LogError("Log", "----TimerTask Running");

            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    void setAdapter(){
        try{

            //   if(Globally.OBD_DataArray.length() > 0){
            JSONArray reverseArray = method.ReverseArray(Globally.OBD_DataArray);
            adapter = new WIredObdAdapter(getActivity(), reverseArray);
            obdDataListView.setAdapter(adapter);
            //   }

        }catch (Exception e){  }
    }


    private void RestartTimer() {
        try {
            clearTimer();
            mTimer = new Timer();
            timerTask = new MyTimerTask();
            mTimer.schedule(timerTask, MIN_TIME_BW_UPDATES, MIN_TIME_BW_UPDATES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    void clearTimer() {
        try {
            if (mTimer != null) {
                mTimer.cancel();
                timerTask.cancel();
                mTimer = null;
                timerTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}