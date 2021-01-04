package com.custom.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adapter.logistic.RecapRecordSignAdapter;
import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.models.RecapSignModel;
import com.simplify.ink.InkView;

import org.joda.time.DateTime;

import java.util.List;

public class SignRecordDialog extends Dialog {

    public interface DateSelectListener {
        public void SignOkBtn(DateTime dateTime, boolean IsSigned);
    }

    private DateSelectListener readyListener;
    ListView signRecordListView;
    List<RecapSignModel> recapRecordsList;
    private RecapRecordSignAdapter recapSignAdapter;
    Context context;
    Constants constants;

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
            getWindow().setLayout(constants.intToPixel(context, 530), ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        signRecordListView         = (ListView) findViewById(R.id.signRecordListView);

        recapSignAdapter = new RecapRecordSignAdapter(context, recapRecordsList);
        signRecordListView.setAdapter(recapSignAdapter);

        signRecordListView.setOnItemClickListener(new SignOkListener());

    }






    private class SignOkListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            readyListener.SignOkBtn(
                    recapRecordsList.get(position).getDate(),
                    recapRecordsList.get(position).isCertified());

        }
    }





}
