package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.logistic.RecapRecordSignAdapter;
import com.adapter.logistic.UnIdentifiedListingAdapter;
import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.RecapSignModel;
import com.simplify.ink.InkView;

import org.joda.time.DateTime;

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

    private RecapRecordSignAdapter recapSignAdapter;
    Context context;
    Constants constants;
    public static int recapSelectedPosition = 0;
    public  static boolean isSignItemClicked = false;
    ArrayList<String> recordSelectedList = new ArrayList<>();

    public SignRecordDialog(Context context, List<RecapSignModel> recapList, DateSelectListener readyListener) {
        super(context);
        this.context = context;
        recapRecordsList = recapList;
        this.readyListener = readyListener;
        constants = new Constants();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.sign_record_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(Globally.isTablet(context)){
            getWindow().setLayout(constants.intToPixel(context, 730), ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            getWindow().setLayout(constants.intToPixel(context, 550), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        signRecordListView      = (ListView) findViewById(R.id.signRecordListView);
        selectAllRecordsCheckBox= (CheckBox) findViewById(R.id.selectAllRecordsCheckBox);
        fromToDateTv            = (TextView)findViewById(R.id.fromToDateTv);

        certifyRecordBtn        = (TextView)findViewById(R.id.certifyRecordTv);
        recapRecordInvisibleTv  = (TextView)findViewById(R.id.recapRecordInvisibleTv);

        if(recapRecordsList.size() > 0) {
            String fromDate = Globally.dateConversionMonthNameWithDay(recapRecordsList.get(0).getDate().toString());
            String toDate   = Globally.dateConversionMonthNameWithDay(recapRecordsList.get(recapRecordsList.size()-1).getDate().toString());

            fromToDateTv.setText(fromDate + " - " + toDate);
        }

        setListSelectionRecord(false);
        recapSignAdapter = new RecapRecordSignAdapter(context, recapRecordsList, recordSelectedList,false, false);
        signRecordListView.setAdapter(recapSignAdapter);

        // temp hide label
        selectAllRecordsCheckBox.setVisibility(View.GONE);
        certifyRecordBtn.setVisibility(View.GONE);

        selectAllRecordsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(!isSignItemClicked) {
                    boolean isAllSelected = compoundButton.isChecked();
                    setListSelectionRecord(isAllSelected);
                    Parcelable state = signRecordListView.onSaveInstanceState();
                    signRecordListView.onRestoreInstanceState(state);
                   // notifyAdapter(isAllSelected, true);

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

                ArrayList<String> selectedDateList = getSelectedItemDate();

            }
        });
    }




    private void setListSelectionRecord(boolean isSelected){
        Log.d("recordSelectedList", "recordSelectedList: " + recordSelectedList);

        if(recordSelectedList.size() > 0) {
            boolean isItemChecked = false;
            for (int i = 0; i < recapRecordsList.size(); i++) {
                if (isSelected) {
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
            boolean isItemChecked = false;
            for (int i = 0; i < recordSelectedList.size(); i++) {
                if (recordSelectedList.get(i).equals("selected")) {
                    selectedDateList.add(recapRecordsList.get(i).getDate().toString());
                }
            }

        }

        return selectedDateList;
    }


}
