package com.ballidaku.etracking.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by sharanpalsingh on 26/03/17.
 */
public class MySharedPreference
{

    public final String PreferenceName = "MyWalletPreference";


    public MySharedPreference()
    {
    }

    public static MySharedPreference instance = null;

    public static MySharedPreference getInstance()
    {
        if (instance == null)
        {
            instance = new MySharedPreference();
        }

        return instance;
    }


    public SharedPreferences getPreference(Context context)
    {
        return context.getSharedPreferences(PreferenceName, Activity.MODE_PRIVATE);
    }



    public void saveToken(Context context, String fcmToken)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(MyConstant.FCM_TOKEN, fcmToken);
        editor.apply();
    }


    public String getToken(Context context)
    {
        return getPreference(context).getString(MyConstant.FCM_TOKEN,"");
    }


    public void saveUser(Context context, String userid, String userType, HashMap<String, Object> hashMap)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();

        editor.putString(MyConstant.USER_ID, userid);
        editor.putString(MyConstant.USER_TYPE, userType);
        editor.putString(MyConstant.USER_NAME, (String) hashMap.get(MyConstant.USER_NAME));
        editor.putString(MyConstant.USER_EMAIL, (String) hashMap.get(MyConstant.USER_EMAIL));
        editor.putString(MyConstant.USER_PHONE, (String) hashMap.get(MyConstant.USER_PHONE));

        if(userType.equals(MyConstant.BEAT))
        {
            editor.putString(MyConstant.RANGE, (String) hashMap.get(MyConstant.RANGE));
            editor.putString(MyConstant.BLOCK, (String) hashMap.get(MyConstant.BLOCK));
            editor.putString(MyConstant.BEAT, (String) hashMap.get(MyConstant.BEAT));
            editor.putString(MyConstant.HEADQUATER, (String) hashMap.get(MyConstant.HEADQUATER));
        }

        editor.apply();
    }

    public String getUserID(Context context)
    {
        return getPreference(context).getString(MyConstant.USER_ID,"");
    }

    public String getUserName(Context context)
    {
        return getPreference(context).getString(MyConstant.USER_NAME,"");
    }

    public void clearUserID(Context context)
    {
         getPreference(context).edit().putString(MyConstant.USER_ID,"").apply();
    }

    public String getUserType(Context context)
    {
        return getPreference(context).getString(MyConstant.USER_TYPE,"");
    }


    public String getUserEmail(Context context)
    {
        return getPreference(context).getString(MyConstant.USER_EMAIL,"");
    }

    public String getUserPhone(Context context)
    {
        return getPreference(context).getString(MyConstant.USER_PHONE,"");
    }

    public String getRange(Context context)
    {
        return getPreference(context).getString(MyConstant.RANGE,"");
    }

    public String getBlock(Context context)
    {
        return getPreference(context).getString(MyConstant.BLOCK,"");
    }

    public String getBeat(Context context)
    {
        return getPreference(context).getString(MyConstant.BEAT,"");
    }

    public String getHeadquater(Context context)
    {
        return getPreference(context).getString(MyConstant.HEADQUATER,"");
    }

}
