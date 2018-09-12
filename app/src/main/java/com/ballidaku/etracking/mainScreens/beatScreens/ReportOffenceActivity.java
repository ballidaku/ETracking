package com.ballidaku.etracking.mainScreens.beatScreens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.CompressionClass;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;

import java.util.HashMap;

public class ReportOffenceActivity extends AbsRuntimeMarshmallowPermission implements View.OnClickListener
{

    String TAG = ReportOffenceActivity.class.getSimpleName();

    Context context;

    View view;

    ImageView imageViewReportOffence;

    EditText editTextSpeciesName;
    EditText editTextDescription;

    private static final int CAMERA_REQUEST = 13;

    boolean isImageClicked;

    //Bitmap photo;
    String imagePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_offence);

        context = this;

        view = findViewById(R.id.main);

        setUpViews();
    }

    private void setUpViews()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TextView) findViewById(R.id.textViewTitle)).setText("Report Offence");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        imageViewReportOffence = (ImageView) findViewById(R.id.imageViewReportOffence);
        imageViewReportOffence.setOnClickListener(this);

        editTextSpeciesName = (EditText) findViewById(R.id.editTextSpeciesName);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);

        findViewById(R.id.textViewPost).setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageViewReportOffence:

                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestAppPermissions(permission, R.string.permission, 54);

                break;

            case R.id.textViewPost:

                checkValidation();

                break;
        }
    }

    private void checkValidation()
    {
        String speciesName=editTextSpeciesName.getText().toString().trim();
        String description=editTextDescription.getText().toString().trim();

        if(!isImageClicked)
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please click image.");
        }
        else if(speciesName.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter species name.");
        }
        else if(description.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter description.");
        }
        else
        {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put(MyConstant.SPECIESNAME, speciesName);
            hashMap.put(MyConstant.DESCRIPTION, description);


            MyFirebase.getInstance().saveImage(context, imagePath, MyConstant.OFFENCE,hashMap);
        }
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
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, CommonMethods.getInstance().getTempraryImageFile());

        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", CommonMethods.getInstance().getTempraryImageFile2());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);

        startActivityForResult(intent, CAMERA_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    /*try
                    {*/
                        isImageClicked=true;


                        //photo = CommonMethods.getInstance().decodeUri(context, CommonMethods.getInstance().getTempraryImageFile());

                         imagePath= CompressionClass.getInstance().compressImage(context,CommonMethods.getInstance().getTempraryImageFile());

                        // MyFirebase.getInstance().saveImage(context, photo);

                        //imageViewReportOffence.setImageBitmap(photo);
                        imageViewReportOffence.setImageURI(Uri.parse("file://"+imagePath));
                    /*}
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }*/
                }
                break;
        }
    }
}
