package com.ballidaku.etracking.mainScreens;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;

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

    private static final int CAMERA_REQUEST = 13;
    private static final int PICK_IMAGE_GALLERY = 14;

    //Bitmap photo;
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

        CommonMethods.getInstance().showImageGlide(context,imageViewProfile,MySharedPreference.getInstance().getUserPhoto(context));


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

                if(isEditable)
                {
                    makeEditable();
                }


                return true;

            case R.id.save:


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {


        if (isEditable)
        {
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_save, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    void makeEditable()
    {
        editTextName.setKeyListener(nameKeyListener);
        editTextPhone.setKeyListener(phoneKeyListener);

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
        Log.e(TAG, "onPermissionGranted " + requestCode);
        if (requestCode == 54)
        {
            selectImage();
        }
    }

    private void selectImage()
    {
        try
        {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED)
            {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (options[item].equals("Take Photo"))
                        {
                            dialog.dismiss();
                            capture();
                        }
                        else if (options[item].equals("Choose From Gallery"))
                        {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        }
                        else if (options[item].equals("Cancel"))
                        {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
            else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

//                    imagePath = CompressionClass.getInstance().compressImage(context, CommonMethods.getInstance().getTempraryImageFile());
                    imagePath = CommonMethods.getInstance().getTempraryImageFile().toString();

                    // start cropping activity for pre-acquired image saved on the device
                    CropImage.activity(Uri.parse("file://" + imagePath))
                            .setAspectRatio(1, 1)
                            .start(this);
                }
                break;


            case PICK_IMAGE_GALLERY:

                Uri selectedImage = data.getData();

                imagePath = getRealPathFromURI(selectedImage);


                CropImage.activity(Uri.parse("file://" + imagePath))
                        .setAspectRatio(1, 1)
                        .start(this);

                break;


            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    Uri resultUri = result.getUri();

                    imageViewProfile.setImageURI(resultUri);
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = result.getError();
                }

                break;

        }
    }


    public String getRealPathFromURI(Uri contentUri)
    {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
