/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.als.logistic;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;

import androidx.multidex.BuildConfig;
import androidx.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.ble.listener.MyReceiveListener;
import com.constants.Constants;
import com.constants.Logger;
import com.google.android.gms.security.ProviderInstaller;
import com.htstart.htsdk.HTBleSdk;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
//import com.onesignal.OneSignal;


public class UILApplication extends Application {

	private static boolean activityVisible = true;

	private boolean isNightModeEnabled = false;
	public static final String NIGHT_MODE = "NIGHT_MODE";

	private static UILApplication singleton = null;

	public static UILApplication getInstance() {

		if(singleton == null)
		{
			singleton = new UILApplication();
		}
		return singleton;
	}




	@Override
	protected void attachBaseContext(Context base) {
		Context context = getInstance().setupTheme(base);
		super.attachBaseContext(context);
		MultiDex.install(this);

	}



	@Override
	public void onCreate() {
		super.onCreate();

		HTBleSdk.Companion.getInstance().initHTBleSDK(this); // , getPackageName()
	//	HTBleSdk.Companion.getInstance().setDebugLogcatEvent(false);

		HTBleSdk.Companion.getInstance().registerCallBack(new MyReceiveListener());

		if (Globally.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}

		if(BuildConfig.DEBUG)
			StrictMode.enableDefaults();

		StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
				.permitDiskWrites()
				.build());
		StrictMode.setThreadPolicy(old);

		initImageLoader(getApplicationContext());


		singleton = this;

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);

		HTBleSdk.Companion.getInstance().initHTBleSDK(this);

	}



	public void setTheme(){

		if (isNightModeEnabled()) { // PhoneLightMode() == Configuration.UI_MODE_NIGHT_YES
			setTheme(R.style.AppTheme);
		} else {
			setTheme(R.style.AppThemeELD);
		}

	}


	public int PhoneLightMode() {
		int nightModeFlags = getResources().getConfiguration().uiMode &
				Configuration.UI_MODE_NIGHT_MASK;
     /*   switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                doStuff();
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                doStuff();
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                doStuff();
                break;
        }*/
		//UI_MODE_NIGHT_NO = 16
		// UI_MODE_NIGHT_YES = 32
		// UI_MODE_NIGHT_UNDEFINED = 0;
		return nightModeFlags;
	}


	public boolean isNightModeEnabled() {
		return isNightModeEnabled;
	}

	public int getThemeColor(){
		if(isNightModeEnabled){
			return Color.parseColor("#444366");
		}else{
			return Color.parseColor("#1A3561");
		}
	}


	public void setIsNightModeEnabled(boolean isNightModeEnabled) {
		this.isNightModeEnabled = isNightModeEnabled;

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
		editor.apply();

		if(isNightModeEnabled) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		}else{
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}


	}






	@SuppressLint("WrongConstant")
	public Context setupTheme(Context context) {

		Resources res = context.getResources();
		int mode = res.getConfiguration().uiMode;
		int savedMode = AppCompatDelegate.MODE_NIGHT_YES;

		switch (savedMode) {
			case AppCompatDelegate.MODE_NIGHT_YES:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
				mode = Configuration.UI_MODE_NIGHT_YES;
				break;
			case AppCompatDelegate.MODE_NIGHT_NO:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
				mode = Configuration.UI_MODE_NIGHT_NO;
				break;
			default:
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
				break;
		}

		Configuration config = new Configuration(res.getConfiguration());
		config.uiMode = mode;
		if (Build.VERSION.SDK_INT >= 17) {
			context = context.createConfigurationContext(config);
		} else {
			res.updateConfiguration(config, res.getDisplayMetrics());
		}
		return context;
	}



	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}


	public static boolean isActivityVisible() {
		if(Constants.IS_ACTIVE_ELD){
			activityVisible = true;
		}
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}



	private void updateAndroidSecurityProvider() {
		try {
			ProviderInstaller.installIfNeeded(this);
		} catch (Exception e) {
			e.getMessage();
		}
	}

}

