package com.background.service;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.als.logistic.Globally;
import com.als.logistic.R;
import com.constants.Constants;
import com.constants.DownloadAppService;
import com.constants.Logger;
import com.constants.SharedPref;
import com.local.db.ConstantsKeys;

import java.io.File;

public class AppDownloadService extends Service {

    private static final int NOTIFICATION_ID = 1;
    String TAG = "DownloadApp";
    private String folder = "", filePath = "", apkName = "";
    boolean isDownloading ;
    long  DownloadId = -1;

    private DownloadManager downloadManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION_START_DOWNLOAD)) {
                String apkUrl = intent.getStringExtra(Constants.EXTRA_APK_URL);
                String VersionCode     = intent.getStringExtra(ConstantsKeys.VersionCode);
                String VersionName     = intent.getStringExtra(ConstantsKeys.VersionName);
                isDownloading          = intent.getBooleanExtra(ConstantsKeys.IsDownloading, false);

                try{
                    folder      = Globally.getAlsApkPath().toString();
                    apkName     = "ALS_"+ VersionCode + "_" + VersionName + ".apk";
                    filePath    = folder + "/" + apkName;
                    Globally.DeleteDirectory(folder);
                }catch (Exception e){
                    Globally.DeleteDirectory(folder);
                    e.printStackTrace();
                }


                startDownload(apkUrl, ("Eld-"+VersionName), apkName);

            }
        }

        return START_NOT_STICKY;
    }

    private void startDownload(String apkUrl, String fileName, String apkFileName) {
        if(apkUrl.contains("http")) {
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(apkUrl);
            // String fileName = uri.getLastPathSegment();

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Downloading " + fileName);
            request.setDescription("Please wait...");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/EldApp/" + apkFileName);      //

            DownloadId = downloadManager.enqueue(request);
            Logger.LogDebug(TAG, "----downloadId: " + DownloadId);

            // Optionally, you can show a notification with progress and handle download completion
            showDownloadNotification(DownloadId);
        }
    }


    private void cancelDownload(long downloadId) {
        if (downloadManager != null) {
            SharedPref.setAsyncCancelStatus(false, getApplicationContext());
            Logger.LogDebug(TAG, "----downloadId: " +downloadId);
            downloadManager.remove(downloadId);
            stopSelf(); // Stop the service after cancelling the download
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        SharedPref.setAsyncCancelStatus(false, getApplicationContext());

        Logger.LogDebug("----AppDownloadService", "onDestroy");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private void showDownloadNotification(long downloadId) {


        try {
            // Create a NotificationChannel for Android Oreo and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("download_channel", "Downloads", NotificationManager.IMPORTANCE_LOW);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // Create the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "download_channel")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Downloading App")
                    .setContentText("Download in progress")
                    .setProgress(100, 0, false)
                    .setOngoing(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

            // Listen for download progress
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);

            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = downloadManager.query(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        @SuppressLint("Range") int progress = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        @SuppressLint("Range") int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (status == DownloadManager.STATUS_RUNNING) {
                            builder.setProgress(total, progress, false);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                            handler.postDelayed(this, 1000); // Update every second
                            downloadProgress(progress, "", false, false);

                            // If cancel request to stop download
                            if (SharedPref.getAsyncCancelStatus(getApplicationContext())) {
                                cancelDownload(DownloadId);
                                DeleteFile(filePath);

                                builder.setContentText("---------Downloading cancelled")
                                        .setProgress(0, 0, false)
                                        .setOngoing(false)
                                        .setAutoCancel(true);
                                notificationManager.notify(NOTIFICATION_ID, builder.build());

                                DownloadId = -1;

                                downloadProgress(progress, "", false, true);

                            }



                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            builder.setContentText("Download complete")
                                    .setProgress(0, progress, false)
                                    .setOngoing(false)
                                    .setAutoCancel(true);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());

                            downloadProgress(progress, filePath, true, false);
                            DownloadId = -1;
                            Logger.LogDebug(TAG, "---------Download cancelled" );

                        } else if (status == DownloadManager.STATUS_FAILED) {
                            builder.setContentText("Download failed")
                                    .setProgress(0, 0, false)
                                    .setOngoing(false)
                                    .setAutoCancel(true);
                            notificationManager.notify(NOTIFICATION_ID, builder.build());

                            downloadProgress(0, "", false,true);
                            DownloadId = -1;

                            DeleteFile(filePath);
                            Logger.LogDebug(TAG, "-----Download cancelled" );

                        }
                    }
                    cursor.close();
                }
            };
            handler.post(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void downloadProgress(int Progress, String Path, boolean isCompleted, boolean IsInterrupt){
        try {
            Intent intent = new Intent(ConstantsKeys.DownloadProgress);
            intent.putExtra(ConstantsKeys.Percentage, Progress);
            intent.putExtra(ConstantsKeys.Path, Path);
            intent.putExtra(ConstantsKeys.IsCompleted, isCompleted);
            intent.putExtra(ConstantsKeys.IsInterrupted, IsInterrupt);

            LocalBroadcastManager.getInstance(AppDownloadService.this).sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void DeleteFile(String file_path){
        try {
            File file = new File(file_path);
            if (file.isFile()) {
                file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}