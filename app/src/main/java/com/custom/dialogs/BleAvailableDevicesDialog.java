package com.custom.dialogs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.constants.Constants;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.EldActivity;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.CtPatFragment;
import com.messaging.logistic.fragment.EldFragment;

import java.util.ArrayList;
import java.util.List;

public class BleAvailableDevicesDialog extends Dialog {

    public interface BleDevicesListener {
        public void SelectedDeviceBtn(String selectedDevice);
    }

    private BleDevicesListener readyListener;
    List<String> availableDeviceList;
    ListView supportListView;
    ArrayAdapter<String> dataAdapter;
    private BroadcastReceiver mMessageReceiver = null;


    public BleAvailableDevicesDialog(Context context, List<String> list, BleDevicesListener readyListener) {
        super(context);
        this.readyListener = readyListener;
        this.availableDeviceList = list;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_support);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setCancelable(false);

         supportListView       = (ListView) findViewById(R.id.supportListView);
        TextView headertextView       = (TextView) findViewById(R.id.headertextView);
        headertextView.setText(getContext().getString(R.string.available_obd_dev));


        TextView supportDismissBtn         = (TextView) findViewById(R.id.bleDismissBtn);

         dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_ble_devices,R.id.bleDeviceTxtView, availableDeviceList);
        supportListView.setAdapter(dataAdapter);

        supportListView.setOnItemClickListener(new SelectedDeviceListener());

        supportDismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Log.d("received", "received from service");

                if (intent.hasExtra(ConstantsKeys.BleDataAfterNotify)) {
                    String device = intent.getStringExtra(ConstantsKeys.BleDataAfterNotify);
                    availableDeviceList = new ArrayList<>();
                    String[] deviceArray       = device.split("@@@");

                    for(int i = 0; i < deviceArray.length ; i++){
                        availableDeviceList.add(deviceArray[i]);
                    }

                    dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_ble_devices,R.id.bleDeviceTxtView, availableDeviceList);
                    supportListView.setAdapter(dataAdapter);

                    if(availableDeviceList.size() == 0){
                        dismiss();
                    }
                }
            }
        };



        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter(ConstantsKeys.BleDataNotifier));

    }



    private class SelectedDeviceListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            readyListener.SelectedDeviceBtn(availableDeviceList.get(position));
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
            dismiss();
        }
    }
}