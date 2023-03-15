package com.constants;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import com.driver.details.DriverConst;
import com.als.logistic.Globally;

public class CommonUtils {

	
	public static int setWidth(Context mContext) {
		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
		Logger.LogDebug("width","width: " +w);

		int width = 500;	//((screenWidth / 3) + 130);
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int densityDpi = (int)(metrics.density * 160f);
		Logger.LogDebug("densityDpi","densityDpi: " +densityDpi);

		int SingleDriverMenuWidth;
		int DualDriverMenuWidth;

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
				//SingleDriverMenuWidth = 500;
				//DualDriverMenuWidth   = 580;

				SingleDriverMenuWidth = 630;
				DualDriverMenuWidth   = 680;

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
