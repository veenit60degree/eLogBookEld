package com.constants;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;

import com.adapter.logistic.SlideMenuAdapter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.custom.dialogs.LoginDialog;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.DriverPermissionMethod;
import com.local.db.HelperMethods;
import com.local.db.RecapViewMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;
import com.messaging.logistic.SuggestedFragmentActivity;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;
import com.models.EldDataModelNew;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.joda.time.DateTime;
import org.joda.time.Days;
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
	public static TextView usernameTV, invisibleRefreshAdapterEvent;
	private TextView appVersionHome;
	ListView menuListView;

	JSONArray mainDriverArray = new JSONArray();
	JSONArray coDriverArray = new JSONArray();
	final int MainDriver = 101;
	final int CoDriver = 102;
	public static LinearLayout driversLayout;
	public static Button MainDriverBtn, CoDriverBtn;
	LoginDialog loginDialog;
	String MainDriverName = "", MainDriverPass = "", CoDriverName = "", CoDriverPass = "";
	String title                      = "<font color='#1A3561'><b>Alert !!</b></font>";
	String titleDesc = "<html>You can't switch while vehicle is moving. Please stop your vehicle first to take this action.</html>";	// <font color='#1A3561'><b>DRIVING/PERSONAL USE</b></font>
	String okText = "<font color='#1A3561' ><b>Ok</b></font>";
	ProgressDialog dialog;
	DBHelper dbHelper;
	HelperMethods hMethod;
	int DriverId, DriverStatus, DRIVING = 3 ;
	MainDriverEldPref MainDriverPref;
	CoDriverEldPref CoDriverPref;
	Constants constants;
	int eldGreenColor, eldWarningColor;
	Globally global;
	SharedPref sharedPref;
	SaveDriverLogPost saveDriverLogPost;
	public SlideMenuAdapter menuAdapter;
	int DriverType;
	AlertDialog certifyLogAlert;
	private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();
	RecapViewMethod recapViewMethod;
	DriverPermissionMethod driverPermissionMethod;
	Globally Global;


	public Slidingmenufunctions() {
		super();
	}

	public Slidingmenufunctions(final SlidingMenu menu, Context context, int DriverType) {

		this.menu = menu;
		this.context = context;
		this.DriverType	= DriverType;

		Global = new Globally();
		driverPermissionMethod = new DriverPermissionMethod();
		recapViewMethod = new RecapViewMethod();
		eldFragment = new EldFragment();
		sharedPref		 = new SharedPref();
		global			 = new Globally();
		hMethod			 = new HelperMethods();
		dbHelper 		 = new DBHelper(context);
		saveDriverLogPost = new SaveDriverLogPost(context, saveLogRequestResponse);
		dialog			 = new ProgressDialog(context);

		appVersionHome	 = (TextView)       menu.findViewById(R.id.appVersionHome);
		usernameTV 		 = (TextView)       menu.findViewById(R.id.usernameTV);
		invisibleRefreshAdapterEvent= (TextView)       menu.findViewById(R.id.invisibleViewEvent);

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

	}


	void listItemClick(int status){

		boolean isActionAllowedWithCoDriver = constants.isActionAllowedWithCoDriver(context, dbHelper, hMethod, Global, SharedPref.getDriverId(context));

		switch (status){

			case Constants.ELD_HOS:
				Constants.ELDActivityLaunchCount = 0;
				TabAct.host.setCurrentTab(0);
				break;

			case Constants.PTI_INSPECTION:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(4);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.CT_PAT_INSPECTION:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(8);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.ODOMETER_READING:
				if(isActionAllowedWithCoDriver) {
					if(!SharedPref.IsOdometerFromOBD(context)) {
						EldFragment.IsMsgClick = false;
						TabAct.host.setCurrentTab(5);
					}else{
						Globally.EldScreenToast(usernameTV, context.getResources().getString(R.string.odometer_permission_desc), context.getResources().getColor(R.color.colorSleeper));
					}
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.NOTIFICATION_HISTORY:
				TabAct.host.setCurrentTab(3);
				break;

			case Constants.SHIPPING_DOC:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(7);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.ELD_DOC:
				TabAct.host.setCurrentTab(10);
				break;

			case Constants.UNIDENTIFIED_RECORD:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(11);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.DATA_MALFUNCTION:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(12);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.SETTINGS:
				if(isActionAllowedWithCoDriver) {
					TabAct.host.setCurrentTab(1);
				}else{
					global.EldScreenToast(menuListView, context.getResources().getString(R.string.stop_vehicle_alert), context.getResources().getColor(R.color.colorVoilation));
				}
				break;

			case Constants.ALS_SUPPORT:
				TabAct.host.setCurrentTab(6);
				break;

			case Constants.ALS_TERMS_COND:
				TabAct.host.setCurrentTab(13);
				break;

			case Constants.LOGOUT:

				if(constants.isActionAllowed(context)){
					boolean isExemptDriver = false;
					if(SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
						isExemptDriver = sharedPref.IsExemptDriverMain(context);
					} else {
						isExemptDriver = sharedPref.IsExemptDriverCo(context);
					}

					if(isExemptDriver && context != null){
						Toast.makeText(context, context.getResources().getString(R.string.exempt_reminder_desc), Toast.LENGTH_LONG).show();
					}

					if(isSignPending()){
						certifyLogAlert();
					}else {
						logoutDialog();
					}
				}else{
					Globally.EldScreenToast(usernameTV, context.getResources().getString(R.string.logout_speed_alert), context.getResources().getColor(R.color.colorVoilation));
				}



				break;

		}
	}

	boolean isSignPending(){
		if(SharedPref.getDriverId(context).trim().length() > 0) {
			JSONObject logPermissionObj = driverPermissionMethod.getDriverPermissionObj(Integer.valueOf(SharedPref.getDriverId(context)), dbHelper);
			boolean isSignPending = constants.GetCertifyLogSignStatus(recapViewMethod, SharedPref.getDriverId(context), dbHelper, global.GetCurrentDeviceDate(),
					DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context), logPermissionObj);
			return isSignPending;
		}else{
			return false;
		}

	}


	private void certifyLogAlert(){

			if (certifyLogAlert != null && certifyLogAlert.isShowing()) {
				Log.d("dialog", "dialog is showing");
			} else {

				closeDialogs();

				String title                      = "<font color='#1A3561'><b>Certify Reminder !!</b></font>";
				String message                   = "<font color='#2E2E2E'><html>" + context.getResources().getString(R.string.certify_previous_days_log_warning) + " </html></font>";


				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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

						if(sharedPref.isSuggestedEditOccur(context) && sharedPref.IsCCMTACertified(context)){
							logoutDialog();
						}else{
							callLogoutApi();
						}
					}
				});

				certifyLogAlert = alertDialogBuilder.create();
				vectorDialogs.add(certifyLogAlert);
				if(context != null) {
					certifyLogAlert.show();
				}


		}
	}


	public void closeDialogs() {
		for (AlertDialog dialog : vectorDialogs)
			if (dialog.isShowing()) dialog.dismiss();
	}



	@Override
	public void onClick(View v) {
		
		if(v.getId() != R.id.invisibleViewEvent){
			menu.showContent();
		}


		switch (v.getId()) {

			case R.id.MainDriverBtn:

				if(!SharedPref.getCurrentDriverType(context).equals("main_driver")) {
					if(SharedPref.getDriverId(context).trim().length() > 0) {
						DriverId = Integer.valueOf(SharedPref.getDriverId(context));
						DriverStatus = hMethod.GetDriverStatus(DriverId, dbHelper);
					}
					//	boolean isMoving = hMethod.isDrivingAllowed(DriverId, dbHelper);

					//if(DriverStatus == DRIVING){
						if(constants.isObdConnected(context) && sharedPref.isVehicleMoving(context)){
							Globally.DriverSwitchAlert(context, title, titleDesc, okText);
						}else{
							ShowLoginDialog(DriverConst.StatusSingleDriver);
						}
				/*	}else{
						if( sharedPref.isVehicleMoving(context)){
							Globally.DriverSwitchAlert(context, title, titleDesc, okText);
						}else{
							ShowLoginDialog(DriverConst.StatusSingleDriver);
						}

					}*/


				}

				break;


			case R.id.CoDriverBtn:
				if(!SharedPref.getCurrentDriverType(context).equals("co_driver")) {
					if(SharedPref.getDriverId(context).trim().length() > 0) {
						DriverId = Integer.valueOf(SharedPref.getDriverId(context));
						DriverStatus = hMethod.GetDriverStatus(DriverId, dbHelper);
					}
				//	boolean isMoving = hMethod.isDrivingAllowed(DriverId, dbHelper);

					//if(DriverStatus == DRIVING){
						if(constants.isObdConnected(context) && sharedPref.isVehicleMoving(context)){
							Globally.DriverSwitchAlert(context, title, titleDesc, okText);
						}else{
							ShowLoginDialog("co_driver");
						}

					/*}else{
						if( sharedPref.isVehicleMoving(context)){
							Globally.DriverSwitchAlert(context, title, titleDesc, okText);
						}else{
							ShowLoginDialog("co_driver");
						}
					}*/

				}

				break;


			case R.id.invisibleViewEvent:
				setMenuAdapter();
				break;


		}

	}


	void setMenuAdapter(){
		try {
			//View c = menuListView.getChildAt(0);
			//int scrolly = -c.getTop() + menuListView.getFirstVisiblePosition() * c.getHeight();
			//int index = menuListView.getFirstVisiblePosition();

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

		if(sharedPref.isSuggestedEditOccur(context) && sharedPref.IsCCMTACertified(context)) {
			if (Globally.isTablet(context)) {
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

		if(sharedPref.isSuggestedEditOccur(context) && sharedPref.IsCCMTACertified(context)){
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
				if(sharedPref.isSuggestedEditOccur(context) && sharedPref.IsCCMTACertified(context)){
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

				callLogoutApi();

			}
		});
		if(context != null) {
			picker.show();
		}

	}


	private void callLogoutApi(){
		try {
			if (context != null) {
				if (Globally.isWifiOrMobileDataEnabled(context)) {
					dialog.show();

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
					Globally.EldScreenToast(usernameTV, Globally.CHECK_INTERNET_MSG, context.getResources().getColor(R.color.colorSleeper));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void SaveDataToServer(JSONArray DriverLogArray, int DriverType){

		if(DriverLogArray.length() > 0) {

			String SavedLogApi = "";
			if(sharedPref.IsEditedData(context)){
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
						constants.SaveEldJsonToList(listModel, DriverJsonArray);  /* Put data as JSON to List */
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
		public void onApiResponse(String response, boolean isLoad, boolean IsRecap, int DriverType, int flag) {

			if(global.isSingleDriver(context)) {
				LogoutUser(SharedPref.getDriverId(context));
			}else{
				if(DriverType == MainDriver){
					if(coDriverArray.length() > 0){
						SaveDataToServer(coDriverArray, CoDriver);
					}else{
						LogoutUser(SharedPref.getDriverId(context));
					}
				}else{
					LogoutUser(SharedPref.getDriverId(context));
				}
			}


		}

		@Override
		public void onResponseError(String error, boolean isLoad, boolean IsRecap, int DriverType, int flag) {
			Log.d("errorrr ", ">>>error dialog: ");
			if(dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if(error.contains("TimeoutError")){
				error = "Connection time out. Please try again.";
			}
			Globally.EldScreenToast(usernameTV, error, context.getResources().getColor(R.color.colorSleeper));
		}
	};



	public void MainDriverView(Context context){
		try {
			if(context != null) {
				MainDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.selected_driver_border));
				MainDriverBtn.setTextColor(context.getResources().getColor(R.color.blue_button_hover));

				CoDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.unselected_driver_border));
				CoDriverBtn.setTextColor(context.getResources().getColor(R.color.gray_hover));
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
					CoDriverBtn.setTextColor(context.getResources().getColor(R.color.blue_button_hover));

					MainDriverBtn.setBackground(context.getResources().getDrawable(R.drawable.unselected_driver_border));
					MainDriverBtn.setTextColor(context.getResources().getColor(R.color.gray_hover));
				} else {
					if (isShown)
						Globally.EldScreenToast(CoDriverBtn, "Co Driver information not available", eldWarningColor);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	void RefreshActivity(){
		try {
			final int currentTab = TabAct.host.getCurrentTab();
			TabAct.host.setCurrentTab(2);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					TabAct.host.setCurrentTab(currentTab);
				}
			}, 100);

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
			if(loginDialog != null)
				loginDialog.dismiss();
		}


		@Override
		public void LoginBtnReady(String UserType, String userName, String Password, EditText UsernameEditText, EditText PasswordEditText) {

			//if(userName.length() > 0) {
				if(Password.length() > 0) {
					if(UserType.equals("main_driver")){
						if(userName.equals(MainDriverName) && Password.equals(MainDriverPass)){
							Globally.hideKeyboardView(context, PasswordEditText);

							if(loginDialog != null)
								loginDialog.dismiss();
							MainDriverView(context);
							ParseLoginDetails.setUserDefault(DriverConst.SingleDriver, context);
							usernameTV.setText(DriverConst.GetDriverDetails( DriverConst.DriverName, context));
							sharedPref.setDrivingAllowedStatus(true, "", context);

							RefreshActivity();
							Globally.hideKeyboardView(context, PasswordEditText);
							Globally.EldScreenToast(usernameTV, "Password confirmed", eldGreenColor );
						}else{
							Globally.EldScreenToast(UsernameEditText, "Incorrect Password", eldWarningColor );
						}
					}else{
						if(userName.equals(CoDriverName) && Password.equals(CoDriverPass)){
							Globally.hideKeyboardView(context, PasswordEditText);

							if(loginDialog != null)
								loginDialog.dismiss();
							CoDriverView(context, false);
							ParseLoginDetails.setUserDefault( DriverConst.TeamDriver, context);
							usernameTV.setText(DriverConst.GetCoDriverDetails( DriverConst.CoDriverName, context));
							sharedPref.setDrivingAllowedStatus(true, "", context);

							RefreshActivity();
							Globally.EldScreenToast(usernameTV, "Password confirmed", eldGreenColor );
						}else{
							Globally.EldScreenToast(UsernameEditText, "Incorrect password", eldWarningColor );
						}
					}
				}else{
					Globally.EldScreenToast(UsernameEditText, "Please enter password", eldWarningColor );
				}
			/*}else{
				Globally.EldScreenToast(UsernameEditText, "Please enter username");
			}*/

		}
	}


	/*================== Logout User ===================*/
	void LogoutUser(final String DriverId){
		final String DriverCompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, context);
		final String TRUCK_NUMBER = DriverConst.GetDriverTripDetails(DriverConst.Truck, context);
		final String VIN = DriverConst.GetDriverTripDetails(DriverConst.VIN, context);

		RequestQueue queue = Volley.newRequestQueue(context);
		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				if(dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

					Log.d("response", " logout response: " + response);
				String status = "", message = "";

				try {
					Globally.obj = new JSONObject(response);
					status 	= Globally.obj.getString(ConstantsKeys.Status);
					message	= Globally.obj.getString(ConstantsKeys.Message);

					if(status.equalsIgnoreCase("true")){
						constants.ClearLogoutData(context);
					}else{
						if(Globally.obj.getString("Message").equals("Device Logout")) {
							constants.ClearLogoutData(context);
						}else{
							Globally.EldScreenToast(usernameTV, message, context.getResources().getColor(R.color.red_eld));
						}
					}

				}catch(Exception e){  }
			}
		},
				new Response.ErrorListener()  {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("response error", "error: " + error.toString());
						if(dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}

						String message = error.toString();
						if(message.contains("TimeoutError")){
							message = "Server not responding. Please try again.";
						}else if(message.contains("ServerError")){
							message = "ALS server not responding";
						}else if(message.contains("NoConnectionError")){
							message = "Connection not working.";
						}

						Globally.EldScreenToast(usernameTV, message, context.getResources().getColor(R.color.red_eld));
					}
				}
		) {
			@Override
			protected Map<String, String> getParams()
			{
				Map<String,String> params = new HashMap<String, String>();
				params.put(ConstantsKeys.DriverId, DriverId);
				params.put(ConstantsKeys.MobileDeviceCurrentDateTime, global.getCurrentDate());
				params.put(ConstantsKeys.TruckEquipment, TRUCK_NUMBER);
				params.put(ConstantsKeys.CompanyId, DriverCompanyId);
				params.put(ConstantsKeys.VIN, VIN);
				params.put(ConstantsKeys.LocationType, sharedPref.getLocMalfunctionType(context));
				params.put(ConstantsKeys.EngineHours,  sharedPref.getObdEngineHours(context));
				params.put(ConstantsKeys.CrntOdodmeter, constants.meterToKm(sharedPref.getHighPrecisionOdometer(context)));

				return params;
			}
		};

		int socketTimeout = 8000;   //8 seconds - change to what you want
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);

	}





}
