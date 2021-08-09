package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.messaging.logistic.R;

import java.util.ArrayList;

public class DotOtherOptionDialog extends Dialog {

    ListView otherFeatureListView;
    OtherOptionDotListener otherOptionDotListener;

    public interface OtherOptionDotListener {
        public void ItemClickReady(int position);
    }

    public DotOtherOptionDialog(Context context, OtherOptionDotListener otherOptionDotListener){
        super(context);
        this.otherOptionDotListener = otherOptionDotListener;
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

        ArrayList<String> list = new ArrayList<>();
        list.add(getContext().getString(R.string.view_inspections));
        list.add(getContext().getString(R.string.Send_Log));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),R.layout.item_other_options, R.id.otherFeatureTxtView, list);

        otherFeatureListView.setAdapter(arrayAdapter);

        RelativeLayout otherOptionMainLay = (RelativeLayout)findViewById(R.id.otherOptionMainLay);
        otherOptionMainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        otherFeatureListView.setOnItemClickListener(new CancelBtnListener());
    }


    private class CancelBtnListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            otherOptionDotListener.ItemClickReady(position);
            dismiss();
        }
    }


}
