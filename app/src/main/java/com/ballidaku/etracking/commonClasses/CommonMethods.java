package com.ballidaku.etracking.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * Created by sharanpalsingh on 05/03/17.
 */
public class CommonMethods
{

    String TAG = "CommonMethods";
    static CommonMethods instance = new CommonMethods();

    public static CommonMethods getInstance()
    {
        return instance;
    }


    public Toast toast;
    public Snackbar snackbar;

    public void show_Toast(Context context, String text)
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
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        snackbar.show();

    }

    public boolean isValidEmail(CharSequence target)
    {
        if (target == null)
        {
            return false;
        }
        else
        {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public boolean isValidMobile(String phone)
    {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone))
        {
//            if(phone.length() < 6 || phone.length() > 13) {
            if (phone.length() != 10)
            {
                check = false;
            }
            else
            {
                check = true;
            }
        }
        else
        {
            check = false;
        }
        return check;
    }


    public String getCurrentDate()
    {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public String getCurrentDateTimeForName()
    {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public String getCurrentDateTime()
    {
        return new SimpleDateFormat("dd MM yyyy HH:mm:ss aaa").format(new Date());
    }

    public void showImageGlide(Context context, ImageView imageView, String path)
    {
        Glide.with(context)
                .load(path)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    public void showImageGlide2(Context context, final ZoomableImageView zoomableImageView, String path)
    {

       // CommonDialogs.getInstance().progressDialog(context);
        Glide.with(context)
                .load(path)
                .asBitmap()
                //.placeholder(R.drawable.default_placeholder)
                .override(1600, 1600)
                .into(new BitmapImageViewTarget(zoomableImageView) {
                    @Override
                    public void onResourceReady(Bitmap  bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                       // CommonDialogs.getInstance().dialog.dismiss();
                        zoomableImageView.setImageBitmap(bitmap);
                    }
                });
    }

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

        Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

        return bitmap;
    }


    public Uri getTempraryImageFile()
    {
        File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "IMG_Temp.jpg");

        Log.e(TAG,"Path "+outputFile.toString());
        return Uri.fromFile(outputFile);
    }

    public void deleteTempraryImage()
    {
        File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "IMG_Temp.jpg");

        deleteRecursive(outputFile);

    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
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



    public String distanceBetweenLatLong(String latLong1,String latLong2 )
    {
        String[] latLong11=latLong1.split(",");
        double lat1=Double.parseDouble(latLong11[0]);
        double lon1=Double.parseDouble(latLong11[1]);

        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);


        String[] latLong22=latLong2.split(",");
        double lat2=Double.parseDouble(latLong22[0]);
        double lon2=Double.parseDouble(latLong22[1]);


        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2)/1000;

        return String.valueOf(new DecimalFormat("##.##").format(distanceInMeters))+" Km";
    }

    public String getTimeTaken(String startTime, String endTime)
    {

        Log.e(TAG,"startTime "+startTime+" endTime "+endTime);
        String timeTaken="";
        try
        {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss aa", Locale.US);
            Date date1 = inputDateFormat.parse(startTime);
            Date date2 = inputDateFormat.parse(endTime);

            long difference = date2.getTime() - date1.getTime();

            int timeInSeconds = (int)difference / 1000;

            int hours = timeInSeconds / 3600;

            timeInSeconds = timeInSeconds - (hours * 3600);

            int min =timeInSeconds / 60;

            timeTaken= hours+"h : "+min+"m";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return timeTaken;
    }




   public  class OnSpinnerItemSelected implements AdapterView.OnItemSelectedListener
    {
        Spinner spinner;
        public OnSpinnerItemSelected(Spinner spinner)
        {
            this.spinner=spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            view.setPadding(0, 0, 0,0);
            TextView textViewSpinner=(TextView)view.findViewById(R.id.textViewSpinner);
            textViewSpinner.setGravity(Gravity.RIGHT);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    }


   /* public String encrypt(Context context, String message)
    {
       return encrypt(message,MySharedPreference.getInstance().getPassword(context));
    }
    public String encrypt(String message,String password)
    {
        String encryptedMsg = "";
        try
        {
            encryptedMsg = AESCrypt.encrypt(password, message);
        } catch (GeneralSecurityException e)
        {
            //handle error
        }

        return encryptedMsg;
    }*/


  /*  public String decrypt(Context context, String message)
    {
        return decrypt(message,MySharedPreference.getInstance().getPassword(context));
    }

    public String decrypt(String encryptedMsg,String password)
    {
        String messageAfterDecrypt = "";

        try
        {
            messageAfterDecrypt = AESCrypt.decrypt(password, encryptedMsg);
        } catch (GeneralSecurityException e)
        {
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }

        return messageAfterDecrypt;
    }*/


}
