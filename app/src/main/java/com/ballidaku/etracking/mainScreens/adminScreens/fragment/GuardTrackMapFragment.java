package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class GuardTrackMapFragment extends Fragment implements OnMapReadyCallback
{

    String TAG ="GuardTrackMapFragment";

    Context context;

    View view = null;


    private static GoogleMap mMap;

    Marker marker;

    String beatName;

    ArrayList<ArrayList<BeatLocationModel.DateLocation>> dateLocationArrayList;

/*    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_beat_location);
        context = this;

        setUpViews();
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_guard_track_map, container, false);

            context = getActivity();


            setUpViews();

        }

        return view;
    }


    private void setUpViews()
    {

        beatName = getArguments().getString(MyConstant.BEAT_NAME);
        String locationData = getArguments().getString(MyConstant.LOCATION);
       // dateLocationArrayList = getArguments().getParcelableArrayList(MyConstant.LOCATION);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ArrayList<BeatLocationModel.DateLocation>>>(){}.getType();
        dateLocationArrayList = gson.fromJson(locationData, type);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity)getActivity()).toolbar.setTitle(beatName+" Track Path");
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

                mMap.setMyLocationEnabled(false);
            }
            else
            {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else
        {
            // buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }


  /*      MyFirebase.getInstance().getBeatLocations(new Interfaces.GetBeatsListener()
        {
            @Override
            public void callback(ArrayList<HashMap<String, Object>> result)
            {
                refreshMap(result);
            }
        });*/

        for (ArrayList<BeatLocationModel.DateLocation> dateLocations:dateLocationArrayList)
        {
            drawPath(dateLocations);
        }

        /*drawPath(dateLocationArrayList.get(0));
        ArrayList<BeatLocationModel.DateLocation> a=dateLocationArrayList.get(0);

        for (int i = 0; i < a.size(); i++)
        {
            Log.e(TAG,i+"   "+a.get(i).getLocation());
        }*/

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            }
            else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        mMap.setMyLocationEnabled(false);
                    }

                }
                else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    int[] imagesArray = {R.mipmap.marker_a, R.mipmap.marker_b, R.mipmap.marker_c, R.mipmap.marker_d, R.mipmap.marker_e, R.mipmap.marker_f, R.mipmap.marker_g,
//            R.mipmap.marker_h, R.mipmap.marker_i, R.mipmap.marker_j, R.mipmap.marker_k, R.mipmap.marker_l, R.mipmap.marker_m, R.mipmap.marker_n};

  /*  public void refreshMap(ArrayList<HashMap<String, Object>> result)
    {

        for (int i = 0; i < result.size(); i++)
        {
            String location = (String) result.get(i).get(MyConstant.LAST_LOCATION);
            String latLong[] = location.split(",");

            if (i == 0)
            {
                setZoom(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[2])), 8);
            }

            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(latLong[1]), Double.parseDouble(latLong[2])))
                    .icon(BitmapDescriptorFactory.fromResource(imagesArray[i]))
                    .title(latLong[0]));

            marker.showInfoWindow();
        }


        try
        {
          *//*  if (mMap == null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                }
            }
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);*//*
            //mMap.setMyLocationEnabled(true);
            *//*mMap.setTrafficEnabled(false);
            mMap.setIndoorEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);*//*
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(SomePos));
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
//                      .target(mMap.getCameraPosition().target)
//                      .zoom(17)
//                      .bearing(30)
//                      .tilt(45)
//                      .build()));
//
//            mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
//                      .position(SomePos)
//                      .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
//                      .title("Hello world"));


           *//* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker arg0)
                {

                    final LatLng startPosition = mCurrLocationMarker.getPosition();
                    //final LatLng finalPosition = new LatLng(12.7801569, 77.4148528);
                    final Handler handler = new Handler();
                    final long start = SystemClock.uptimeMillis();
                    final Interpolator interpolator = new AccelerateDecelerateInterpolator();
                    final float durationInMs = 3000;
                    final boolean hideMarker = false;

                    count++;


                    float bearing = getLocation(previousLatLong).bearingTo(getLocation(list.get(count)));

                    //float bearing = prevLoc.bearingTo(newLoc) ;
                    mCurrLocationMarker.setRotation(bearing);


                    previousLatLong = list.get(count);


                    handler.post(new Runnable()
                    {
                        long elapsed;
                        float t;
                        float v;

                        @Override
                        public void run()
                        {
                            // Calculate progress using interpolator
                            elapsed = SystemClock.uptimeMillis() - start;
                            t = elapsed / durationInMs;


                            LatLng currentPosition = new LatLng(
                                    startPosition.latitude * (1 - t) + list.get(count).latitude * t,
                                    startPosition.longitude * (1 - t) + list.get(count).longitude * t);


                            mCurrLocationMarker.setPosition(currentPosition);


                            //rotateMarker(mCurrLocationMarker, (float) bearingBetweenLocations(currentLatLng, list.get(count)));


                            // running(endLatLng);
                            // Repeat till progress is complete.
                            if (t < 1)
                            {
                                // Post again 16ms later.
                                handler.postDelayed(this, 16);
                            }
                            else
                            {
                                if (hideMarker)
                                {
                                    mCurrLocationMarker.setVisible(false);
                                }
                                else
                                {
                                    mCurrLocationMarker.setVisible(true);
                                }
                            }
                        }
                    });

                    return true;

                }

            });*//*

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

    public void setZoom(LatLng currentLatLng, int zoom)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(mMap.getCameraPosition().target)
                .zoom(zoom)
                //.bearing(30)
                //.tilt(270)
                .build()));

       /* mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker_a))
                        .anchor(0.5f, 0.5f)
//                  .flat(true)
                //.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context,R.drawable.ic_sedan_car_model)))

                  *//*.title("Hello world")*//*);*/
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public void drawPath(ArrayList<BeatLocationModel.DateLocation> dateLocationArrayList)
    {

       // mMap.clear();

        ArrayList<LatLng> points = new ArrayList<>();
        PolylineOptions lineOptions = new PolylineOptions();

        // Traversing through all the routes
        for (int i = 0; i < dateLocationArrayList.size(); i++)
        {

            String[] latLong = dateLocationArrayList.get(i).getLocation().split(",");
            double lat = Double.parseDouble(latLong[0]);
            double lng = Double.parseDouble(latLong[1]);
            LatLng position = new LatLng(lat, lng);

            points.add(position);


            // Adding all the points in the route to LineOptions


        }

        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.RED);

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null)
        {
            mMap.addPolyline(lineOptions);
        }

        String[] latLong = dateLocationArrayList.get(0).getLocation().split(",");

        Log.e(TAG,"Start Lat "+latLong[0] +" Long "+latLong[1]);

        setZoom(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])), 16);


        marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latLong[0]), Double.parseDouble(latLong[1])))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_track_start))
                //.title(latLong[0]));
        );

        marker.showInfoWindow();

        String[] latLong1 = dateLocationArrayList.get(dateLocationArrayList.size() - 1).getLocation().split(",");

        Log.e(TAG,"Start Lat "+latLong1[0] +" Long "+latLong1[1]);

        marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latLong1[0]), Double.parseDouble(latLong1[1])))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_track_stop))
                //.title(latLong[0]));
        );

        marker.showInfoWindow();
    }


}
