package com.messaging.logistic;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.constants.CheckIsUpdateReady;
import com.constants.Constants;
import com.constants.ConstantsEnum;
import com.constants.SharedPref;
import com.constants.Slidingmenufunctions;
import com.constants.UrlResponce;
import com.constants.Utils;
import com.custom.dialogs.AppUpdateDialog;
import com.driver.details.DriverConst;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;

public class TabAct extends TabActivity implements View.OnClickListener {


    //TabHost.TabSpec chat_spec;
    public static TabHost host;
    FrameLayout tabcontent;
    Constants constants;
    public static SlidingMenu smenu;
    public static RelativeLayout sliderLay;
    public static Button wiredObdDataBtn, dayNightBtn, openUpdateDialogBtn;
    TextView noObdConnTV;
    String WiredOBD     = "wired_obd";
    String WifiOBD      = "wifi_obd";
    Slidingmenufunctions slideMenu;
    Intent inState;
    private PowerManager.WakeLock wl;
    PowerManager pm;
    int DriverType = 0, animCount = 0;
    boolean IsTablet = false;
    boolean isPlayStoreDownload = false;
    public static boolean isUpdateDirectly = false;
    DBHelper dbHelper;
    HelperMethods hMethods;
    SharedPref sharedPref;

    private BroadcastReceiver mMessageReceiver = null;
    Animation fadeInAnim, fadeOutAnim;
    AppUpdateDialog appUpdateDialog;
    Utils util;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        UILApplication.getInstance().setTheme();

        setContentView(R.layout.chat_friend_list);

        util = new Utils(this);
        sharedPref    = new SharedPref();
        dbHelper      = new DBHelper(this);
        hMethods      = new HelperMethods();
        constants     = new Constants();

        IsTablet = Globally.isTablet(this);

        tabcontent = (FrameLayout)findViewById(android.R.id.tabcontent);
        wiredObdDataBtn = (Button)findViewById(R.id.wiredObdDataBtn);
        dayNightBtn = (Button)findViewById(R.id.dayNightBtn);
        openUpdateDialogBtn = (Button)findViewById(R.id.openUpdateDialogBtn);
        noObdConnTV = (TextView)findViewById(R.id.noObdConnTV);
        sliderLay = (RelativeLayout)findViewById(R.id.sliderLay);
        sliderLay.setVisibility(View.GONE);

        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeInAnim.setDuration(2000);
        fadeOutAnim.setDuration(2000);

        TabDeclaration();

        try {
            util.createAppUsageLogFile();
        }catch (Exception e){
            e.printStackTrace();
        }

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                  Log.d("received", "received from service");
                boolean isSuggestedEdit = intent.getBooleanExtra(ConstantsKeys.SuggestedEdit, false);
                boolean isFreshLogin    = sharedPref.GetNewLoginStatus(TabAct.this);
                if(isSuggestedEdit && isFreshLogin == false){
                    Intent i = new Intent(TabAct.this, SuggestedFragmentActivity.class);
                    i.putExtra(ConstantsKeys.suggested_data, "");
                    i.putExtra(ConstantsKeys.Date, "");
                    startActivity(i);

                }

            }
        };


        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (animCount < 12) {
                    noObdConnTV.startAnimation(fadeInAnim);
                }else {
                    fadeInAnim.cancel();
                    noObdConnTV.startAnimation(fadeOutAnim);
                    noObdConnTV.setVisibility(View.GONE);

                }

                animCount++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        // Check app version
        if (!sharedPref.GetNewLoginStatus(this)) {
            if(!sharedPref.GetUpdateAppDialogTime(this).equals(Globally.GetCurrentDeviceDate())){
                getAppVersion();
            }

        }


        if(sharedPref.getCurrentDriverType(TabAct.this).equals(DriverConst.StatusSingleDriver)){
            DriverType = 0;
        }else{
            DriverType = 1;
        }


        /*==================== Left Slide Menu  =====================*/
        setSlidingMenu();
        slideMenu = new Slidingmenufunctions(smenu, TabAct.this, DriverType);

        smenu.addIgnoredView(tabcontent);

        if(slideMenu.usernameTV != null) {
            String MainDriverName = "", CoDriverName = "";
            MainDriverName  = DriverConst.GetDriverDetails( DriverConst.DriverName, TabAct.this);
            CoDriverName    = DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, TabAct.this);

            if(DriverType == 0){
                slideMenu.MainDriverView(TabAct.this);      //MainDriverBtn.performClick();
                slideMenu.usernameTV.setText(MainDriverName);
            }else{
                DriverType = 1;
                slideMenu.CoDriverView(TabAct.this, false);      //CoDriverView(TabAct.this, false);
                slideMenu.usernameTV.setText(CoDriverName);
            }
            if(sharedPref.getDriverType(TabAct.this).equals(DriverConst.SingleDriver)){
                slideMenu.driversLayout.setVisibility(View.GONE);
                slideMenu.usernameTV.setVisibility(View.VISIBLE);
            }else{
                slideMenu.driversLayout.setVisibility(View.VISIBLE);
                slideMenu.usernameTV.setVisibility(View.GONE);
            }

            slideMenu.MainDriverBtn.setText(MainDriverName);
            slideMenu.CoDriverBtn.setText(CoDriverName);

        }


        inState = getIntent();
        if(inState.hasExtra("keyDriverJob")) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Globally.NOTIFICATIONS_ID[1]);
        }

        Intent i = getIntent();
        if(i.hasExtra("EXIT")){
            if(i.getBooleanExtra("EXIT", false)){
                // Log.d("true", "---true: " );
                finish();
            }
        }


        wiredObdDataBtn.setOnClickListener(this);
        sliderLay.setOnClickListener(this);
        dayNightBtn.setOnClickListener(this);
        openUpdateDialogBtn.setOnClickListener(this);


    }


    private void setSlidingMenu() {
        if(getApplicationContext() != null) {
            try {

                smenu = new SlidingMenu(getApplicationContext());
                smenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                smenu.setShadowWidthRes(R.dimen.shadow_width);
                smenu.setShadowDrawable(R.drawable.shadow);
                smenu.setBehindOffsetRes(R.dimen.sliding_offset);
                smenu.setBehindOffsetRes(R.dimen.sliding_offset);
                if(Globally.isTablet(getApplicationContext())) {
                    smenu.setBehindWidth(constants.intToPixel(getApplicationContext(), 560));
                }else{
                    smenu.setBehindWidth(constants.intToPixel(getApplicationContext(), 320));
                }
                smenu.setFadeDegree(0.35f);
                smenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
                smenu.setMenu(R.layout.slide_menu);


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void TabDeclaration() {

        /* Friend_list */
        host = getTabHost();
        TabWidget tab1 = (TabWidget) findViewById(android.R.id.tabs);
        Intent intent;

        TabHost.TabSpec eld_spec = host.newTabSpec("eld_tab");
        eld_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, EldActivity.class);
        eld_spec.setContent(intent);


        TabHost.TabSpec setting_spec = host.newTabSpec("setting_tab");
        setting_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, SettingActivity.class);
        setting_spec.setContent(intent);

        TabHost.TabSpec blank_spec = host.newTabSpec("blank_tab");
        blank_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, BlankActivity.class);
        blank_spec.setContent(intent);

        TabHost.TabSpec history_spec = host.newTabSpec("history");
        history_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, NotificationHistoryActivity.class);
        history_spec.setContent(intent);

        TabHost.TabSpec shipping_spec = host.newTabSpec("shipping");
        shipping_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, ShippingLogActivity.class);
        shipping_spec.setContent(intent);


        TabHost.TabSpec inspection_spec = host.newTabSpec("inspection_tab");
        inspection_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, PrePostTripInspActivity.class);
        inspection_spec.setContent(intent);

        TabHost.TabSpec odometer_spec = host.newTabSpec("odometer_tab");
        odometer_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, OdometerActivity.class);
        odometer_spec.setContent(intent);

        TabHost.TabSpec support_spec = host.newTabSpec("support_tab");
        support_spec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, AlsSupportActivity.class);
        support_spec.setContent(intent);


        TabHost.TabSpec ctPatSpec = host.newTabSpec("ct_pat_tab");
        ctPatSpec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, CtPatActivity.class);
        ctPatSpec.setContent(intent);


        TabHost.TabSpec obdConfigSpec = host.newTabSpec("obd_config_tab");
        obdConfigSpec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, ObdConfigActivity.class);
        obdConfigSpec.setContent(intent);


        TabHost.TabSpec eldDocSpec = host.newTabSpec("eld_doc_tab");
        eldDocSpec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, EldDocumentActivity.class);
        eldDocSpec.setContent(intent);


        TabHost.TabSpec unidentifiedSpec = host.newTabSpec("uniden_spec_tab");
        unidentifiedSpec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, UnidentifiedActivity.class);
        unidentifiedSpec.setContent(intent);


        TabHost.TabSpec malfunctionSpec = host.newTabSpec("malfunction_spec_tab");
        malfunctionSpec.setIndicator("", getResources().getDrawable(R.drawable.als_logo));
        intent = new Intent(TabAct.this, MalfunctionActivity.class);
        malfunctionSpec.setContent(intent);



        // Add Tab specs in TabHost
        host.addTab(eld_spec);
        host.addTab(setting_spec);
        host.addTab(blank_spec);
        host.addTab(history_spec);
        host.addTab(inspection_spec);
        host.addTab(odometer_spec);
        host.addTab(support_spec);
        host.addTab(shipping_spec);
        host.addTab(ctPatSpec);
        host.addTab(obdConfigSpec);
        host.addTab(eldDocSpec);
        host.addTab(unidentifiedSpec);
        host.addTab(malfunctionSpec);

        host.getTabWidget().setVisibility(View.GONE);
        Globally.hideSoftKeyboard(TabAct.this);


    }


    @SuppressLint("InvalidWakeLockTag")
    void ActiveScreen(){
        try{
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
            wl.acquire();
        }catch (Exception e){e.printStackTrace();}
    }


    public Bitmap TakeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }



    @Override
    protected void onResume() {
        super.onResume();

        if(Globally.isWifiOrMobileDataEnabled(TabAct.this)){
            Constants.IsAlsServerResponding = true;
        }else{
            Constants.IsAlsServerResponding = false;
        }

        UILApplication.activityResumed();
        ActiveScreen();

        // save app display status log
        constants.saveAppUsageLog(ConstantsEnum.StatusForeground, false, false, util);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(ConstantsKeys.SuggestedEdit));

    }


    @Override
    protected void onPause() {
        super.onPause();
        UILApplication.activityPaused();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

    }


    protected void onStop() {
        super.onStop();

        UILApplication.activityPaused();
        Globally.IS_LOGOUT = false;

        // save app display status log
        constants.saveAppUsageLog(ConstantsEnum.StatusBackground, false, false, util);

        try{
            if(wl != null)
                wl.release();
        }catch (Exception e){e.printStackTrace();}


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.wiredObdDataBtn:
                //  StartStopServer(WifiOBD);
                break;

            case R.id.sliderLay:
                Globally.hideSoftKeyboard(TabAct.this);
                smenu.showMenu();
                smenu.addIgnoredView(tabcontent);

                slideMenu.menuAdapter.notifyDataSetInvalidated();

                break;

            case R.id.dayNightBtn:
                if (UILApplication.getInstance().isNightModeEnabled()){
                    UILApplication.getInstance().setIsNightModeEnabled(false);
                } else {
                    UILApplication.getInstance().setIsNightModeEnabled(true);
                }

                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
                break;


            case R.id.openUpdateDialogBtn:
                getAppVersion();
                break;
        }
    }




    private boolean isUpdateAvailable(String responseVersion) {

        boolean isUpdateAvailable = false;
        String currentVersion = Globally.GetAppVersion(getApplicationContext(), "VersionName");

        if (!currentVersion.equals(responseVersion)) {
            String[] updatedVersionArray = responseVersion.split("\\.");
            String[] installedVersionArray = currentVersion.split("\\.");

            for (int i = 0; i < updatedVersionArray.length; i++) {
                int storedVersionName = Integer.valueOf(updatedVersionArray[i]);
                int installedVersionName = -1;
                if (i < installedVersionArray.length) {
                    installedVersionName = Integer.valueOf(installedVersionArray[i]);
                }

                if (storedVersionName > installedVersionName) {
                    isUpdateAvailable = true;
                    break;
                }else{
                    if (installedVersionName > storedVersionName) {
                        break;
                    }
                }
            }

        }

        return isUpdateAvailable;

    }



    private void getAppVersion(){
        try {
            new CheckIsUpdateReady("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en", new UrlResponce() {
                @Override
                public void onReceived(String responseVersion) {
                    Log.d("responseStr", "responseStr: " + responseVersion);

                    if (isUpdateAvailable(responseVersion)) {
                        String playStorePackage = "com.android.vending";
                        isPlayStoreDownload = constants.appInstalledOrNot(playStorePackage, TabAct.this);
                        try {
                            if (appUpdateDialog != null && appUpdateDialog.isShowing()) {
                                appUpdateDialog.dismiss();
                            }

                            appUpdateDialog = new AppUpdateDialog(TabAct.this, isPlayStoreDownload, responseVersion, host, sharedPref);
                            appUpdateDialog.show();

                        } catch (final IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }).execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
