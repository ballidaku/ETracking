package com.ballidaku.etracking.mainScreens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.CompressImageVideo;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class ProfileActivity extends AbsRuntimeMarshmallowPermission implements View.OnClickListener
{

    String TAG = ProfileActivity.class.getSimpleName();

    Context context;

    Toolbar toolbar;

    TextView textViewUserType;
    TextView textViewRange;
    TextView textViewBlock;
    TextView textViewBeat;
    TextView textViewHeadquater;

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPhone;

    LinearLayout linearLayoutBeat;

    ImageView imageViewProfile;

    ScrollView scrollView;

    String imagePath = "";

    boolean isEditable;

    KeyListener nameKeyListener;
    KeyListener phoneKeyListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;

        setUpView();
    }

    private void setUpView()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        scrollView = findViewById(R.id.scrollView);
        linearLayoutBeat = findViewById(R.id.linearLayoutBeat);

        ((TextView) findViewById(R.id.textViewTitle)).setText("Profile");
//        findViewById(R.id.textViewVersion).setVisibility(View.VISIBLE);

        textViewRange = findViewById(R.id.textViewRange);
        textViewBlock = findViewById(R.id.textViewBlock);
        textViewBeat = findViewById(R.id.textViewBeat);
        textViewHeadquater = findViewById(R.id.textViewHeadquater);

        textViewUserType = findViewById(R.id.textViewUserType);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);

        imageViewProfile = findViewById(R.id.imageViewProfile);

        imageViewProfile.setOnClickListener(this);


        setValues();
    }

    private void setValues()
    {

        String userType = MySharedPreference.getInstance().getUserType(context).toUpperCase();

        textViewUserType.setText(userType);
        editTextName.setText(MySharedPreference.getInstance().getUserName(context));
        editTextEmail.setText(MySharedPreference.getInstance().getUserEmail(context));
        editTextPhone.setText(MySharedPreference.getInstance().getUserPhone(context));

        String userPhoto = MySharedPreference.getInstance().getUserPhoto(context);
        if (!userPhoto.isEmpty())
        {
            CommonMethods.getInstance().showImageGlide(context, imageViewProfile, MySharedPreference.getInstance().getUserPhoto(context));
        }

        nameKeyListener = editTextName.getKeyListener();
        phoneKeyListener = editTextPhone.getKeyListener();

        editTextName.setKeyListener(null);
        editTextEmail.setKeyListener(null);
        editTextPhone.setKeyListener(null);


        if (userType.equals("BEAT"))
        {
            textViewRange.setText(MySharedPreference.getInstance().getRange(context));
            textViewBlock.setText(MySharedPreference.getInstance().getBlock(context));
            textViewBeat.setText(MySharedPreference.getInstance().getBeat(context));
            textViewHeadquater.setText(MySharedPreference.getInstance().getHeadquater(context));

        }
        else
        {
            linearLayoutBeat.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();

                return true;

            case R.id.edit:

                isEditable = true;
                if (isEditable)
                {
                    makeEditable();
                }

                return true;

            case R.id.save:

                checkData();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkData()
    {
        final String name = editTextName.getText().toString();
        final String phoneNumber = editTextPhone.getText().toString();

        if (name.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(scrollView, context, "Please enter name.");
        }
        else if (phoneNumber.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(scrollView, context, "Please enter phone number.");
        }
        else if (!CommonMethods.getInstance().isValidMobile(phoneNumber))
        {
            CommonMethods.getInstance().show_snackbar(scrollView, context, "Please enter phone number of 10 digits.");
        }

        if (!imagePath.isEmpty())
        {
            MyFirebase.getInstance().saveImage(context, imagePath, MyConstant.USER_IMAGE, new Interfaces.onImageUpload()
            {
                @Override
                public void imagePathAfterUpload(String path)
                {
                    updateData(name, phoneNumber, path);
                }
            });
        }
        else
        {
            CommonDialogs.getInstance().progressDialog(context);
            updateData(name, phoneNumber, "");
        }
    }

    void updateData(final String name, final String phoneNumber, final String imagePath)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!imagePath.isEmpty())
        {
            hashMap.put(MyConstant.USER_PHOTO, imagePath);
        }
        hashMap.put(MyConstant.USER_NAME, name);
        hashMap.put(MyConstant.USER_PHONE, phoneNumber);


        MyFirebase.getInstance().updateUser(context, hashMap, new OnCompleteListener()
        {
            @Override
            public void onComplete()
            {
                MySharedPreference.getInstance().saveUserData(context, MyConstant.USER_NAME, name);
                MySharedPreference.getInstance().saveUserData(context, MyConstant.USER_PHONE, phoneNumber);

                if (!imagePath.isEmpty())
                {
                    MySharedPreference.getInstance().saveUserData(context, MyConstant.USER_PHOTO, imagePath);
                }

                makeNonEditable();

            }
        });

    }

    public interface OnCompleteListener
    {
        void onComplete();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (isEditable)
        {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_save, menu);
        }
        else
        {
            menu.clear();
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    void makeEditable()
    {
        editTextName.setKeyListener(nameKeyListener);
        editTextPhone.setKeyListener(phoneKeyListener);

        invalidateOptionsMenu();
    }

    void makeNonEditable()
    {
        editTextName.setKeyListener(null);
        editTextPhone.setKeyListener(null);

        isEditable = false;

        invalidateOptionsMenu();
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageViewProfile:

                if (isEditable)
                {
                    String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestAppPermissions(permission, R.string.permission, 54);
                }


                break;
        }
    }


    @Override
    public void onPermissionGranted(int requestCode)
    {
        if (requestCode == 54)
        {
            CommonDialogs.getInstance().selectImageDialog(context, null);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (resultCode != -1)
            return;

        switch (requestCode)
        {
            case MyConstant.CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    String imagePathTemp = CompressImageVideo.getInstance().getTempraryImageUri(context).toString();

                    CropImage.activity(Uri.parse("file://" + imagePathTemp)).setAspectRatio(1, 1).start(this);
                }
                break;

            case MyConstant.PICK_IMAGE_GALLERY:

                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage).setAspectRatio(1, 1).start(this);

                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();
                    imagePath = CompressImageVideo.getInstance().getRealPathFromURI(context, resultUri);


                    CommonMethods.getInstance().showImageGlide(context, imageViewProfile, imagePath);
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    CommonMethods.getInstance().showToast(context, result.getError().toString());
                }

                break;

        }
    }
}
