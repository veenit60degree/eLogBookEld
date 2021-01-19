package com.custom.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.adapter.logistic.OtherOptionsAdapter;
import com.adapter.logistic.OtherReviewLogAdapter;
import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.VolleyRequest;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;
import com.models.OtherOptionsModel;
import com.models.RecapModel;

import java.util.List;
import java.util.Map;

public class OtherReviewLogDialog extends Dialog {

    List<RecapModel> otherLogList;
    ListView otherFeatureListView;
    RelativeLayout otherOptionMainLay;

    private ReviewLogListener logListener;

    public interface ReviewLogListener {
        public void JobBtnReady(String date, int position);

    }


    public OtherReviewLogDialog(@NonNull Context context, List<RecapModel> otherLogList, ReviewLogListener logListener) {
        super(context);
        this.otherLogList = otherLogList;
        this.logListener = logListener;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_other_options);
        setCancelable(true);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        otherFeatureListView = (ListView) findViewById(R.id.otherFeatureListView);
        otherOptionMainLay = (RelativeLayout) findViewById(R.id.otherOptionMainLay);


        OtherReviewLogAdapter adapter = new OtherReviewLogAdapter(getContext(), otherLogList);
        otherFeatureListView.setAdapter(adapter);

        otherFeatureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("itemClick", "itemClick: " + position);

                logListener.JobBtnReady(otherLogList.get(position).getDate(), position);

            }
        });

        otherOptionMainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
