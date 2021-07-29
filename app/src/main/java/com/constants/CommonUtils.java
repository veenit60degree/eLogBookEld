package com.constants;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.driver.details.DriverConst;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import java.util.Objects;

public class CommonUtils {

	
	public static int setWidth(Context mContext) {
		//Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		//int w = display.getWidth();

		int width = 500;	//((screenWidth / 3) + 130);
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int densityDpi = (int)(metrics.density * 160f);
		Log.d("densityDpi","densityDpi: " +densityDpi);

		int SingleDriverMenuWidth;
		int DualDriverMenuWidth;

/*		TelephonyManager manager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Objects.requireNonNull(manager).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
				Globally.ShowLocalNotification(mContext, "Screen Dimensions", "DisplayMetrics IsTablet: " + Constants.isTabletDevice(mContext) +
						"\nTelephonyManager: Tablet \nPixels: " + ""+metrics.widthPixels + "x" + metrics.heightPixels, 2003);
			} else {
				Globally.ShowLocalNotification(mContext, "Screen Dimensions", "DisplayMetrics IsTablet: " + Constants.isTabletDevice(mContext) +
						"\nTelephonyManager: Mobile Phone \nPixels: " + ""+metrics.widthPixels + "x" + metrics.heightPixels, 2003);
			}
		}*/


		if(Globally.isTablet(mContext)) {

			//Globally.ShowLocalNotification(mContext, "ALS SideMenu", "Tablet density pixel: " + densityDpi, 2003);

			if(densityDpi <= 220){
				SingleDriverMenuWidth = 425;
				DualDriverMenuWidth   = 500;
			}else if (densityDpi >= 240 && densityDpi <= 340){
				SingleDriverMenuWidth = 530;
				DualDriverMenuWidth   = 590;
			}else{
				SingleDriverMenuWidth = 520;
				DualDriverMenuWidth   = 580;
			}

			if(SharedPref.getDriverType(mContext).equals(DriverConst.SingleDriver)){
				width = SingleDriverMenuWidth;
			}else {
				width = DualDriverMenuWidth;
			}
		}else{
		//	Globally.ShowLocalNotification(mContext, "ALS SideMenu", "Phone density pixel: " + densityDpi, 2003);

			if(densityDpi <= 320){
				SingleDriverMenuWidth = 560;
				DualDriverMenuWidth   = 600;
			}else if(densityDpi > 320 && densityDpi <= 420){
				SingleDriverMenuWidth = 450;
				DualDriverMenuWidth   = 490;
			}else{
				SingleDriverMenuWidth = 470;
				DualDriverMenuWidth   = 540;
			}

			// this check is for special device resolution (Chinese tablet)
			if(metrics.widthPixels == 1280 && metrics.heightPixels == 800){
				SingleDriverMenuWidth = 500;
				DualDriverMenuWidth   = 580;
			}else if(metrics.widthPixels > 2200 && metrics.heightPixels >= 1080){
				SingleDriverMenuWidth = 980;
				DualDriverMenuWidth   = 1050;
			}

			if(SharedPref.getDriverType(mContext).equals(DriverConst.SingleDriver)) {
				width = SingleDriverMenuWidth;
			}else{
				width = DualDriverMenuWidth;
			}



		}


		return width;

	}



}
