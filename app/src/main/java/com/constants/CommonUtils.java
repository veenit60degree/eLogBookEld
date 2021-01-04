package com.constants;

import android.app.Activity;
import android.content.Context;
import android.view.Display;

public class CommonUtils {

	
	public static int setWidth(Context c) {
		Display display = ((Activity) c).getWindowManager().getDefaultDisplay();

		int w = display.getWidth();

		int width = ((w / 3) + 130);

		return width;

	}
	
	
	
}
