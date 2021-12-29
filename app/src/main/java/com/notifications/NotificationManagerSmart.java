package com.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import com.constants.Constants;
import com.messaging.logistic.Globally;
import com.messaging.logistic.R;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationManagerSmart {

    final int ID_BIG_NOTIFICATION       = 101;
    /* final int ID_BIG_VIOLATION          = 0;
     final int ID_BIG_DRIVING            = 0;
     final int ID_BIG_OFF_DUTY           = 0;
     final int ID_BIG_SLEEPER            = 0;
 */
    final int ID_SMALL_NOTIFICATION     = 201;
    private Context mCtx;

    public NotificationManagerSmart(Context mCtx) {
        this.mCtx = mCtx;
    }




    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder
                //.setSmallIcon(R.drawable.app_icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.app_icon)
                //   .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.app_icon))
                //    .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, int ID, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        Notification notification;
        notification = mBuilder
                //.setSmallIcon(R.drawable.app_icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.als_notification)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.als_notification_big))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }


    public static void dismissNotification(Context context, int id){
        try{
            if(context != null) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showLocalNotification(String title, String message, int ID, Intent intent) {
        Globally.PlaySound(mCtx);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx);
        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "als_01";// The id of the channel.
            CharSequence name = mCtx.getResources().getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            // Create a notification and set the notification channel.
            notification = mBuilder
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(resultPendingIntent)
                    .setSmallIcon(R.drawable.als_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.als_notification_big))
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
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.als_notification_big))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .build();

        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(ID, notification);

    }


    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
