package com.messaging.logistic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.adapter.logistic.EditedLogAdapter;
import com.messaging.logistic.EditedLogActivity;
import com.messaging.logistic.R;

import org.json.JSONArray;

public class EditedLogFragment extends Fragment {

    View rootView;
    EditedLogActivity editedLogActivity;
    String DriverId, DeviceId;
    int offsetFromUTC;
    TextView statusEditedTxtView , startTimeEditedTxtView, endTimeEditedTxtView, durationEditedTxtView;
    ListView editLogListView;
    WebView editLogWebView;
    LinearLayout editedItemMainLay, editedLogMainLay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.webview_log_preview, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        editLogWebView           = (WebView)rootView.findViewById(R.id.previewLogWebView);
        editLogListView          = (ListView)rootView.findViewById(R.id.editLogListView);
        statusEditedTxtView      = (TextView)rootView.findViewById(R.id.statusEditedTxtView);
        startTimeEditedTxtView   = (TextView)rootView.findViewById(R.id.startTimeEditedTxtView);
        endTimeEditedTxtView     = (TextView)rootView.findViewById(R.id.endTimeEditedTxtView);
        durationEditedTxtView    = (TextView)rootView.findViewById(R.id.durationEditedTxtView);

        editedItemMainLay        = (LinearLayout)rootView.findViewById(R.id.editedItemMainLay);
        editedLogMainLay         = (LinearLayout)rootView.findViewById(R.id.editedLogMainLay);

        statusEditedTxtView.setTextAppearance(getActivity(), R.style.edit_text_style_bold);
        startTimeEditedTxtView.setTextAppearance(getActivity(), R.style.edit_text_style_bold);
        endTimeEditedTxtView.setTextAppearance(getActivity(), R.style.edit_text_style_bold);
        durationEditedTxtView.setTextAppearance(getActivity(), R.style.edit_text_style_bold);

        editedLogActivity   = new EditedLogActivity();
        DeviceId            = editedLogActivity.sharedPref.GetSavedSystemToken(getActivity());
        DriverId            = editedLogActivity.sharedPref.getDriverId( getActivity());
        offsetFromUTC       = (int) editedLogActivity.globally.GetTimeZoneOffSet();


        editedLogActivity.LoadDataOnWebView(editLogWebView, EditedLogActivity.editedLogArray, EditedLogActivity.LogDate, true);

        if(EditedLogActivity.editedLogList.size() > 0) {
            EditedLogAdapter adapter = new EditedLogAdapter(getActivity(), EditedLogActivity.editedLogList);
            editLogListView.setAdapter(adapter);

            SetCertifyListViewHeight();
        }

        return rootView;
    }



    void SetCertifyListViewHeight(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int DividerHeigh = editedLogActivity.constants.intToPixel( getActivity(), editLogListView.getDividerHeight() );
                    int itemLayoutHeight = editedItemMainLay.getHeight();
                    int listSize     = EditedLogActivity.editedLogList.size() ;
                    int DriverLogListHeight      = itemLayoutHeight + ((itemLayoutHeight + DividerHeigh ) * listSize) + 50;
                    editLogListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DriverLogListHeight ));

                    Log.d("ViewHeight", "DriverLogListHeight: " + DriverLogListHeight);
                }catch (Exception e){}

            }
        }, 200);

    }



}