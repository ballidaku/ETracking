package com.ballidaku.etracking.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ballidaku.etracking.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Created by sharanpalsingh on 05/03/17.
 */
public class CommonMethods
{

    private String TAG = "CommonMethods";
    private static CommonMethods instance = new CommonMethods();

    public static CommonMethods getInstance()
    {
        return instance;
    }


    private Toast toast;
    private Snackbar snackbar;

    public void showToast(Context context, String text)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);

        toast.show();
    }


    /*TO SHOW SNACKBAR*/

    public void show_snackbar(View view, Context context, String message)
    {

        if (snackbar != null)
        {
            snackbar.dismiss();
        }

        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        snackbar.show();

    }

    public boolean isValidEmail(CharSequence target)
    {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public boolean isValidMobile(String phone)
    {
        return  !Pattern.matches("[a-zA-Z]+", phone) && phone.length() == 10;
    }


    public void showImageGlide(Context context, ImageView imageView, String path)
    {
        Glide.with(context)
             .load(path)
             .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.ic_user_placeholder).error(R.mipmap.ic_user_placeholder))
             .into(imageView);
    }

    public void showImageGlide2(Context context, ImageView imageView, String path)
    {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.ic_placeholder_loading).error(R.drawable.ic_placeholder_video_not_available))
                .into(imageView);
    }

    public void shareVideoLink(Context context,String link)
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT, link);
            context.startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    /*public void showImageGlide2(Context context, final ZoomableImageView zoomableImageView, String path)
    {

        // CommonDialogs.getInstance().progressDialog(context);
        Glide.with(context)
             .load(path)
             .asBitmap()
             //.placeholder(R.drawable.default_placeholder)
             .override(1600, 1600)
             .into(new BitmapImageViewTarget(zoomableImageView)
             {
                 @Override
                 public void onResourceReady(Bitmap bitmap, GlideAnimation anim)
                 {
                     super.onResourceReady(bitmap, anim);
                     // CommonDialogs.getInstance().dialog.dismiss();
                     zoomableImageView.setImageBitmap(bitmap);
                 }
             });
    }*/




    public void switchfragment(Fragment fromWhere, Fragment toWhere)
    {
        FragmentTransaction fragmentTransaction = fromWhere.getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, toWhere);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    public void switchfragment(Context context, Fragment toWhere)
    {

        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, toWhere);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void hideKeypad(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException
    {
        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 250;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 1;

        while (true)
        {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
            {
                break;
            }
            width_tmp /= 2;

            height_tmp /= 2;

            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();

        o2.inSampleSize = scale;

       return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

    }




    /**
     * Converting dp to pixel
     */
    public int dpToPx(Context context, int dp)
    {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public String setSpaceFormat(String value)
    {
        DecimalFormat df;

        if (value.length() == 19)
        {
            df = new DecimalFormat("####,####,####,####,###");
        }
        else
        {
            df = new DecimalFormat("####,####,####,####,####,####");
        }

        return df.format(Long.parseLong(value)).replaceAll(",", " ");

    }


    public float distanceBetweenLatLong(String latLong1, String latLong2)
    {
        String[] latLong11 = latLong1.split(",");
        double lat1 = Double.parseDouble(latLong11[0]);
        double lon1 = Double.parseDouble(latLong11[1]);

        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);


        String[] latLong22 = latLong2.split(",");
        double lat2 = Double.parseDouble(latLong22[0]);
        double lon2 = Double.parseDouble(latLong22[1]);


        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2) / 1000;
        Log.e(TAG,"distanceInMeters "+distanceInMeters);

        //return String.valueOf(new DecimalFormat("##.##").format(distanceInMeters)) + " Km";
        return distanceInMeters;
    }

    public String getDistanceFormat(float distanceInMeters)
    {
        return String.valueOf(new DecimalFormat("##.##").format(distanceInMeters)) + " Km";
    }

    public int getTimeTaken(String startTime, String endTime)
    {

        Log.e(TAG, "startTime " + startTime + " endTime " + endTime);
        int timeInSeconds = 0;
        try
        {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss aa", Locale.US);
            Date date1 = inputDateFormat.parse(startTime);
            Date date2 = inputDateFormat.parse(endTime);

            long difference = date2.getTime() - date1.getTime();

            timeInSeconds = (int) difference / 1000;


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return timeInSeconds;
    }

    public String getTimeFormat(int timeInSeconds)
    {

        int hours = timeInSeconds / 3600;

        timeInSeconds = timeInSeconds - (hours * 3600);

        int min = timeInSeconds / 60;

        return hours + "h : " + min + "m";
    }

    public Date stringToDate(String dateString)
    {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try
        {
            date = format.parse(dateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }

    public String getCurrentDate()
    {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date());
    }

    public String getCurrentDateTimeForName()
    {
        return new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(new Date());
    }

    public String getCurrenTime()
    {
        return new SimpleDateFormat("hh:mm:ss", Locale.US).format(new Date());
    }

    public String convertTimeStampToDateTime(long timeStamp)
    {
        String dateTime="";

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.US);
        dateTime = sfd.format(new Date(timeStamp));

        return dateTime;
    }

    public String convertTimeStampToDateTime2(long timeStamp)
    {
        String dateTime="";

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yy hh:mm aa", Locale.US);
        dateTime = sfd.format(new Date(timeStamp));

        return dateTime;
    }

    public String convertDateToDateFormat(String date)
    {
        String dateLocal = null;

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        try
        {
            dateLocal = outputFormat.format(inputFormat.parse(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateLocal;
    }

    public String convertTimeToTimeFormat(String time)
    {
        String timeLocal = null;

        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        try
        {
            timeLocal = outputFormat.format(inputFormat.parse(time));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return timeLocal;
    }

    public String getFirstLastDateOfMonth(Calendar calendar, boolean b)
    {
        Calendar calendarLocal = Calendar.getInstance();
        calendarLocal.setTime(calendar.getTime());

        calendarLocal.add(Calendar.MONTH, b == true ? 1 : 0);
        calendarLocal.set(Calendar.DAY_OF_MONTH, 1);
        calendarLocal.add(Calendar.DATE, b == true ? -1 : 0);

        Date lastDayOfMonth = calendarLocal.getTime();

        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        return sdf.format(lastDayOfMonth);
    }

    public ArrayList<String> getMonthDates(Calendar calendar)
    {
        String startDate = getFirstLastDateOfMonth(calendar, false);
        ;
        String lasttDate = getFirstLastDateOfMonth(calendar, true);

        Log.e(TAG, "startDate " + startDate + " lasttDate " + lasttDate);
        //Log.e(TAG,"Month Dates "+getDates(startDate,lasttDate));

        return getDates(startDate, lasttDate);
    }


    private static ArrayList<String> getDates(String dateString1, String dateString2)
    {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd", Locale.US);

        Date date1 = null;
        Date date2 = null;

        try
        {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2))
        {
            dates.add(df1.format(cal1.getTime()));
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public String getPreviousMonthYear(Calendar calendar)
    {
        calendar.add(Calendar.MONTH, -1);

        Date lastDayOfMonth = calendar.getTime();

        DateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.US);

        return sdf.format(lastDayOfMonth);

    }

    public String getNextMonthYear(Calendar calendar)
    {
        calendar.add(Calendar.MONTH, +1);

        Date lastDayOfMonth = calendar.getTime();

        DateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.US);

        return sdf.format(lastDayOfMonth);

    }

    public String getCurrentMonthYear()
    {
        return new SimpleDateFormat("MMM yyyy", Locale.US).format(new Date());
    }

    public boolean checkDateLiesInPresentMonth(Calendar calendar, String givenDate)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        //set the given date in one of the instance and current date in another
        cal1.setTime(stringToDate(givenDate));
        cal2.setTime(calendar.getTime());

        //now compare the dates using functions
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);

    }



    public class OnSpinnerItemSelected implements AdapterView.OnItemSelectedListener
    {
        Spinner spinner;

        public OnSpinnerItemSelected(Spinner spinner)
        {
            this.spinner = spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            view.setPadding(0, 0, 0, 0);
            TextView textViewSpinner = (TextView) view.findViewById(R.id.textViewSpinner);
            textViewSpinner.setGravity(Gravity.RIGHT);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }

    public void call(Context context, String number)
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public SpacesItemDecoration(int spanCount, int spacing, boolean includeEdge)
        {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge)
            {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount)
                { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            }
            else
            {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount)
                {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    public String getAddress(Context context, String latLong)
    {

        String add = "";
        String[] latLongArray = latLong.split(",");
        double lat = Double.parseDouble(latLongArray[0]);
        double lng = Double.parseDouble(latLongArray[1]);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);


        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return add;
    }


    //**********************************************************************************************
    //**********************************************************************************************

    public void capture(Context context, Fragment fragment)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", CompressImageVideo.getInstance().getTempraryImageFile(context));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);

        /*if (fragment!=null && fragment instanceof AddCustomerFragment)
        {
            fragment.startActivityForResult(intent, MyConstant.CAMERA_REQUEST);
        }
        else
        {*/
            ((Activity) context).startActivityForResult(intent, MyConstant.CAMERA_REQUEST);
//        }
    }











}
