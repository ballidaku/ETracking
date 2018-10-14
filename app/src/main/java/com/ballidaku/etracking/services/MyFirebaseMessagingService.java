package com.ballidaku.etracking.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.commonClasses.NotificationHelper;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    String message, title;
    Uri notificationSound;

    JSONObject object;

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);

        addDataToSharedPreference(s);
    }

    private void addDataToSharedPreference(String refreshedToken)
    {

        String key = MySharedPreference.getInstance().getUserID(getApplicationContext());


        if (!key.equals("") && !key.isEmpty())
        {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(MyConstant.FCM_TOKEN, refreshedToken);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .getRoot()
                    .child(MyConstant.USERS).child(MyConstant.USER_TYPE).child(key).updateChildren(hashMap);
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication());
        String body = remoteMessage.getData().get("body");
        title = remoteMessage.getData().get("title");

        if (body != null)
        {


                JsonObject jsonObject = new JsonObject().getAsJsonObject(body);

                Bundle extras = new Bundle();
                String type = "";
                if (jsonObject.has(MyConstant.TYPE))
                {
                    type = jsonObject.getString(MyConstant.TYPE);

                    extras.putString(MyConstant.TYPE, type);
                }
                if (jsonObject.has(MyConstant.MESSAGEE))
                {
                    message = jsonObject.getString(MyConstant.MESSAGEE);

                }

                Intent intent1 = new Intent(getApplication(), MainActivity.class);


                intent1.putExtras(extras);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

                if (type.equals(MyConstant.CUSTOMERS) && MySharedPreference.getInstance().getNoti(getApplicationContext(), MyConstant.CUSTOMER))
                {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent);
                }
                else if (type.equals(MyConstant.USERFEEDS) && MySharedPreference.getInstance().getNoti(getApplicationContext(), MyConstant.NEWSFEEDS))
                {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent);
                }

                else if (type.equals(MyConstant.LEAD) && MySharedPreference.getInstance().getNoti(getApplicationContext(), MyConstant.LEAD))
                {
                    showSmallNotification(mBuilder, title, message, resultPendingIntent);
                }


        }
    }


    private void showSmallNotification(NotificationCompat.Builder mBuilder, String title, String message, PendingIntent resultPendingIntent)
    {

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            notificationHelper.notify((int) System.currentTimeMillis(), notificationHelper.getNotification1(title, message, resultPendingIntent));
        }
        else
        {
            NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

            inboxStyle.bigText(message);

            Notification notification;
            notification = mBuilder.setSmallIcon(R.mipmap.ic_logo_round)
                    .setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(notificationSound)
                    .setStyle(inboxStyle)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.ic_logo_round))
                    .setContentText(message)
                    .build();


            if (notificationManager != null)
            {
                notificationManager.notify(0, notification);
            }
        }

    }
}

