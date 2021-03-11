package com.constants;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.driver.details.DriverConst;
import com.messaging.logistic.Globally;

public class CommonUtils {

	
	public static int setWidth(Context mContext) {
		//Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		//int w = display.getWidth();

		int width = 500;	//((screenWidth / 3) + 130);
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		int densityDpi = (int)(metrics.density * 160f);
		Log.d("densityDpi","densityDpi: " +densityDpi);

		int SingleDriverMenuWidth = 385;
		int DualDriverMenuWidth   = 500;
		if(Globally.isTablet(mContext)) {

			//Globally.ShowLocalNotification(mContext, "ALS SideMenu", "Tablet density pixel: " + densityDpi, 2003);

			if(densityDpi <= 220){
				SingleDriverMenuWidth = 355;
				DualDriverMenuWidth   = 460;
			}else if (densityDpi >= 240 && densityDpi <= 340){
				SingleDriverMenuWidth = 470;
				DualDriverMenuWidth   = 540;
			}

			if(SharedPref.getDriverType(mContext).equals(DriverConst.SingleDriver)){
				width = SingleDriverMenuWidth;
			}else {
				width = DualDriverMenuWidth;
			}
		}else{
		//	Globally.ShowLocalNotification(mContext, "ALS SideMenu", "Phone density pixel: " + densityDpi, 2003);

			if(densityDpi <= 320){
				SingleDriverMenuWidth = 310;
				DualDriverMenuWidth   = 333;
			}else if(densityDpi > 320 && densityDpi <= 420){
				SingleDriverMenuWidth = 275;
				DualDriverMenuWidth   = 295;
			}else{
				SingleDriverMenuWidth = 310;
				DualDriverMenuWidth   = 335;
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
