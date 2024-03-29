package com.messaging.logistic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.constants.Constants;
import com.constants.SharedPref;
import com.constants.Utils;
import com.driver.details.DriverConst;
import com.driver.details.ParseLoginDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.local.db.ConstantsKeys;
import com.local.db.DBHelper;
import com.messaging.logistic.fragment.EldFragment;
import com.notifications.NotificationManagerSmart;
import com.shared.pref.CoDriverEldPref;
import com.shared.pref.CoNotificationPref;
import com.shared.pref.MainDriverEldPref;
import com.shared.pref.NotificationPref;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import models.RulesResponseObject;

/*========================= CONSTANT VARIABLES =======================*/
public class Globally {

	public static String registrationId = "";
	public static String LATITUDE = "0.0", LONGITUDE = "0.0";
	public static int VEHICLE_SPEED = -1;
	public static JSONObject obj ;
	public static Intent i;
	public static final int PICK_FROM_CAMERA = 1, MEDIA_TYPE_VIDEO = 4575;
	public static String DateFormat 				= "yyyy'-'MM'-'dd'T'HH':'mm':'ss";
	public static String DateFormatLocal			= "yyyy'-'MM'-'dd HH':'mm':'ss";
	public static String DateTimeFormat 			= "yyMMddHHmm";
	public static String DateFormatMMddyyyy			= "MM/dd/yyyy";
	public static String DateFormatMMddyy			= "MM/dd/yy";
	static String DateFormatMMddyyyyHHss			= "MM/dd/yyyy HH:mm:ss";
	public static String DateFormatHalf 			= "yyyy'-'MM'-'dd";
	public static String DateFormatMMddyyyyHyphen 	= "MM'-'dd'-'yyyy";
	static String DateFormatMMddyyyyHH				= "MM/dd/yyyy HH:mm";
	static String Timehhmma							= "hh:mm a";
	static String TimehhmmaWithOutSpace				= "hh:mma";
	static String DateFormatFullWithSec				= "MMddyyyyHHmmss";
	static String DateFormatMalfunction				= "MMM dd, hh:mm a";
	static String DateFormatMMM_dd_yyyy				= "MMM dd, yyyy";
	static String DateFormatMMMM_ddd_dd				= "MMMM,MMM,EEEE";
	static String DateFormat_dd_MMM_yyyy			= "dd MMM, yyyy";

	public static String DateFormatWithMillSec 		= "yyyy'-'MM'-'dd'T'HH':'mm':'ss.SSS'Z'";
	public static String[] MONTHS 					= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public static String[] MONTHS_FULL 				= {"January", "February", "March", "April", "May", "June",
														"July", "August", "September", "October", "November", "December"};

	public static boolean IS_LOGOUT 			= false;
	public static boolean IS_CONNECTED 			= false;
	public static boolean IS_OBD_IGNITION		= false;
	public static String CONNECTION_ERROR		= "Connection Error";
	public static String INTERNET_MSG 			= "Not connected to Internet";
	public static String CHECK_INTERNET_MSG 	= "Your internet connection is not working.";
	static int[] NETWORK_TYPES = {
			ConnectivityManager.TYPE_WIFI,
			ConnectivityManager.TYPE_MOBILE,
			ConnectivityManager.TYPE_BLUETOOTH,
			ConnectivityManager.TYPE_ETHERNET };


	public static Intent serviceIntent;



	/*-------- Project ID ----------*/
	public static String PROJECT_ID                           = "1";

	/*-------- DRIVER STATUS ----------*/
	public static String OFF_DUTY              				  = "1";
	public static String SLEEPER                  			  = "2";
	public static String DRIVING                      		  = "3";
	public static String ON_DUTY			                  = "4";
	public static String PERSONAL			                  = "5";

	/*-------- ELD CYCLES IDs ----------*/
	public static String CANADA_CYCLE_1                       = "1";
	public static String CANADA_CYCLE_2                       = "2";
	public static String USA_WORKING_6_DAYS                   = "3";
	public static String USA_WORKING_7_DAYS                   = "4";

	public static String CANADA_CYCLE_1_NAME                  = "C 70/7";
	public static String CANADA_CYCLE_2_NAME                  = "C 120/14";
	public static String USA_WORKING_6_DAYS_NAME              = "U 60/7";
	public static String USA_WORKING_7_DAYS_NAME              = "U 70/8";

	public static String CANADA_SOUTH_OPERATION_NAME          = "Canada South 60°N (";
	public static String CANADA_NORTH_OPERATION_NAME          = "Canada North 60°N (";

	public static String TRUCK_NUMBER                   	  = "--";
	public static String TRAILOR_NUMBER                   	  = "--";
	public static String PLATE_NUMBER                   	  = "--";
	public static String SHIPPER_NAME                   	  = "--";
	public static String CONSIGNEE_NAME                   	  = "--";
	public static String TRIP_NUMBER	                   	  = "--";
	public static String SECOND_DRIVER_NAME                	  = "--";

	public static String DRIVER_TYPE                	  	  = "driver_type";
	public static String CURRENT_DRIVER_TYPE				  = "current_driver_type";

	public static String TEMP_USERNAME                	  	  = "";
	public static String TEMP_PASSWORD				  		  = "";

	public static JSONArray OBD_DataArray = new JSONArray();

	public static List<String> onDutyRemarks = new ArrayList<String>();

	//public static Bundle bundle = new Bundle();
	//public static Bundle getBundle;

	public static int[] NOTIFICATIONS_ID = {
			0,
			1,		// DRIVER JOB CONFIRMATION
			2
	};


	public Globally() {
		super();
	}


	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}


	public static boolean isConnected(Context context) {
		try {
			ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			for (int networkType : NETWORK_TYPES) {
				NetworkInfo netInfo = connec.getNetworkInfo(networkType);
				if (netInfo != null && netInfo.isConnectedOrConnecting() && Constants.IsAlsServerResponding) {
					//return IS_CONNECTED;  // IS_CONNECTED is used in UILApplication class methos with FirebaseDatabase
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}


	public static boolean isWifiOrMobileDataEnabled(Context context) {
		try {
			ConnectivityManager connec = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			for (int networkType : NETWORK_TYPES) {
				NetworkInfo netInfo = connec.getNetworkInfo(networkType);
				if (netInfo != null && netInfo.isConnectedOrConnecting() ) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}


	public void openDialog(){
		/*final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
		View mView = getLayoutInflater().inflate(R.layout.dialog_engine_restarted,null);
		alert.setView(mView);
		final androidx.appcompat.app.AlertDialog alertDialog = alert.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
*/
	}


	public void InternetErrorDialog(Context context){
		/*final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);
		Activity activity = (Activity) context;
		View customSnackView = activity.getLayoutInflater().inflate(R.layout.dialog_internet_connection, null);
		snackbar.getView().setBackgroundColor(Color.TRANSPARENT);	//Color.parseColor("#99a8a8a8")
		Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
		snackbarLayout.setPadding(0, 0, 0, 0);
		snackbarLayout.addView(customSnackView, 0);
		snackbar.show();*/


		final Dialog picker = new Dialog(context);
		picker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picker.setContentView(R.layout.dialog_internet_connection);
		picker.setCancelable(false);

		picker.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(picker.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		picker.getWindow().setAttributes(lp);


		final ImageView closeDialogImg = (ImageView) picker.findViewById(R.id.closeDialogImg);
		closeDialogImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				picker.dismiss();
			}
		});

		if(context != null) {
			picker.show();
		}

	}



	/*========================= Show Toast message =====================*/
	public static void showToast(View view, String message) {
		try {
			Snackbar snackbar = Snackbar
					.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(Color.parseColor("#2E2E2E"));
			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);

			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/*========================= Show Toast message =====================*/
	public static void EldScreenToast(View view, String message, int color) {

		try {
			Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(color);

			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();

			if(view.getContext() != null){
				//Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
				EldScreenToast1(view.getContext(), message, color);
			}
		}

	}

	/*========================= Show Toast message =====================*/
	public static void EldScreenToast1(Context context, String message, int color) {

		try {
			Activity activity = (Activity) context;
			Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(color);

			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();

			if(context != null){
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		}

	}

	/*========================= Show Toast message =====================*/
	public static void EldToastWithDuration(View view, String message, int color) {

		try {
			Snackbar snackbar = Snackbar
					.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(color);
			snackbar.setDuration(6000);	// 6 sec

			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();
		}

	}



	public static void EldToastWithDuration4Sec(View view, String message, int color) {

		try {
			Snackbar snackbar = Snackbar
					.make(view, message, Snackbar.LENGTH_LONG);

			snackbar.setActionTextColor(Color.WHITE);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(color);
			snackbar.setDuration(3600);	// 6 sec

			TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
			textView.setTextColor(Color.WHITE);
			snackbar.show();

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/*========================= SnackBar Violation View =====================*/
	public static void SnackBarViolation(View v, final String message, final Context context) {

		try {
			final Snackbar mSnackBar = Snackbar.make(v, message.replaceAll(";", ".  "), Snackbar.LENGTH_LONG);
			View view = mSnackBar.getView();
			FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
			params.gravity = Gravity.TOP;
			view.setLayoutParams(params);
			view.setBackgroundColor(Color.parseColor("#C92627"));  // Violation red color
			TextView mainTextView = (TextView) (view).findViewById(R.id.snackbar_text);
			mainTextView.setTextColor(Color.WHITE);

			mSnackBar.setAction("READ", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String violationMsg = message.replaceAll(";", "<br />");
					ReadViolationDialog(violationMsg, context);
						mSnackBar.dismiss();

				}
			});
			mSnackBar.setActionTextColor(Color.WHITE);
			if(context != null) {
				mSnackBar.show();
			}

		}catch (Exception e){
			e.printStackTrace();
		}

	}



	public void AlertDialog(final Dialog AlertPicker, String title, String desc, String type, int notification,
							Activity activityReference){

		AlertPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		AlertPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
		AlertPicker.setContentView(R.layout.alert_dialog);
		activityReference.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		final Button alertOkBtn = (Button)AlertPicker.findViewById(R.id.alertOkBtn);

		final TextView alertTitleTv = (TextView) AlertPicker.findViewById(R.id.alertTitleTv);
		final TextView alertDescTv  = (TextView) AlertPicker.findViewById(R.id.alertDescTv);
		TextView notiAlertTitle     = (TextView)AlertPicker.findViewById(R.id.notiAlertTitle);
		TextView notiAlertTV        = (TextView)AlertPicker.findViewById(R.id.notiAlertTV);

		alertTitleTv.setText(title);
		alertDescTv.setText(desc);

		if(type.equals("warning")){
			notiAlertTV.setBackgroundResource(R.drawable.sleeper_default);
			notiAlertTitle.setBackgroundColor(activityReference.getResources().getColor(R.color.colorSleeper));
			alertTitleTv.setTextColor(activityReference.getResources().getColor(R.color.colorSleeper));
			alertDescTv.setTextColor(activityReference.getResources().getColor(R.color.colorSleeper));
			alertOkBtn.setBackgroundResource(R.drawable.sleeper_selector);
		}

		alertOkBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if (AlertPicker != null && AlertPicker .isShowing())
						AlertPicker.dismiss();
				} catch (final IllegalArgumentException e) {
					e.printStackTrace();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

		if(activityReference != null) {
			AlertPicker.show();
		}

		if(notification == Constants.PersonalDrivingExceed){

		}else {
			try {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try{
							if (AlertPicker != null && AlertPicker.isShowing())
								AlertPicker.dismiss();
						} catch (final IllegalArgumentException e) {
							e.printStackTrace();
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				}, 8000);
			} catch (Exception e) {  }
		}
	}









	public void updateCurrentUtcTime(Context context){

    	String utcDate = SharedPref.getCurrentUTCTime(context);
		DateTime utcDateTime = getDateTimeObj(utcDate, false);
		if(utcDateTime != null)
		{
			utcDateTime = utcDateTime.plusMinutes(1);
			//Log.d("utcDateTime", "utcDateTime" + utcDateTime);
			// Save time with shared pref
			SharedPref.setCurrentUTCTime( utcDateTime.toString() , context);

		}

	}

	// Check saved time should be smaller then current time
	public boolean isCurrentTimeBigger(Context context){
		String utcDate = SharedPref.getCurrentUTCTime(context);
		DateTime savedUtcDateTime = getDateTimeObj(utcDate, false);
		DateTime currentUtcDateTime = GetCurrentUTCDateTime();

		int minDiff = currentUtcDateTime.getMinuteOfDay() - savedUtcDateTime.getMinuteOfDay();
		if(currentUtcDateTime.isAfter(savedUtcDateTime) || currentUtcDateTime.equals(savedUtcDateTime) || minDiff >= -5){
			return true;
		}else{
			return false;
		}
	}


	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public boolean isCorrectTime(Context context){
    	boolean isTimeCorrect = true;
		String utcDate = SharedPref.getCurrentUTCTime(context);
		DateTime savedUtcDateTime = getDateTimeObj(utcDate, false);
		DateTime currentUtcDateTime = GetCurrentUTCDateTime();

		if(savedUtcDateTime != null && savedUtcDateTime.toString().length() > 16 && currentUtcDateTime.toString().length() > 16) {
			int dayDiff = Constants.getDayDiff(savedUtcDateTime.toString(), currentUtcDateTime.toString());

			if (dayDiff == 0) {	//savedUtcDateTime.toString().substring(0, 10).equals(currentUtcDateTime.toString().substring(0, 10))
				int minDiff = currentUtcDateTime.getMinuteOfDay() - savedUtcDateTime.getMinuteOfDay();
				if (minDiff >= -5) {	//Math.max(-7, minDiff) == Math.min(minDiff, 7)
					isTimeCorrect = true;
				} else {
					isTimeCorrect = false;
				}
			}else{
				if(dayDiff < 0){
					isTimeCorrect = false;
				}
			}

			/*else {
				if (currentUtcDateTime.toString().substring(11, 13).equals("00")) {
					int min = Integer.valueOf(currentUtcDateTime.toString().substring(14, 16));

					if (min <= 7) {
						isTimeCorrect = true;
					} else {
						isTimeCorrect = false;
					}
				} else {
					isTimeCorrect = true;
				}
				isTimeCorrect = true;
			}*/
		}

		if(!isTimeCorrect && SharedPref.isServiceOnDestoryCalled(context)){
			isTimeCorrect = true;
		}

		return isTimeCorrect;
	}



	public boolean isSingleDriver(Context context){
    	boolean isSingleDriver = true;
		try {
			if (SharedPref.getDriverType(context).equals(DriverConst.SingleDriver)) {
				isSingleDriver = true;
			} else {
				isSingleDriver = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSingleDriver;
	}





	/* ####################################################################################################### */
		/* --------------- Date Format -------------	*/

// Convert MM/dd/yyyy date format to default DateTime format
	public static String ConvertDateFormat(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyy);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormat);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	// Convert MM/dd/yyyy date format to default DateTime format
	public static String ConvertDateSlashWithHyphen(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyyHyphen);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyyyy);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}


	// Convert MM/dd/yyyy date format to default DateTime format
	public static boolean CheckCorrectFormat(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyy);
		try {
			inputFormat.parse(time);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}





	// Convert 12 hour time format
	public static String Convert12HourFormatTime(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(Timehhmma);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}


	public static String convertToMonNameFormat(String time){
		// 2020-12-04T00:00:02   - Dec 08
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(TimehhmmaWithOutSpace);

		Date date = null;
		String timeAmPm = "";
		String monthNameStr = "";

		try {
			date = inputFormat.parse(time);
			timeAmPm = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try{
			int mnth = Integer.valueOf(time.substring(5, 7));
			monthNameStr = MONTHS[mnth - 1] + " " + time.substring(8, 10) + ", " + timeAmPm;
			//Log.d("MONTHS", "MONTHS: " +monthNameStr);

		}catch (Exception e){
			e.printStackTrace();
		}
		return monthNameStr;
	}


	public static String dateConversionMalfunction(String time){
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMalfunction);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;

	}

	public static String dateConversionMonthNameWithDay(String time){
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMM_dd_yyyy);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;

	}

	public static String dateConversionMMMM_ddd_dd(String time){
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMMM_ddd_dd);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;

	}


	// Convert default DateTime format to MM/dd/yyyy date format
	public static String ConvertDateFormatMMddyyyy(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyyyy);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String ConvertDateFormatMMddyy(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyy);

		Date date = null;
		String str = "";

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	// Convert default DateTime format to MM/dd/yyyy date format
	public static String ConvertDateFormatddMMMyyyy(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormat_dd_MMM_yyyy);

		Date date = null;
		String str = "";

		if(time.length() > 10) {
			try {
				date = inputFormat.parse(time);
				str = outputFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			str = time;
		}
		return str;
	}




	// Convert default DateTime format to MM/dd/yyyy date format
	public static String ConvertInspectionsDateFormat(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat inputFormat2 = new SimpleDateFormat(DateFormatMMddyyyyHHss);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyyyy);

		Date date = null;
		String str = null;
		boolean isFormatChanged = false;
		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			isFormatChanged = true;
			Log.d("DateParseException", "DateParseException");
			//e.printStackTrace();
		}

		if(isFormatChanged){
			try {
				date = inputFormat2.parse(time);
				str = outputFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return str;
	}





	// Convert default DateTime format to MM/dd/yyyy HH:MM:SS date format
	public static String ConvertDateFormatMMddyyyyHHmm(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyyyyHHss);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}



	public static Date ParseDate(String dateStr){
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatMMddyyyyHHss);
		Date date = null;

		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;

	}


	public static String ConvertDateFormatyyyy_MM_dd(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyy);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatHalf);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}


	public static String ConvertDeviceDateTimeFormat(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyyHHss);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormat);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}


	public static String convertUSTtoMM_dd_yyyy_hh_mm(String time){

		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormat);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormatMMddyyyyHH);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;


	}



	public static String InspectionDateTimeFormat(String time) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(DateFormatMMddyyyyHHss);
		SimpleDateFormat outputFormat = new SimpleDateFormat(DateFormat);

		Date date = null;
		String str = null;

		try {
			date = inputFormat.parse(time);
			str = outputFormat.format(date);
		} catch (ParseException e) {
			str = time;
			//e.printStackTrace();
		}
		return str;
	}



	public static String GetUTCFromDate(String dateStr, int offSet){
		String utcDateStr = "";
		try {
			DateTime utcDate = getDateTimeObj(dateStr, true);
			utcDateStr = String.valueOf(utcDate.minusHours(offSet));
			if(utcDateStr.equals("null")){
				utcDateStr = "";
			}
		}catch (Exception e){
			utcDateStr = "";
			e.printStackTrace();
		}

		return utcDateStr;
	}


    public String getCurrentDate(){
        SimpleDateFormat currentDateFormat = new SimpleDateFormat(DateFormat);
        Calendar c = Calendar.getInstance();
        String StringCurrentDate = "";

        try {
            StringCurrentDate = currentDateFormat.format(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        return StringCurrentDate;
    }


	public static DateTime GetCurrentJodaDateTime(){

		SimpleDateFormat currentDateFormat = new SimpleDateFormat(DateFormat);
		Calendar c = Calendar.getInstance();
		DateTime date = new DateTime();
		try {
			String dateStr = currentDateFormat.format(c.getTime());
			date = getDateTimeObj(dateStr, false);
		}catch (Exception e){
			e.printStackTrace();
		}

		return date;
	}

	public String getCurrentDateLocal(){
		SimpleDateFormat currentDateFormat = new SimpleDateFormat(DateFormatLocal);
		Calendar c = Calendar.getInstance();
		String StringCurrentDate = "";

		try {
			StringCurrentDate = currentDateFormat.format(c.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		return StringCurrentDate;
	}



	public static DateTime GetCurrentUTCDateTime(){

		SimpleDateFormat currentDateFormat = new SimpleDateFormat(DateFormat);
		currentDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar c = Calendar.getInstance();
		DateTime utcDate = new DateTime();
		try {
			String dateStr = currentDateFormat.format(c.getTime());
			utcDate = getDateTimeObj(dateStr, false);
		}catch (Exception e){
			e.printStackTrace();
		}

		return utcDate;
	}



	public static String GetCurrentUTCTime(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatMMddyyyyHHss);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }

    public static String ConvertDateTimeFormat(String date){

        String convertedDate = "";
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatWithMillSec);
        try {
            Date newDate = dateFormatGmt.parse(date);
            dateFormatGmt = new SimpleDateFormat("dd MMM yyyy");
            date = dateFormatGmt.format(newDate);
            convertedDate = date.toString();
        } catch (ParseException e) {
            convertedDate = "";
          //  e.printStackTrace();
        }

        return convertedDate;
    }

    public static String GetCurrentUTCDate(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatMMddyyyy);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }


    public static String GetCurrentDeviceDate(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatMMddyyyy);
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }

	public static String GetCurrentFullDateTime(){
		String currentTime = "";
		try {
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatFullWithSec);
			currentTime = dateFormatGmt.format(new Date());
		}catch (Exception e){}
		return currentTime;
	}




    public static String GetCurrentDeviceDateDefault(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatHalf);
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }


    public static String GetCurrentDeviceDateTime(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormatMMddyyyyHHss);
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }



    public static String GetCurrentDateTime(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormat);
        //dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }

    public static String GetCurrentUTCTimeFormat(){
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DateFormat);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentTime = dateFormatGmt.format(new Date());
        return currentTime;
    }






	public static DateTime GetStartDate(DateTime date, int days){
        return date.minusDays(days);
    }

    public static DateTime GetStartDatePlus(DateTime date, int days){
        return date.plusDays(days);
    }

    public static String ConvertToTimeFormat(String date, String dateFormat){
        String convertedDate = "";
        Date newDate = new Date();
        boolean IsMilliSecDateFormat = true;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormat);
        try {
            newDate = dateFormatGmt.parse(date);
        } catch (ParseException e) {
            IsMilliSecDateFormat = false;
        }

        if(!IsMilliSecDateFormat){
            dateFormatGmt = new SimpleDateFormat(DateFormat);
            try {
                newDate = dateFormatGmt.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dateFormatGmt = new SimpleDateFormat("HH:mm");   // hh:mm a  -- 12 hours format (am/pm)
        date = dateFormatGmt.format(newDate);
        convertedDate = date.toString();

        return convertedDate;
    }

	public static String ConvertTo12HTimeFormat(String date, String dateFormat){
		String convertedDate = "";
		Date newDate = new Date();
		boolean IsMilliSecDateFormat = true;
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat(dateFormat);
		try {
			newDate = dateFormatGmt.parse(date);
		} catch (ParseException e) {
			IsMilliSecDateFormat = false;
		}

		if(!IsMilliSecDateFormat){
			dateFormatGmt = new SimpleDateFormat(DateFormat);
			try {
				newDate = dateFormatGmt.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		dateFormatGmt = new SimpleDateFormat("hh:mm a");   // hh:mm a  -- 12 hours format (am/pm)
		date = dateFormatGmt.format(newDate);
		convertedDate = date.toString();

		return convertedDate;
	}


    public long GetTimeZoneOffSet(){
        String currentDate = getCurrentDate();
        String currentUTCDate = GetCurrentUTCTimeFormat();
		long minutes = 0;
    	try {
			DateTime currentDateTime = getDateTimeObj(currentDate, false);
			DateTime currentUtcdateTime = getDateTimeObj(currentUTCDate, false);
			long diffInMillis = currentDateTime.getMillis() - currentUtcdateTime.getMillis();
			minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
			minutes = minutes / 60;
		}catch (Exception e){
    		e.printStackTrace();
		}
        return minutes;
    }

    public static DateTime getDateTimeObj(String date, boolean isInputInUTC) {
        DateTime oDate = null;
        DateTimeFormatter dtf = null;
        boolean requireOnlyTime = false;

        try {
            if (date != null && date != "") {
                if (requireOnlyTime) {
                    dtf = org.joda.time.format.DateTimeFormat.forPattern("HH:mm:ss");
                    oDate = dtf.parseDateTime(date);
                } else {
                    if (isInputInUTC) {
                        if (!date.contains(".") && !(date.toLowerCase().contains("z"))) {
                            date += ".000Z";
                        }
                        oDate = new DateTime(date, DateTimeZone.UTC);
                        // oDate = DateTime.parse(date);
                    } else {
                        if (date.contains(".")) {
                            oDate = DateTime.parse(date).toDateTime(DateTimeZone.UTC);
                        } else {
                            oDate = new LocalDateTime(date).toDateTime(DateTimeZone.UTC);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return oDate;
    }



    public String GetDayOfWeek(String SelectedDate){
		SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy");
		inFormat.setTimeZone(TimeZone.getDefault());
		Date date = null;
		try {
			date = inFormat.parse(SelectedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
		outFormat.setTimeZone(TimeZone.getDefault());

		return outFormat.format(date);

	}





    /*====================== Hide keyboard =====================*/
	public static void hideKeyboard(Context cxt, RelativeLayout lay){
		InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(lay.getWindowToken(), 0);
	}


	public static void hideKeyboardView(Context cxt, View lay){
		InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(lay.getWindowToken(), 0);
	}



	public static void hideSoftKeyboard(Activity activity) {
		try {
		
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		
		} catch (Exception e) {   }
	}


	// Check Device type. Is it phone or tablet
	public static boolean isTablet(Context context) {
		if(context != null) {
			boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
			boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
			return (xlarge || large);
		}else {
			return false;
		}
	}


	public static void StopService(Context context){
		try {
			SharedPref.setDriverId( "", context);
			SharedPref.setPassword( "", context);
			SharedPref.setUserName( "", context);
			context.stopService(serviceIntent);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public static int dp2px(int dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}



	/*Create file directory with file */
	public static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Logistic");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "Carpoolie" + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == PICK_FROM_CAMERA) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpeg");
		}  else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");



		} else {
			return null;
		}
		return mediaFile;
	}


	public static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			return true;
		}
		return false;
	}



	public static String FileName(){

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),"Logistic");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
				return null;
			}
		}

		//Random generator = new Random();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());
		//int n = 1000;
		//	n = generator.nextInt(n);
		String fname = "ELD_"+ timeStamp +".txt";

		return fname;
	}


	public static File getAlsApkPath(Context context){
		File apkStorageDir = new File(context.getExternalFilesDir(null),"Logistic/AlsApp");

		// Create the storage directory if it does not exist
		if (!apkStorageDir.exists()) {
			if (!apkStorageDir.mkdirs()) {
				Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
				return null;
			}
		}

		return apkStorageDir;
	}


	public ArrayList<String> getDownloadedDocs(Context context){
		ArrayList<String> docList = new ArrayList<String>();

		String path = context.getExternalFilesDir(null).toString()+"/Logistic/AlsDoc";
		Log.d("Files", "Path: " + path);
		File directory = new File(path);
		File[] files = directory.listFiles();
		if(files != null) {
			Log.d("Files", "Size: "+ files.length);
			for (int i = 0; i < files.length; i++) {
				docList.add(files[i].getName());
				Log.d("Files", "FileName:" + files[i].getName());
			}
		}
		return  docList;
	}


	public static File getAlsDocPath(Context context){
		File apkStorageDir = new File(context.getExternalFilesDir(null),"Logistic/AlsDoc");

		// Create the storage directory if it does not exist
		if (!apkStorageDir.exists()) {
			if (!apkStorageDir.mkdirs()) {
				Log.d("IMAGE_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
				return null;
			}
		}

		return apkStorageDir;
	}



	public static void DeleteDirectory(String directory) {
		try {
			// External sdcard location
			File mediaStorageDir = new File(directory);
			// delete the storage directory if it exists
			if (mediaStorageDir.isDirectory()) {
				String[] children = mediaStorageDir.list();
				for (int i = 0; i < children.length; i++) {
					new File(mediaStorageDir, children[i]).delete();
				}
				//mediaStorageDir.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public static String GetAppVersion(Context context, String type){
		String AppVersion = "";
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;

		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			if(type.equals("VersionName")) {
				AppVersion = info.versionName;
			}else{
				AppVersion = String.valueOf(info.versionCode);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return AppVersion;
	}


	public static File GetSavedFile(Context context, String fileName, String extension) {
		// External sdcard location	Environment.getExternalStorageDirectory()
		File mediaStorageDir = new File(context.getExternalFilesDir(null),"Logistic");
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Text_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
				return null;
			}
		}

		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName + "." + extension);
		//Log.d("fileeee", "fileeeee: " + mediaFile);

		return mediaFile;
	}


	public static File GetWiredLogFile(Context context, String fileName, String extension) {
		// External sdcard location
		File mediaStorageDir = new File(context.getExternalFilesDir(null),"Logistic/AlsLog");
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Text_DIRECTORY_NAME", "Oops! Failed create " + "Logistic" + " directory");
				return null;
			}
		}

		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName + "." + extension);
		//Log.d("fileeee", "fileeeee: " + mediaFile);

		return mediaFile;
	}



	public static File SaveFileInSDCard(String fileType, String savedData, boolean Is18DaysLog, Context context){
		String SavedFileName = "", DriverName = "";
		FileOutputStream fos;
		File myFile = null;
		if(SharedPref.getCurrentDriverType(context).equals(DriverConst.StatusSingleDriver))
			DriverName = SharedPref.getUserName( context);
		else
			DriverName = SECOND_DRIVER_NAME;

		if(Is18DaysLog){
			DriverName = "";
		}

		try {
			SavedFileName = FileNameStr(fileType, DriverName, Is18DaysLog);
			if(isExternalStorageAvailable()) {
				myFile = GetSavedFile(context, SavedFileName, "txt"); //new File("/sdcard/"+filename);
				myFile.createNewFile();

				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(savedData);
				myOutWriter.close();
				fOut.close();

				//Log.d("FileSaved", "---Saved File: " + filename + " \n" +savedData);
				//Log.d("myFile", "---myFile: " + myFile);
			}

		} catch (FileNotFoundException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
		
		return myFile;
	}

	public static String FileNameStr(String fileType, String DriverName, boolean Is18DaysLog){
		// Create a media file name
		String timeStamp = "";
		if(Is18DaysLog) {
			timeStamp = fileType;
		}else{
			timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
			timeStamp = fileType + DriverName+"_" + timeStamp;
		}
		return  timeStamp;
	}


	public static String SaveBitmapToFile( Bitmap bm, String name, int quality, Context context) {

		File imageFile = GetSavedFile(context, name, "png");
		try {
			imageFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imageFile);
			bm.compress(Bitmap.CompressFormat.PNG, quality,fos);
			fos.close();
			//Log.d("FileSaved", "Saved File: " + imageFile + " saved");
			return imageFile.toString();
		}catch (IOException e) {
			Log.e("app",e.getMessage());
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}



	public static String ConvertImageToByteAsString(String file){
		Bitmap bm = BitmapFactory.decodeFile(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
		byte[] byteArray = baos.toByteArray();

		Bitmap bitmap1 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap1.compress(Bitmap.CompressFormat.PNG, 70, stream);
		String byteStr = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);

		return byteStr;
	}


	public static String CheckLongitudeWithCycle(String longitude){

		if (!longitude.contains("-")) {
			longitude = "-" + longitude;
		}

		return longitude;
	}



	public static Bitmap ConvertStringBytesToBitmap(String byteStr){
		byte[] byteArray22 = Base64.decode(byteStr, Base64.DEFAULT);
		final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray22, 0, byteArray22.length);
		return bitmap;
	}


	// ---------- return array with new saved record --------------
	public JSONArray getSaveCycleRecords(int CycleId, String changeType, Context context){
		JSONArray cycleDetailArray = new JSONArray();

		try{
			JSONObject obdModel = new JSONObject();
			obdModel.put(ConstantsKeys.CurrentCycleId, CycleId);
			obdModel.put(ConstantsKeys.CurrentDateTime, getCurrentDate());
			obdModel.put(ConstantsKeys.CurrentUTCTime, GetCurrentUTCDateTime());
			obdModel.put(ConstantsKeys.CycleChangeType, changeType);
			obdModel.put(ConstantsKeys.IsInternet, isConnected(context));

			cycleDetailArray = new JSONArray(SharedPref.GetCycleDetails(context));
			cycleDetailArray.put(obdModel);

		}catch (Exception e){
			e.printStackTrace();
		}

		return cycleDetailArray;
	}




	public void SaveCurrentCycle( int DriverType, String CountryName, String changeType, Context context) {
		try{

			String CurrentCycle = "", CurrentCycleId = "";
			if(CountryName.equalsIgnoreCase("CANADA")){
				CurrentCycleId 	= DriverConst.GetDriverSettings(DriverConst.CANCycleId, context);
				CurrentCycle 	= DriverConst.GetDriverSettings(DriverConst.CANCycleName, context);
			}else if (CountryName.equalsIgnoreCase("USA")){
				CurrentCycleId 	= DriverConst.GetDriverSettings(DriverConst.USACycleId, context);
				CurrentCycle 	= DriverConst.GetDriverSettings(DriverConst.USACycleName, context);
			}

			if(CurrentCycle.length() > 0) {

				/* ------------- Save Cycle details with time is different with earlier cycle --------------*/
				if(changeType.length() > 0){
					int cycleIdInt = Integer.valueOf(CurrentCycleId);
					String CurrentSavedCycle   = DriverConst.GetDriverCurrentCycle(DriverConst.CurrentCycleId, context );
					if(cycleIdInt != -1 && !CurrentSavedCycle.equals(cycleIdInt)) {
						JSONArray cycleDetailArray = getSaveCycleRecords(cycleIdInt, changeType, context);
						SharedPref.SetCycleOfflineDetails(cycleDetailArray.toString(), context);
					}
				}


				DriverConst.SetDriverCurrentCycle(CurrentCycle, CurrentCycleId, context);
				DriverConst.SetCoDriverCurrentCycle(CurrentCycle, CurrentCycleId, context);

			}
		}catch (Exception e){
			e.printStackTrace();
		}


	}



	@SuppressLint("MissingPermission")
	public static void PlaySound(Context context){
		try {
			if(context != null) {
				int resID = context.getResources().getIdentifier("beep_tone", "raw", context.getPackageName());
				MediaPlayer mediaPlayer = MediaPlayer.create(context, resID);
				mediaPlayer.start();

				Vibrator mVibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				long pattern[] = {0, 600, 200, 600, 300, 1000, 400, 1000}; //4000
				// 2nd argument is for repetition pass -1 if you do not want to repeat the Vibrate
				mVibrate.vibrate(pattern, -1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}



	@SuppressLint("MissingPermission")
	public static void PlayNotificationSound(Context context){
		try {
			if(context != null) {
				int resID = context.getResources().getIdentifier("new_notification", "raw", context.getPackageName());
				MediaPlayer mediaPlayer = MediaPlayer.create(context, resID);
				mediaPlayer.start();

				Vibrator mVibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				long pattern[] = {0, 600, 200, 600, 300, 100, 100, 100}; //4000
				// 2nd argument is for repetition pass -1 if you do not want to repeat the Vibrate
				mVibrate.vibrate(pattern, -1);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	public static float DipToPixels(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}


	public static long DateDifference(Date startDate, Date endDate) {
		//milliseconds
		long different = endDate.getTime() - startDate.getTime();

		//System.out.println("startDate : " + startDate);
	//	System.out.println("endDate : "+ endDate);
		//System.out.println("different : " + different);

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		/*long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;

		System.out.printf(
				"%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

		return elapsedDays;
	}


	public static int HourFromMin(int min){
		int hours = min / 60; //since both are ints, you get an int
		return hours;
	}

	public static int MinFromHourOnly(int hour){
		int minutes = hour % 60;
		return minutes;
	}


	public static String FinalValue(int min){
		int hour = HourFromMin(min);
		int minut = MinFromHourOnly(min);

		String finalValue = TwoDecimalView(hour) + ":" + TwoDecimalView(minut);
		finalValue = finalValue.replaceAll("-", "");

		if(hour < 0 || minut < 0){
			finalValue = "-" + finalValue;
		}
		return finalValue;
	}


	public static String TwoDecimalView(int value){
		String val = String.valueOf(value);
		val 		= val.replaceAll("-","");
		if(val.trim().length() == 1)
			val = "0" + value;

		return val;
	}


	public static String JobStatus(int pos, boolean isPersonalOrYardMove){
		String JobStatus = "";
		switch (pos){
			case 1:
				if(isPersonalOrYardMove)
					JobStatus = "Personal";
				else
					JobStatus = "Off Duty";
				break;

			case 2:
				JobStatus = "Sleeper";
				break;

			case 3:
				JobStatus = "Driving";
				break;

			case 4:
				if(isPersonalOrYardMove) {
					JobStatus = "On Duty(YM)";
				}else{
					JobStatus = "On Duty";
				}
				break;
		}
		return  JobStatus;
	}



/*

	private void getSpeedFromOdometer(){

		double  odometerDistance = 10800;
		double  timeInSec = 569;
		double speed = odometerDistance/timeInSec;
		double finalSpeed = speed * 18 / 5;

		Log.d("speed", "speed: " + finalSpeed );
		Log.d("speed", "speed in int: " + (int)finalSpeed );

	}

	private void getSpeedFromOdometer(){

		double  odometerDistance = 10800;
		double  timeInSec = 569;
		double speed = odometerDistance/timeInSec;
		double finalSpeed = speed * 18 / 5;
		DecimalFormat df2 = new DecimalFormat("#.##");

		Log.d("speed", "speed: " + finalSpeed );
		Log.d("speed", "speed 2 places: " + df2.format(finalSpeed) );

	}
*/



	public void OdometerDialog(final Context context, String title, boolean IsCancelable, final int status,
							   final View view, AlertDialog alertDialog){

		if(!SharedPref.IsOdometerFromOBD(context)) {

				try {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setTitle("Odometer Reading !!");
					alertDialogBuilder.setMessage(title);
					alertDialogBuilder.setCancelable(false);

					if (IsCancelable) {

						alertDialogBuilder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										EldFragment.IsPopupDismissed = true;
										TabAct.host.setCurrentTab(5);
									}
								});
					} else {
						alertDialogBuilder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										EldFragment.IsPopupDismissed = true;
										TabAct.host.setCurrentTab(5);
									}
								});

						alertDialogBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								SharedPref.OdometerSaved(true, context);
								EldFragment.IsPopupDismissed = true;
								view.performClick();
								dialog.dismiss();
							}
						});
					}

					if (alertDialog != null && alertDialog.isShowing())
						alertDialog.dismiss();

					alertDialog = alertDialogBuilder.create();
					if(context != null) {
						alertDialog.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}


	public void InspectTrailerDialog(final Context context, String title, String msg, AlertDialog alertDialog){
		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle(title);
			alertDialogBuilder.setMessage(msg);
			alertDialogBuilder.setCancelable(false);

				alertDialogBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								TabAct.host.setCurrentTab(4);
							}
						});


			if(alertDialog != null && alertDialog.isShowing())
				alertDialog.dismiss();

			alertDialog = alertDialogBuilder.create();
			if(context != null) {
				alertDialog.show();
			}
		}catch (Exception e){e.printStackTrace();}
	}


	public static void DriverSwitchAlertWithDismiss(final Context context, final String title, final String msg,
													final String okText, AlertDialog alertDialog, boolean isDismiss){
		try {
			if(isDismiss){
				if(alertDialog != null && alertDialog.isShowing()){
					alertDialog.dismiss();
				}
			}else{
				alertDialog.setTitle(Html.fromHtml(title));
				alertDialog.setMessage(Html.fromHtml(msg));

				// Setting OK Button
				alertDialog.setButton(Html.fromHtml(okText), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

				// Showing Alert Message
				if (context != null) {
					alertDialog.show();
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}



	public static void DriverSwitchAlert(final Context context, final String title, final String msg, final String okText){
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setTitle(Html.fromHtml(title));
			alertDialog.setMessage(Html.fromHtml(msg));

			// Setting OK Button
			alertDialog.setButton(Html.fromHtml(okText), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
				}
			});

			// Showing Alert Message
			if(context != null) {
				alertDialog.show();
			}
		}catch (Exception e){e.printStackTrace();}
	}



	public static void SwitchAlertWIthTabPosition(final Context context, final String title, final String msg, final String okText, final int position){
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setTitle(Html.fromHtml(title));
			alertDialog.setMessage(Html.fromHtml(msg));

			// Setting OK Button
			alertDialog.setButton(Html.fromHtml(okText), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				//	Constants.isCycleRequestAlert = true;
					TabAct.host.setCurrentTab(position);
					dialog.dismiss();
				}
			});

			// Showing Alert Message
			if(context != null) {
				alertDialog.show();
			}
		}catch (Exception e){e.printStackTrace();}
	}


	public static void ReadViolationDialog(final String title, final Context context){
		try {
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setTitle(Html.fromHtml("<font color='red'><b>Violation !!</b></font>"));
			alertDialog.setMessage(Html.fromHtml(title));

			// Setting OK Button
			alertDialog.setButton(Html.fromHtml("<font color='#1A3561'><b>Ok</b></font>"), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					SharedPref.SetIsReadViolation(true, context);
					dialog.dismiss();
				}
			});

			// Showing Alert Message
			if(context != null) {
				alertDialog.show();
			}
		}catch (Exception e){e.printStackTrace();}
	}



	/**
	 * Method to verify google play services on the device
	 * */
	public boolean checkPlayServices(Context context) {

		if(context != null) {
			GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

			int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

			if (resultCode != ConnectionResult.SUCCESS) {
				if (googleApiAvailability.isUserResolvableError(resultCode)) {
					Log.d("GooglePlayServices", "UserResolvableError");
				} else {
					Log.d("GooglePlayServices", "This device is not supported.");
				}
				return false;
			}
			return true;
		}
		return false;
	}



	public static void ShowNotificationWithSound(Context context, RulesResponseObject RemainingTimeObj, NotificationManagerSmart mNotificationManager){

	//	mNotificationManager.clearNotifications(context);
		int id = RemainingTimeObj.getNotificationType();

	//	Log.d("id", "---id: " + id);
		switch (id){

			case Constants.EldAlert:
			case Constants.USADrivingNotification:
			case Constants.USAOnDutyNotification:
			case Constants.USAConsecutiveOnDutyNotification:
			case Constants.USA6DayRuleNotification:
			case Constants.USA7DayRuleNotification:
			case Constants.CANADADrivingNotification:
			case Constants.CANADAOnDutyNotification:
			case Constants.CANADAConsecutiveOnDutyNotification:
			case Constants.CANADACycle1Notification:
			case Constants.CANADACycle2Notification:
			case Constants.CANADAShiftNotification:
			case Constants.USAShiftNotification:

				String title = "ALS";
				String message = "Alert";

				if(RemainingTimeObj.isViolation()){
					title = "Violation";
					message = RemainingTimeObj.getViolationReason();
				}

				Intent intent = new Intent(context, TabAct.class);   //
				mNotificationManager.showLocalNotification(title, message, id, intent);

				break;

		}
	}



	public static void ShowShiftAlertNotification(Context context, RulesResponseObject RemainingTimeObj, NotificationManagerSmart mNotificationManager){

		int id = RemainingTimeObj.getNotificationType() + 1000;
		Intent intent = new Intent(context, TabAct.class);   //
		mNotificationManager.showLocalNotification("ALS",  RemainingTimeObj.getMessage(), id , intent);

	}



	public static void ShowNotificationWithSound(Context context, String title, String message, NotificationManagerSmart mNotificationManager){

		Intent intent = new Intent(context, TabAct.class);   //
		mNotificationManager.showLocalNotification(title, message, 100001, intent);

	}

	public static void ShowLogoutNotificationWithSound(Context context, String title, String message, NotificationManagerSmart mNotificationManager){

		Intent intent = new Intent(context, LoginActivity.class);   //
		mNotificationManager.showLocalNotification(title, message, 101010, intent);

	}


	public static void ShowLocalNotification(Context context, String title, String message, int id ){

		Intent intent = new Intent(context, TabAct.class);   //
		//mNotificationManager.showLocalNotification(title, message, id , intent);

		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						context,
						101,
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String CHANNEL_ID = "als_01";// The id of the channel.
			CharSequence name = context.getResources().getString(R.string.app_name);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

			// Create a notification and set the notification channel.
			notification = mBuilder
					.setAutoCancel(true)
					.setContentTitle(title)
					.setContentText(message)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.als_notification)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.als_notification_big))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
					.setChannelId(CHANNEL_ID)
					.build();

			notificationManager.createNotificationChannel(mChannel);
		}else {
			notification = mBuilder
					.setAutoCancel(true)
					.setContentTitle(title)
					.setContentText(message)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.als_notification)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.als_notification_big))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
					.build();

		}

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(id, notification);
	}


	public static void ShowLogoutSpeedNotification(Context context, String title, String message, int id ){

		Intent intent = new Intent(context, LoginActivity.class);   //
		//mNotificationManager.showLocalNotification(title, message, id , intent);

		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						context,
						101,
						intent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String CHANNEL_ID = "als_01";// The id of the channel.
			CharSequence name = context.getResources().getString(R.string.app_name);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

			// Create a notification and set the notification channel.
			notification = mBuilder
					.setAutoCancel(true)
					.setContentTitle(title)
					.setContentText(message)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.als_notification)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.als_notification_big))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
					.setChannelId(CHANNEL_ID)
					.build();

			notificationManager.createNotificationChannel(mChannel);
		}else {
			notification = mBuilder
					.setAutoCancel(true)
					.setContentTitle(title)
					.setContentText(message)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.als_notification)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.als_notification_big))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
					.build();

		}

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(id, notification);
	}


	public static void ClearAllFields(Context c) {

		IS_LOGOUT = true;

		try {
			SharedPref.setPassword("", c);
			SharedPref.setUserName("", c);
			SharedPref.setServiceOnDestoryStatus(true, c);
			SharedPref.SetTruckStartLoginStatus(true, c);
			SharedPref.SetUpdateAppDialogTime("", c);
			SharedPref.setLastCalledWiredCallBack(0, c);
			//SharedPref.SetObdPreference(Constants.OBD_PREF_WIFI, c);

			ClearSqliteDB(c);

			Constants.ClearNotifications(c);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	public static void ClearSqliteDB(Context c) {

		try {
			ParseLoginDetails userData = new ParseLoginDetails();
			userData.ClearList(c);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SECOND_DRIVER_NAME = "--";
		TRUCK_NUMBER = "--";
		TRAILOR_NUMBER = "--";
		SHIPPER_NAME = "--";
		CONSIGNEE_NAME = "--";
		TRIP_NUMBER = "--";
		SECOND_DRIVER_NAME = "--";

		try {
			SharedPref.setLoadId("", c);
			SharedPref.setDriverId("", c);
			SharedPref.setDriverStatusId("", c);
			SharedPref.setCountryCycle("CountryCycle", "", c);
			SharedPref.setTimeZone("", c);
			SharedPref.setUTCTimeZone("utc_time_zone", "", c);
			SharedPref.setVINNumber("", c);
			SharedPref.setTrailorNumber("", c);
			SharedPref.setVehicleId("", c);
			SharedPref.SetViolation(false, c);
			SharedPref.SetViolationReason("", c);
			SharedPref.SetSystemToken("", c);
			SharedPref.SetIsReadViolation(false, c);
			SharedPref.setCurrentDate("", c);
			SharedPref.SetIsAOBRD(false, c);
			SharedPref.SetAOBRDAutomatic(false, c);
			SharedPref.SetAOBRDAutoDrive(false, c);
			SharedPref.SetDOTStatus(false, c);
			SharedPref.SetOdometerFromOBD(false, c);
			SharedPref.set16hrHaulExcptn(false, c);
			SharedPref.set16hrHaulExcptnCo(false, c);
			SharedPref.notificationDeleted(false, c);
			SharedPref.SetOBDPingAllowedStatus(false, c);
			SharedPref.SetAutoDriveStatus(false, c);
			SharedPref.SaveConnectionInfo("", "", c);

		} catch (Exception e) {
			e.printStackTrace();
		}



		try {
				DriverConst.SetDriverLoginDetails( "", "", c);
				DriverConst.SetDriverDetails( "", "", "", "", "", "","", "", c);
				DriverConst.SetDriverCurrentCycle("", "", c);
				DriverConst.SetDriverSettings("", "","", "","", "", "", "", "" , c);
				DriverConst.SetDriverTripDetails( "", "", "", "", "", "", "", "", "", "", "", "","", "", "", c);
				DriverConst.SetDriverLogDetails( "", "", "", "", "", "", "", "", "", "","", "", "", "", "", c);

		} catch (Exception e) {    }

			try {
				DriverConst.SetCoDriverLoginDetails("", "", c);
				DriverConst.SetCoDriverDetails("", "", "", "", "", "", "", "", c);
				DriverConst.SetCoDriverCurrentCycle("", "", c);
				DriverConst.SetCoDriverSettings("", "", "","", "", "", "", "", "", c);
				DriverConst.SetCoDriverTripDetails("", "", "", "", "", "", "", "", "", "", "", "","", "", "", c);
				DriverConst.SetCoDriverLogDetails("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", c);
			} catch (Exception e) {    }

			try {
				MainDriverEldPref MainDriverPref = new MainDriverEldPref();
				CoDriverEldPref CoDriverPref = new CoDriverEldPref();
				MainDriverPref.ClearLocFromList(c);
				CoDriverPref.ClearLocFromList(c);

				NotificationPref notificationPref = new NotificationPref();
				CoNotificationPref coNotificationPref = new CoNotificationPref();
				notificationPref.RemoveNotification(c);
				coNotificationPref.RemoveNotification(c);

			} catch (Exception e) {    }

			try {
                Utils util = new Utils(c);
                util.deleteWiredObdLog();
                util.deleteAppUsageLog();
            }catch (Exception e){ }

			try {
				//c.deleteDatabase(DBHelper.DATABASE_NAME);
				DBHelper dbHelper            = new DBHelper(c);
				dbHelper.DeleteTable();
				dbHelper.DeleteShipmentTable();
				dbHelper.DeleteShipment18DaysTable();
				dbHelper.DeleteOdometerTable();
				dbHelper.DeleteOdometer18DaysTable();
				dbHelper.DeleteInspectionTable();
				dbHelper.DeleteRecap18DaysTable();
				dbHelper.DeleteSyncDataTable();
				dbHelper.DeleteDriverLocTable();
				dbHelper.DeleteLatLongTable();
				dbHelper.DeleteSupportTable();
				dbHelper.DeleteDriverPermissionTable() ;
				dbHelper.DeleteDriverLogRecordTable();
				dbHelper.DeleteInspection18DaysTable();
				dbHelper.DeleteInspectionOfflineTable();
				dbHelper.DeleteShippingLogTable();
				dbHelper.DeleteNotificationTable();
				dbHelper.DeleteNotificationSaveToTable();
				dbHelper.DeleteCtPatInspectionTable();
				dbHelper.DeleteCtPat18DaysInspTable();
				dbHelper.DeleteMalfunctionDiagnosticTable();
				dbHelper.DeleteMalfunctionDiagnosticTable1();
				dbHelper.DeleteMalDiaOccTimeTable();
				dbHelper.DeletePowerComplianceTable();

			}catch (Exception e){
				e.printStackTrace();
			}
		}

}



