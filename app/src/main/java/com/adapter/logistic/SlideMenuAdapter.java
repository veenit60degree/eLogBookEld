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
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;
import com.models.SlideMenuModel;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.NotificationPref;

import java.util.List;

public class SlideMenuAdapter extends BaseAdapter {


    Context context;
    LayoutInflater mInflater;
    List<SlideMenuModel> menuList;
    Constants constants;
    int DriverType;
    NotificationPref notificationPref;
    CoNotificationPref coNotificationPref;


    public SlideMenuAdapter(Context context, List<SlideMenuModel> list, int DriverType) {
        this.context = context;
        this.menuList = list;
        this.DriverType = DriverType;

        mInflater = LayoutInflater.from(context);
        notificationPref        = new NotificationPref();
        coNotificationPref      = new CoNotificationPref();
        constants = new Constants();

    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_slide_menu, null);

            holder.menuTitleTxtView = (TextView) convertView.findViewById(R.id.menuTitleTxtView);
            holder.menuBadgeTxtView = (TextView) convertView.findViewById(R.id.menuBadgeTxtView);
            holder.menuImgView      = (ImageView) convertView.findViewById(R.id.menuImgView);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.menuTitleTxtView.setText(menuList.get(position).getTitle());
        holder.menuImgView.setImageResource(menuList.get(position).getIcon());


        int badgeCount = constants.getPendingNotifications(DriverType, notificationPref , coNotificationPref, context);

        int currentTab = TabAct.host.getCurrentTab();
        if(currentTab == menuList.get(position).getStatus()){

            holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.color_eld_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.color_eld_theme));
        }else{
            holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.slide_menu_default), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.gray_unidenfied));
        }


        if(badgeCount > 0){
            if(menuList.get(position).getStatus() == Constants.NOTIFICATION_HISTORY){
                holder.menuBadgeTxtView.setVisibility(View.VISIBLE);
                holder.menuBadgeTxtView.setText(""+badgeCount);
            }else{
                holder.menuBadgeTxtView.setVisibility(View.GONE);
            }
        }

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

    public class ViewHolder {
        TextView menuTitleTxtView, menuBadgeTxtView;
        ImageView menuImgView;
    }

    /*        host.addTab(eld_spec);        0
        host.addTab(setting_spec);      1
        host.addTab(blank_spec);        2
        host.addTab(history_spec);      3
        host.addTab(inspection_spec);   4
        host.addTab(odometer_spec);     5
        host.addTab(support_spec);      6
        host.addTab(shipping_spec);     7
        host.addTab(ctPatSpec);         8
        host.addTab(obdConfigSpec);     9
        host.addTab(eldDocSpec);        10
        host.addTab(unidentifiedSpec);  11
        host.addTab(malfunctionSpec);   12

        */

    void highlightedCurrentView( ImageView imgView, TextView txtView){
        int currentTab = TabAct.host.getCurrentTab();
        switch (currentTab){
            case Constants.ELD_HOS:
                imgView.setColorFilter(ContextCompat.getColor(context, R.color.color_eld_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                txtView.setTextColor(context.getResources().getColor(R.color.color_eld_theme));
                break;

            case Constants.PTI_INSPECTION:

                break;

            case Constants.CT_PAT_INSPECTION:

                break;

            case Constants.ODOMETER_READING:

                break;

            case Constants.NOTIFICATION_HISTORY:

                break;

            case Constants.SHIPPING_DOC:

                break;

            case Constants.ELD_DOC:

                break;

            case Constants.UNIDENTIFIED_RECORD:

                break;

            case Constants.DATA_MALFUNCTION:

                break;

            case Constants.SETTINGS:
                imgView.setColorFilter(ContextCompat.getColor(context, R.color.color_eld_theme), android.graphics.PorterDuff.Mode.MULTIPLY);
                txtView.setTextColor(context.getResources().getColor(R.color.color_eld_theme));
                break;

            case Constants.ALS_SUPPORT:

                break;

            case Constants.LOGOUT:

                break;
        }
    }
}