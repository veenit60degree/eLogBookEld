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


		int width = ((screenWidth / 3) + 130);	//((screenWidth / 3) + 130);

		return width;

	}
	
	
	
}
