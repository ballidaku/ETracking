package com.ballidaku.etracking.commonClasses;

/**
 * Created by sharanpalsingh on 23/07/17.
 */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

/**
 * Created by brst-pc20 on 11/8/16.
 */

public abstract class AbsRuntimeMarshmallowPermission extends AppCompatActivity
{
    public static final int GALLERY_CAMERA_PERMISSION_CODE = 1111;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE =1112;
    public static final int READ_PHONE_STATE_REQUEST_CODE = 1113;
    public static final int CAMERA_REQUEST_CODE = 1114;
    public static final int ALL_PERMISSIONS = 1115;
    public static final int READ_PHONE_STATE_REQUEST_CODE2 = 1120;
    public static final int GET_ACCOUNTS = 1117;
    public static final int READ_XTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1116;
    public static final int INTERNET_PERMISSION = 1118;
    public static final int SHARE_EXTERNAL_STORAGE_PERMISSION_CODE=1119;
    public static final int DOWNLOADING_ASSET_PERMISSION=11211;

    private SparseIntArray mErrorString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();

    }

    public abstract void onPermissionGranted(int requestCode);


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int permissionCheck= PackageManager.PERMISSION_GRANTED;
        for (int permission: grantResults)
        {
            permissionCheck=permissionCheck+permission;
        }

        if (grantResults.length>0&& PackageManager.PERMISSION_GRANTED==permissionCheck)
        {
            onPermissionGranted(requestCode);
        }
        else{
            Snackbar.make(findViewById(android.R.id.content),mErrorString.get(requestCode), Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.parse("package:"+getPackageName()));
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivity(i);
                }
            }).show();
        }
    }

    public void requestAppPermissions(final String[] requestedPermissions, final int stringId, final int requestCode) {
        mErrorString.put(requestCode,stringId);

        int permissioncheck= PackageManager.PERMISSION_GRANTED;

        boolean showRequestedPermissions=false;
        for(String permission:requestedPermissions)
        {
            permissioncheck=permissioncheck+ ContextCompat.checkSelfPermission(this,permission);
            showRequestedPermissions=showRequestedPermissions|| ActivityCompat.shouldShowRequestPermissionRationale(this,permission);

        }
        if(permissioncheck!= PackageManager.PERMISSION_GRANTED)
        {
            if (showRequestedPermissions)
            {
                Snackbar.make(findViewById(android.R.id.content),stringId, Snackbar.LENGTH_INDEFINITE).setAction("GRANT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(AbsRuntimeMarshmallowPermission.this,requestedPermissions,requestCode);
                    }
                }).show();
            }
            else{
                ActivityCompat.requestPermissions(this,requestedPermissions,requestCode);
            }
        }
        else{
            onPermissionGranted(requestCode);
        }




    }



}