package com.als.logistic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.background.service.BleDataService;
import com.constants.APIs;
import com.constants.Anim;
import com.constants.Constants;
import com.constants.DualSimManager;
import com.constants.Logger;
import com.constants.SharedPref;
import com.constants.Utils;
import com.constants.VolleyRequest;
import com.constants.VolleyRequestWithoutRetry;
import com.custom.dialogs.BleAvailableDevicesDialog;
import com.custom.dialogs.TimeZoneDialog;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.local.db.HelperMethods;
import com.local.db.MalfunctionDiagnosticMethod;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.MainDriverEldPref;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends FragmentActivity implements OnClickListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener{

	final int LOCATION_REQUEST          = 101;
	final int STORAGE_REQUEST           = 102;
	final int NEARBY_DEVICES_REQUEST    = 103;
	final int STORAGE_UTIL 				= 104;
	final int FINE_LOCATION 			= 105;

	String userName = "", password = "";
	public static boolean isDriving = false;
	EditText userNameText, passwordText, coDriverUserNameText, coDriverPasswordText;
	TextView driverTitleTV, appVersion, appTypeView, welcomeToAlsTV;
	RelativeLayout mainLoginLayout, loginLayout, userTypeLayout, loginCoDriverLayout, loginScrollChildLay;
	Button loginBtn, mainDriverBtn, CoDriverBtn, coDriverLoginBtn;
	ImageView backImgView, wifiImgBtn, loginBleStatusBtn, askLoginIV, logoImg;
	ProgressDialog progressDialog;
	//private BroadcastReceiver mRegistrationBroadcastReceiver;
	ProgressBar progressBarLogin;
	String ImeiNumber = "", LoginUserType = "", loginResponseData = "", aaa;
	String StrSingleUserame = "", StrSinglePass = "", StrCoDriverUsername = "", StrCoDriverPass = "", StrOSType = "", AppVersion = "";
	String status = "", message = "", deviceType = "";
	Anim animation;
	boolean IsLoginSuccess = false, IsTablet = false, IsBleConnected = false, WiredConnected = false;
	String Sim1 = "", Sim2 = "", DeviceSimInfo = "", TruckID = "", CompanyId = "";
	int ObdPreference = 0;
	Constants constants;
	Globally global;
	Utils obdUtil;
	RetryPolicy policy;
	RequestQueue queue;
	Animation connectionStatusAnimation;
	MalfunctionDiagnosticMethod malfunctionDiagnosticMethod;
	boolean isApiCalled = false;

	BleAvailableDevicesDialog bleAvailableDevicesDialog;
	List<String> availableDevicesList = new ArrayList<>();
	boolean isBleNearByScanCalled = false;
	AlertDialog statusAlertDialog;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if(UILApplication.getInstance().isNightModeEnabled()){
			this.setTheme(R.style.DarkTheme);
		} else {
			this.setTheme(R.style.LightTheme);
		}
		setContentView(R.layout.login_activity);

		//WiFiConf = new WiFiConfig();
		//wifiList = WiFiConf.GetSavedSSIDList();
		//	pos 	 = WiFiConf.getWifiListPosition(this);
		global				= new Globally();
		IsTablet 			= global.isTablet(this);
		constants			= new Constants();
		TruckID             = SharedPref.getTruckNumber(getApplicationContext());   //DriverConst.GetDriverTripDetails(DriverConst.Truck, getApplicationContext());
		CompanyId           = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());


		try{
			if(isStorageGrantedForUtil()) {
				obdUtil = new Utils(LoginActivity.this);
				obdUtil.createLogFile();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		queue 					= Volley.newRequestQueue(this);
		policy 					= new DefaultRetryPolicy(Constants.SocketTimeout60Sec, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		progressBarLogin 		= (ProgressBar) findViewById(R.id.progressBarLogin);
		appVersion 				= (TextView) findViewById(R.id.appVersion);
		driverTitleTV 			= (TextView) findViewById(R.id.driverTitleTV);
		appTypeView				= (TextView) findViewById(R.id.appTypeView);
		welcomeToAlsTV			= (TextView) findViewById(R.id.welcomeToAlsTV);

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
		loginBleStatusBtn		= (ImageView) findViewById(R.id.loginBleStatusBtn);
		askLoginIV				= (ImageView) findViewById(R.id.askLoginIV);
		logoImg					= (ImageView) findViewById(R.id.logoImg);

		loginScrollChildLay	= (RelativeLayout) findViewById(R.id.loginScrollChildLay);
		mainLoginLayout 		= (RelativeLayout) findViewById(R.id.mainLoginLayout);
		loginLayout 			= (RelativeLayout) findViewById(R.id.loginLayout);
		userTypeLayout 			= (RelativeLayout) findViewById(R.id.userTypeLayout);
		loginCoDriverLayout 	= (RelativeLayout) findViewById(R.id.loginCoDriverLayout);
		//mTelephonyManager 	= (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		malfunctionDiagnosticMethod = new MalfunctionDiagnosticMethod();
		statusAlertDialog =  new AlertDialog.Builder(getApplicationContext()).create();

		backImgView.setVisibility(View.GONE);

		progressBarLogin.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY);

		AppVersion = global.GetAppVersion(this, "VersionName");
		StrOSType = "Android - " + AppVersion;
		appVersion.setText("Version " + AppVersion);

		if(APIs.DOMAIN_URL_ALS.contains("dev.alsrealtime.com") || APIs.DOMAIN_URL_ALS.contains("104.167.9") ){
			appTypeView.setVisibility(View.VISIBLE);
			userNameText.setText(Globally.TEMP_USERNAME);
			passwordText.setText(Globally.TEMP_PASSWORD);

		}else if(APIs.DOMAIN_URL_ALS.contains("182.73.78") || APIs.DOMAIN_URL_ALS.contains("192.168.0")){
			appTypeView.setVisibility(View.VISIBLE);
			userNameText.setText(Globally.TEMP_USERNAME);
			passwordText.setText(Globally.TEMP_PASSWORD);

			if(APIs.DOMAIN_URL_ALS.contains("182.73.78")){
				appTypeView.setText("INDIAN Open");
			}else{
				appTypeView.setText("INDIAN Lan");
			}
		}else{
			appTypeView.setVisibility(View.GONE);
		}


		//if(!IsTablet)
		wifiImgBtn.setVisibility(View.GONE);

		animation = new Anim();
		File apkStorageDir = new File(LoginActivity.this.getExternalFilesDir(null), "Logistic");
		global.DeleteDirectory(apkStorageDir.toString());
		try {
			if(SharedPref.getUserName( this).length() == 0) {
				MainDriverEldPref MainDriverPref = new MainDriverEldPref();
				CoDriverEldPref CoDriverPref = new CoDriverEldPref();
				MainDriverPref.ClearLogFromList(this);
				CoDriverPref.ClearCoDrLogFromList(this);
			}
		} catch (Exception e) {
		}

		ObdPreference = SharedPref.getObdPreference(getApplicationContext());
		if(ObdPreference == Constants.OBD_PREF_BLE){
			loginBleStatusBtn.setImageResource(R.drawable.ble_ic);
			if(CompanyId.length() == 0) {
				loginBleStatusBtn.setVisibility(View.GONE);
			}

		}else if (ObdPreference == Constants.OBD_PREF_WIRED){
			loginBleStatusBtn.setImageResource(R.drawable.obd_inactive);
		}


		connectionStatusAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		connectionStatusAnimation.setDuration(1500);

		connectionStatusAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				try {
					if(getApplicationContext() != null) {

						if(ObdPreference == Constants.OBD_PREF_BLE) {

							if(TruckID.length() > 0 && CompanyId.length() > 0) {
								if (IsBleConnected) {
									connectionStatusAnimation.cancel();
									loginBleStatusBtn.setAlpha(1f);
									loginBleStatusBtn.setColorFilter(getResources().getColor(R.color.colorPrimary));

								} else {
									loginBleStatusBtn.startAnimation(connectionStatusAnimation);
								}
							}else{
								connectionStatusAnimation.cancel();
								loginBleStatusBtn.setVisibility(View.GONE);
							}
						}else if(ObdPreference == Constants.OBD_PREF_WIRED){
							if (WiredConnected) {
								connectionStatusAnimation.cancel();
								loginBleStatusBtn.setAlpha(1f);
								loginBleStatusBtn.setColorFilter(getResources().getColor(R.color.colorPrimary));

							} else {
								loginBleStatusBtn.startAnimation(connectionStatusAnimation);
							}
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});


		mainLoginLayout.setOnClickListener(this);
		loginLayout.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		mainDriverBtn.setOnClickListener(this);
		CoDriverBtn.setOnClickListener(this);
		backImgView.setOnClickListener(this);
		wifiImgBtn.setOnClickListener(this);
		coDriverLoginBtn.setOnClickListener(this);
		loginBleStatusBtn.setOnClickListener(this);

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


		// Get Phone Number from Sim Card
		getMyPhoneNumber();

		Intent i = getIntent();
		if (i.hasExtra("EXIT")) {
			if (i.getBooleanExtra("EXIT", false)) {
				finish();
			}
		}

		userName = SharedPref.getUserName( LoginActivity.this);
		password = SharedPref.getPassword( LoginActivity.this);

		Logger.LogDebug("", "-----username: " +userName + ", pass: " +password);

		CheckUserCredientials();

	}


	/*========= Some times on splash screen user cred returns null and login screen opens default, we are cross checking here user cred if exist then open home screen =============*/
	void CheckUserCredientials(){
		if (userName.length() > 0 && password.length() > 0) {
			Intent intent = new Intent(LoginActivity.this, TabAct.class);
			intent.putExtra("user_type", "splash");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	protected void onResume() {
		super.onResume();

		if (userName.length() == 0 && password.length() == 0) {
			IsLoginSuccess = false;
			loginBtn.setEnabled(true);
			ObdPreference = SharedPref.getObdPreference(getApplicationContext());

			if (ObdPreference == Constants.OBD_PREF_BLE) {

				if (!IsBleConnected && CompanyId.length() > 0) {
					loginBleStatusBtn.setVisibility(View.VISIBLE);
					loginBleStatusBtn.startAnimation(connectionStatusAnimation);
				}

			} else if (ObdPreference == Constants.OBD_PREF_WIRED) {
				loginBleStatusBtn.setVisibility(View.VISIBLE);

				if (!WiredConnected) {
					loginBleStatusBtn.startAnimation(connectionStatusAnimation);
				}

			}

			if (UILApplication.getInstance().isNightModeEnabled()) {
				askLoginIV.setColorFilter(getResources().getColor(R.color.dark_cream_white));
				logoImg.setColorFilter(getResources().getColor(R.color.dark_cream_white));
			}


			startService();
			UILApplication.activityResumed();

		}

		LocalBroadcastManager.getInstance(LoginActivity.this).registerReceiver(progressReceiver, new IntentFilter(ConstantsKeys.IsEventUpdate));

	}






	private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			try{

				boolean IsEventUpdate = intent.getBooleanExtra(ConstantsKeys.IsEventUpdate, false);
				boolean Status = intent.getBooleanExtra(ConstantsKeys.Status, false);
				String IsEldEcmAlert = intent.getStringExtra(ConstantsKeys.IsEldEcmALert);
				boolean EcmAlertStatus = false;
				if(IsEldEcmAlert != null && IsEldEcmAlert.length() > 0) {
					EcmAlertStatus = IsEldEcmAlert.equals("Yes") || IsEldEcmAlert.equals("No");
				}

				if(IsEventUpdate){
					if(Status){

						if(ObdPreference == Constants.OBD_PREF_BLE ){
							IsBleConnected = true;
						}else if(ObdPreference == Constants.OBD_PREF_WIRED){
							WiredConnected = true;
						}

						if(!connectionStatusAnimation.hasEnded()) {
							connectionStatusAnimation.cancel();
						}

						loginBleStatusBtn.setAlpha(1f);
						loginBleStatusBtn.setColorFilter(getResources().getColor(R.color.colorPrimary));



					}else if(EcmAlertStatus){
						if(IsEldEcmAlert.equals("Yes")) {
							global.InternetErrorDialog(LoginActivity.this, true, false);
						}else{
							global.InternetErrorDialog(LoginActivity.this, false, false);
						}
					}else{

						if(ObdPreference == Constants.OBD_PREF_BLE) {
							IsBleConnected = false;
						}else if(ObdPreference == Constants.OBD_PREF_WIRED){
							WiredConnected = false;
						}

						if(CompanyId.length() > 0) {
							loginBleStatusBtn.setColorFilter(getResources().getColor(R.color.black_transparent));
							loginBleStatusBtn.startAnimation(connectionStatusAnimation);
						}
					}

				}else{
					if(intent.hasExtra(ConstantsKeys.BleDevices)){
						try {
							availableDevicesList = new ArrayList<>();

							String availableDevices = intent.getStringExtra(ConstantsKeys.BleDevices);

							if(availableDevices != null) {
								String[] deviceArray = availableDevices.split("@@@");

								if (!availableDevices.equals("")) {
									for (int i = 0; i < deviceArray.length; i++) {
										availableDevicesList.add(deviceArray[i]);
									}
								}
								if (availableDevicesList.size() > 0) {
									if (bleAvailableDevicesDialog != null && bleAvailableDevicesDialog.isShowing()) {
										// send broadcast
										sendDeviceCast(availableDevices);
									} else {
										bleAvailableDevicesDialog = new BleAvailableDevicesDialog(LoginActivity.this,
												availableDevicesList, new BleDevicesListener());
										bleAvailableDevicesDialog.show();
									}
								} else {
									if (bleAvailableDevicesDialog != null && bleAvailableDevicesDialog.isShowing()) {
										bleAvailableDevicesDialog.dismiss();
									}
								}
							}else {
								if (bleAvailableDevicesDialog != null && bleAvailableDevicesDialog.isShowing()) {
									bleAvailableDevicesDialog.dismiss();
									Toast.makeText(LoginActivity.this, getString(R.string.ble_turned_off), Toast.LENGTH_SHORT).show();
								}
							}

						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	};


	private void sendDeviceCast(String BleDevices){
		try{
			Intent intent = new Intent(ConstantsKeys.BleDataNotifier);
			intent.putExtra(ConstantsKeys.BleDataAfterNotify, BleDevices);
			LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(intent);

		}catch (Exception e){}
	}

	/*================== Ble Multiple device handler Listener ====================*/
	private class BleDevicesListener implements BleAvailableDevicesDialog.BleDevicesListener {

		@Override
		public void SelectedDeviceBtn(String selectedDevice) {
			SharedPref.SetPingStatus("device", LoginActivity.this);
			TabAct.SelectDeviceName = selectedDevice;
			TabAct.SelectDevice = true;

			Intent serviceIntent = new Intent(LoginActivity.this, AfterLogoutService.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(serviceIntent);
			}
			startService(serviceIntent);

		}
	}


	public static boolean isImmersiveAvailable() {
        return Build.VERSION.SDK_INT >= 19;
    }



	public boolean isStoragePermissionGranted() {
		global.hideKeyboard(LoginActivity.this, mainLoginLayout);
		if (Build.VERSION.SDK_INT >= 23) {
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {
				Logger.LogVerbose("TAG", "Permission is granted");

				requestLocationPermission(false);

				return true;
			} else {
				Logger.LogVerbose("TAG", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						STORAGE_REQUEST);
				Toast.makeText(this, getString(R.string.storage_per_revoked), Toast.LENGTH_SHORT).show();
				//requestLocationPermission();

				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Logger.LogVerbose("TAG", "Permission is granted");
			login();
			return true;
		}

	}



	public boolean isStorageGrantedForUtil() {
		try {
			if (Build.VERSION.SDK_INT >= 23) {
				if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
					return true;
				} else {
					Logger.LogVerbose("TAG", "Permission is revoked");
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_UTIL);
					return false;
				}
			} else { //permission is automatically granted on sdk<23 upon installation
				Logger.LogVerbose("TAG", "Permission is granted");
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}


	}


	private boolean requestLocationPermission(boolean IsbleService) {


		if (Build.VERSION.SDK_INT >= 23) {
			int PreciseLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			int ApproximateLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);


			if (PreciseLocation != PackageManager.PERMISSION_GRANTED &&
					ApproximateLocation != PackageManager.PERMISSION_GRANTED) {

				Logger.LogVerbose("TAG", "Permission is revoked");
				if(IsbleService){
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_LOCATION);
					Toast.makeText(this, getString(R.string.loc_per_denied), Toast.LENGTH_LONG).show();
				}else {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION,
							Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST);
					Toast.makeText(this, getString(R.string.loc_per_revoked), Toast.LENGTH_SHORT).show();
				}
				//requestPermissionPhone();
				return false;

			} else {

				if(ApproximateLocation == PackageManager.PERMISSION_GRANTED && PreciseLocation != PackageManager.PERMISSION_GRANTED){
					ActivityCompat.requestPermissions(LoginActivity.this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
							LOCATION_REQUEST);
				}else {
					if (IsbleService) {
						if (constants.CheckGpsStatusToCheckMalfunction(this)) {
							BleDataService.IsScanClick = true;
							SharedPref.SetPingStatus("ble_start", getApplicationContext());
							loginBleStatusBtn.startAnimation(connectionStatusAnimation);
							startService();
						} else {
							global.EldScreenToast(mainLoginLayout, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
						}
					} else {
						isNearByDevicesGranted();
						//login();
					}
				}
				return true;
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Logger.LogVerbose("TAG", "Permission is granted");
			if(IsbleService){
				if (constants.CheckGpsStatusToCheckMalfunction(this)) {
					BleDataService.IsScanClick = true;
					SharedPref.SetPingStatus("ble_start", getApplicationContext());
					loginBleStatusBtn.startAnimation(connectionStatusAnimation);
					startService();
				} else {
					global.EldScreenToast(mainLoginLayout, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
				}
			}else {
				login();
			}
			return true;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);


		switch (requestCode) {

			case STORAGE_REQUEST:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Logger.LogVerbose("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
					//resume tasks needing this permission
					requestLocationPermission(false);
				}
				break;

			case STORAGE_UTIL:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Logger.LogVerbose("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);
					try{
						obdUtil = new Utils(LoginActivity.this);
						obdUtil.createLogFile();
					}catch (Exception e){
						e.printStackTrace();
					}
				}

				break;


			case LOCATION_REQUEST:
				isNearByDevicesGranted();
				//login();
				break;

			case 5:
				Logger.LogVerbose("TAG", "Permission Granted: ");
				isNearByDevicesGranted();
				//login();

				break;


			case NEARBY_DEVICES_REQUEST:
				Logger.LogVerbose("TAG", "NearBy Permission Granted: ");
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					login();
				}
				break;

			case 4:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					ImeiNumber = Constants.getIMEIDeviceId(StrSingleUserame, getApplicationContext());
					SharedPref.setImEiNumber(ImeiNumber, LoginActivity.this);
				}
				break;

			case FINE_LOCATION:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (constants.CheckGpsStatusToCheckMalfunction(this)) {
						BleDataService.IsScanClick = true;
						SharedPref.SetPingStatus("ble_start", getApplicationContext());
						loginBleStatusBtn.startAnimation(connectionStatusAnimation);
						startService();
					} else {
						global.EldScreenToast(mainLoginLayout, getResources().getString(R.string.gps_alert), getResources().getColor(R.color.colorVoilation));
					}
				}
				break;
		}

	}


	public  boolean isNearByDevicesGranted() {
		if (Build.VERSION.SDK_INT > 30) {
			if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
					== PackageManager.PERMISSION_GRANTED) {
				Logger.LogVerbose("TAG","Permission is granted");
				login();

				return true;
			} else {
				Logger.LogVerbose("TAG","Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN,
						Manifest.permission.BLUETOOTH_CONNECT}, NEARBY_DEVICES_REQUEST);

				if(isBleNearByScanCalled) {
					login();
				}
				isBleNearByScanCalled = true;


				return false;
			}
		} else { //permission is automatically granted on sdk<23 upon installation


			Logger.LogVerbose("TAG","Permission is granted");
			login();
			return true;

		}

	}


	private void CheckDeviceIDsStatus() {

		ImeiNumber = Constants.getIMEIDeviceId(StrSingleUserame, getApplicationContext());
		ImeiNumber = checkNullStatus(ImeiNumber);

		if(ImeiNumber.length() == 0){
			ImeiNumber = "000D"+Globally.GetCurrentFullDateTime();
		}

		SharedPref.setImEiNumber(ImeiNumber, LoginActivity.this);

	}







	private void startService(){
		/*========= Start Logout Service to check truck is moving in logout=============*/
		try {
			Intent serviceIntent = new Intent(LoginActivity.this, AfterLogoutService.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(serviceIntent);
			}
			startService(serviceIntent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		UILApplication.activityPaused();

		LocalBroadcastManager.getInstance(LoginActivity.this).unregisterReceiver(progressReceiver);

	}

	protected void onStop() {
		super.onStop();
		UILApplication.activityPaused();

	}



	void login() {

	/*	DateTime selectedDateTime = Globally.getDateTimeObj("2022-08-07T00:00:00", false);
		DateTime currentUtcDate = Globally.getDateTimeObj("2022-08-07T23:59:59", false);
		long secDiff = Constants.getDateTimeDuration(selectedDateTime, currentUtcDate).getStandardSeconds();
		long minDiff = Constants.getDateTimeDuration(selectedDateTime, currentUtcDate).getStandardMinutes();
		//int dayDiff = Constants.getDayDiff(selectedDateTime.toString(), currentUtcDate.toString());
		double secInDouble = secDiff/60;
		double secIn2 = secInDouble/60;

		Logger.LogDebug("secDiff","secDiff:" +secDiff);
		Logger.LogDebug("dayDiff","dayDiff:" +minDiff);
		Logger.LogDebug("secInDouble","secInDouble:" +secIn2);
*/

	/*	int dayDiff = 1;
		boolean isMalfunctionForClear = malfunctionDiagnosticMethod.isMalfunctionEvent(Constants.PowerComplianceMalfunction) && dayDiff != 0;
		boolean isMalfunctionForClear1 = malfunctionDiagnosticMethod.isMalfunctionEvent(Constants.DataTransferDiagnostic) && dayDiff != 0;
		boolean isMalfunctionForClear2 = malfunctionDiagnosticMethod.isMalfunctionEvent(Constants.DataTransferMalfunction) && 0 != 0;
		Logger.LogDebug("secDiff","isMalfunctionForClear:" +isMalfunctionForClear);
		Logger.LogDebug("secDiff","isMalfunctionForClear1:" +isMalfunctionForClear1);
		Logger.LogDebug("secDiff","isMalfunctionForClear2:" +isMalfunctionForClear2);
*/

		StrSingleUserame = userNameText.getText().toString().trim();
		StrSinglePass = passwordText.getText().toString();
		StrCoDriverUsername = coDriverUserNameText.getText().toString().trim();
		StrCoDriverPass = coDriverPasswordText.getText().toString();
		StrOSType = "Android-" + Build.VERSION.RELEASE + " | ELD-" + AppVersion ;

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
							SharedPref.setDriverType(LoginUserType, LoginActivity.this);
							DriverConst.SetDriverLoginDetails(StrSingleUserame, StrSinglePass, LoginActivity.this);
							checkNullInputs();

							/*========== LOGIN API =========== */
							//LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);
							GetServerCurrentUtcTime();
						}
					} else {
						global.EldScreenToast(mainLoginLayout, getString(R.string.enter_your_pass), getResources().getColor(R.color.colorVoilation));
					}
				} else {
					global.EldScreenToast(mainLoginLayout, getString(R.string.enter_userName), getResources().getColor(R.color.colorVoilation));
				}
			} else {
				global.EldScreenToast(mainLoginLayout, getString(R.string.enter_username_pass), getResources().getColor(R.color.colorVoilation));
			}
		} else {
			global.EldScreenToast(mainLoginLayout, global.INTERNET_MSG, getResources().getColor(R.color.colorVoilation));
		}
	}




	VolleyRequest.VolleyCallback ResponseCallBack = new VolleyRequest.VolleyCallback() {

		@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void getResponse(String response, int flag) {

			Logger.LogDebug("response", "TimeZone logout response: " + response);

			try {
				String status = "";

				JSONObject obj = new JSONObject(response);
				status = obj.getString("Status");

				if(status.equalsIgnoreCase("true")){

					// Save current UTC date time
					SharedPref.setCurrentUTCTime( obj.getString("Data") , getApplicationContext() );
					boolean isCorrectTime = global.isCorrectLoginTime(getApplicationContext());

					if( isCorrectTime){
						/*========== LOGIN API =========== */
						LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);
					}else{
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						global.EldScreenToast(mainLoginLayout, "Incorrect device time. Set your time automatic or correct time from settings and try again." , getResources().getColor(R.color.colorVoilation));
					}


				}else{

					if (global.isCurrentTimeBigger(getApplicationContext())) {
						SharedPref.setCurrentUTCTime(global.GetCurrentUTCTimeFormat(), getApplicationContext());
						/*========== LOGIN API =========== */
						LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);
					} else {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						global.EldScreenToast(mainLoginLayout, "Connection Error !!" , getResources().getColor(R.color.colorVoilation));
					}
				}

			}catch(Exception e){
				e.printStackTrace();
			}

		}
	};

	VolleyRequest.VolleyErrorCall ErrorCallBack = new VolleyRequest.VolleyErrorCall() {

		@Override
		public void getError(VolleyError error, int flag) {
			 Logger.LogDebug("onDuty error", "onDuty error: " + error.toString());

			try {
				if(SharedPref.getCurrentUTCTime(getApplicationContext()).length() == 0){
					SharedPref.setCurrentUTCTime(global.GetCurrentUTCTimeFormat(), getApplicationContext());
					global.EldScreenToast(mainLoginLayout, "Connection Error !!" , getResources().getColor(R.color.colorVoilation));
				}else {
					if (global.isCurrentTimeBigger(getApplicationContext())) {
						SharedPref.setCurrentUTCTime(global.GetCurrentUTCTimeFormat(), getApplicationContext());

						LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);
					} else {
						if (progressDialog != null && progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						global.EldScreenToast(mainLoginLayout, "Connection Error !!" , getResources().getColor(R.color.colorVoilation));
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	};



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


	private void resetValues(){
		try {
			Constants.IsHomePageOnCreate = true;
			Constants.IsInspectionDetailViewBack = false;
			HelperMethods hMethods  = new HelperMethods();
			DBHelper dbHelper 		= new DBHelper(LoginActivity.this);

			SharedPref.setVINNumber( "", getApplicationContext());
			SharedPref.SetCycleOfflineDetails("[]", getApplicationContext());
			SharedPref.SetNewLoginStatus(true, getApplicationContext());
			SharedPref.setLastUsageDataSavedTime("", getApplicationContext());
			SharedPref.SetTruckStartLoginStatus(true, getApplicationContext());
			SharedPref.SetAfterLoginConfStatus(false, getApplicationContext());
			SharedPref.SaveObdStatus(Constants.NO_CONNECTION,  "", "", getApplicationContext());
			SharedPref.setRefreshDataTime("", getApplicationContext());
			SharedPref.setDayStartOdometer("-1", "-1", "", getApplicationContext());
			SharedPref.setCertifyAlertViewTime("", getApplicationContext());
			SharedPref.setEldOccurences(false, false, false, false, getApplicationContext());
			SharedPref.setEldOccurencesCo(false, false, false, false, getApplicationContext());
			SharedPref.setSuggestedRecallStatus(true, getApplicationContext());
			SharedPref.setSuggestedRecallStatusCo(true, getApplicationContext());
			SharedPref.setUnidentifiedAlertViewStatus(true, getApplicationContext());
			SharedPref.setUnidentifiedAlertViewStatusCo(true, getApplicationContext());
			SharedPref.setVehicleVin("", getApplicationContext());
			SharedPref.setDriverStatusId("", getApplicationContext());
			SharedPref.setVehilceMovingStatus(false, getApplicationContext());
			SharedPref.saveHighPrecisionOdometer("", "", getApplicationContext());
			SharedPref.setEcmObdLocationWithTime("0", "0", "0", "","", getApplicationContext());
			SharedPref.setLocationEventType("", getApplicationContext());
			SharedPref.saveLocMalfunctionOccurStatus(false, "", "",getApplicationContext());
			SharedPref.SetCycleRequestStatusCo(false, getApplicationContext());
			SharedPref.SetCycleRequestStatusMain(false, getApplicationContext());
			SharedPref.SetCycleRequestAlertViewStatus(false, getApplicationContext());
			SharedPref.SetELDNotificationAlertViewStatus(false, getApplicationContext());
			SharedPref.SetELDNotification(false, getApplicationContext());
			SharedPref.setDrivingAllowedStatus(true, "", getApplicationContext());
			SharedPref.saveEngSyncDiagnstcStatus(false, getApplicationContext());
			SharedPref.saveEngSyncMalfunctionStatus(false, getApplicationContext());
			SharedPref.SetWiredObdCallTime("", getApplicationContext());
			SharedPref.SetCoDriverSwitchTime("", getApplicationContext());
			SharedPref.setPersonalUse75Km(false, getApplicationContext());
			SharedPref.setDeferralForMain(false, "", "0", getApplicationContext());
			SharedPref.setDeferralForCo(false, "", "0", getApplicationContext());
			SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
		//	SharedPref.SaveTruckInfoOnIgnitionChange("", "","", "","0", "0", getApplicationContext());
			SharedPref.setUnIdenLastDutyStatus("", getApplicationContext());
			SharedPref.SaveUnidentifiedIntermediateRecord("", "", "", "", "", getApplicationContext());
			SharedPref.setUnAssignedVehicleMilesId("0", getApplicationContext());
			SharedPref.setIntermediateLogId("0", getApplicationContext());
			SharedPref.setTotalPUOdometerForDay("-1","", getApplicationContext());
			SharedPref.SetWrongVinAlertView(false, getApplicationContext());
			SharedPref.saveParticularMalDiaStatus( false ,false ,false ,false ,false , getApplicationContext());
			SharedPref.setTruckNumber("", getApplicationContext());
			SharedPref.saveCoDriverEngSyncDiaStatus(false, getApplicationContext());

			SharedPref.saveMissingDiaStatus(false, getApplicationContext());
			constants.saveMalfncnStatus(getApplicationContext(), false);
			SharedPref.SetObdOdometer("0", getApplicationContext());
			SharedPref.SetObdEngineHours("0", getApplicationContext());
			SharedPref.SaveBleOBDMacAddress("", getApplicationContext());
			SharedPref.SetLocReceivedFromObdStatus(false, getApplicationContext());
			SharedPref.SetIgnitionOffCalled(false, getApplicationContext());
			SharedPref.setAgricultureExemption(false, getApplicationContext());
			SharedPref.saveCoDriverSwitchingStatus(false, getApplicationContext());
			SharedPref.setObdStatusAfterLogin(false, getApplicationContext());
			// set edited log status false
			SharedPref.SetMainDriverEditedLogStatus(false, getApplicationContext());
			SharedPref.SetCoDriverEditedLogStatus(false, getApplicationContext());

			SharedPref.SetHighPrecisionUnit("M", getApplicationContext());
			SharedPref.setPuExceedCheckDate("", getApplicationContext());

			SharedPref.saveTimingMalfunctionStatus(false, "", getApplicationContext());
			SharedPref.saveTimingMalfunctionWarningTime("", getApplicationContext());

			SharedPref.setCheckUnassignedReqTime(Globally.GetCurrentUTCTimeFormat(), getApplicationContext());
			SharedPref.saveLocDiagnosticStatus(SharedPref.isLocDiagnosticOccur(getApplicationContext()), Globally.GetDriverCurrentDateTime(global, getApplicationContext()),
					Globally.GetCurrentUTCTimeFormat(), getApplicationContext());

			SharedPref.saveOtherMalDiaStatus( false, false, false, false,
					false, false, false, getApplicationContext());
			//SharedPref.setApiCallStatus("[]", getApplicationContext());
			SharedPref.updateApiCallStatus( 0, false, global, getApplicationContext());
			SharedPref.setUnIdenLastDutyStatus("", getApplicationContext());
			SharedPref.setAutomaticTimeAlertWithStatus("", false, getApplicationContext());

			// clear array in table
			if(CompanyId.length() > 0) {
				hMethods.UnidentifiedRecordLogHelper(Integer.parseInt(CompanyId), dbHelper, new JSONArray());
			}

			Constants.ClearNotifications(LoginActivity.this);

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	void clearUnIdentifiedRecordBeforeLogin(){
		//String LastDutyStatus = SharedPref.getUnIdenLastDutyStatus(getApplicationContext());
		//if(LastDutyStatus.equals("DR") || LastDutyStatus.equals("OD")){
		SharedPref.SetPingStatus(ConstantsKeys.ClearUnIdentifiedData, LoginActivity.this);
		startService();
		//}
	}


	void LoginUser(final String DeviceId, final String username, final String pass, final String CoDriverUsername,
				   final String CoDriverPassword, final String TeamDriverType, final String OSType,
				   final String DeviceSimInfo, final String Sim2) {

		//if(SharedPref.isLoginAllowed(LoginActivity.this)) {

			if(!isApiCalled) {
				isApiCalled = true;
				clearUnIdentifiedRecordBeforeLogin();

				loginBtn.setEnabled(false);
				/*try {
					MainDriverEldPref MainDriverPref = new MainDriverEldPref();
					CoDriverEldPref CoDriverPref = new CoDriverEldPref();
					MainDriverPref.ClearLogFromList(LoginActivity.this);
					CoDriverPref.ClearCoDrLogFromList(LoginActivity.this);

				} catch (Exception e) {
				}*/

				try {
					if (obdUtil == null) {
						obdUtil = new Utils(LoginActivity.this);
					}

					// delete previous obd server logs
					obdUtil.deleteServerObdLog();

					constants.saveLoginDetails(username, OSType, DeviceSimInfo, ImeiNumber, obdUtil);

					// delete previous obd server logs
					//obdUtil.deleteServerObdLog();

				} catch (Exception e) {
					e.printStackTrace();
				}

				if (IsTablet) {
					deviceType = "Tab";
				} else {
					deviceType = "Mob";
				}

				/*progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setMessage("Loading...");
				progressDialog.setCancelable(false);
				progressDialog.show();
*/
				StringRequest postRequest = new StringRequest(Request.Method.POST, APIs.LOGIN_NEW,
						new Response.Listener<String>() {
							@Override
							public void onResponse(String response) {
								Logger.LogDebug("Response", ">>>response: " + response);

								isApiCalled = false;
								SharedPref.setServiceOnDestoryStatus(false, getApplicationContext());

								//SharedPref.SetConnectionType(constants.ConnectionMalfunction, getApplicationContext());
								Globally.TEMP_USERNAME = userNameText.getText().toString();
								Globally.TEMP_PASSWORD = passwordText.getText().toString();

								try {

									JSONObject obj = new JSONObject(response);
									status = obj.getString("Status");
									message = obj.getString("Message");
									int rulesVersion = 1;

									if (obj.has(ConstantsKeys.rulesVersion) && !obj.isNull(ConstantsKeys.rulesVersion)) {
										rulesVersion = obj.getInt(ConstantsKeys.rulesVersion);
									}

									SharedPref.SetRulesVersion(rulesVersion, getApplicationContext());

									if (status.equalsIgnoreCase("true")) {

										try {
											//global.DisConnectBleDevice(LoginActivity.this);
											/*String CompanyId = DriverConst.GetDriverDetails(DriverConst.CompanyId, getApplicationContext());
											if (CompanyId.length() > 0) {
												malfunctionDiagnosticMethod.UnidentifiedLogoutRecordHelper(Integer.valueOf(CompanyId),
														new DBHelper(getApplicationContext()), new JSONArray());
											}*/
										} catch (Exception e) {
											e.printStackTrace();
										}

										if (!obj.isNull("Data")) {
											loginResponseData = obj.getString("Data");
											// reset user data
											Constants.IS_ELD_ON_CREATE = true;


											resetValues();

											new ParseLoginJsonData().execute();

										} else {
											loginBtn.setEnabled(true);
											if (progressDialog != null && progressDialog.isShowing()) {
												progressDialog.dismiss();
											}

											global.EldScreenToast(mainLoginLayout, "Error, Info not found." + message, getResources().getColor(R.color.colorVoilation));
										}


									} else if (status.equalsIgnoreCase("false")) {
										loginBtn.setEnabled(true);
										if (progressDialog != null && progressDialog.isShowing()) {
											progressDialog.dismiss();
										}

										String errorStr = constants.getErrorMsg(message);
										global.EldScreenToast(mainLoginLayout, errorStr, getResources().getColor(R.color.colorVoilation));

										global.DriverSwitchAlertWithDismiss(LoginActivity.this, "App Update !!", errorStr,
												"Ok", statusAlertDialog, false);
									}
								} catch (Exception e) {
									e.printStackTrace();
									loginBtn.setEnabled(true);
									global.EldScreenToast(mainLoginLayout, "Error", getResources().getColor(R.color.colorVoilation));

									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}

								}

							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								isApiCalled = false;

								try {
									if (progressDialog != null && progressDialog.isShowing()) {
										progressDialog.dismiss();
									}
									loginBtn.setEnabled(true);

									Logger.LogDebug("error", "error: " + error);
									queue 					= Volley.newRequestQueue(LoginActivity.this);

									String message = constants.getErrorMsg(error.toString());

									global.EldScreenToast(mainLoginLayout, message, getResources().getColor(R.color.colorVoilation));
								} catch (Exception e) {
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


						String utcDate = Globally.GetCurrentUTCTimeFormat();
						String date = global.GetDriverCurrentDateTime(global, getApplicationContext());
						String IsOffsetAvailable = IsOffsetAvailable(username, CoDriverUsername);

						params.put(ConstantsKeys.DeviceId, DeviceId);
						params.put(ConstantsKeys.Password, pass);
						params.put(ConstantsKeys.Username, username);
						params.put(ConstantsKeys.CoDriverUsername, CoDriverUsername);
						params.put(ConstantsKeys.CoDriverPassword, CoDriverPassword);
						params.put(ConstantsKeys.TeamDriverType, TeamDriverType);
						params.put(ConstantsKeys.IMEINumber, ImeiNumber);
						params.put(ConstantsKeys.OSType, OSType);
						params.put(ConstantsKeys.DeviceType, deviceType);
						params.put(ConstantsKeys.MobileDeviceCurrentDateTime, date);
						params.put(ConstantsKeys.MobileUtcDate, utcDate);

						params.put(ConstantsKeys.SIM1, DeviceSimInfo);

						params.put(ConstantsKeys.IsOffsetAvailable, IsOffsetAvailable);
						params.put(ConstantsKeys.AppBuildVersion, AppVersion);

						//IsOffsetAvailable   AppBuildVersion
						//Logger.LogDebug("DateLogin", ">>>MobileDeviceCurrentDateTime: " +date);

						return params;
					}
				};

				postRequest.setRetryPolicy(policy);
				queue.add(postRequest);

			}

		/*}else{
			global.EldScreenToast(mainLoginLayout, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
					getString(R.string.login_speed_alert), getResources().getColor(R.color.colorVoilation));
		}*/

	}



	String IsOffsetAvailable(String mainDriverUsername, String CoDriverUsername){
		String IsOffsetAvailable = "false";
		String lastLoginMainDr = SharedPref.getLastLoginMainDriver(getApplicationContext());
		String lastLoginCoDr = SharedPref.getLastLoginCoDriver(getApplicationContext());
		long savedOffSet = SharedPref.getDriverTimeZoneOffSet(getApplicationContext());
		if(savedOffSet < 0 ){
			if(lastLoginCoDr.length() > 0){
				if(CoDriverUsername.length() == 0) {
					if (lastLoginMainDr.equals(mainDriverUsername) || lastLoginCoDr.equals(mainDriverUsername)) {
						IsOffsetAvailable = "true";
					}
				}else{
					if ( (lastLoginMainDr.equals(mainDriverUsername) || lastLoginMainDr.equals(CoDriverUsername)) ||
							(lastLoginCoDr.equals(mainDriverUsername) || lastLoginCoDr.equals(CoDriverUsername))) {
						IsOffsetAvailable = "true";
					}
				}
			}else{
				if(CoDriverUsername.length() == 0) {
					if (lastLoginMainDr.equals(mainDriverUsername)) {
						IsOffsetAvailable = "true";
					}
				}else{
					if (lastLoginMainDr.equals(mainDriverUsername) || lastLoginMainDr.equals(CoDriverUsername)) {
						IsOffsetAvailable = "true";
					}
				}
			}
		}

		return IsOffsetAvailable;
	}



	void GetServerCurrentUtcTime(){
		if(SharedPref.isLoginAllowed(LoginActivity.this)) {
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();

			VolleyRequest GetServerTimeRequest = new VolleyRequest(getApplicationContext());
			Map<String, String> params = new HashMap<String, String>();
			GetServerTimeRequest.executeRequest(Request.Method.GET, APIs.CONNECTION_UTC_DATE, params, 1001,
					Constants.SocketTimeout5Sec, ResponseCallBack, ErrorCallBack);

		}else{
			global.EldScreenToast(mainLoginLayout, "Vehicle speed is " + BackgroundLocationService.obdVehicleSpeed +" km/h. " +
					getString(R.string.login_speed_alert), getResources().getColor(R.color.colorVoilation));
		}


	}


	@Override
	public void onConnected(@Nullable Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}


	private class ParseLoginJsonData extends AsyncTask<String, String, String> {
		private JSONArray dataJson = null;

		@RequiresApi(api = Build.VERSION_CODES.KITKAT)
		@Override
		protected String doInBackground(String... strings) {

			try {
				//Logger.LogDebug("strings", "strings: " + strings);

				dataJson = new JSONArray(loginResponseData);

				ParseLoginDetails parseLoginDetails = new ParseLoginDetails();
				parseLoginDetails.ParseLoginDetails(dataJson, LoginActivity.this);

				SharedPref.setUserName(StrSingleUserame, LoginActivity.this);
				SharedPref.setPassword(StrSinglePass, LoginActivity.this);
				SharedPref.setUserCred(StrSingleUserame, StrCoDriverUsername, LoginActivity.this);

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
					global.EldScreenToast(mainLoginLayout, message, UILApplication.getInstance().getThemeColor());
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
		StrOSType = "Android-" + Build.VERSION.RELEASE + " | ELD-" + AppVersion ;

		CheckDeviceIDsStatus();

		getMyPhoneNumber();
		Constants.IsAlsServerResponding = true;

		if (global.isConnected(LoginActivity.this)) {
			if (StrCoDriverUsername.length() > 0 && StrCoDriverPass.length() > 0) {
				if (StrCoDriverUsername.length() > 0) {
					if (StrCoDriverPass.length() > 0) {

						if (!StrSingleUserame.equals(StrCoDriverUsername)) {
							SharedPref.setDriverType(LoginUserType, LoginActivity.this);
							DriverConst.SetDriverLoginDetails(StrSingleUserame, StrSinglePass, LoginActivity.this);
							DriverConst.SetCoDriverLoginDetails(StrCoDriverUsername, StrCoDriverPass, LoginActivity.this);
							checkNullInputs();

							/*========== LOGIN API =========== */
							//LoginUser(global.registrationId, StrSingleUserame, StrSinglePass, StrCoDriverUsername, StrCoDriverPass, LoginUserType, StrOSType, DeviceSimInfo, Sim2);
							GetServerCurrentUtcTime();

						//	ViewOnlyLogin(StrSingleUserame, StrSinglePass);
						} else {
							global.EldScreenToast(mainLoginLayout, getString(R.string.enter_different_user_co), getResources().getColor(R.color.colorVoilation));
						}
					} else
						global.EldScreenToast(mainLoginLayout, getString(R.string.enter_your_pass), getResources().getColor(R.color.colorVoilation));
				} else
					global.EldScreenToast(mainLoginLayout, getString(R.string.enter_userName), getResources().getColor(R.color.colorVoilation));

			} else
				global.EldScreenToast(mainLoginLayout, getString(R.string.enter_username_pass), getResources().getColor(R.color.colorVoilation));
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
				backImgView.setVisibility(View.GONE);
				OutToRightAnim(loginCoDriverLayout);
				InFromLeftAnim(loginLayout);
			} else if (loginLayout.getVisibility() == View.VISIBLE) {
				welcomeToAlsTV.setVisibility(View.VISIBLE);
				ClearMainDriverFields();
				backImgView.setVisibility(View.GONE);
				OutToRightAnim(loginLayout);
				InFromLeftAnim(userTypeLayout);
			}
		} else {
			ClearMainDriverFields();
			backImgView.setVisibility(View.GONE);
			welcomeToAlsTV.setVisibility(View.VISIBLE);
			OutToRightAnim(loginLayout);
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

		String BrandName = Build.BRAND;
		String Model = Build.MODEL;
		String Version = Build.VERSION.RELEASE;

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions

			DeviceSimInfo = constants.DeviceSimInfo("", "", BrandName, Model, Version, "");

			return;
		}else {

			try {
				DualSimManager info = new DualSimManager(LoginActivity.this);
				String Sim1SerialNumber = "", Sim2SerialNumber = "";
				String operatorSIM = info.getNETWORK_OPERATOR_NAME(1);
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

									if (Sim1SerialNumber.length() == 0) {
										Sim1SerialNumber = Constants.getSerialNumber(LoginActivity.this);
									}
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


				global.registrationId = checkNullStatus(global.registrationId);
				ImeiNumber = checkNullStatus(ImeiNumber);
				StrOSType = checkNullStatus(StrOSType);
				Sim1 = checkNullStatus(Sim1);
				Sim2 = checkNullStatus(Sim2);
				operatorSIM = checkNullStatus(operatorSIM);
				Sim1SerialNumber = checkNullStatus(Sim1SerialNumber);
				//MobileNo				= checkNullStatus(MobileNo);


				if (Sim1.length() > 0) {
					MobileNo = Sim1;
				} else {
					if (Sim2.length() > 0) {
						MobileNo = Sim2;
						operatorSIM = info.getNETWORK_OPERATOR_NAME(1);
						Sim1SerialNumber = Sim2SerialNumber;
					}
				}

				DeviceSimInfo = constants.DeviceSimInfo(MobileNo, Sim1SerialNumber, BrandName, Model, Version, operatorSIM);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {

			case R.id.loginBleStatusBtn:


				String LeftOnDuty = Globally.FinalValue(119);
				Logger.LogDebug("LeftOnDuty","LeftOnDuty: " +LeftOnDuty);

				try {
					int timeSettingStatus = Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME);
					Logger.LogDebug("timeSettingStatus","timeSettingStatus: " +timeSettingStatus);
				} catch (Settings.SettingNotFoundException e) {
					e.printStackTrace();
				}

			/*	DateTime selectedDateTime = Globally.getDateTimeObj("2023-04-20T16:57:42", false);
				DateTime currentTime = Globally.getDateTimeObj("2023-04-20T17:00:00", false);
				long hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardHours();
				long minDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardMinutes();
				int minDiffNew = Constants.getTimeDiffInMin(selectedDateTime.toString(), currentTime);

				if (Math.max(-10, minDiff) == Math.min(minDiff, 10)) {	//minDiff >= -5
					Logger.LogDebug("isTimeCorrect","Time Correct: " );
				}else{
					Logger.LogDebug("isTimeCorrect","TimeInCorrect: " );
				}

				Logger.LogDebug("hourDiff","hourDiff: " +hourDiff );
				Logger.LogDebug("minDiff","minDiff: " +minDiff );
				Logger.LogDebug("minDiffUp","minDiffUp: " +minDiffNew );

				selectedDateTime = Globally.getDateTimeObj("2023-02-13T15:59:40", false);
				currentTime = Globally.getDateTimeObj("2023-02-13T00:59:42", false);
				hourDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardHours();
				minDiff = Constants.getDateTimeDuration(selectedDateTime, currentTime).getStandardMinutes();
				minDiffNew = Constants.getTimeDiffInMin(selectedDateTime.toString(), currentTime);
				Logger.LogDebug("hourDiffNew","hourDiffNew: " +hourDiff );
				Logger.LogDebug("minDiffNew","minDiffNew: " +minDiff );
				Logger.LogDebug("minDiffNewNew","minDiffNew: " +minDiffNew );
*/


				if(ObdPreference == Constants.OBD_PREF_BLE) {
					if(!IsBleConnected) {
						requestLocationPermission(true);
						global.EldScreenToast(mainLoginLayout, getString(R.string.obd_ble_disconnected), getResources().getColor(R.color.color_storage500));
					}else{
						global.EldScreenToast(mainLoginLayout, getString(R.string.obd_ble), UILApplication.getInstance().getThemeColor());
					}
				}else if(ObdPreference == Constants.OBD_PREF_WIRED){
					if(SharedPref.getObdStatus(getApplicationContext()) == Constants.WIRED_CONNECTED){
						WiredConnected = true;
					}else{
						WiredConnected = false;
					}

					if(WiredConnected) {
						loginBleStatusBtn.setColorFilter(UILApplication.getInstance().getThemeColor());

						global.EldScreenToast(mainLoginLayout, getString(R.string.wired_tablet_connected),
								UILApplication.getInstance().getThemeColor());
					}else{
						loginBleStatusBtn.setColorFilter(getResources().getColor(R.color.black_transparent));
						loginBleStatusBtn.startAnimation(connectionStatusAnimation);

						global.EldScreenToast(mainLoginLayout, getString(R.string.wired_tablet_disconnected),
								getResources().getColor(R.color.colorVoilation));
					}
				}


				break;

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

				isDriving = false;

				if(SharedPref.isLoginAllowed(LoginActivity.this)) {
					LoginUserType = DriverConst.SingleDriver;
					loginBtn.setText("Login");
					welcomeToAlsTV.setVisibility(View.INVISIBLE);
					driverTitleTV.setVisibility(View.INVISIBLE);
					backImgView.setVisibility(View.VISIBLE);
					OutToLeftAnim(userTypeLayout);
					InFromRightAnim(loginLayout);

				}else{
					global.EldScreenToast(mainLoginLayout, "Vehicle speed is " +BackgroundLocationService.obdVehicleSpeed +" km/h. " +
							getString(R.string.login_speed_alert), getResources().getColor(R.color.colorVoilation));
				}

				break;


			case R.id.CoDriverBtn:
				if(isDriving){
					isDriving = false;
				}else{
					isDriving = true;
				}

				if(SharedPref.isLoginAllowed(LoginActivity.this)) {
					welcomeToAlsTV.setVisibility(View.INVISIBLE);
					LoginUserType = DriverConst.TeamDriver;
					loginBtn.setText("Next");
					driverTitleTV.setVisibility(View.VISIBLE);
					backImgView.setVisibility(View.VISIBLE);
					OutToLeftAnim(userTypeLayout);
					InFromRightAnim(loginLayout);
				}else{
					global.EldScreenToast(mainLoginLayout, "Vehicle speed is " +BackgroundLocationService.obdVehicleSpeed +" km/h. " +
							getString(R.string.login_speed_alert), getResources().getColor(R.color.colorVoilation));
				}

				break;

			case R.id.backImgView:

				BackPressedViews();
				break;


		}
	}


	/*void checkDriverTime(){

		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					TimeZoneDialog timeZoneDialog = new TimeZoneDialog(LoginActivity.this, true, false, false);
					timeZoneDialog.show();

				}
			});

		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}*/


	@Override
	public void onBackPressed() {

		if(userTypeLayout.getVisibility() == View.VISIBLE){
			super.onBackPressed();
		}else{
			BackPressedViews();
		}

	}



}
