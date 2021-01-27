package com.messaging.logistic.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.adapter.logistic.OtherReviewLogAdapter;
import com.constants.Constants;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestedLogListFragment extends Fragment implements View.OnClickListener {


    View rootView;
    ImageView eldMenuBtn;
    RelativeLayout eldMenuLay, rightMenuBtn;
    ListView suggestedLogListView;
    TextView noDataEldTV, EldTitleTV;
    FragmentManager fragManager;
    Constants constants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.noti_history_fragment, container, false);
            rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } catch (InflateException e) {
            e.printStackTrace();
        }

        initView(rootView);

        return rootView;

    }

    void initView(View view){

        constants           = new Constants();
        noDataEldTV         = (TextView)view.findViewById(R.id.noDataEldTV);
        EldTitleTV          = (TextView)view.findViewById(R.id.EldTitleTV);

        eldMenuBtn          = (ImageView)view.findViewById(R.id.eldMenuBtn);
        eldMenuLay          = (RelativeLayout)view.findViewById(R.id.eldMenuLay);
        rightMenuBtn        = (RelativeLayout)view.findViewById(R.id.rightMenuBtn);
        suggestedLogListView= (ListView)view.findViewById(R.id.notiHistoryListView);

        EldTitleTV.setText(getResources().getString(R.string.review_carrier_edits));


        suggestedLogListView.setDivider(getResources().getDrawable(R.color.gray_divider_edit_log));
        suggestedLogListView.setDividerHeight(constants.intToPixel(getActivity(), 1));
        eldMenuBtn.setImageResource(R.drawable.back_btn);
        rightMenuBtn.setVisibility(View.GONE);
        suggestedLogListView.setVisibility(View.VISIBLE);



        eldMenuLay.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();

        try {

            if(SuggestedFragmentActivity.otherLogList.size() > 0){
                noDataEldTV.setVisibility(View.GONE);
            }else{
                noDataEldTV.setVisibility(View.VISIBLE);
            }

            OtherReviewLogAdapter adapter = new OtherReviewLogAdapter(getContext(), SuggestedFragmentActivity.otherLogList);
            suggestedLogListView.setAdapter(adapter);


        }catch (Exception e){
            e.printStackTrace();
        }
        suggestedLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("itemClick", "itemClick: " + position);

                String suggestedData = "";
                String date = SuggestedFragmentActivity.otherLogList.get(position).getDate();
                try {
                    for (int i = 0; i < SuggestedFragmentActivity.dataArray.length(); i++) {

                        JSONObject obj = (JSONObject)SuggestedFragmentActivity.dataArray.get(i);
                        String selectedDate = obj.getString(ConstantsKeys.DriverLogDate);

                        if (selectedDate.equals(date)) {
                            suggestedData = SuggestedFragmentActivity.dataArray.get(i).toString();

                            break;

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                SuggestedLogFragment logFragment = new SuggestedLogFragment();
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsKeys.suggested_data, suggestedData);
                bundle.putString(ConstantsKeys.Date,  date);
                logFragment.setArguments(bundle);

                fragManager = getFragmentManager();
                FragmentTransaction fragmentTran = fragManager.beginTransaction();
                fragmentTran.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTran.replace(R.id.job_fragment, logFragment);
                fragmentTran.addToBackStack("SuggestedLog");
                fragmentTran.commit();

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.eldMenuLay:
                if(SuggestedFragmentActivity.dataArray.length() > 0 && SharedPref.isSuggestedEditOccur(getActivity())){
                    SharedPref.setSuggestedRecallStatus(false, getActivity());
                }else{
                    SharedPref.setSuggestedRecallStatus(true, getActivity());
                }

                getActivity().finish();
                break;
        }
    }
}
