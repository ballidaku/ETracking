package com.ballidaku.etracking.mainScreens.beatScreens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ballidaku.etracking.BuildConfig;
import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.CompressImageVideo;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.frontScreens.LoginActivity;
import com.ballidaku.etracking.mainScreens.ProfileActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.util.Date;

import static com.ballidaku.etracking.commonClasses.MyConstant.REQUEST_CHECK_SETTINGS;


public class BeatActivity extends AbsRuntimeMarshmallowPermission implements  View.OnClickListener
{


    String TAG = BeatActivity.class.getSimpleName();

    Context context;

    // location updates interval - 5sec
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 5;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    public static Location mCurrentLocation;

    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;


    Button buttonStart;
    Button buttonStop;
    Button buttonImageReport;
    Button buttonViewImageReport;
    Button buttonReportOffence;
    Button buttonViewReportedOffence;


    TextView textViewLastUpdateTime;
    String startTrackKey;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat);

        context = this;

        setUpViews();

        // initialize the necessary libraries
        init();

        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
    }


    //**********************************************************************************************
    //**********************************************************************************************
    // Get Location Code
    //**********************************************************************************************
    //**********************************************************************************************

    private void init()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void restoreValuesFromBundle(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            if (savedInstanceState.containsKey("is_requesting_updates"))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location"))
            {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on"))
            {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    private void updateLocationUI()
    {
        if (mCurrentLocation != null)
        {
            textViewLastUpdateTime.setText(mLastUpdateTime);

            String locationString = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();

            MyFirebase.getInstance().saveUserLocation(context, startTrackKey, locationString);
        }
        setButtonsEnabledState();
    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates()
    {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>()
                {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse)
                    {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode)
                        {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try
                                {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(BeatActivity.this, REQUEST_CHECK_SETTINGS);
                                }
                                catch (IntentSender.SendIntentException sie)
                                {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(BeatActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    private void openSettings()
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startUpdatesButtonHandler()
    {

        startTrackKey = MyFirebase.getInstance().getStartLoactionNode(context);

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener()
                {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        if (response.isPermanentlyDenied())
                        {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void stopUpdatesButtonHandler()
    {
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
    }

    protected void stopLocationUpdates()
    {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        setButtonsEnabledState();
                    }
                });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions())
        {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private void setButtonsEnabledState()
    {
        if (mRequestingLocationUpdates)
        {
            buttonStart.setVisibility(View.GONE);
            buttonStop.setVisibility(View.VISIBLE);
        }
        else
        {
            buttonStart.setVisibility(View.VISIBLE);
            buttonStop.setVisibility(View.GONE);
        }
    }

    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy()
    {

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }

        super.onDestroy();
    }










    private void setUpViews()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TextView) findViewById(R.id.textViewTitle)).setText("Tracking");


        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);
        buttonImageReport = findViewById(R.id.buttonImageReport);
        buttonViewImageReport = findViewById(R.id.buttonViewImageReport);
        buttonReportOffence = findViewById(R.id.buttonReportOffence);
        buttonViewReportedOffence = findViewById(R.id.buttonViewReportedOffence);

        findViewById(R.id.buttonViewSendSOS).setOnClickListener(this);
        findViewById(R.id.buttonVideoReport).setOnClickListener(this);
        findViewById(R.id.buttonViewReportedVideos).setOnClickListener(this);
        findViewById(R.id.buttonViewNotifications).setOnClickListener(this);


        textViewLastUpdateTime = findViewById(R.id.textViewLastUpdateTime);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonImageReport.setOnClickListener(this);
        buttonViewImageReport.setOnClickListener(this);
        buttonReportOffence.setOnClickListener(this);
        buttonViewReportedOffence.setOnClickListener(this);


        // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonStart:
                startUpdatesButtonHandler();
                break;

            case R.id.buttonStop:
                stopUpdatesButtonHandler();
                break;

            case R.id.buttonImageReport:
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestAppPermissions(permission, R.string.permission, 54);
                break;

            case R.id.buttonViewImageReport:
                startActivity(new Intent(context, ReportedImagesActivity.class));
                break;

            case R.id.buttonVideoReport:
                String[] permission1 = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestAppPermissions(permission1, R.string.permission, 55);
                break;

            case R.id.buttonViewReportedVideos:
                startActivity(new Intent(context, ReportedVideosActivity.class));
                break;

            case R.id.buttonReportOffence:
                startActivity(new Intent(context, ReportOffenceActivity.class));
                break;

            case R.id.buttonViewReportedOffence:

                startActivity(new Intent(context, ViewReportedOffenceActivity.class));

                break;

            case R.id.buttonViewSendSOS:

                String[] permissionNEW = {Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestAppPermissions(permissionNEW, R.string.permission, MyConstant.SMS_REQUEST);

                break;

            case R.id.buttonViewNotifications:

                startActivity(new Intent(context, NotificationActivity.class));

                break;
        }
    }

    private void sendSOS()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

      /*  Location location;
        if (mCurrentLocation != null)
        {
            location = mCurrentLocation;
        }
        else
        {

            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);
        }*/

        String name = "\nMyself : " + MySharedPreference.getInstance().getUserName(context);
        String range = "\nRange : " + MySharedPreference.getInstance().getRange(context);
        String block = "\nBlock : " + MySharedPreference.getInstance().getBlock(context);

        String longitude = "";
        String latitude = "";

        if (mCurrentLocation != null)
        {
            longitude = "\nLonditude : " + mCurrentLocation.getLongitude();
            latitude = "\nLatitude : " + mCurrentLocation.getLatitude();

        }

        Date date = new Date();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);
        String time = "\nTime: " + dateFormat.format(date);


        String locationString = latitude.isEmpty() ? "" : "\nMy current location is : " + latitude + longitude;

        SmsManager sm = SmsManager.getDefault();
        String message = "Help!!" + name + range + block + locationString + time;
        String number = "8894888999";

        sm.sendTextMessage(number, null, message, null, null);


        CommonMethods.getInstance().showToast(context, "Message send successfully");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.profile:

                startActivity(new Intent(context, ProfileActivity.class));

                break;

            case R.id.signOut:

                MySharedPreference.getInstance().clearUserID(context);

                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPermissionGranted(int requestCode)
    {
        if (requestCode == 54)
        {
            capture();
        }
        else if (requestCode == 55)
        {
            dispatchTakeVideoIntent();
        }
        else if (requestCode == MyConstant.SMS_REQUEST)
        {
            sendSOS();
        }
    }

    private void dispatchTakeVideoIntent()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takeVideoIntent, MyConstant.REQUEST_VIDEO_CAPTURE);
        }
    }


    void capture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", CompressImageVideo.getInstance().getTempraryImageFile(context));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);

        startActivityForResult(intent, MyConstant.CAMERA_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;

            case MyConstant.CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    String imagePath = CompressImageVideo.getInstance().compressImage(context, CompressImageVideo.getInstance().getTempraryImageUri(context));
                    MyFirebase.getInstance().saveImage(context, imagePath, MyConstant.IMAGE, null);
                }
                break;

            case MyConstant.REQUEST_VIDEO_CAPTURE:

                if (resultCode == Activity.RESULT_OK)
                {
                    Uri videoUri = intent.getData();

                    final String videoPath = CompressImageVideo.getInstance().getRealPathFromURI(context, videoUri);

                    CommonDialogs.getInstance().showProgressDialog(context, "Compressing Video Please Wait....");

                    CompressImageVideo.getInstance().compressVideo(context, videoPath, new Interfaces.CompressionInterface()
                    {
                        @Override
                        public void onCompressionCompleted(String path)
                        {
                            CommonDialogs.getInstance().dismissDialog();

                            CompressImageVideo.getInstance().deletePath(videoPath);

                            MyFirebase.getInstance().saveVideo(context, path);

                            Log.e(TAG, "Compress VideoPath : " + path);
                        }
                    });
                    Log.e(TAG, "VideoPath : " + videoPath);

                }
                break;
        }
    }

}