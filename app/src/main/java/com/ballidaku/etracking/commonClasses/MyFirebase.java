package com.ballidaku.etracking.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.dataModels.BeatDataModel;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.dataModels.ImageDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.ballidaku.etracking.mainScreens.beatScreens.BeatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by sharanpalsingh on 05/03/17.
 */
public class MyFirebase
{

    String TAG = MyFirebase.class.getSimpleName();

    public static MyFirebase instance = new MyFirebase();

    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    public static MyFirebase getInstance()
    {
        return instance;
    }


    void showDialog(Context context)
    {
        CommonDialogs.getInstance().progressDialog(context);
    }

    void dismissDialog()
    {
        if (CommonDialogs.getInstance().dialog.isShowing())
        {
            CommonDialogs.getInstance().dialog.dismiss();
        }
    }


    public void createUser(final Context context, String userType, HashMap<String, Object> map)
    {
        String key = root.child(MyConstant.USERS).child(userType).push().getKey();
        create(context, userType, key, map);

    }


    public void checkIsAdminExists(final Interfaces.MyListener myListener)
    {
        root.child(MyConstant.USERS).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.e(TAG, dataSnapshot.getValue().toString());
                if (dataSnapshot.hasChild(MyConstant.ADMIN))
                {
                    myListener.callback(true);
                }
                else
                {
                    myListener.callback(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    public void create(final Context context, final String userType, final String userID, final HashMap<String, Object> result)
    {
        root.child(MyConstant.USERS).child(userType).child(userID).updateChildren(result).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    openActivity(context, userID, userType, result);
                    CommonMethods.getInstance().show_Toast(context, "User created successfully");
                }
            }
        });
    }

    public void logInUser(final Context context, final String email)
    {

        root.child(MyConstant.USERS).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (final DataSnapshot child : dataSnapshot.getChildren())
                {
                    Log.e(TAG, child.getKey());

                    root.child(MyConstant.USERS).child(child.getKey()).orderByChild(MyConstant.USER_EMAIL).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {

                            for (DataSnapshot child2 : dataSnapshot.getChildren())
                            {
                                Log.e(TAG, "User Key " + child2.getKey());
                                Log.e(TAG, "User type " + child.getKey());

                                HashMap<String, Object> hashMap = (HashMap<String, Object>) child2.getValue();

                                Log.e(TAG, "User hashMap " + hashMap);

                                openActivity(context, child2.getKey(), child.getKey(), hashMap);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.e(TAG, "ERROR     " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

    }


    private void openActivity(Context context, String userID, String userType, HashMap<String, Object> result)
    {

        dismissDialog();

        MySharedPreference.getInstance().saveUser(context, userID, userType, result);

        Intent intent = new Intent(context, userType.equals(MyConstant.ADMIN) || userType.equals(MyConstant.SUB_ADMIN) ? MainActivity.class : BeatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        ((Activity) context).finish();
    }


    public void saveUserLocation(Context context, HashMap<String, Object> hashMap)
    {

        String userID = MySharedPreference.getInstance().getUserID(context);
        String userName = MySharedPreference.getInstance().getUserName(context);

        root.child(MyConstant.LOCATION).child(userID).child(CommonMethods.getInstance().getCurrentDate()).push().updateChildren(hashMap);

        root.child(MyConstant.LAST_LOCATION).child(userID).setValue(userName + "," + hashMap.get(MyConstant.LOCATION));
    }


    public void getBeatLocations(final Interfaces.GetBeatsListener getBeatsListener)
    {
        root.child(MyConstant.LAST_LOCATION).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    Log.e(TAG, "BeatDataModel " + child);

                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put(MyConstant.BEAT_ID, child.getKey());
                    hashMap.put(MyConstant.LAST_LOCATION, child.getValue());

                    list.add(hashMap);
                }

                getBeatsListener.callback(list);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //Storing images to firebase
    //**********************************************************************************************
    //**********************************************************************************************

    public void saveImage(final Context context, Bitmap bitmap)
    {

        showDialog(context);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Points to "images"
        StorageReference mountainImagesRef = storageRef.child("images/" + CommonMethods.getInstance().getCurrentDateTimeForName() + ".jpg");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads

                exception.printStackTrace();
                dismissDialog();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Log.e(TAG, "ImagePath " + downloadUrl);

                CommonMethods.getInstance().deleteTempraryImage();

                reportImage(context, downloadUrl.toString());
            }
        });
    }


    public void reportImage(final Context context, String path)
    {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(MyConstant.IMAGE_PATH, path);
        hashMap.put(MyConstant.REPORTED_TIME, CommonMethods.getInstance().getCurrentDateTime());


        final String userID = MySharedPreference.getInstance().getUserID(context);
        root.child(MyConstant.REPORTED_IMAGES).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    CommonMethods.getInstance().show_Toast(context, "Image reported successfully");
                }

                dismissDialog();
            }
        });
    }

    //**********************************************************************************************
    //**********************************************************************************************
    //Get images from firebase
    //**********************************************************************************************
    //**********************************************************************************************


    public void getFirebaseImages(Context context, final Interfaces.ReportedImagesListener reportedImagesListener)
    {

        String userId = MySharedPreference.getInstance().getUserID(context);

        root.child(MyConstant.REPORTED_IMAGES).child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<ImageDataModel> arrayList = new ArrayList<ImageDataModel>();
                for (final DataSnapshot child1 : dataSnapshot.getChildren())
                {
                    //Log.e(TAG, ""+child.getValue());

                    ImageDataModel imageDataModel = new ImageDataModel((String) child1.child(MyConstant.IMAGE_PATH).getValue(), (String) child1.child(MyConstant.REPORTED_TIME).getValue());


                    arrayList.add(imageDataModel);
                    //  Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());


                }

                Collections.reverse(arrayList);
                reportedImagesListener.callback(arrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    public void getReportedImagesByAdmin(final Interfaces.ReportedImagesListener reportedImagesListener)
    {
        root.child(MyConstant.REPORTED_IMAGES).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<ImageDataModel> arrayList = new ArrayList<ImageDataModel>();

                for (final DataSnapshot beatIds : dataSnapshot.getChildren())
                {

                    for (final DataSnapshot keys : beatIds.getChildren())
                    {
                        //Log.e(TAG, ""+child.getValue());

                        ImageDataModel imageDataModel = new ImageDataModel((String) keys.child(MyConstant.IMAGE_PATH).getValue(), (String) keys.child(MyConstant.REPORTED_TIME).getValue());


                        arrayList.add(imageDataModel);
                        // Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());

                    }

                    Collections.reverse(arrayList);
                    reportedImagesListener.callback(arrayList);


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    public void getAllBeats(final Interfaces.GetAllBeatListener getAllBeatListener)
    {
        root.child(MyConstant.USERS).child(MyConstant.BEAT).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<BeatDataModel> arrayList = new ArrayList<BeatDataModel>();

                for (final DataSnapshot child : dataSnapshot.getChildren())
                {


                   Log.e(TAG, ""+child.child(MyConstant.USER_NAME).getValue());

                    BeatDataModel beatDataModel = new BeatDataModel();
                    beatDataModel.setBeatId(child.getKey());
                    beatDataModel.setBeatEmail((String)child.child(MyConstant.USER_EMAIL).getValue());
                    beatDataModel.setBeatName((String)child.child(MyConstant.USER_NAME).getValue());
                    beatDataModel.setBeatPhoneNumber((String)child.child(MyConstant.USER_PHONE).getValue());

                    arrayList.add(beatDataModel);
                }
                getAllBeatListener.callback(arrayList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    public void getBeatsByCategory(String range,String block,String beat,final Interfaces.GetAllBeatListener getAllBeatListener)
    {
        root.child(MyConstant.USERS).child(MyConstant.BEAT)/*.orderByChild(MyConstant.RANGE).equalTo(range)*/
                //.orderByChild(MyConstant.BLOCK).equalTo(block)
                .orderByChild(MyConstant.BEAT).equalTo(beat).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<BeatDataModel> arrayList = new ArrayList<BeatDataModel>();

                for (final DataSnapshot child : dataSnapshot.getChildren())
                {


                    Log.e(TAG, ""+child.child(MyConstant.USER_NAME).getValue());

                    BeatDataModel beatDataModel = new BeatDataModel();
                    beatDataModel.setBeatId(child.getKey());
                    beatDataModel.setBeatEmail((String)child.child(MyConstant.USER_EMAIL).getValue());
                    beatDataModel.setBeatName((String)child.child(MyConstant.USER_NAME).getValue());
                    beatDataModel.setBeatPhoneNumber((String)child.child(MyConstant.USER_PHONE).getValue());

                    arrayList.add(beatDataModel);
                }
                getAllBeatListener.callback(arrayList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    public void getBeatDateLocationData(String beatId,final Interfaces.GetBeatDateLocationListener getBeatDateLocationListener)
    {
        root.child(MyConstant.LOCATION).child(beatId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                ArrayList<BeatLocationModel> arrayList = new ArrayList<BeatLocationModel>();
                for (final DataSnapshot child : dataSnapshot.getChildren())
                {
                    BeatLocationModel beatLocationModel = new BeatLocationModel();
                    beatLocationModel.setDate(child.getKey());

                    ArrayList<BeatLocationModel.DateLocation> dateLocationArrayList = new ArrayList<BeatLocationModel.DateLocation>();
                    for (final DataSnapshot date : child.getChildren())
                    {

                        BeatLocationModel.DateLocation dateLocation=new BeatLocationModel.DateLocation();
                        dateLocation.setLocation((String)date.child(MyConstant.LOCATION).getValue());
                        dateLocation.setTime((String)date.child(MyConstant.TIME).getValue());

                        dateLocationArrayList.add(dateLocation);
                    }

                    beatLocationModel.setDateLocations(dateLocationArrayList);

                    arrayList.add(beatLocationModel);
                }


                getBeatDateLocationListener.callback(arrayList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    /*public void createUserIfNotExists(final Context context, final HashMap<String, String> map)
    {

        final HashMap<String, Object> result = new HashMap<>();
        result.put(MyConstant.USER_NAME, map.get(MyConstant.USER_NAME));
        //  result.put(MyConstant.USER_TYPE, map.get(MyConstant.USER_TYPE));


        if (map.get(MyConstant.USER_TYPE).equals(MyConstant.ADMIN))
        {

            root.child(MyConstant.USERS).child(MyConstant.ADMIN).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

                    // Log.e("Key", "Kida");

                    if (!dataSnapshot.hasChildren())
                    {
                        userID = root.child(MyConstant.USERS).child(MyConstant.ADMIN).push().getKey();
                    }
                    else
                    {
                        for (DataSnapshot child : dataSnapshot.getChildren())
                        {
                            userID = child.getKey();
                        }
                    }

                    Log.e("Key", "Inside " + userID);

                    //create(context, map.get(MyConstant.USER_TYPE), result, userID);
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    Log.e("Key", "OUTSIDE");
                }
            });

        }
        else
        {
            userID = root.child(MyConstant.USERS).child(map.get(MyConstant.USER_TYPE)).push().getKey();
        }


    }*/





   /* public void checkUserIfAlreadyExists(final Context context, final HashMap<String, String> map)
    {

        root.child(MyConstant.USERS).orderByChild(MyConstant.USER_NAME).equalTo(map.get(MyConstant.USER_NAME)).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isChildThere = false;
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    Log.e("User val", child.getValue().toString());
                    isChildThere = true;
                }

                if (!isChildThere)
                {
                    createUserIfNotExists(context, map);
                }
                else
                {
                    CommonMethods.show_Toast(context, "User Name already exists");
                }

                Log.e(TAG, "CHILD there     " + isChildThere);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "ERROR     " + databaseError.getMessage());
            }
        });
    }*/

   /* public void checkUserCredentials(final Context context,final HashMap<String,String> map)
    {
        root.child(MyConstant.USERS).orderByChild(MyConstant.USER_NAME).equalTo(map.get(MyConstant.USER_NAME)).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                boolean isChildThere = false;
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    isChildThere = true;
//                    Log.e("User key", child.getKey());
//                    Log.d("User ref", child.getRef().toString());
                    Log.e("User val", child.getValue().toString());

                   UserDataModel userDataModel= child.getValue(UserDataModel.class);

                    //Log.e("User ID", child.getKey());
                    //Log.e("User Name", userDataModel.getUser_name());
                    Log.e("User Password", myUtil.decrypt(userDataModel.getPassword(),map.get(MyConstant.PASSWORD)));


                    if(myUtil.decrypt(userDataModel.getPassword(),map.get(MyConstant.PASSWORD)).equals(map.get(MyConstant.PASSWORD)))
                    {
                        MySharedPreference.getInstance().saveUser(context, child.getKey(), userDataModel.getUser_name(),map.get(MyConstant.PASSWORD));

                        CommonMethods.show_Toast(context, "Login successfull");
//
                        context.startActivity(new Intent(context, MainActivity.class));

                        ((Activity) context).finish();
                    }
                    else
                    {
                        CommonMethods.show_Toast(context, "Password did not match");
                    }


                }

                if (!isChildThere)
                {
                    CommonMethods.show_Toast(context, "User Name does not exists");
                }
                Log.e(TAG, "CHILD there     " + isChildThere);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "ERROR     " + databaseError.getMessage());
            }
        });
    }*/




  /*  public Task<Void> deleteBankDetails(Context context, String key)
    {

        String userID = MySharedPreference.getInstance().getUserID(context);

        return root.child(MyConstant.BANK_DETAILS).child(userID).child(key).removeValue();
    }


    // To get User account details
    public Query getMyAccountDetails(Context context)
    {
        return root.child(MyConstant.BANK_DETAILS).child(MySharedPreference.getInstance().getUserID(context));
    }*/


}
