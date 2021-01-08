package com.constants;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.SyncingMethod;
import com.messaging.logistic.Globally;
import com.messaging.logistic.LoginActivity;
import com.messaging.logistic.R;
import com.messaging.logistic.TabAct;
import com.messaging.logistic.fragment.EldFragment;
import com.models.EldDataModelNew;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Slidingmenufunctions implements OnClickListener {

	SlidingMenu menu;
	Context context;
	public static TextView usernameTV, homeTxtView, notiBadgeView;
	private TextView appVersionHome;
	RelativeLayout jobLayout, tripLayout, tripHistoryLay, tripExpenseLay, logoutLay, eldLay,
			historyLay, settingLay, supportLay, InspectionLay, shippingLay, ctPatLay, obdLay,eldDocumentLay ;
	RelativeLayout unIdentifyLay, malfunctionLay;
	public static RelativeLayout odometerLay;
	public static LinearLayout driversLayout, slideMenuAlsView;
	public static Button MainDriverBtn, CoDriverBtn;
	LoginDialog loginDialog;
	String MainDriverName = "", MainDriverPass = "", CoDriverName = "", CoDriverPass = "";
	String title                      = "<font color='black'><b>Alert !!</b></font>";
	String titleDesc = "<html>You can't switch while <font color='#228B22'><b>DRIVING</b></font>. Please change your status first to switch with your co-driver.</html>";
	String okText = "<font color='#228B22' ><b>Ok</b></font>";
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
	SyncingMethod syncingMethod;
	File syncingFile;


	public Slidingmenufunctions() {
		super();
	}

	public Slidingmenufunctions(SlidingMenu menu, Context context) {

		this.menu = menu;
		this.context = context;

		sharedPref		 = new SharedPref();
		syncingMethod	  = new SyncingMethod();
		global			 = new Globally();
		hMethod			 = new HelperMethods();
		dbHelper 		 = new DBHelper(context);
		dialog			 = new ProgressDialog(context);
		appVersionHome	 = (TextView)       menu.findViewById(R.id.appVersionHome);
		homeTxtView		 = (TextView)       menu.findViewById(R.id.homeTxtView);
		usernameTV 		 = (TextView)       menu.findViewById(R.id.usernameTV);
		notiBadgeView	 = (TextView)       menu.findViewById(R.id.notiBadgeSlideView);

		jobLayout 		 = (RelativeLayout) menu.findViewById(R.id.jobLayout);
		tripLayout 		 = (RelativeLayout) menu.findViewById(R.id.tripLayout);
		tripHistoryLay 	 = (RelativeLayout) menu.findViewById(R.id.tripHistoryLay);
		tripExpenseLay 	 = (RelativeLayout) menu.findViewById(R.id.tripExpenseLay);
		eldLay 			 = (RelativeLayout) menu.findViewById(R.id.eldLay);
		settingLay		 = (RelativeLayout) menu.findViewById(R.id.settingLay);
		supportLay		 = (RelativeLayout) menu.findViewById(R.id.supportLay);
		historyLay		 = (RelativeLayout) menu.findViewById(R.id.historyLay);
		InspectionLay	 = (RelativeLayout) menu.findViewById(R.id.InspectionLay);
		odometerLay		 = (RelativeLayout) menu.findViewById(R.id.odometerLay);
		logoutLay 		 = (RelativeLayout) menu.findViewById(R.id.logoutLay);
		shippingLay		 = (RelativeLayout) menu.findViewById(R.id.shippingLay);
		ctPatLay		 = (RelativeLayout) menu.findViewById(R.id.ctPatLay);
		obdLay			 = (RelativeLayout) menu.findViewById(R.id.obdLay);
		eldDocumentLay	 = (RelativeLayout) menu.findViewById(R.id.eldDocumentLay);
		unIdentifyLay	 = (RelativeLayout) menu.findViewById(R.id.unIdentifyLay);
		malfunctionLay	 = (RelativeLayout) menu.findViewById(R.id.malfunctionLay);

		slideMenuAlsView = (LinearLayout)   menu.findViewById(R.id.slideMenuAlsView);
		driversLayout	 = (LinearLayout)   menu.findViewById(R.id.driversLayout);

		MainDriverBtn	 = (Button)         menu.findViewById(R.id.MainDriverBtn);
		CoDriverBtn		 = (Button)		    menu.findViewById(R.id.CoDriverBtn);
		MainDriverPref   = new MainDriverEldPref();
		CoDriverPref     = new CoDriverEldPref();
		constants        = new Constants();

		eldGreenColor = context.getResources().getColor(R.color.color_eld_theme);
		eldWarningColor = context.getResources().getColor(R.color.colorVoilation);

		appVersionHome.setText( "Version " +  Globally.GetAppVersion(context, "VersionName"));

		dialog.setMessage("Loading..");

		obdLay.setVisibility(View.GONE);

		jobLayout.setOnClickListener(this);
		tripLayout.setOnClickListener(this);
		tripHistoryLay.setOnClickListener(this);
		tripExpenseLay.setOnClickListener(this);
		eldLay.setOnClickListener(this);
		settingLay.setOnClickListener(this);
		supportLay.setOnClickListener(this);
		InspectionLay.setOnClickListener(this);
		odometerLay.setOnClickListener(this);
		shippingLay.setOnClickListener(this);
		historyLay.setOnClickListener(this);
		logoutLay.setOnClickListener(this);
		MainDriverBtn.setOnClickListener(this);
		CoDriverBtn.setOnClickListener(this);
		driversLayout.setOnClickListener(this);
		ctPatLay.setOnClickListener(this);
		obdLay.setOnClickListener(this);
		eldDocumentLay.setOnClickListener(this);
		unIdentifyLay.setOnClickListener(this);
		malfunctionLay.setOnClickListener(this);

		if(sharedPref.IsShowUnidentifiedRecords(context) == false){
			unIdentifyLay.setVisibility(View.GONE);
		}


		malfunctionLay.setVisibility(View.GONE);
		if(sharedPref.IsAllowMalfunction(context) == false && sharedPref.IsAllowDiagnostic(context) == false) {
			malfunctionLay.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		
		
		menu.showContent();

		switch (v.getId()) {

			case R.id.MainDriverBtn:

				if(!SharedPref.getCurrentDriverType(context).equals("main_driver")) {
					DriverId   		= Integer.valueOf(SharedPref.getDriverId(context) );
					DriverStatus	= hMethod.GetDriverStatus(DriverId, dbHelper);

					//ShowLoginDialog(DriverConst.StatusSingleDriver);
					if(DriverStatus == DRIVING){
						Globally.DriverSwitchAlert(context, title, titleDesc, okText);
					}else{
						ShowLoginDialog(DriverConst.StatusSingleDriver);
					}


				}

				break;


			case R.id.CoDriverBtn:
				if(!SharedPref.getCurrentDriverType(context).equals("co_driver")) {
					DriverId   		= Integer.valueOf(SharedPref.getDriverId(context) );
					DriverStatus	= hMethod.GetDriverStatus(DriverId, dbHelper);

					//ShowLoginDialog("co_driver");

					if(DriverStatus == DRIVING){
						Globally.DriverSwitchAlert(context, title, titleDesc, okText);
					}else{
						ShowLoginDialog("co_driver");
					}

				}

				break;



			case R.id.eldLay:
				Constants.ELDActivityLaunchCount = 0;
				TabAct.host.setCurrentTab(0);
				break;




			case R.id.historyLay:
				/*EldFragment.IsMsgClick = false;
				if(TabAct.host.getCurrentTab() == 3){
					RefreshActivity();
				}else {*/
					TabAct.host.setCurrentTab(3);
				//}
				break;


			case R.id.tripExpenseLay:
				EldFragment.IsMsgClick = false;
				TabAct.host.setCurrentTab(3);


			break;


			case R.id.settingLay:
				TabAct.host.setCurrentTab(1);
				break;



			case R.id.InspectionLay:
				TabAct.host.setCurrentTab(4);
				break;


			case R.id.odometerLay:
			if(!SharedPref.IsOdometerFromOBD(context)) {
				EldFragment.IsMsgClick = false;
				TabAct.host.setCurrentTab(5);
			}else{
				Globally.EldScreenToast(usernameTV, context.getResources().getString(R.string.odometer_permission_desc), context.getResources().getColor(R.color.colorSleeper));
			}
				break;

			case R.id.shippingLay:
				TabAct.host.setCurrentTab(7);
				break;

			case R.id.supportLay:
				TabAct.host.setCurrentTab(6);
				break;


			case R.id.ctPatLay:
				TabAct.host.setCurrentTab(8);
				break;


			case R.id.obdLay:
				TabAct.host.setCurrentTab(9);
				break;

			case R.id.eldDocumentLay:
				TabAct.host.setCurrentTab(10);
				break;

			case R.id.unIdentifyLay:
				TabAct.host.setCurrentTab(11);
				break;

			case R.id.malfunctionLay:
				TabAct.host.setCurrentTab(12);
				break;


			case R.id.logoutLay:

			final Dialog picker = new Dialog(context);
			picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
			picker.setContentView(R.layout.popup_edit_delete_lay);
			//picker.setTitle("Select Date and Time");


			final TextView changeTitleView, titleDescView;
			changeTitleView = (TextView) picker.findViewById(R.id.changeTitleView);
			titleDescView=(TextView)picker.findViewById(R.id.titleDescView);
			final Button confirmPopupButton = (Button)picker.findViewById(R.id.confirmPopupButton);
			Button cancelPopupButton = (Button)picker.findViewById(R.id.cancelPopupButton);

			changeTitleView.setText(context.getResources().getString(R.string.Confirmation));
			titleDescView.setText(context.getResources().getString(R.string.want_to_logout));
			confirmPopupButton.setText(context.getResources().getString(R.string.logout));

			cancelPopupButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					picker.dismiss();
				}
			});

			confirmPopupButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					picker.dismiss();

					if(Globally.isWifiOrMobileDataEnabled(context) ) {

						DriverId   		= Integer.valueOf(SharedPref.getDriverId(context) );
						dialog.show();

						JSONArray driverLogArray = GetDriversSavedArray();
						if(driverLogArray.length() == 0){
							LogoutUser(SharedPref.getDriverId(context));
						}else{
							SyncData();

						//	Globally.EldScreenToast(usernameTV, context.getResources().getString(R.string.found_local_data) ,
							//		context.getResources().getColor(R.color.colorSleeper));
						}
					}else{
						Globally.EldScreenToast(usernameTV, Globally.CHECK_INTERNET_MSG, context.getResources().getColor(R.color.colorSleeper));
					}
				}
			});

				picker.show();



			break;



		default:
			break;
		}

	}




	private void SyncData(){

		JSONArray savedSyncedArray = syncingMethod.getSavedSyncingArray(Integer.valueOf(DriverId), dbHelper);
		if(savedSyncedArray.length() > 0) {

			syncingFile = global.SaveFileInSDCard("Sync_", savedSyncedArray.toString(), false, context);

			// Sync driver log API data to server with SAVE_LOG_TEXT_FILE
			SyncDataUpload syncDataUpload = new SyncDataUpload(context, String.valueOf(DriverId), syncingFile, null, null, false, asyncResponse );
			syncDataUpload.execute();
		}else{
			LogoutUser(SharedPref.getDriverId(context));
		}



	}



	AsyncResponse asyncResponse = new AsyncResponse() {
		@Override
		public void onAsyncResponse(String response) {

			try {

				JSONObject obj = new JSONObject(response);
				String status = obj.getString("Status");
				if (status.equalsIgnoreCase("true")) {
					deleteLogWithLogoutUser();
				}else {
					deleteLogWithLogoutUser();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};



	void deleteLogWithLogoutUser(){
		if(syncingFile != null && syncingFile.exists())
			syncingFile.delete();
		syncingMethod.SyncingLogHelper(Integer.valueOf(DriverId), dbHelper, new JSONArray());

		LogoutUser(SharedPref.getDriverId(context));
	}


	public JSONArray GetDriversSavedArray(){
		int listSize = 0;
		JSONArray DriverJsonArray = new JSONArray();
		List<EldDataModelNew> tempList = new ArrayList<EldDataModelNew>();

		if(SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver)) {
			try {
				listSize = MainDriverPref.LoadSavedLoc(context).size();
				tempList = MainDriverPref.LoadSavedLoc(context);
			} catch (Exception e) {
				listSize = 0;
			}
		}else{
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
					if(listModel != null) {
						constants.SaveEldJsonToList(          /* Put data as JSON to List */
								listModel,
								DriverJsonArray
						);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return DriverJsonArray;
		// Log.d("Arraay", "Arraay: " + DriverJsonArray.toString());
	}



	public void MainDriverView(Context context){
			MainDriverBtn.setBackgroundColor(menu.getResources().getColor(R.color.color_eld_theme_one));
			MainDriverBtn.setTextColor(menu.getResources().getColor(R.color.whiteee));

			CoDriverBtn.setBackgroundColor(menu.getResources().getColor(R.color.whiteee));
			CoDriverBtn.setTextColor(menu.getResources().getColor(R.color.gray_text2));

	}


	public void CoDriverView(Context context, boolean isShown){
		try {
			if(context != null) {
				if (SharedPref.getDriverType(context).equals(DriverConst.TeamDriver)) {
					CoDriverBtn.setBackgroundColor(context.getResources().getColor(R.color.color_eld_theme_one));
					CoDriverBtn.setTextColor(context.getResources().getColor(R.color.whiteee));

					MainDriverBtn.setBackgroundColor(context.getResources().getColor(R.color.whiteee));
					MainDriverBtn.setTextColor(context.getResources().getColor(R.color.gray_text2));
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
			int currentTab = TabAct.host.getCurrentTab();
			TabAct.host.setCurrentTab(2);
			TabAct.host.setCurrentTab(currentTab);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private void ShowLoginDialog(String userType){
		MainDriverName = DriverConst.GetDriverLoginDetails( DriverConst.UserName, context);
		MainDriverPass = DriverConst.GetDriverLoginDetails( DriverConst.Passsword, context);

		CoDriverName = DriverConst.GetCoDriverLoginDetails( DriverConst.CoUserName, context);
		CoDriverPass = DriverConst.GetCoDriverLoginDetails( DriverConst.CoPasssword, context);

		loginDialog = new LoginDialog(context ,
				userType,
				"certify",
				MainDriverName,
				CoDriverName, new LoginListener());
		loginDialog.show();
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

		RequestQueue queue = Volley.newRequestQueue(context);
		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.DRIVER_LOGOUT , new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {

				if(dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}

					Log.d("response", " logout response: " + response);
				String status = "";

				try {
					Globally.obj = new JSONObject(response);
					status = Globally.obj.getString("Status");

					if(status.equalsIgnoreCase("true")){
						constants.ClearLogoutData(context);
					}else{
						if(Globally.obj.getString("Message").equals("Device Logout"))
							constants.ClearLogoutData(context);
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
				params.put("DriverId", DriverId);
				params.put("MobileDeviceCurrentDateTime", global.getCurrentDate());

				return params;
			}
		};

		int socketTimeout = 8000;   //8 seconds - change to what you want
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);

	}





}
