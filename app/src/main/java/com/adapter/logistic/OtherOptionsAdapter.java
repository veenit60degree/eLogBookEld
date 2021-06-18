package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.messaging.logistic.R;
import com.models.OtherOptionsModel;

import java.util.List;

public class OtherOptionsAdapter extends BaseAdapter {

    Context context;
    SharedPref sharedPref;
    LayoutInflater mInflater;
    List<OtherOptionsModel> LogList;
    boolean isPendingNotification, isGps, isCycleRequest;
    int pendingNotificationCount, DriverType;

    public OtherOptionsAdapter(Context context, boolean isPendingNotification, int pendingNotificationCount, boolean isGps, List<OtherOptionsModel> logList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.isPendingNotification = isPendingNotification;
        this.pendingNotificationCount = pendingNotificationCount;
        this.isGps = isGps;
        LogList = logList;
        sharedPref = new SharedPref();

        if (sharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
            DriverType = Constants.MAIN_DRIVER_TYPE;
            isCycleRequest = sharedPref.IsCycleRequestMain(context);
        }else{
            DriverType = Constants.CO_DRIVER_TYPE;
            isCycleRequest = sharedPref.IsCycleRequestCo(context);
        }
    }


    @Override
    public int getCount() {
        return LogList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return LogList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final OtherOptionsModel LogItem = (OtherOptionsModel) getItem(position);


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_other_options, null);


            holder.otherFeatureImgView = (ImageView) convertView.findViewById(R.id.otherFeatureImgView);
            holder.otherFeatureTxtView = (TextView) convertView.findViewById(R.id.otherFeatureTxtView);
            holder.notiBadgeView       = (TextView) convertView.findViewById(R.id.notiBadgeView);
            holder.errorOtherView      = (TextView) convertView.findViewById(R.id.errorOtherView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.otherFeatureImgView.setImageResource(LogItem.getDrawable());
        holder.otherFeatureTxtView.setText(LogItem.getTitle());

        updateView(LogItem.getStatus(), holder.otherFeatureImgView, holder.otherFeatureTxtView, holder.errorOtherView, holder.notiBadgeView);

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    void makeViewHighlighed(ImageView otherFeatureImgView, TextView otherFeatureTxtView, TextView errorOtherView){
        otherFeatureImgView.setColorFilter(ContextCompat.getColor(context, R.color.red_custom), android.graphics.PorterDuff.Mode.SRC_IN);
        otherFeatureTxtView.setTextColor(context.getResources().getColor(R.color.red_custom));
        errorOtherView.setVisibility(View.VISIBLE);
    }

    void updateView(int status, ImageView imgView, TextView titleView, TextView errorView, TextView notiBadgeView){
        switch (status){

            case Constants.NOTIFICATION:
                if(isPendingNotification || isCycleRequest){
                    imgView.setColorFilter(ContextCompat.getColor(context, R.color.red_custom), android.graphics.PorterDuff.Mode.SRC_IN);
                    titleView.setTextColor(context.getResources().getColor(R.color.red_custom));
                    notiBadgeView.setVisibility(View.VISIBLE);
                    if(pendingNotificationCount == 0 && isCycleRequest){
                        pendingNotificationCount = 1;
                    }
                    notiBadgeView.setText(""+pendingNotificationCount);
                }

                break;

            case Constants.GPS:
                if(!isGps){
                    makeViewHighlighed(imgView, titleView, errorView);
                }
                break;

            case Constants.MALFUNCTION:
                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                    if (sharedPref.isMalfunctionOccur(context) || sharedPref.isDiagnosticOccur(context)||
                            sharedPref.isLocMalfunctionOccur(context) || sharedPref.isEngSyncMalfunction(context) ||
                            sharedPref.isEngSyncDiagnstc(context) ) {
                        makeViewHighlighed(imgView, titleView, errorView);
                    }
                }else{
                    if (sharedPref.isMalfunctionOccurCo(context) || sharedPref.isDiagnosticOccurCo(context) ||
                            sharedPref.isLocMalfunctionOccur(context) || sharedPref.isEngSyncMalfunction(context) ||
                            sharedPref.isEngSyncDiagnstc(context) ) {
                        makeViewHighlighed(imgView, titleView, errorView);
                    }
                }

                break;

            case Constants.UNIDENTIFIED:
                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                    if (sharedPref.isUnidentifiedOccur(context)) {
                        makeViewHighlighed(imgView, titleView, errorView);
                    }
                }else{
                    if (sharedPref.isUnidentifiedOccurCo(context)) {
                        makeViewHighlighed(imgView, titleView, errorView);
                    }
                }
                break;

            case Constants.SUGGESTED_LOGS:
                boolean isSuggestedOccur = false;
                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                    isSuggestedOccur = sharedPref.isSuggestedEditOccur(context);
                }else{
                    isSuggestedOccur = sharedPref.isSuggestedEditOccurCo(context);
                }

                if(isSuggestedOccur){
                    makeViewHighlighed(imgView, titleView, errorView);
                }else{
                    imgView.setColorFilter(ContextCompat.getColor(context, R.color.gray_other_feature), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;

            case Constants.OBD:
               /* if(sharedPref.getObdStatus(context) != Constants.WIFI_CONNECTED && sharedPref.getObdStatus(context) != Constants.WIRED_CONNECTED &&
                        sharedPref.getObdStatus(context) != Constants.BLE_CONNECTED){
                    makeViewHighlighed(imgView, titleView, errorView);
                }*/
                break;

        }
    }

    public class ViewHolder {
        TextView otherFeatureTxtView, notiBadgeView, errorOtherView;
        ImageView otherFeatureImgView;

    }


}