package com.constants;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import com.messaging.logistic.Globally;

public class CommonUtils {

	
	public static int setWidth(Context mContext) {
		//Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		//int w = display.getWidth();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenWidth = displaymetrics.widthPixels;
		//int screenHeight = displaymetrics.heightPixels;

		int width = 500;	//((screenWidth / 3) + 130);

		if(Globally.isTablet(mContext)){
			width = ((screenWidth / 3) - 450);
			if(width < 500){
				width = 500;
			}
		}else{
			width = ((screenWidth / 3) - 300);
			if(width < 400){
				width = 400;
			}
		}


		return width;

	}
	
	
	
}
