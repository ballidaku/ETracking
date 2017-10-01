package com.ballidaku.etracking.mainScreens.beatScreens;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.mainScreens.ProfileActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;


public class BeatActivity extends AbsRuntimeMarshmallowPermission implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener
{

    String TAG = BeatActivity.class.getSimpleName();

    Context context;

    /**
     * Update location information every 10 seconds. Actually it may be somewhat more frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 1;

    /**
     * The fastest update interval. It is not updated more frequently than this value.
     */
    //public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS;


    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 10;
    private static final int CAMERA_REQUEST = 13;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
//    private String mLatitudeLabel;
//    private String mLongitudeLabel;


    Button buttonStart;
    Button buttonStop;
    Button buttonImageReport;
    Button buttonViewImageReport;


    //    TextView latitude_text;
//    TextView longitude_text;
    TextView textViewLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beat);

        context = this;

//        mLatitudeLabel = getResources().getString(R.string.latitude_label);
//        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();

        setUpViews();
    }


    private void setUpViews()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TextView) findViewById(R.id.textViewTitle)).setText("Tracking");


        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonImageReport = (Button) findViewById(R.id.buttonImageReport);
        buttonViewImageReport = (Button) findViewById(R.id.buttonViewImageReport);


//        latitude_text = (TextView) findViewById(R.id.latitude_text);
//        longitude_text = (TextView) findViewById(R.id.longitude_text);
        textViewLastUpdateTime = (TextView) findViewById(R.id.textViewLastUpdateTime);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonImageReport.setOnClickListener(this);
        buttonViewImageReport.setOnClickListener(this);


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
        }
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
                finish();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPermissionGranted(int requestCode)
    {

        Log.e(TAG, "onPermissionGranted " + requestCode);
        if (requestCode == 54)
        {

            capture();

        }
    }


    void capture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CommonMethods.getInstance().getTempraryImageFile());
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    private void updateValuesFromBundle(Bundle savedInstanceState)
    {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null)
        {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY))
            {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY))
            {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY))
            {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startUpdatesButtonHandler()
    {
        clearUI();
        if (!isPlayServicesAvailable(this)) return;
        if (!mRequestingLocationUpdates)
        {
            mRequestingLocationUpdates = true;
        }
        else
        {
            return;
        }

        if (Build.VERSION.SDK_INT < 23)
        {
            setButtonsEnabledState();
            startLocationUpdates();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            setButtonsEnabledState();
            startLocationUpdates();
        }
        else
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                showRationaleDialog();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    public void stopUpdatesButtonHandler()
    {
        if (mRequestingLocationUpdates)
        {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates()
    {
        Log.i(TAG, "startLocationUpdates");

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        // Check whether setting of position information is valid before acquiring current position
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
            {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // Since the setting is valid, the current position is acquired
                        if (ContextCompat.checkSelfPermission(BeatActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, BeatActivity.this);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Since the setting is not valid, a dialog is displayed
                        try
                        {
                            status.startResolutionForResult(BeatActivity.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
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

    private void clearUI()
    {
//        latitude_text.setText("");
//        longitude_text.setText("");
        textViewLastUpdateTime.setText("");
    }

    private void updateUI()
    {
        if (mCurrentLocation == null) return;

//        latitude_text.setText(String.format("%s: %f", mLatitudeLabel,
//                mCurrentLocation.getLatitude()));
//        longitude_text.setText(String.format("%s: %f", mLongitudeLabel,
//                mCurrentLocation.getLongitude()));
        textViewLastUpdateTime.setText(mLastUpdateTime);
    }

    protected void stopLocationUpdates()
    {
        Log.i(TAG, "stopLocationUpdates");
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, BeatActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setButtonsEnabledState();
                    startLocationUpdates();
                }
                else
                {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                    {
                        mRequestingLocationUpdates = false;
                        Toast.makeText(BeatActivity.this, "To enable the function of this application please enable location permission of the application from the setting screen of the terminal.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        showRationaleDialog();
                    }
                }
                break;
            }
        }
    }

    private void showRationaleDialog()
    {
        new AlertDialog.Builder(this)
                .setPositiveButton("To give permission", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ActivityCompat.requestPermissions(BeatActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                })
                .setNegativeButton("しない", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(BeatActivity.this, "Position information permission was not allowed.", Toast.LENGTH_SHORT).show();
                        mRequestingLocationUpdates = false;
                    }
                })
                .setCancelable(false)
                .setMessage("This application needs to allow use of location information.")
                .show();
    }

    public static boolean isPlayServicesAvailable(Context context)
    {
        // Google Play Service APK  Check if it is valid
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, resultCode, 2).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;

            case CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    try
                    {
                        Bitmap photo = CommonMethods.getInstance().decodeUri(context, CommonMethods.getInstance().getTempraryImageFile());

                        MyFirebase.getInstance().saveImage(context, photo);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        isPlayServicesAvailable(this);

        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (mGoogleApiClient.isConnected())
//        {
//            stopLocationUpdates();
//        }
    }

    @Override
    protected void onStop()
    {
        Log.e(TAG, "onStop");
//        stopLocationUpdates();
//        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {

        if (mGoogleApiClient.isConnected())
        {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();

        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        Log.i(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        if (mCurrentLocation == null)
        {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        if (mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "onLocationChanged   getLatitude" + location.getLatitude() + "  location.getLatitude() " + location.getLongitude());
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, getResources().getString(R.string.location_updated_message), Toast.LENGTH_SHORT).show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MyConstant.LOCATION, location.getLatitude() + "," + location.getLongitude());
        hashMap.put(MyConstant.TIME, mLastUpdateTime);

        MyFirebase.getInstance().saveUserLocation(context, hashMap);
    }


    @Override
    public void onConnectionSuspended(int i)
    {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

   /* public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }*/
}