package com.messaging.logistic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.background.service.AfterLogoutService;
import com.background.service.BackgroundLocationService;
import com.constants.APIs;
import com.constants.Anim;
import com.constants.Constants;
import com.constants.DualSimManager;
import com.constants.SharedPref;
import com.constants.Utils;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.local.db.ConstantsKeys;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;
import com.wifi.settings.WiFiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends FragmentActivity implements OnClickListener {

	EditText userNameText, passwordText, coDriverUserNameText, coDriverPasswordText;
	TextView driverTitleTV, appVersion, appTypeView;
	RelativeLayout mainLoginLayout, loginLayout, userTypeLayout, loginCoDriverLayout, loginScrollChildLay;
	Button loginBtn, mainDriverBtn, CoDriverBtn, coDriverLoginBtn;
	ImageView backImgView, wifiImgBtn;
	ProgressDialog progressDialog;
	//private BroadcastReceiver mRegistrationBroadcastReceiver;
	ProgressBar progressBarLogin;
	String ImeiNumber = "", LoginUserType = "";
	String StrSingleUserame = "", StrSinglePass = "", StrCoDriverUsername = "", StrCoDriverPass = "", StrOSType = "", AppVersion = "";
	String status = "", message = "", deviceType = "";
	Anim animation;
	boolean IsLoginSuccess = false, IsTablet = false;
	String Sim1 = "", Sim2 = "", DeviceSimInfo = "";
	Constants constants;
	Globally global;
	SharedPref sharedPref;
	Utils obdUtil;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_activity);

		//WiFiConf = new WiFiConfig();
		//wifiList = WiFiConf.GetSavedSSIDList();
		//	pos 	 = WiFiConf.getWifiListPosition(this);
		sharedPref				= new SharedPref();
		global					= new Globally();
		IsTablet 				= global.isTablet(this);
		constants				= new Constants();

		/*========= Start Service =============*/
		Intent serviceIntent = new Intent(this, AfterLogoutService.class);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(serviceIntent);
		}
		startService(serviceIntent);




		try{
			if(isStorageGrantedForUtil()) {
				obdUtil = new Utils(LoginActivity.this);
				obdUtil.createLogFile();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		progressBarLogin 		= (ProgressBar) findViewById(R.id.progressBarLogin);
		appVersion 				= (TextView) findViewById(R.id.appVersion);
		driverTitleTV 			= (TextView) findViewById(R.id.driverTitleTV);
		appTypeView				= (TextView) findViewById(R.id.appTypeView);

		userNameText 			= (EditText) findViewById(R.id.userNameText);
		passwordText 			= (EditText) findViewById(R.id.passwordText);
		coDriverUserNameText 	= (EditText) findViewById(R.id.coDriverUserNameText);
		coDriverPasswordText 	= (EditText) findViewById(R.id.coDriverPasswordText);

		loginBtn 				= (Button) findViewById(R.id.loginBtn);
		mainDriverBtn 			= (Button) findViewById(R.id.mainDriverBtn);
		CoDriverBtn 			= (Button) findViewById(R.id.CoDriverBtn);
		coDriverLoginBtn 		= (Button) findViewById(R.id.coDriverLoginBtn);

		backImgView 			= (ImageView) findViewById(R.id.backImgView);
		wifiImgBtn 				= (ImageView) findViewById(R.id.wifiImgBtn);

		loginScrollChildLay	= (RelativeLayout) findViewById(R.id.loginScrollChildLay);
		mainLoginLayout 		= (RelativeLayout) findViewById(R.id.mainLoginLayout);
		loginLayout 			= (RelativeLayout) findViewById(R.id.loginLayout);
		userTypeLayout 			= (RelativeLayout) findViewById(R.id.userTypeLayout);
		loginCoDriverLayout 	= (RelativeLayout) findViewById(R.id.loginCoDriverLayout);
		//mTelephonyManager 	= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		backImgView.setVisibility(View.GONE);
		progressBarLogin.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFFFFF"), android.graphics.PorterDuff.Mode.MULTIPLY);

		AppVersion = global.GetAppVersion(this, "VersionName");
		StrOSType = "Android - " + AppVersion;
		appVersion.setText("Version " + AppVersion);

		if(APIs.DOMAIN_URL_ALS.contains("dev.alsrealtime.com") || APIs.DOMAIN_URL_ALS.contains("104.167.9") ){
			appTypeView.setVisibility(View.VISIBLE);
		}else if(APIs.DOMAIN_URL_ALS.contains("182.73.78") || APIs.DOMAIN_URL_ALS.contains("192.168.0")){
			appTypeView.setVisibility(View.VISIBLE);
			appTypeView.setText("INDIAN");
		}else{
			appTypeView.setVisibility(View.GONE);
		}


		//if(!IsTablet)
		wifiImgBtn.setVisibility(View.GONE);

		animation = new Anim();
		File apkStorageDir = new File(LoginActivity.this.getExternalFilesDir(null), "Logistic");
		global.DeleteDirectory(apkStorageDir.toString());
		try {
			MainDriverEldPref MainDriverPref = new MainDriverEldPref();
			CoDriverEldPref CoDriverPref = new CoDriverEldPref();
			MainDriverPref.ClearLocFromList(this);
			CoDriverPref.ClearLocFromList(this);
		} catch (Exception e) {
		}

		// if (UILApplication.getInstance().getInstance().PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES) {
		if(UILApplication.getInstance().isNightModeEnabled()){
			mainLoginLayout.setBackgroundColor(getResources().getColor(R.color.gray_background));
			loginScrollChildLay.setBackgroundColor(getResources().getColor(R.color.gray_background));
			loginLayout.setBackgroundColor(getResources().getColor(R.color.gray_background));
			loginCoDriverLayout.setBackgroundColor(getResources().getColor(R.color.gray_background));
		}


		mainLoginLayout.setOnClickListener(this);
		loginLayout.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		mainDriverBtn.setOnClickListener(this);
		CoDriverBtn.setOnClickListener(this);
		backImgView.setOnClickListener(this);
		wifiImgBtn.setOnClickListener(this);
		coDriverLoginBtn.setOnClickListener(this);

		passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					// =============== Check storage permission =====================
					isStoragePermissionGranted();
				}
				return false;
			}
		});

		coDriverPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					// =============== Check storage permission =====================
					LoginWithCoDriver();
				}
				return false;
			}
		});


/*		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// checking for type intent filter
				if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
					// gcm successfully registered. Now subscribe to `global` topic to receive app wide notifications
					FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

					*//* ============== Get Device Token For Notification ============ *//*
					GetDeviceTokenForNotification();

				}
			}
		};*/


		/* ============== Get Device Token For Notification ============ */
		//GetDeviceTokenForNotification();

		// Get Phone Number from Sim Card
		getMyPhoneNumber();

		Intent i = getIntent();
		if (i.hasExtra("EXIT")) {
			if (i.getBooleanExtra("EXIT", false)) {
				finish();
			}
		}

	}


    public static boolean isImmersiveAvailable() {
        return Build.VERSION.SDK_INT >= 19;
    }


    public void setFullscreen(Activity activity) {
        if (Build.VERSION.SDK_INT > 10) {
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (isImmersiveAvailable()) {
                flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        } else {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }


/*
	void GetDeviceTokenForNotification() {

		SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
		global.registrationId = pref.getString("regId", null);
		sharedPref.SetSystemToken(global.registrationId, LoginActivity.this);

		Log.e("FCM_token", "FCM token: " + global.registrationId);

	}
*/


	public boolean isStoragePermissionGranted() {
		global.hideKeyboard(LoginActivity.this, mainLoginLayout);
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v("TAG", "Permission is granted");
				requestLocationPermission();

				return true;
			} else {
				Log.v("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG", "Permission is granted");
			login();
			return true;
		}

	}



	public boolean isStorageGrantedForUtil() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {
				return true;
			} else {
				Log.v("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG", "Permission is granted");
			return true;
		}

	}



	public boolean requestPermissionForCamera() {

		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.CAMERA)
					== PackageManager.PERMISSION_GRANTED) {
				Log.v("TAG", "Permission is granted");
				requestPermissionPhone();

				return true;
			} else {
				Log.v("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG", "Permission is granted");
			login();
			return true;
		}

	}


	public boolean requestPermissionPhone() {

		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
				Log.v("TAG", "Permission is granted");
				login();

				return true;
			} else {
				Log.v("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 5);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG", "Permission is granted");
			login();
			return true;
		}

	}



	private boolean requestLocationPermission() {


		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
					== PackageManager.PERMISSION_GRANTED) {
				requestPermissionForCamera();

				return true;
			} else {
				Log.v("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("TAG", "Permission is granted");
			login();
			return true;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);


		switch (requestCode) {

			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
					//resume tasks needing this permission
					requestLocationPermission();
				}
				break;

			case 111:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
					try{
						obdUtil = new Utils(LoginActivity.this);
						obdUtil.createLogFile();
					}catch (Exception e){
						e.printStackTrace();
					}
				}else{
					isStorageGrantedForUtil();
				}
				break;


			case 2:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
					requestPermissionForCamera();
				}
				break;

			case 3:
				Log.v("TAG", "Permission Granted: ");
				requestPermissionPhone();

				break;


			case 5:
				Log.v("TAG", "Permission Granted: ");
				login();

				break;


			case 4:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					ImeiNumber = Constants.getIMEIDeviceId(StrSingleUserame, getApplicationContext());
					sharedPref.setImEiNumber(ImeiNumber, LoginActivity.this);
				}
				break;
		}

	}


	private void CheckDeviceIDsStatus() {
		/*try {
			if (global.registrationId == null) {
				GetDeviceTokenForNotification();
			}
			Log.d("token", "token: " + global.registrationId);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		ImeiNumber = Constants.getIMEIDeviceId(StrSingleUserame, getApplicationContext());
		ImeiNumber = checkNullStatus(ImeiNumber);

		if(ImeiNumber.length() == 0){
			ImeiNumber = "000D"+Globally.GetCurrentFullDateTime();
		}

		sharedPref.setImEiNumber(ImeiNumber, LoginActivity.this);

	}






	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onResume() {
		super.onResume();

		IsLoginSuccess = false;
		loginBtn.setEnabled(true);
		// register GCM registration complete receiver
	/*	LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(Config.REGISTRATION_COMPLETE));

		// register new push message receiver
		// by doing this, the activity will be notified each time a new message arrives
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(Config.PUSH_NOTIFICATION));

		// clear the notification area when the app is opened
		NotificationManagerSmart.clearNotifications(getApplicationContext());
		*/

	}



	@Override
	protected void onPause() {
		//LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}


	void login() {

		StrSingleUserame = userNameText.getText().toString().trim();
		StrSinglePass = passwordText.getText().toString();
		StrCoDriverUsername = coDriverUserNameText.getText().toString().trim();
		StrCoDriverPass = coDriverPasswordText.getText().toString();
		StrOSType = "Android - " + AppVersion;

		CheckDeviceIDsStatus();
		getMyPhoneNumber();
		Constants.IsAlsServerResponding = true;


		if (global.isConnected(LoginActivity.this)) {
			//if(global.registrationId != null) {
			if (StrSingleUserame.length() > 0 && StrSinglePass.length() > 0) {
				if (StrSingleUserame.length() > 0) {
					if (StrSinglePass.length() > 0) {
						if (LoginUserType.equals(DriverConst.TeamDriver)) {
							OutToLeftAnim(loginLayout);
							InFromRightAnim(loginCoDriverLayout);
						} else {
							sharedPref.setDriverType(LoginUserType, LoginActivity.this);
							DriverConst.SetDriverLoginDetails(StrSingleUserame, StrSinglePass, LoginActivity.this);
							checkNullInputs();

							/*========== LOGIN API =========== */
							LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername,StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);

						//	ViewOnlyLogin(StrSingleUserame, StrSinglePass);
						}
					} else {
						global.EldScreenToast(mainLoginLayout, "Please enter your Password", getResources().getColor(R.color.colorVoilation));
					}
				} else {
					global.EldScreenToast(mainLoginLayout, "Please enter the UserName", getResources().getColor(R.color.colorVoilation));
				}
			} else {
				global.EldScreenToast(mainLoginLayout, "Please enter your Username and Password", getResources().getColor(R.color.colorVoilation));
			}
		} else {
			global.EldScreenToast(mainLoginLayout, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
		}
	}


	private void checkNullInputs(){
		global.registrationId = checkNullStatus(global.registrationId);
		StrSingleUserame = checkNullStatus(StrSingleUserame);
		StrSinglePass = checkNullStatus(StrSinglePass);
		StrCoDriverUsername = checkNullStatus(StrCoDriverUsername);
		StrCoDriverPass = checkNullStatus(StrCoDriverPass);
		LoginUserType = checkNullStatus(LoginUserType);
		StrOSType = checkNullStatus(StrOSType);
		DeviceSimInfo = checkNullStatus(DeviceSimInfo);
		Sim2 = checkNullStatus(Sim2);

		if(ImeiNumber.length() == 0){
			ImeiNumber = "000D"+Globally.GetCurrentFullDateTime();
		}

	}


	String checkNullStatus(String val){
		try {
			if(val == null || val.equalsIgnoreCase("null") ){
				val = "";
			}
		}catch (Exception e){
			val = "";
			e.printStackTrace();
		}
		return val.trim();
	}


	void LoginUser(final String DeviceId, final String username, final String pass, final String CoDriverUsername,
				   final String CoDriverPassword, final String TeamDriverType, final String OSType,
				   final String DeviceSimInfo, final String Sim2) {


		loginBtn.setEnabled(false);
		try {
			MainDriverEldPref MainDriverPref = new MainDriverEldPref();
			CoDriverEldPref CoDriverPref = new CoDriverEldPref();
			MainDriverPref.ClearLocFromList(LoginActivity.this);
			CoDriverPref.ClearLocFromList(LoginActivity.this);

		} catch (Exception e) { }

		try {
			if(obdUtil == null){
				obdUtil = new Utils(LoginActivity.this);
			}
			constants.saveLoginDetails(username, OSType, DeviceSimInfo, ImeiNumber, obdUtil);
		}catch (Exception e){
			e.printStackTrace();
		}

		if (IsTablet) {
			deviceType = "Tablet";
		} else {
			deviceType = "Mobile";
		}

		RequestQueue queue = Volley.newRequestQueue(this);

		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.show();

		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.LOGIN_USER,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						// response
						Log.d("Response", ">>>response: " + response);
						//	global.SaveFileInSDCard("LoginOutput", response, LoginActivity.this);

						sharedPref.setServiceOnDestoryStatus(false, getApplicationContext());
						//sharedPref.SetConnectionType(constants.ConnectionMalfunction, getApplicationContext());

						try {
							global.obj = new JSONObject(response);
							status = global.obj.getString("Status");
							message = global.obj.getString("Message");
							int rulesVersion = 1;

							if(global.obj.has(ConstantsKeys.rulesVersion) && !global.obj.isNull(ConstantsKeys.rulesVersion) ){
								rulesVersion = global.obj.getInt(ConstantsKeys.rulesVersion);
							}
							sharedPref.SetRulesVersion(rulesVersion, getApplicationContext());

							if (status.equalsIgnoreCase("true")) {

								if (!global.obj.isNull("Data")) {
									//ClearSqliteDB(LoginActivity.this);

									sharedPref.SetCycleOfflineDetails("[]", getApplicationContext());
									sharedPref.SetNewLoginStatus(true, getApplicationContext());
									sharedPref.setLastUsageDataSavedTime("", getApplicationContext());
									sharedPref.SetTruckStartLoginStatus(true, getApplicationContext());
									sharedPref.SaveObdStatus(Constants.NO_CONNECTION, getApplicationContext());

									new ParseLoginJsonData().execute();

								} else {
									loginBtn.setEnabled(true);

									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									global.EldScreenToast(mainLoginLayout, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
								}


							} else if (status.equalsIgnoreCase("false")) {
								loginBtn.setEnabled(true);

								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								if (message.contains("Object reference")) {
									global.EldScreenToast(mainLoginLayout,"Invalid Username/Password.", getResources().getColor(R.color.colorVoilation));
								} else if(message.contains("timeout")){
									global.EldScreenToast(mainLoginLayout,"Please check your internet connection", getResources().getColor(R.color.colorVoilation));
								}else {
									global.EldScreenToast(mainLoginLayout, message, getResources().getColor(R.color.colorVoilation));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							loginBtn.setEnabled(true);
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
						}

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						try {
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
							loginBtn.setEnabled(true);

							Log.d("error", "error: " + error);
							String errorStr = error.toString();

							if (errorStr.contains("Network") || errorStr.contains("NoConnectionError") || errorStr.contains("timeout")) {
								errorStr = "Internet connection problem";
							} else if (errorStr.contains("ServerError")) {
								errorStr = "ALS server not responding";
							}

							global.EldScreenToast(mainLoginLayout, errorStr, getResources().getColor(R.color.colorVoilation));
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}
		) {

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				if (response.headers == null) {
					// cant just set a new empty map because the member is final.
					response = new NetworkResponse(
							response.statusCode,
							response.data,
							Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
							response.notModified,
							response.networkTimeMs);
				}
				return super.parseNetworkResponse(response);
			}

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();

				params.put("DeviceId", DeviceId);
				params.put("Password", pass);
				params.put("Username", username);
				params.put("CoDriverUsername", CoDriverUsername);
				params.put("CoDriverPassword", CoDriverPassword);
				params.put("TeamDriverType", TeamDriverType);
				params.put("IMEINumber", ImeiNumber);
				params.put("OSType", OSType);
				params.put("DeviceType", deviceType);
				params.put("MobileDeviceCurrentDateTime", global.getCurrentDate());

				params.put("SIM1", DeviceSimInfo);
				params.put("SIM2", "");

				return params;
			}
		};

		RetryPolicy policy = new DefaultRetryPolicy(Constants.SocketTimeout60Sec, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);


	}


	void ViewOnlyLogin(final String username, final String pass) {


		try {
			MainDriverEldPref MainDriverPref = new MainDriverEldPref();
			CoDriverEldPref CoDriverPref = new CoDriverEldPref();
			MainDriverPref.ClearLocFromList(LoginActivity.this);
			CoDriverPref.ClearLocFromList(LoginActivity.this);

		} catch (Exception e) {
		}

		if (IsTablet) {
			deviceType = "Tablet";
		} else {
			deviceType = "Mobile";
		}

		RequestQueue queue = Volley.newRequestQueue(this);

		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage("Loading...");
		progressDialog.show();

		StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.LOGIN_DEMO,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						// response
						Log.d("Response", ">>>response: " + response);

						try {
							global.obj = new JSONObject(response);
							status = global.obj.getString("Status");
							message = global.obj.getString("Message");

							if (status.equalsIgnoreCase("true")) {

								if (!global.obj.isNull("Data")) {
									sharedPref.SetNewLoginStatus(false, LoginActivity.this);
									new ParseLoginJsonData().execute();
								} else {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									global.EldScreenToast(mainLoginLayout, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
								}


							} else if (status.equalsIgnoreCase("false")) {
								if (progressDialog != null && progressDialog.isShowing()) {
									progressDialog.dismiss();
								}
								if (message.contains("Object reference")) {
									global.EldScreenToast(mainLoginLayout,"Invalid Username/Password.", getResources().getColor(R.color.colorVoilation));
								} else if(message.contains("timeout")){
									global.EldScreenToast(mainLoginLayout,"Please check your internet connection", getResources().getColor(R.color.colorVoilation));
								}else {
									global.EldScreenToast(mainLoginLayout, message, getResources().getColor(R.color.colorVoilation));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							if (progressDialog != null && progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
						}

					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						Log.d("error", "error: " + error);
						String errorStr = error.toString();

						if(errorStr.contains("Network") || errorStr.contains("NoConnectionError") || errorStr.contains("timeout")){
							errorStr = "Internet connection problem";
						}else if(errorStr.contains("ServerError")){
							errorStr = "ALS server not responding";
						}

						global.EldScreenToast(mainLoginLayout, errorStr, getResources().getColor(R.color.colorVoilation));
					}
				}
		) {

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				if (response.headers == null) {
					// cant just set a new empty map because the member is final.
					response = new NetworkResponse(
							response.statusCode,
							response.data,
							Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
							response.notModified,
							response.networkTimeMs);
				}
				return super.parseNetworkResponse(response);
			}

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();

				params.put("Password", pass);
				params.put("Username", username);
				params.put("TeamDriverType", "1");

				return params;
			}
		};

		int socketTimeout = 60000;   //150 seconds - change to what you want
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);
		queue.add(postRequest);


	}



	private class ParseLoginJsonData extends AsyncTask<String, String, String> {
		private JSONArray dataJson = null;

		@RequiresApi(api = Build.VERSION_CODES.KITKAT)
		@Override
		protected String doInBackground(String... strings) {

			try {
				//Log.d("strings", "strings: " + strings);
				dataJson = new JSONArray(global.obj.getString("Data"));

				ParseLoginDetails parseLoginDetails = new ParseLoginDetails();
				parseLoginDetails.ParseLoginDetails(dataJson, LoginActivity.this);

				sharedPref.setUserName(StrSingleUserame, LoginActivity.this);
				sharedPref.setPassword(StrSinglePass, LoginActivity.this);

				DriverConst.SetDriverLoginDetails(StrSingleUserame, StrSinglePass, LoginActivity.this);
				DriverConst.SetCoDriverLoginDetails(StrCoDriverUsername, StrCoDriverPass, LoginActivity.this);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}


			return null;
		}

		@Override
		protected void onPostExecute(String temp) {

			if(getApplicationContext() != null) {
				try {
					global.EldScreenToast(mainLoginLayout, message, getResources().getColor(R.color.colorPrimary));
					if (progressDialog != null && progressDialog.isShowing()) {
						progressDialog.dismiss();
					}

					// Update UTC date time
					//global.saveCurrentUtcTime(LoginActivity.this);


					IsLoginSuccess = true;
					Intent i = new Intent(LoginActivity.this, TabAct.class);
					i.putExtra("user_type", "login");
					startActivity(i);
					finish();

				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}

	}


	void LoginWithCoDriver() {
		global.hideKeyboard(LoginActivity.this, mainLoginLayout);
		StrCoDriverUsername = coDriverUserNameText.getText().toString().trim();
		StrCoDriverPass = coDriverPasswordText.getText().toString();
		StrOSType = "Android - " + AppVersion;

		CheckDeviceIDsStatus();

		getMyPhoneNumber();
		Constants.IsAlsServerResponding = true;

		if (global.isConnected(LoginActivity.this)) {
			if (StrCoDriverUsername.length() > 0 && StrCoDriverPass.length() > 0) {
				if (StrCoDriverUsername.length() > 0) {
					if (StrCoDriverPass.length() > 0) {

						if (!StrSingleUserame.equals(StrCoDriverUsername)) {
							sharedPref.setDriverType(LoginUserType, LoginActivity.this);
							DriverConst.SetDriverLoginDetails(StrSingleUserame, StrSinglePass, LoginActivity.this);
							DriverConst.SetCoDriverLoginDetails(StrCoDriverUsername, StrCoDriverPass, LoginActivity.this);
							checkNullInputs();

							/*========== LOGIN API =========== */

							LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);

						//	ViewOnlyLogin(StrSingleUserame, StrSinglePass);
						} else {
							global.EldScreenToast(mainLoginLayout, "Please enter different username for Co-Driver.", getResources().getColor(R.color.colorVoilation));
						}
					} else
						global.EldScreenToast(mainLoginLayout, "Please enter your Password", getResources().getColor(R.color.colorVoilation));
				} else
					global.EldScreenToast(mainLoginLayout, "Please enter the UserName", getResources().getColor(R.color.colorVoilation));

			} else
				global.EldScreenToast(mainLoginLayout, "Please enter your Username and Password", getResources().getColor(R.color.colorVoilation));
		} else {
			global.EldScreenToast(mainLoginLayout, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
		}
	}

	void ClearMainDriverFields() {
		userNameText.setText("");
		passwordText.setText("");
	}

	void CleanCoDriverFields() {
		coDriverUserNameText.setText("");
		coDriverPasswordText.setText("");
	}

	private void BackPressedViews() {
		global.hideSoftKeyboard(LoginActivity.this);
		if (loginBtn.getText().toString().equals("Next")) {
			if (loginCoDriverLayout.getVisibility() == View.VISIBLE) {
				CleanCoDriverFields();
				backImgView.setVisibility(View.VISIBLE);
				//loginCoDriverLayout.setVisibility(View.GONE);
				OutToRightAnim(loginCoDriverLayout);
				//loginLayout.setVisibility(View.VISIBLE);
				InFromLeftAnim(loginLayout);
			} else if (loginLayout.getVisibility() == View.VISIBLE) {
				ClearMainDriverFields();
				backImgView.setVisibility(View.GONE);
				//loginLayout.setVisibility(View.GONE);
				OutToRightAnim(loginLayout);
				//userTypeLayout.setVisibility(View.VISIBLE);
				InFromLeftAnim(userTypeLayout);
			}
		} else {
			ClearMainDriverFields();
			backImgView.setVisibility(View.GONE);
			//loginLayout.setVisibility(View.GONE);
			OutToRightAnim(loginLayout);
			//	userTypeLayout.setVisibility(View.VISIBLE);
			InFromLeftAnim(userTypeLayout);
		}
	}

	private void InFromRightAnim(final RelativeLayout view) {
		view.setVisibility(View.VISIBLE);
		view.startAnimation(animation.InFromRightAnimation());
	}

	private void InFromLeftAnim(final RelativeLayout view) {
		view.setVisibility(View.VISIBLE);
		view.startAnimation(animation.InFromLeftAnimation());

	}


	private void OutToRightAnim(final RelativeLayout view) {
		view.setVisibility(View.GONE);
		view.startAnimation(animation.OutToRightAnimation());
	}

	private void OutToLeftAnim(final RelativeLayout view) {
		view.setVisibility(View.GONE);
		view.startAnimation(animation.OutToLeftAnimation());
	}


	private void getMyPhoneNumber() {

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		try {
			DualSimManager info = new DualSimManager(LoginActivity.this);
			String Sim1SerialNumber = "", Sim2SerialNumber = "";
			String BrandName = Build.BRAND;
			String Model = Build.MODEL;
			String Version = Build.VERSION.RELEASE;
			String operatorSIM = info.getNETWORK_OPERATOR_NAME(0);
			String MobileNo = "";

			try {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
					SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
					@SuppressLint("MissingPermission") List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

					if (subsInfoList != null) {
						int count = 0;
						for (SubscriptionInfo subscriptionInfo : subsInfoList) {

							if (count == 0) {
								count = 1;
								Sim1 = subscriptionInfo.getNumber();
								Sim1SerialNumber = subscriptionInfo.getIccId();
							} else {
								Sim2 = subscriptionInfo.getNumber();
								Sim2SerialNumber = subscriptionInfo.getIccId();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}


			global.registrationId 	= checkNullStatus(global.registrationId);
			ImeiNumber 				= checkNullStatus(ImeiNumber);
			StrOSType 				= checkNullStatus(StrOSType);
			Sim1 					= checkNullStatus(Sim1);
			Sim2 					= checkNullStatus(Sim2);
			operatorSIM				= checkNullStatus(operatorSIM);
			Sim1SerialNumber		= checkNullStatus(Sim1SerialNumber);
			MobileNo				= checkNullStatus(MobileNo);


			if(Sim1.length() > 0){
				MobileNo = Sim1;
			}else{
				MobileNo = Sim2;
				operatorSIM = info.getNETWORK_OPERATOR_NAME(1);
				Sim1SerialNumber = Sim2SerialNumber;
			}

			DeviceSimInfo = constants.DeviceSimInfo(MobileNo, Sim1SerialNumber, BrandName, Model, Version, operatorSIM);

		}catch (Exception e){
			e.printStackTrace();
		}

	}




	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.mainLoginLayout:
				global.hideKeyboard(LoginActivity.this, mainLoginLayout);
				break;

			case R.id.coDriverLoginBtn:
				LoginWithCoDriver();
				break;

			case R.id.loginLayout:
				global.hideKeyboard(LoginActivity.this, mainLoginLayout);
				break;

			case R.id.loginBtn:
				global.hideKeyboard(LoginActivity.this, mainLoginLayout);
		// =============== Check storage permission =====================
				isStoragePermissionGranted();
				break;

			case R.id.mainDriverBtn:
				LoginUserType = DriverConst.SingleDriver;
				loginBtn.setText("Login");
				driverTitleTV.setVisibility(View.INVISIBLE);
				backImgView.setVisibility(View.VISIBLE);

				//userTypeLayout.setVisibility(View.GONE);
				OutToLeftAnim(userTypeLayout);
			//	loginLayout.setVisibility(View.VISIBLE);
				InFromRightAnim(loginLayout);
				break;


			case R.id.CoDriverBtn:
				LoginUserType = DriverConst.TeamDriver;
				loginBtn.setText("Next");
				driverTitleTV.setVisibility(View.VISIBLE);
				backImgView.setVisibility(View.VISIBLE);

				//userTypeLayout.setVisibility(View.GONE);
				OutToLeftAnim(userTypeLayout);
				//loginLayout.setVisibility(View.VISIBLE);
				InFromRightAnim(loginLayout);

				break;

			case R.id.backImgView:

				BackPressedViews();
				break;


		}
	}


	@Override
	public void onBackPressed() {

		if(userTypeLayout.getVisibility() == View.VISIBLE){
			super.onBackPressed();
		}else{
			BackPressedViews();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(!IsLoginSuccess && IsTablet) {
			//WiFiConf.ForgetWifiConfig(LoginActivity.this);
		}
	}


}
