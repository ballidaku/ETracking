package com.ballidaku.etracking.commonClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.ballidaku.etracking.R;


public class NotificationHelper extends ContextWrapper
{
    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "com.crm.ONE";
    public static final String CHANNEL_ONE_NAME = "Channel One";

//Create your notification channels//

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base)
    {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels()
    {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                  CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);



    }

//Create the notification that’ll be posted to Channel One//

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationCompat.Builder getNotification1(String title, String body, PendingIntent resultPendingIntent)
    {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                  .setContentTitle(title)
                  .setContentText(body)
                  .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                  .setSmallIcon(R.mipmap.ic_logo_round)
                  .setContentIntent(resultPendingIntent)
                  .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_round))
                  .setAutoCancel(true);
    }



    public void notify(int id, NotificationCompat.Builder notification)
    {
        getManager().notify(id, notification.build());
    }

//Send your notifications to the NotificationManager system service//

    private NotificationManager getManager()
    {
        if (notifManager == null)
        {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
