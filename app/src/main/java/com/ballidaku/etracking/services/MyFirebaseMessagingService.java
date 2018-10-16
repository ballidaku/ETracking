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
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.commonClasses.NotificationHelper;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.ballidaku.etracking.mainScreens.beatScreens.NotificationActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;

import java.util.HashMap;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    String TAG = MyFirebaseMessagingService.class.getSimpleName();

    Uri notificationSound;

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
        String title = remoteMessage.getData().get("title");

        Log.e(TAG, "Body " + body + " title " + title);

        if (body != null)
        {
            JsonObject jsonObject = CommonMethods.getInstance().convertStringToGsonObject(body);

            String message = jsonObject.get(MyConstant.NOTIFICATION_TEXT).getAsString();

            String userType = MySharedPreference.getInstance().getUserType(getApplicationContext());

            Intent intent;
            if (userType.equals(MyConstant.ADMIN) || userType.equals(MyConstant.SUB_ADMIN))
            {
//                Bundle extras = new Bundle();

                intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra(MyConstant.FROM_WHERE, MyConstant.NOTIFICATIONS);
//                intent.putExtras(extras);
            }
            else
            {
                intent = new Intent(getApplication(), NotificationActivity.class);
            }


            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            showSmallNotification(mBuilder, title, message, resultPendingIntent);

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

