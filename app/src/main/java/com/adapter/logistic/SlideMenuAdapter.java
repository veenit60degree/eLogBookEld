package com.adapter.logistic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.constants.Constants;
import com.constants.SharedPref;
import com.driver.details.DriverConst;
import com.als.logistic.R;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
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
    public long getItemId(int position) {
        //
        if(menuList.size() > position) {
            return getItem(position).hashCode();
        }else{
            return position;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

         if(UILApplication.getInstance().isNightModeEnabled()){
            context.setTheme(R.style.DarkTheme);
        } else {
             context.setTheme(R.style.LightTheme);
        }

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_slide_menu, null);

            holder.menuTitleTxtView = (TextView) convertView.findViewById(R.id.menuTitleTxtView);
            holder.menuBadgeTxtView = (TextView) convertView.findViewById(R.id.menuBadgeTxtView);
            holder.appVerTxtView    = (TextView) convertView.findViewById(R.id.appVerTxtView);

            holder.menuImgView      = (ImageView) convertView.findViewById(R.id.menuImgView);
            holder.menuErrorImgView = (ImageView) convertView.findViewById(R.id.mwnuErrorImgView);

            holder.menuItemLay      = (LinearLayout)convertView.findViewById(R.id.menuItemLay);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if(menuList.size() > position) {
            holder.menuTitleTxtView.setText(menuList.get(position).getTitle());
            holder.menuImgView.setImageResource(menuList.get(position).getIcon());


            int badgeCount = constants.getPendingNotifications(DriverType, notificationPref, coNotificationPref, context);

            int currentTab = TabAct.host.getCurrentTab();
            if (currentTab == menuList.get(position).getStatus()) {
                if(UILApplication.getInstance().isNightModeEnabled()){
                    holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.white));
                    holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    holder.menuItemLay.setBackgroundColor(context.getResources().getColor(R.color.unselect_button));
                } else {
                    holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.color_eld_theme_one), android.graphics.PorterDuff.Mode.MULTIPLY);
                    holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.color_eld_theme_one));
                    holder.menuItemLay.setBackgroundColor(context.getResources().getColor(R.color.white_hover));
                }
            } else {
                if(UILApplication.getInstance().isNightModeEnabled()){
                    holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.white));
                    holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
                    holder.menuItemLay.setBackgroundColor(context.getResources().getColor(R.color.unselect_button));
                } else {
                    holder.menuTitleTxtView.setTextColor(context.getResources().getColor(R.color.gray_unidenfied));
                    holder.menuImgView.setColorFilter(ContextCompat.getColor(context, R.color.slide_menu_default), android.graphics.PorterDuff.Mode.MULTIPLY);
                    holder.menuItemLay.setBackgroundColor(context.getResources().getColor(R.color.whiteee));
                }

                if (menuList.get(position).getStatus() == Constants.VERSION) {
                    holder.menuItemLay.setVisibility(View.GONE);
                    holder.appVerTxtView.setVisibility(View.VISIBLE);
                    String[] versionArray = menuList.get(position).getTitle().split(",");
                    holder.appVerTxtView.setText(versionArray[0] + "\n" + versionArray[1]);
                }
            }


            if (menuList.get(position).getStatus() == Constants.NOTIFICATION_HISTORY) {
                if (badgeCount > 0) {
                    holder.menuBadgeTxtView.setVisibility(View.VISIBLE);
                    holder.menuBadgeTxtView.setText("" + badgeCount);
                } else {
                    boolean isCycleRequest;
                    if (SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {  // If Current driver is Main Driver
                        isCycleRequest = SharedPref.IsCycleRequestMain(context);
                    } else {
                        isCycleRequest = SharedPref.IsCycleRequestCo(context);
                    }

                    if (isCycleRequest) {
                        holder.menuBadgeTxtView.setVisibility(View.VISIBLE);
                        holder.menuBadgeTxtView.setText("1");
                    } else {
                        holder.menuBadgeTxtView.setVisibility(View.GONE);
                    }
                }
            }


            if (menuList.get(position).getStatus() == Constants.DATA_MALFUNCTION) {

                if (SharedPref.isMalfunctionOccur(context) || SharedPref.isDiagnosticOccur(context)||
                        SharedPref.isLocMalfunctionOccur(context) || SharedPref.isEngSyncMalfunction(context) ||
                        SharedPref.isEngSyncDiagnstc(context) ) {
                      holder.menuErrorImgView.setVisibility(View.VISIBLE);
                } else {
                    holder.menuErrorImgView.setVisibility(View.GONE);
                }

            }

            if (menuList.get(position).getStatus() == Constants.UNIDENTIFIED_RECORD) {
                if (SharedPref.isUnidentifiedOccur(context)) {
                    holder.menuErrorImgView.setVisibility(View.VISIBLE);
                } else {
                    holder.menuErrorImgView.setVisibility(View.GONE);
                }
            }

            if (menuList.get(position).getStatus() == Constants.SETTINGS) {
                holder.menuErrorImgView.setVisibility(View.GONE);

                /*boolean isHaulExcptn ;
                boolean isAdverseExcptn;
                if(DriverType == Constants.MAIN_DRIVER_TYPE) {
                    isHaulExcptn        = SharedPref.get16hrHaulExcptn(context);
                    isAdverseExcptn     = SharedPref.getAdverseExcptn(context);
                }else{
                    isHaulExcptn        = SharedPref.get16hrHaulExcptnCo(context);
                    isAdverseExcptn     = SharedPref.getAdverseExcptnCo(context);
                }

                if(isHaulExcptn || isAdverseExcptn){
                    holder.menuErrorImgView.setVisibility(View.VISIBLE);
                }else {
                    holder.menuErrorImgView.setVisibility(View.GONE);
                }*/
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

    @Override
    public boolean hasStableIds()
    {
        return false;
    }



    public class ViewHolder {
        TextView menuTitleTxtView, menuBadgeTxtView, appVerTxtView;
        ImageView menuImgView, menuErrorImgView;
        LinearLayout menuItemLay;
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

}