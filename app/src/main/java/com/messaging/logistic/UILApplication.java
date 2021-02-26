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
package com.messaging.logistic;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class UILApplication extends Application {



	boolean IsNewVersion = false;
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



	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		super.onCreate();

		if (Globally.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
		}

		StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
				.permitDiskWrites()
				.build());
		//doCorrectStuffThatWritesToDisk();
		StrictMode.setThreadPolicy(old);

		initImageLoader(getApplicationContext());


		singleton = this;

		SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false);

		//NetworkUtil util = new NetworkUtil();
	//	util.isConnected();

	//	new GetVersionCode().execute();
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
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
	}




}

/*

	private class GetVersionCode extends AsyncTask<Void, String, String> {
		@Override
		protected String doInBackground(Void... voids) {

			String newVersion = null;
			try {  //MainActivity.this.getPackageName()
				newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.messaging.logistic&hl=it")
						.timeout(30000)
						.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.referrer("http://www.google.com")
						.get()
						.select("div[itemprop=softwareVersion]")
						.first()
						.ownText();
				return newVersion;
			} catch (Exception e) {
				return newVersion;
			}
		}

		@Override
		protected void onPostExecute(String onlineVersion) {
			super.onPostExecute(onlineVersion);
			if (onlineVersion != null && !onlineVersion.isEmpty()) {
				// if (Float.parseFloat(currentVersion) < Float.parseFloat(onlineVersion)) {
				//show dialog
				// }

				String AppVersion = "";
				PackageManager manager = getPackageManager();
				PackageInfo info = null;

				try {
					info = manager.getPackageInfo("com.messaging.logistic", 0);
					AppVersion = info.versionName;
					Log.d("versionCode", "---versionName: " + AppVersion);
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}

				String[] AppVersionArray    = AppVersion.split("\\.");
				String[] OnlineVersionArray = onlineVersion.split("\\.");

				for(int i = 0 ; i < AppVersionArray.length ; i++){
					int OnlineVersionInt    = Integer.valueOf(OnlineVersionArray[i]);
					int AppVersionInt       = Integer.valueOf(AppVersionArray[i]);

					if(OnlineVersionInt > AppVersionInt){
						Log.d("update", "----- New Version Available ");
						IsNewVersion = true;
					}
				}

				new Handler().postDelayed(new Runnable() {
					public void run() {
						if(IsNewVersion){
							Intent i = new Intent(getApplicationContext(), UpdateAppActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(i);
						}
					}
				}, 1500);

			}
			//	Log.d("update", "-----playstore version " + onlineVersion);
		}
	}
*/

