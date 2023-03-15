package com.constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.adapter.logistic.SlideMenuAdapter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.background.service.BackgroundLocationService;
import com.custom.dialogs.LoginDialog;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.local.db.RecapViewMethod;
import com.als.logistic.Globally;
import com.als.logistic.R;
import com.als.logistic.SuggestedFragmentActivity;
import com.als.logistic.TabAct;
import com.als.logistic.UILApplication;
import com.als.logistic.fragment.EldFragment;
import com.models.EldDataModelNew;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Slidingmenufunctions implements OnClickListener {

	EldFragment eldFragment;
	SlidingMenu menu;
	Context context;
	public static TextView usernameTV, invisibleRefreshAdapterEvent, invisibleLogoutEvent;
	private TextView appVersionHome;
	ListView menuListView;

	JSONArray mainDriverArray = new JSONArray();
	JSONArray coDriverArray = new JSONArray();
	final int MainDriver = 101;
	final int CoDriver = 102;
	final int SaveMalDiagnstcEvent          = 1616;
	public static LinearLayout driversLayout;
	public static Button MainDriverBtn, CoDriverBtn;
	LoginDialog loginDialog;
	String MainDriverName = "", MainDriverPass = "", CoDriverName = "", CoDriverPass = "";
	String title     = "<font color='#1A3561'><b>Alert !!</b></font>";
	String titleDesc = "<html>You can't switch while vehicle is moving. Please stop your vehicle first to take this action.</html>";	// <font color='#1A3561'><b>DRIVING/PERSONAL USE</b></font>
	String okText = "<font color='#1A3561' ><b>Ok</b></font>";
	ProgressDialog dialog;
	DBHelper dbHelper;
	HelperMethods hMethod;
	int DriverId, DriverStatus;
	MainDriverEldPref MainDriverPref;
	CoDriverEldPref CoDriverPref;
	Constants constants;
	int eldGreenColor, eldWarningColor;
	Globally global;
	SaveDriverLogPost saveDriverLogPost;
	public SlideMenuAdapter menuAdapter;
	int DriverType;
	AlertDialog certifyLogAlert;
	private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();
	RecapViewMethod recapViewMethod;
	DriverPermissionMethod driverPermissionMethod;
	MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;


	public Slidingmenufunctions() {
		super();
	}

	public Slidingmenufunctions(final SlidingMenu menu, Context context, int DriverType) {

		this.menu = menu;
		this.context = context;
		this.DriverType	= DriverType;

		driverPermissionMethod = new DriverPermissionMethod();
		recapViewMethod = new RecapViewMethod();
		eldFragment = new EldFragment();
		global			 = new Globally();
		hMethod			 = new HelperMethods();
		dbHelper 		 = new DBHelper(context);
		malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();

		saveDriverLogPost = new SaveDriverLogPost(context, saveLogRequestResponse);
		dialog			 = new ProgressDialog(context);

		appVersionHome	 = (TextView)       menu.findViewById(R.id.appVersionHome);
		usernameTV 		 = (TextView)       menu.findViewById(R.id.usernameTV);
		invisibleRefreshAdapterEvent= (TextView)       menu.findViewById(R.id.invisibleViewEvent);
		invisibleLogoutEvent = (TextView)menu.findViewById(R.id.invisibleLogoutEvent);

		driversLayout	 = (LinearLayout)   menu.findViewById(R.id.driversLayout);
		menuListView 	 = (ListView)		menu.findViewById(R.id.menuListView);

		MainDriverBtn	 = (Button)         menu.findViewById(R.id.MainDriverBtn);
		CoDriverBtn		 = (Button)		    menu.findViewById(R.id.CoDriverBtn);
		MainDriverPref   = new MainDriverEldPref();
		CoDriverPref     = new CoDriverEldPref();
		constants        = new Constants();

		eldGreenColor = context.getResources().getColor(R.color.color_eld_theme);
		eldWarningColor = context.getResources().getColor(R.color.colorVoilation);

		//appVersionHome.setText(context.getResources().getString(R.string.Powered_by) +" (V - " +  Globally.GetAppVersion(context, "VersionName") + ")");
		appVersionHome.setVisibility(View.GONE);
		dialog.setMessage("Loading..");

		ImageView slideLogoIV = (ImageView)menu.findViewById(R.id.slideLogoIV);
		if (UILApplication.getInstance().isNightModeEnabled()) {
			slideLogoIV.setColorFilter(context.getResources().getColor(R.color.dark_cream_white));
		}



		setMenuAdapter();

		menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				if(TabAct.menuList.size() > position) {
					int status = TabAct.menuList.get(position).getStatus();
					if (status != Constants.VERSION) {
						menu.showContent();
					}
					listItemClick(status);
				}
			}
		});



		MainDriverBtn.setOnClickListener(this);
		CoDriverBtn.setOnClickListener(this);
		invisibleRefreshAdapterEvent.setOnClickListener(this);
		invisibleLogoutEvent.setOnClickListener(this);


	}


	void listItemClick(int status){

		//boolean isActionAllowedWithCoDriver = constants.isActionAllowedWithCoDriver(context, dbHelper, hMethod, global, SharedPref.getDriverId(context));
		boolean isActionAllowedWhileDriving = hMethod.isActionAllowedWhileMoving(context, global, SharedPref.getDriverId(context), dbHelper);
		if(isActionAllowedWhileDriving){
			switch (status){

				case Constants.ELD_HOS:
					Constants.ELDActivityLaunchCount = 0;
					TabAct.host.setCurrentTab(0);
					break;

				case Constants.PTI_INSPECTION:
					//if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(4);
			/*	}else{
					global.EldScreenToast(MainDriverBtn, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}*/
					break;

				case Constants.CT_PAT_INSPECTION:
					TabAct.host.setCurrentTab(8);
					break;

				case Constants.ODOMETER_READING:
					if(!SharedPref.IsOdometerFromOBD(context)) {
						EldFragment.IsMsgClick = false;
						TabAct.host.setCurrentTab(5);
					}else{
						global.EldScreenToast(MainDriverBtn, context.getResources().getString(R.string.odometer_permission_desc), context.getResources().getColor(R.color.colorSleeper));
					}
					break;

				case Constants.NOTIFICATION_HISTORY:
					TabAct.host.setCurrentTab(3);
					break;

				case Constants.SHIPPING_DOC:
					TabAct.host.setCurrentTab(7);
					break;

				case Constants.ELD_DOC:
					TabAct.host.setCurrentTab(10);
					break;

				case Constants.UNIDENTIFIED_RECORD:
					TabAct.host.setCurrentTab(11);
					break;

				case Constants.DATA_MALFUNCTION:
					TabAct.host.setCurrentTab(12);
					break;

				case Constants.SETTINGS:
					TabAct.host.setCurrentTab(1);
					break;

				case Constants.ALS_SUPPORT:
					TabAct.host.setCurrentTab(6);
					break;

				case Constants.ALS_TERMS_COND:
					TabAct.host.setCurrentTab(13);
					break;

				case Constants.LOGOUT:


					logoutEvent();

					break;

			}
		} else {

			switch (status) {

				case Constants.ELD_HOS:
					Constants.ELDActivityLaunchCount = 0;
					TabAct.host.setCurrentTab(0);
					break;

			/*	case Constants.ALS_SUPPORT:
					TabAct.host.setCurrentTab(6);
					break;
*/
				default:

					if(	TabAct.host.getCurrentTab() != 0){
						TabAct.host.setCurrentTab(0);
					}
					global.EldScreenToast(MainDriverBtn, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
									context.getString(R.string.stop_vehicle_alert),
							context.getResources().getColor(R.color.colorVoilation));
					break;
			}

		}




	}

	void logoutEvent(){

		String mainDriverStatus = SharedPref.getDriverStatusId(context);
		int CoDriverStatus  = hMethod.getCoDriverStatus(SharedPref.getDriverId(context), context, dbHelper);
		if(constants.isActionAllowed(context) && !mainDriverStatus.equals(Globally.DRIVING) &&
				CoDriverStatus != Constants.DRIVING){
			if(isSignPending()){
				certifyLogAlert();
			}else {
				logoutDialog();
			}

			if (context != null && !constants.isObdConnectedWithELD(context)) {
				global.InternetErrorDialog(context, true, true);
			}

		}else{
			if(mainDriverStatus.equals(Globally.DRIVING)){
				global.EldScreenToast(MainDriverBtn, context.getResources().getString(R.string.chnge_dr_to_othr), context.getResources().getColor(R.color.colorVoilation));
			}else if(CoDriverStatus == Constants.DRIVING){
				global.EldScreenToast(MainDriverBtn, context.getResources().getString(R.string.chnge_codriver_dr_to_othr), context.getResources().getColor(R.color.colorVoilation));
			}else {
				global.EldScreenToast(MainDriverBtn, "Vehicle speed is " +BackgroundLocationService.obdVehicleSpeed +" km/h. " +
						context.getResources().getString(R.string.logout_speed_alert), context.getResources().getColor(R.color.colorVoilation));
			}
		}
	}


	boolean isSignPending(){
		if(SharedPref.getDriverId(context).trim().length() > 0) {
			JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(SharedPref.getDriverId(context)), dbHelper);
			boolean isSignPending = constants.GetCertifyLogSignStatus(recapViewMethod, SharedPref.getDriverId(context), dbHelper,
										global.GetCurrentDeviceDate(null, global, context),
							DriverConst.GetCurrentCycleId(DriverConst.GetCurrentDriverType(context), context), logPermissionObj);
			return isSignPending;
		}else{
			return false;
		}

	}


	private void certifyLogAlert(){

			if (certifyLogAlert != null && certifyLogAlert.isShowing()) {
				Logger.LogDebug("dialog", "dialog is showing");
			} else {

				closeDialogs();

				String colorCode                  = "#1A3561";
				if (UILApplication.getInstance().isNightModeEnabled()) {
					colorCode = "#ffffff";
				}

				String title                      = "<font color='" + colorCode + "'><b>Certify Reminder !!</b></font>";
				String message                   = "<font color='#2E2E2E'><html>" + context.getResources().getString(R.string.certify_previous_days_log_warning) + " </html></font>";

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.AlertDialogStyle);
				alertDialogBuilder.setTitle(Html.fromHtml(title));
				alertDialogBuilder.setMessage(Html.fromHtml(message));
				//alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("Agree",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();

							EldFragment.moveToCertifyPopUpBtn.performClick();

							}
						});

				alertDialogBuilder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						if(constants.isActionAllowed(context)) {
							if(SharedPref.isSuggestedEditOccur(context) && SharedPref.IsCCMTACertified(context)){
								logoutDialog();
							}else{
								callLogoutApi();
							}
						}else{
							global.EldScreenToast(MainDriverBtn, "Vehicle speed is " +BackgroundLocationService.obdVehicleSpeed +" km/h. " +
									context.getResources().getString(R.string.logout_speed_alert), context.getResources().getColor(R.color.colorVoilation));
						}

					}
				});

				certifyLogAlert = alertDialogBuilder.create();
				if(UILApplication.getInstance().isNightModeEnabled()) {
					certifyLogAlert.getWindow().setBackgroundDrawableResource(R.color.layout_color_dot);
				}
				vectorDialogs.add(certifyLogAlert);
				if(context != null) {
					certifyLogAlert.show();
					if(UILApplication.getInstance().isNightModeEnabled()) {
						certifyLogAlert.getWindow().setBackgroundDrawableResource(R.color.layout_color_dot);
						certifyLogAlert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.white));
						certifyLogAlert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.white));
					}
				}


		}
	}


	public void closeDialogs() {
		for (AlertDialog dialog : vectorDialogs)
			if (dialog.isShowing()) dialog.dismiss();
	}



	@Override
	public void onClick(View v) {

		int viewId = v.getId();
		if(viewId != R.id.invisibleViewEvent && viewId != R.id.invisibleLogoutEvent){
			menu.showContent();
		}



		switch (viewId) {

			case R.id.MainDriverBtn:

				if(!SharedPref.getCurrentDriverType(context).equals("main_driver")) {
					if(SharedPref.getDriverId(context).trim().length() > 0) {
						DriverId = Integer.valueOf(SharedPref.getDriverId(context));
						DriverStatus = hMethod.GetDriverStatus(DriverId, dbHelper);
					}

					/*if(constants.isObdConnected(context) && SharedPref.isVehicleMoving(context)){
						global.DriverSwitchAlert(context, title, titleDesc, okText);
					}else{*/
						ShowLoginDialog(DriverConst.StatusSingleDriver);
					//}

				}

				break;


			case R.id.CoDriverBtn:
				if(!SharedPref.getCurrentDriverType(context).equals("co_driver")) {
					if(SharedPref.getDriverId(context).trim().length() > 0) {
						DriverId = Integer.valueOf(SharedPref.getDriverId(context));
						DriverStatus = hMethod.GetDriverStatus(DriverId, dbHelper);
					}

					/*if(constants.isObdConnected(context) && SharedPref.isVehicleMoving(context)){
						global.DriverSwitchAlert(context, title, titleDesc, okText);
					}else{*/
						ShowLoginDialog("co_driver");
				//	}
				}

				break;


			case R.id.invisibleViewEvent:
				setMenuAdapter();
				break;

			case R.id.invisibleLogoutEvent:
				logoutEvent();
				break;


		}

	}


	void setMenuAdapter(){
		try {

			// save index and top position
			int index = menuListView.getFirstVisiblePosition();
			View v = menuListView.getChildAt(0);
			int top = (v == null) ? 0 : (v.getTop() - menuListView.getPaddingTop());

			menuAdapter = new SlideMenuAdapter(context, TabAct.menuList, DriverType);
			menuListView.setAdapter(menuAdapter);
			menuAdapter.notifyDataSetChanged();

			if(index > 5){
				index = 5;
			}

			if(top < -25){
				top = -25;
			}
			// restore index and position
			menuListView.setSelectionFromTop(index, top);
			menuListView.smoothScrollToPosition(index);

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void logoutDialog(){

		final Dialog picker = new Dialog(context);
		picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picker.setContentView(R.layout.popup_edit_delete_lay);

		if(SharedPref.isSuggestedEditOccur(context) && SharedPref.IsCCMTACertified(context)) {
			if (global.isTablet(context)) {
				picker.getWindow().setLayout(constants.intToPixel(context, 700), ViewGroup.LayoutParams.WRAP_CONTENT);
			} else {
				picker.getWindow().setLayout(constants.intToPixel(context, 500), ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		}

		final TextView changeTitleView, titleDescView;
		changeTitleView = (TextView) picker.findViewById(R.id.changeTitleView);
		titleDescView=(TextView)picker.findViewById(R.id.titleDescView);
		final Button confirmPopupButton = (Button)picker.findViewById(R.id.confirmPopupButton);
		Button cancelPopupButton = (Button)picker.findViewById(R.id.cancelPopupButton);

		confirmPopupButton.setText(context.getResources().getString(R.string.logout));
		changeTitleView.setText(context.getResources().getString(R.string.Confirmation));

		if(SharedPref.isSuggestedEditOccur(context) && SharedPref.IsCCMTACertified(context)){
			titleDescView.setText(context.getResources().getString(R.string.pending_carrier_edit));
			cancelPopupButton.setText(context.getResources().getString(R.string.review_carrier_edits));

			titleDescView.setTextColor(context.getResources().getColor(R.color.gray_text1));
			cancelPopupButton.setTextColor(context.getResources().getColor(R.color.black_hos));

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 30, 0);
			cancelPopupButton.setLayoutParams(params);

		}else {
			titleDescView.setText(context.getResources().getString(R.string.want_to_logout));
		}

		cancelPopupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picker.dismiss();
				if(SharedPref.isSuggestedEditOccur(context) && SharedPref.IsCCMTACertified(context)){
					Intent i = new Intent(context, SuggestedFragmentActivity.class);
					i.putExtra(ConstantsKeys.suggested_data, "");
					i.putExtra(ConstantsKeys.Date, "");
					context.startActivity(i);
				}
			}
		});

		confirmPopupButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picker.dismiss();
				if(constants.isActionAllowed(context)) {
					callLogoutApi();
				}else{
					global.EldScreenToast(MainDriverBtn, "Vehicle speed is " +BackgroundLocationService.obdVehicleSpeed +" km/h. " +
							context.getResources().getString(R.string.logout_speed_alert), context.getResources().getColor(R.color.colorVoilation));
				}

			}
		});
		if(context != null) {
			picker.show();
		}

	}


	private void callLogoutApi(){
		Logger.LogDebug("callLogoutApi", "callLogoutApi");
		try {
			if (context != null) {
				if (global.isWifiOrMobileDataEnabled(context)) {
					dialog.show();

					boolean IsAllowMissingDataDiagnostic = SharedPref.GetOtherMalDiaStatus(ConstantsKeys.MissingDataDiag, context);
					String RPM = SharedPref.getRPM(context);

					// create logout time missing data diagnostic
					if((RPM.equals("0") || !constants.isObdConnectedWithELD(context) ) &&
							IsAllowMissingDataDiagnostic && !constants.isExemptDriver(context)) {

						saveMissingDiagnostic(context.getString(R.string.obd_data_is_missing), RPM, "Logout Event");

					}



					// clear other diagnostic events in service like engine sync
					Constants.isLogoutEvent = true;
					SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, context);
					startService();


					if (SharedPref.getDriverId(context).trim().length() > 0) {
						DriverId = Integer.valueOf(SharedPref.getDriverId(context));
					}

					if (global.isSingleDriver(context)) {
						JSONArray driverArray = GetDriversSavedData(true);
						if (driverArray.length() == 0) {
							LogoutUser(SharedPref.getDriverId(context));
						} else {
							SaveDataToServer(driverArray, MainDriver);
						}
					} else {
						//boolean isMainDriver = SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver);
						mainDriverArray = GetDriversSavedData(true);
						coDriverArray = GetDriversSavedData(false);

						if (mainDriverArray.length() == 0 && coDriverArray.length() == 0) {
							LogoutUser(SharedPref.getDriverId(context));
						} else {
							if (mainDriverArray.length() > 0) {
								SaveDataToServer(mainDriverArray, MainDriver);
							} else {
								SaveDataToServer(coDriverArray, CoDriver);
							}
						}
					}


				} else {
					global.EldScreenToast(MainDriverBtn, global.CHECK_INTERNET_MSG, context.getResources().getColor(R.color.colorSleeper));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void SaveDataToServer(JSONArray DriverLogArray, int DriverType){

		if(DriverLogArray.length() > 0) {

			String SavedLogApi = "";
			if(SharedPref.IsEditedData(context)){
				SavedLogApi = APIs.SAVE_DRIVER_EDIT_LOG_NEW;
			}else{
				SavedLogApi = APIs.SAVE_DRIVER_STATUS;
			}

			int socketTimeout = constants.SocketTimeout10Sec;	//10 seconds
			if(DriverLogArray.length() > 10 ){
				socketTimeout = constants.SocketTimeout20Sec;  //20 seconds
			}
			saveDriverLogPost.PostDriverLogData(DriverLogArray, SavedLogApi, socketTimeout, false,
					false, DriverType, 101);

		}else{
			LogoutUser(SharedPref.getDriverId(context));
		}



	}


	private void saveMissingDiagnostic(String remarks, String RPM, String type){
		try {

			String desc = "";
			if (RPM.equals("0")) {
				remarks = "Vehicle ignition is off at ";
				desc = " due to Vehicle ignition is off.";
			} else {
				desc = " due to OBD not connected with E-Log Book";
			}

			// save malfunction occur event to server with few inputs
			JSONObject newOccuredEventObj = malfunctionDiagnosticMethod.GetMalDiaEventJson(
					SharedPref.getDriverId(context), SharedPref.GetSavedSystemToken(context), SharedPref.getVINNumber(context),
					SharedPref.getTruckNumber(context),
					DriverConst.GetDriverDetails(DriverConst.CompanyId, context),
					constants.get2DecimalEngHour(context),
					SharedPref.getObdOdometer(context),
					SharedPref.getObdOdometer(context),
					Globally.GetCurrentUTCTimeFormat(), constants.MissingDataDiagnostic,
					remarks + " " + type,
					false, "", "",
					"", "", type);

			// save Occurred event locally until not posted to server
			JSONArray malArray = malfunctionDiagnosticMethod.getSavedMalDiagstcArray(dbHelper);
			malArray.put(newOccuredEventObj);
			malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, malArray);

			// save malfunction entry in duration table
			malfunctionDiagnosticMethod.addNewMalDiaEventInDurationArray(dbHelper, SharedPref.getDriverId(context),
					Globally.GetCurrentUTCTimeFormat(),  Globally.GetCurrentUTCTimeFormat(),
					Constants.MissingDataDiagnostic, type, "", type,
					constants, context);


			// call api
			if (malArray.length() > 0) {

				Globally.PlayNotificationSound(context);
				Globally.ShowLocalNotification(context,
						context.getResources().getString(R.string.missing_dia_event),
						context.getResources().getString(R.string.missing_event_occured_desc) + " in logout event " + desc, 2091);

				saveDriverLogPost.PostDriverLogData(malArray, APIs.MALFUNCTION_DIAGNOSTIC_EVENT, Constants.SocketTimeout15Sec,
						false, false, 1, SaveMalDiagnstcEvent);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}




	/*===== Get Driver Jobs in Array List======= */
	private JSONArray GetDriversSavedData(boolean isMainDriver) {
		int listSize = 0;
		JSONArray DriverJsonArray = new JSONArray();
		List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

		if(isMainDriver) {
			try {
				listSize = MainDriverPref.LoadSavedLoc(context).size();
				tempList = MainDriverPref.LoadSavedLoc(context);
			} catch (Exception e) {
				listSize = 0;
			}
		} else {
			try {
				listSize = CoDriverPref.LoadSavedLoc(context).size();
				tempList = CoDriverPref.LoadSavedLoc(context);
			} catch (Exception e) {
				listSize = 0;
			}
		}

		try {
			if (listSize > 0) {
				for (int i = 0; i < tempList.size(); i++) {
					EldDataModelNew listModel = tempList.get(i);

					if (listModel != null) {
						constants.SaveEldJsonToList(listModel, DriverJsonArray, context);  /* Put data as JSON to List */
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return DriverJsonArray;
	}




	/* ---------------------- Save Log Request Response ---------------- */
	DriverLogResponse saveLogRequestResponse = new DriverLogResponse() {
		@RequiresApi(api = Build.VERSION_CODES.KITKAT)
		@Override
		public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag, JSONArray inputData) {

			try {
				if (global.isSingleDriver(context)) {
					LogoutUser(SharedPref.getDriverId(context));
				} else {
					if (DriverType == MainDriver) {
						if (coDriverArray.length() > 0) {
							SaveDataToServer(coDriverArray, CoDriver);
						} else {
							LogoutUser(SharedPref.getDriverId(context));
						}
					} else {
						LogoutUser(SharedPref.getDriverId(context));
					}
				}


				if(flag == SaveMalDiagnstcEvent){
					// clear malfunction array
					malfunctionDiagnosticMethod.MalfnDiagnstcLogHelper(dbHelper, new JSONArray());
				}

			}catch (Exception e){
				e.printStackTrace();
			}


		}

		@Override
		public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
			Logger.LogDebug("errorrr ", ">>>error dialog: ");
			if(dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if(error.contains("TimeoutError")){
				error = "Connection time out. Please try again.";
			}
			global.EldScreenToast(MainDriverBtn, error, context.getResources().getColor(R.color.colorSleeper));
		}
	};



	public void MainDriverView(Context context){
		try {
			if(context != null) {
				MainDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.selected_driver_border));
				MainDriverBtn.setTextColor(R.attr.blue_button_hover);

				CoDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.unselected_driver_border));
				CoDriverBtn.setTextColor(R.attr.gray_text2);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public void CoDriverView(Context context, boolean isShown){
		try {
			if(context != null) {
				if (SharedPref.getDriverType(context).equals(DriverConst.TeamDriver)) {
					CoDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.selected_driver_border));
					CoDriverBtn.setTextColor(R.attr.blue_button_hover);

					MainDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.unselected_driver_border));
					MainDriverBtn.setTextColor(R.attr.gray_text2);
				} else {
					if (isShown)
						global.EldScreenToast(MainDriverBtn, "Co Driver information not available", eldWarningColor);
				}
			}
		}catch (Exception e){
			//e.printStackTrace();
		}
	}


	void RefreshActivity(){
		try {
			/*final int currentTab = TabAct.host.getCurrentTab();
			TabAct.host.setCurrentTab(2);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					TabAct.host.setCurrentTab(currentTab);
				}
			}, 200);
*/

			Intent intent = ((Activity) context).getIntent();
			((Activity) context).finish();
			context.startActivity(intent);

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void ShowLoginDialog(String userType){
		MainDriverName = DriverConst.GetDriverLoginDetails( DriverConst.UserName, context);
		MainDriverPass = DriverConst.GetDriverLoginDetails( DriverConst.Passsword, context);

		CoDriverName = DriverConst.GetCoDriverLoginDetails( DriverConst.CoUserName, context);
		CoDriverPass = DriverConst.GetCoDriverLoginDetails( DriverConst.CoPasssword, context);

		if(context != null) {
			loginDialog = new LoginDialog(context,
					userType,
					"certify",
					MainDriverName,
					CoDriverName, new LoginListener());
			loginDialog.show();
		}
	}


	private class LoginListener implements LoginDialog.LoginListener{


		@Override
		public void CancelReady() {

			SharedPref.saveCoDriverSwitchingStatus(false, context);

			if(loginDialog != null)
				loginDialog.dismiss();


		}


		@Override
		public void LoginBtnReady(String UserType, String userName, String Password, EditText UsernameEditText, EditText PasswordEditText) {

				if(Password.length() > 0) {
					if(UserType.equals("main_driver")){
						if(userName.equals(MainDriverName) && Password.equals(MainDriverPass)){
							global.hideKeyboardView(context, PasswordEditText);

							// this check is use to avoid cycle call at driver switching time
							SharedPref.saveCoDriverSwitchingStatus(true, context);

							Constants.lastDriverId = SharedPref.getDriverId(context);
							MainDriverView(context);
							ParseLoginDetails.setUserDefault(DriverConst.SingleDriver, context);
							usernameTV.setText(DriverConst.GetDriverDetails( DriverConst.DriverName, context));
							SharedPref.setDrivingAllowedStatus(true, "", context);

						//	RefreshActivity();
							global.hideKeyboardView(context, PasswordEditText);
							global.EldScreenToast(MainDriverBtn, "Password confirmed", eldGreenColor );


							SharedPref.SetCoDriverSwitchTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);
							Constants.isDriverSwitchEvent = true;
							Constants.isDriverSwitchEventForHome = true;
							SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, context);
							startService();

							if(loginDialog != null)
								loginDialog.dismiss();

							RefreshActivity();

						}else{
							global.EldScreenToast(UsernameEditText, "Incorrect Password", eldWarningColor );
						}
					}else{
						if(userName.equals(CoDriverName) && Password.equals(CoDriverPass)){
							global.hideKeyboardView(context, PasswordEditText);

							// this check is use to avoid cycle call at driver switching time
							SharedPref.saveCoDriverSwitchingStatus(true, context);

							Constants.lastDriverId = SharedPref.getDriverId(context);
							CoDriverView(context, false);
							ParseLoginDetails.setUserDefault( DriverConst.TeamDriver, context);
							usernameTV.setText(DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, context));
							SharedPref.setDrivingAllowedStatus(true, "", context);

							//RefreshActivity();
							global.EldScreenToast(MainDriverBtn, "Password confirmed", eldGreenColor );

							SharedPref.SetCoDriverSwitchTime(Globally.GetDriverCurrentDateTime(new Globally(), context), context);
							Constants.isDriverSwitchEvent = true;
							Constants.isDriverSwitchEventForHome = true;
							SharedPref.SetPingStatus(ConstantsKeys.SaveOfflineData, context);
							startService();

							if(loginDialog != null)
								loginDialog.dismiss();

							RefreshActivity();

						}else{
							global.EldScreenToast(UsernameEditText, "Incorrect password", eldWarningColor );
						}
					}
				}else{
					global.EldScreenToast(UsernameEditText, "Please enter password", eldWarningColor );
				}


		}
	}


	private void startService(){

		// update malfunction/diagnostic status with offline records
		//malfunctionDiagnosticMethod.updateMalfDiaStatusForEnable(global, constants, dbHelper, context);

		Intent serviceIntent = new Intent(context, BackgroundLocationService.class);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(serviceIntent);
		}
		context.startService(serviceIntent);

	}




	/*================== Logout User ===================*/
	void LogoutUser(final String DriverId){

		dialog.setMessage("Logging out..");
		boolean isExemptDriver;
		if(SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
			isExemptDriver = SharedPref.IsExemptDriverMain(context);
		} else {
			isExemptDriver = SharedPref.IsExemptDriverCo(context);
		}
		if(isExemptDriver && context != null ){
			//Toast.makeText(context, context.getResources().getString(R.string.exempt_logout_desc), Toast.LENGTH_LONG).show();
			global.ShowToastWithDuration(context.getResources().getString(R.string.exempt_logout_desc), context);
		}

		//global.ShowToastWithDuration(context.getResources().getString(R.string.exempt_logout_desc), context);

		final String DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);
		final String TRUCK_NUMBER = SharedPref.getTruckNumber(context);	//DriverConst.GetDriverTripDetails(DriverConst.Truck, context);
		final String VIN = SharedPref.getVINNumber(context);	//DriverConst.GetDriverTripDetails(DriverConst.VIN, context);

		RequestQueue queue = Volley.newRequestQueue(context);
		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				if (context != null) {
					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}
				}

					Logger.LogDebug("response", " Slidemenu logout response: " + response);
				String status = "", message = "";

				try {
					JSONObject obj = new JSONObject(response);
					status 	= obj.getString(ConstantsKeys.Status);
					message	= obj.getString(ConstantsKeys.Message);

					if(status.equalsIgnoreCase("true")){
						constants.ClearLogoutData(context);
					}else{
						if(obj.getString("Message").equals("Device Logout")) {
							constants.ClearLogoutData(context);
						}else{
							if (context != null) {
								global.EldScreenToast(MainDriverBtn, message,
										context.getResources().getColor(R.color.red_eld));
							}
						}
					}

				}catch(Exception e){  }
			}
		},
				new Response.ErrorListener()  {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.LogDebug("response error", "error: " + error.toString());

						try {
							if (context != null) {
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}

								String message = error.toString();
								if (message.contains("TimeoutError")) {
									message = "Connection timeout. Please try again.";
								} else if (message.contains("ServerError")) {
									message = "ALS server not responding";
								} else if (message.contains("NoConnectionError")) {
									message = "Internet connection error";
								}

								global.EldScreenToast(MainDriverBtn, message,
										context.getResources().getColor(R.color.red_eld));
							}
						}catch (Exception e){}
					}
				}
		) {
			@Override
			protected Map<String, String> getParams()
			{
				String date = global.GetDriverCurrentDateTime(global, context);

				Map<String,String> params = new HashMap<String, String>();
				params.put(ConstantsKeys.DriverId, DriverId);
				params.put(ConstantsKeys.MobileDeviceCurrentDateTime, date);
				params.put(ConstantsKeys.MobileUtcDate, Globally.GetCurrentUTCTimeFormat());
				params.put(ConstantsKeys.TruckEquipment, TRUCK_NUMBER);
				params.put(ConstantsKeys.CompanyId, DriverCompanyId);
				params.put(ConstantsKeys.VIN, VIN);
				params.put(ConstantsKeys.LocationType, SharedPref.getLocationEventType(context));
				params.put(ConstantsKeys.EngineHours,  Constants.get2DecimalEngHour(context));	//SharedPref.getObdEngineHours(context));
				params.put(ConstantsKeys.CrntOdodmeter, SharedPref.getObdOdometer(context));
				params.put(ConstantsKeys.Latitude,  Globally.LATITUDE);
				params.put(ConstantsKeys.Longitude, Globally.LONGITUDE);

				Logger.LogDebug("DateLogout", ">>>MobileDeviceCurrentDateTime: " +date);

				return params;
			}
		};

		int socketTimeout = 8000;   //8 seconds - change to what you want
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);

	}





}
