package com.messaging.logistic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.adapter.logistic.EditedLogAdapter;
import com.constants.SharedPref;
import com.messaging.logistic.R;

public class EditedLogConfirmationFragment extends Fragment {

    View rootView;
    SuggestedLogFragment suggestedLogFragment;
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

        suggestedLogFragment = new SuggestedLogFragment();
        DeviceId            = SharedPref.GetSavedSystemToken(getActivity());
        DriverId            = SharedPref.getDriverId( getActivity());
        offsetFromUTC       = (int) suggestedLogFragment.globally.GetTimeZoneOffSet();


        suggestedLogFragment.LoadDataOnWebView(editLogWebView, SuggestedLogFragment.editedLogArray, SuggestedLogFragment.LogDate, true);

        if(SuggestedLogFragment.editedLogList.size() > 0) {
            EditedLogAdapter adapter = new EditedLogAdapter(getActivity(), SuggestedLogFragment.editedLogList);
            editLogListView.setAdapter(adapter);

            SetCertifyListViewHeight();
        }

        return rootView;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }


    void SetCertifyListViewHeight(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    int DividerHeigh = suggestedLogFragment.constants.intToPixel( getActivity(), editLogListView.getDividerHeight() );
                    int itemLayoutHeight = editedItemMainLay.getHeight();
                    int listSize     = SuggestedLogFragment.editedLogList.size() ;
                    int DriverLogListHeight      = itemLayoutHeight + ((itemLayoutHeight + DividerHeigh ) * listSize) + 50;
                    editLogListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DriverLogListHeight ));

                    Log.d("ViewHeight", "DriverLogListHeight: " + DriverLogListHeight);
                }catch (Exception e){}

            }
        }, 200);

    }



}