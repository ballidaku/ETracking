package com.ballidaku.etracking.commonClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.dataModels.GuardDataModel;
import com.ballidaku.etracking.dataModels.ImageDataModel;
import com.ballidaku.etracking.dataModels.NotificationModel;
import com.ballidaku.etracking.dataModels.OffenceDataModel;
import com.ballidaku.etracking.dataModels.VideoDataModel;
import com.ballidaku.etracking.frontScreens.LoginActivity;
import com.ballidaku.etracking.mainScreens.ProfileActivity;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.ballidaku.etracking.mainScreens.beatScreens.BeatActivity;
import com.ballidaku.etracking.mainScreens.beatScreens.ReportOffenceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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
public class MyFirebase<Z, T>
{

    private String TAG = MyFirebase.class.getSimpleName();

    private static MyFirebase instance = new MyFirebase();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    public static MyFirebase getInstance()
    {
        return instance;
    }


    public void createUser(final Context context, String userType, HashMap<String, Object> map)
    {
        String key = root.child(MyConstant.USERS).child(userType).push().getKey();
        create(context, userType, key, map);

    }

    public void updateUser(final Context context, HashMap<String, Object> map, final ProfileActivity.OnCompleteListener onCompleteListener)
    {
        root.child(MyConstant.USERS).child(MySharedPreference.getInstance().getUserType(context)).child(MySharedPreference.getInstance().getUserID(context)).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                CommonDialogs.getInstance().dismissDialog();

                if (task.isSuccessful())
                {
                    onCompleteListener.onComplete();
                    CommonMethods.getInstance().showToast(context, "User updated successfully");
                }
                else
                {
                    CommonMethods.getInstance().showToast(context, "Please try again");
                }
            }
        });


    }

    public void createNotification(final Context context, String text, final Interfaces.OnMessageUpdateListener onMessageUpdateListener)
    {
        CommonDialogs.getInstance().progressDialog(context);
        String userID = MySharedPreference.getInstance().getUserID(context);
        String userEmail = MySharedPreference.getInstance().getUserEmail(context);
        String userType = MySharedPreference.getInstance().getUserType(context);

        getAllUserTokenAndSendNotification(context);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MyConstant.NOTIFICATION_TEXT, text);
        hashMap.put(MyConstant.REPORTED_TIME, ServerValue.TIMESTAMP);
        hashMap.put(MyConstant.USER_EMAIL, userEmail);
        hashMap.put(MyConstant.USER_ID, userID);
        hashMap.put(MyConstant.USER_TYPE, userType);

        String key = root.child(MyConstant.NOTIFICATIONS).push().getKey();

        root.child(MyConstant.NOTIFICATIONS).child(key).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                CommonDialogs.getInstance().dismissDialog();

                if (task.isSuccessful())
                {
                    CommonMethods.getInstance().showToast(context, "Notification successfully posted.");
                    onMessageUpdateListener.onUpdated();

                }
            }
        });
    }

    public void getAllUserTokenAndSendNotification(Context context)
    {
        root.child(MyConstant.USERS).child(MyConstant.BEAT).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                {
                    String key=childDataSnapshot.getKey();

                    if(childDataSnapshot.hasChild(MyConstant.FCM_TOKEN))
                    {
                        String fcmToken=(String)childDataSnapshot.child(MyConstant.FCM_TOKEN).getValue();
                        HashMap map = new HashMap();
                        map.put("title", "Notification");
                        map.put("body", "Hello baby");

                        CommonMethods.getInstance().sendNotification(fcmToken, map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    ArrayList<NotificationModel> notificationModelArrayList;

    public void getAllNotifications(Context context, boolean b, final Interfaces.GetNotificationListener getNotificationListener)
    {
        if(b)
        CommonDialogs.getInstance().progressDialog(context);

        root.child(MyConstant.NOTIFICATIONS).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotMain)
            {
                notificationModelArrayList = new ArrayList<>();

                final int size = (int) dataSnapshotMain.getChildrenCount();
                if (size == 0)
                {
                    CommonDialogs.getInstance().dismissDialog();
                }

                for (final DataSnapshot childNotification : dataSnapshotMain.getChildren())
                {
                    Log.e(TAG, childNotification.getKey());

                    final String email = (String) childNotification.child(MyConstant.USER_EMAIL).getValue();
                    final String userType = (String) childNotification.child(MyConstant.USER_TYPE).getValue();

                    root.child(MyConstant.USERS).child(userType).orderByChild(MyConstant.USER_EMAIL).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2)
                        {
                            final int size2 = (int) dataSnapshot2.getChildrenCount();

                            for (DataSnapshot child2 : dataSnapshot2.getChildren())
                            {
                                Log.e(TAG, "User Key " + child2.getKey());

                                NotificationModel notificationModel = new NotificationModel();
                                notificationModel.setNotification((String) childNotification.child(MyConstant.NOTIFICATION_TEXT).getValue());
                                notificationModel.setNotificationDateTime(CommonMethods.getInstance().convertTimeStampToDateTime2((long)childNotification.child(MyConstant.REPORTED_TIME).getValue()));
                                notificationModel.setSenderId(child2.getKey());
                                notificationModel.setSenderName((String) child2.child(MyConstant.USER_NAME).getValue());

                                notificationModelArrayList.add(notificationModel);

                                if (notificationModelArrayList.size() == size)
                                {
                                    CommonDialogs.getInstance().dismissDialog();
                                    Collections.reverse(notificationModelArrayList);
                                    getNotificationListener.callback(notificationModelArrayList);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError)
                        {
                            Log.e(TAG, "ERROR     " + databaseError.getMessage());
                            CommonDialogs.getInstance().dismissDialog();
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                CommonDialogs.getInstance().dismissDialog();
            }
        });
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
                    //openActivity(context, userID, userType, result);
                    openLogin(context);
                    CommonMethods.getInstance().showToast(context, "User created successfully");
                }
            }
        });
    }

    public void openLogin(Context context)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        ((Activity) context).finish();
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


                                HashMap<String, Object> hashMapNew = new HashMap<>();
                                hashMapNew.put(MyConstant.FCM_TOKEN, FirebaseInstanceId.getInstance().getToken());
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .getRoot()
                                        .child(MyConstant.USERS).child(child.getKey()).child(child2.getKey()).updateChildren(hashMapNew);

                                if (hashMap.get(MyConstant.USER_ALLOWED).equals("true"))
                                {
                                    openActivity(context, child2.getKey(), child.getKey(), hashMap);
                                }
                                else
                                {
                                    CommonDialogs.getInstance().dismissDialog();
                                    CommonMethods.getInstance().showToast(context, "Contact DFO for permission to login");
                                }

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

        CommonDialogs.getInstance().dismissDialog();

        MySharedPreference.getInstance().saveUser(context, userID, userType, result);

        Intent intent = new Intent(context, userType.equals(MyConstant.ADMIN) || userType.equals(MyConstant.SUB_ADMIN) ? MainActivity.class : BeatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        ((Activity) context).finish();
    }

    public String getStartLoactionNode(Context context)
    {
        String userID = MySharedPreference.getInstance().getUserID(context);

        return root.child(MyConstant.LOCATION).child(userID).child(CommonMethods.getInstance().getCurrentDate()).push().getKey();
    }

    public void saveUserLocation(Context context, String startTrackKey, String locationString)
    {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MyConstant.LOCATION, locationString);
        hashMap.put(MyConstant.TIME, ServerValue.TIMESTAMP);


        String userID = MySharedPreference.getInstance().getUserID(context);
        String userName = MySharedPreference.getInstance().getUserName(context);

        root.child(MyConstant.LOCATION).child(userID).child(CommonMethods.getInstance().getCurrentDate()).child(startTrackKey).push().updateChildren(hashMap);

        //root.child(MyConstant.LAST_LOCATION).child(userID).setValue(userName + "," + hashMap.get(MyConstant.LOCATION));
    }


/*    public void getBeatLocations(final Interfaces.GetBeatsListener getBeatsListener)
    {
        root.child(MyConstant.LAST_LOCATION).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                for (DataSnapshot child : dataSnapshot.getChildren())
                {
                    Log.e(TAG, "GuardDataModel " + child);

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
    }*/

    //**********************************************************************************************
    //**********************************************************************************************
    //Storing images to firebase
    //**********************************************************************************************
    //**********************************************************************************************

    public void saveImage(final Context context, final Z imagePath, final String imageOffence, final T data)
    {
        if (!imageOffence.equals(MyConstant.VIDEO_THUMBNAIL))
            CommonDialogs.getInstance().progressDialog(context);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        final StorageReference mountainImagesRef;

        if (imageOffence.equals(MyConstant.USER_IMAGE))
        {
            mountainImagesRef = storageRef.child("profile_images/" + CommonMethods.getInstance().getCurrentDateTimeForName() + ".jpg");
        }
        else if (imageOffence.equals(MyConstant.VIDEO_THUMBNAIL))
        {
            mountainImagesRef = storageRef.child("video_thumbnails/" + CommonMethods.getInstance().getCurrentDateTimeForName() + ".jpg");
        }
        else
        {
            mountainImagesRef = storageRef.child("images/" + CommonMethods.getInstance().getCurrentDateTimeForName() + ".jpg");
        }


        UploadTask uploadTask;
        if (imageOffence.equals(MyConstant.VIDEO_THUMBNAIL))
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ((Bitmap) imagePath).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bitmapByte = baos.toByteArray();

            uploadTask = mountainImagesRef.putBytes(bitmapByte);
        }
        else
        {
            uploadTask = mountainImagesRef.putFile(Uri.parse("file://" + imagePath));
        }


        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                // Handle unsuccessful uploads

                exception.printStackTrace();
                CommonDialogs.getInstance().dismissDialog();


            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                mountainImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        String downloadUrl = task.getResult().toString();

                        Log.e(TAG, "ImagePath " + downloadUrl);

                        CompressImageVideo.getInstance().deleteTempraryImageDirectory(context);

                        if (imageOffence.equals(MyConstant.IMAGE))
                        {
                            reportImage(context, downloadUrl);
                        }
                        else if (imageOffence.equals(MyConstant.OFFENCE))
                        {
                            reportOffence(context, downloadUrl, (HashMap<String, Object>) data);
                        }
                        else if (imageOffence.equals(MyConstant.USER_IMAGE))
                        {
                            ((Interfaces.onImageUpload) data).imagePathAfterUpload(downloadUrl);
                        }
                        else if (imageOffence.equals(MyConstant.VIDEO_THUMBNAIL))
                        {
                            ((Interfaces.onImageUpload) data).imagePathAfterUpload(downloadUrl);
                        }
                    }
                });


            }
        });
    }

    /**
     * API for creating thumbnail from Video
     *
     * @param filePath - video file path
     * @param type     - size MediaStore.Images.Thumbnails.MINI_KIND or MICRO_KIND
     * @return thumbnail bitmap
     */
    public Bitmap createThumbnailFromPath(String filePath, int type)
    {
        return ThumbnailUtils.createVideoThumbnail(filePath, type);
    }

    public void saveVideo(final Context context, String videoPath)
    {
        CommonDialogs.getInstance().showProgressDialog(context, "Video Uploading Please Wait...");

        final Bitmap bitmap = createThumbnailFromPath(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        final String videoName = CommonMethods.getInstance().getCurrentDateTimeForName() + ".mp4";

        final StorageReference mountainImagesRef = storageRef.child("reported_videos/" + videoName);


        UploadTask uploadTask = mountainImagesRef.putFile(Uri.parse("file://" + videoPath));
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                exception.printStackTrace();
                CommonDialogs.getInstance().dismissDialog();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                //Getting url of video
                mountainImagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        final String videoUrl = task.getResult().toString();

                        CompressImageVideo.getInstance().deleteTempraryVideoDirectory(context);

                        saveImage(context, (Z) bitmap, MyConstant.VIDEO_THUMBNAIL, (T) new Interfaces.onImageUpload()
                        {
                            @Override
                            public void imagePathAfterUpload(String videoThumbnailPath)
                            {
                                reportVideo(context, videoUrl, videoThumbnailPath, videoName);
                            }
                        });


                    }
                });


            }
        });
    }

    private void reportOffence(final Context context, String path, HashMap<String, Object> hashMap)
    {
        hashMap.put(MyConstant.IMAGE_PATH, path);
        hashMap.put(MyConstant.REPORTED_TIME, ServerValue.TIMESTAMP);

        String latLong = BeatActivity.mCurrentLocation == null ? "" : BeatActivity.mCurrentLocation.getLatitude() + "," + BeatActivity.mCurrentLocation.getLongitude();

        hashMap.put(MyConstant.LOCATION, latLong);

        final String userID = MySharedPreference.getInstance().getUserID(context);
        root.child(MyConstant.REPORTED_OFFENCE).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    CommonMethods.getInstance().showToast(context, "Offence reported successfully");

                    ((ReportOffenceActivity) context).finish();
                }
                CommonDialogs.getInstance().dismissDialog();
            }
        });
    }


    private void reportImage(final Context context, String path)
    {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put(MyConstant.IMAGE_PATH, path);
        //hashMap.put(MyConstant.REPORTED_TIME, CommonMethods.getInstance().getCurrentDateTime());
        hashMap.put(MyConstant.REPORTED_TIME, ServerValue.TIMESTAMP);


        final String userID = MySharedPreference.getInstance().getUserID(context);
        root.child(MyConstant.REPORTED_IMAGES).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    CommonMethods.getInstance().showToast(context, "Image reported successfully");
                }

                CommonDialogs.getInstance().dismissDialog();
            }
        });
    }

    private void reportVideo(final Context context, String videoUrl, String videoThumbnailPath, String videoName)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(MyConstant.VIDEO_PATH, videoUrl);
        hashMap.put(MyConstant.REPORTED_TIME, ServerValue.TIMESTAMP);
        hashMap.put(MyConstant.VIDEO_THUMBNAIL, videoThumbnailPath);
        hashMap.put(MyConstant.VIDEO_NAME, videoName);


        final String userID = MySharedPreference.getInstance().getUserID(context);
        root.child(MyConstant.REPORTED_VIDEOS).child(userID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    CommonMethods.getInstance().showToast(context, "Video reported successfully");
                }

                CommonDialogs.getInstance().dismissDialog();
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<ImageDataModel> arrayList = new ArrayList<ImageDataModel>();
                for (final DataSnapshot child1 : dataSnapshot.getChildren())
                {
                    //Log.e(TAG, ""+child1.getValue());

                    ImageDataModel imageDataModel = new ImageDataModel((String) child1.child(MyConstant.IMAGE_PATH).getValue(),
                            CommonMethods.getInstance().convertTimeStampToDateTime((long) child1.child(MyConstant.REPORTED_TIME).getValue()),
                            "",
                            "",
                            "");


                    arrayList.add(imageDataModel);
                    //  Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());


                }

                Collections.reverse(arrayList);
                reportedImagesListener.callback(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    public void getFirebaseVideos(Context context, final Interfaces.ReportedVideosListener reportedVideosListener)
    {

        String userId = MySharedPreference.getInstance().getUserID(context);

        root.child(MyConstant.REPORTED_VIDEOS).child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<VideoDataModel> arrayList = new ArrayList<>();
                for (final DataSnapshot child1 : dataSnapshot.getChildren())
                {
                    //Log.e(TAG, ""+child1.getValue());

                    VideoDataModel videoDataModel = new VideoDataModel();
                    videoDataModel.setVideoPath((String) child1.child(MyConstant.VIDEO_PATH).getValue());
                    videoDataModel.setVideoName((String) child1.child(MyConstant.VIDEO_NAME).getValue());
                    videoDataModel.setReportedTime(CommonMethods.getInstance().convertTimeStampToDateTime((long) child1.child(MyConstant.REPORTED_TIME).getValue()));
                    videoDataModel.setVideoThumbnailPath((String) child1.child(MyConstant.VIDEO_THUMBNAIL).getValue());

                    arrayList.add(videoDataModel);
                }

                Collections.reverse(arrayList);
                reportedVideosListener.callback(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    public void getFirebaseOffence(Context context, final Interfaces.ReportedOffenceListener reportedOffenceListener)
    {

        String userId = MySharedPreference.getInstance().getUserID(context);

        root.child(MyConstant.REPORTED_OFFENCE).child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                ArrayList<OffenceDataModel> arrayList = new ArrayList<OffenceDataModel>();
                for (final DataSnapshot child1 : dataSnapshot.getChildren())
                {
                    //Log.e(TAG, ""+child.getValue());

                    OffenceDataModel imageDataModel = new OffenceDataModel((String) child1.child(MyConstant.IMAGE_PATH).getValue(),
                            CommonMethods.getInstance().convertTimeStampToDateTime((long) child1.child(MyConstant.REPORTED_TIME).getValue()),
                            "",
                            (String) child1.child(MyConstant.DESCRIPTION).getValue(),
                            (String) child1.child(MyConstant.SPECIESNAME).getValue(),
                            (String) child1.child(MyConstant.LOCATION).getValue(),
                            "",
                            "");


                    arrayList.add(imageDataModel);
                    //  Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());


                }

                Collections.reverse(arrayList);
                reportedOffenceListener.callback(arrayList);
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
                final ArrayList<ImageDataModel> arrayList = new ArrayList<ImageDataModel>();

                if (dataSnapshot.getValue() == null)
                {
                    CommonDialogs.getInstance().dialog.dismiss();
                    return;
                }
                for (final DataSnapshot beatIds : dataSnapshot.getChildren())
                {

                    Log.e(TAG, "beatIds.getKey() " + beatIds.getKey());

                    root.child(MyConstant.USERS).child(MyConstant.BEAT).child(beatIds.getKey()).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            String guardName = (String) dataSnapshot.child(MyConstant.USER_NAME).getValue();

                            for (final DataSnapshot keys : beatIds.getChildren())
                            {
                                ImageDataModel imageDataModel = new ImageDataModel((String) keys.child(MyConstant.IMAGE_PATH).getValue(),
                                        CommonMethods.getInstance().convertTimeStampToDateTime((long) keys.child(MyConstant.REPORTED_TIME).getValue()),
                                        guardName,
                                        beatIds.getKey(),
                                        keys.getKey());

                                arrayList.add(imageDataModel);
                                // Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }


    public void getReportedVideosByAdmin(final Interfaces.ReportedVideosListener reportedVideosListener)
    {
        root.child(MyConstant.REPORTED_VIDEOS).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final ArrayList<VideoDataModel> arrayList = new ArrayList<>();

                if (dataSnapshot.getValue() == null)
                {
                    CommonDialogs.getInstance().dialog.dismiss();
                    return;
                }

                for (final DataSnapshot beatIds : dataSnapshot.getChildren())
                {

                    Log.e(TAG, "beatIds.getKey() " + beatIds.getKey());

                    root.child(MyConstant.USERS).child(MyConstant.BEAT).child(beatIds.getKey()).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            String guardName = (String) dataSnapshot.child(MyConstant.USER_NAME).getValue();

                            for (final DataSnapshot child1 : beatIds.getChildren())
                            {
                               /* (String) keys.child(MyConstant.IMAGE_PATH).getValue(),
                                        CommonMethods.getInstance().convertTimeStampToDateTime((long) keys.child(MyConstant.REPORTED_TIME).getValue()),
                                        guardName,
                                        beatIds.getKey(),
                                        keys.getKey());

                                arrayList.add(imageDataModel);*/


                                VideoDataModel videoDataModel = new VideoDataModel();
                                videoDataModel.setVideoPath((String) child1.child(MyConstant.VIDEO_PATH).getValue());
                                videoDataModel.setVideoName((String) child1.child(MyConstant.VIDEO_NAME).getValue());
                                videoDataModel.setReportedTime(CommonMethods.getInstance().convertTimeStampToDateTime((long) child1.child(MyConstant.REPORTED_TIME).getValue()));
                                videoDataModel.setVideoThumbnailPath((String) child1.child(MyConstant.VIDEO_THUMBNAIL).getValue());
                                videoDataModel.setReportedBy(guardName);
                                videoDataModel.setBeatID(beatIds.getKey());
                                videoDataModel.setReportedVideoID(child1.getKey());

                                arrayList.add(videoDataModel);
                            }
                            Collections.reverse(arrayList);
                            reportedVideosListener.callback(arrayList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
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


    public void getReportedOffenceByAdmin(final Interfaces.ReportedOffenceListener reportedOffenceListener)
    {

        root.child(MyConstant.REPORTED_OFFENCE).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final ArrayList<OffenceDataModel> arrayList = new ArrayList<OffenceDataModel>();

                if (dataSnapshot.getValue() == null)
                {
                    CommonDialogs.getInstance().dialog.dismiss();
                    return;
                }
                for (final DataSnapshot beatIds : dataSnapshot.getChildren())
                {

                    Log.e(TAG, "beatIds.getKey() " + beatIds.getKey());

                    root.child(MyConstant.USERS).child(MyConstant.BEAT).child(beatIds.getKey()).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {


                            String guardName = (String) dataSnapshot.child(MyConstant.USER_NAME).getValue();

                            for (final DataSnapshot keys : beatIds.getChildren())
                            {
                                Log.e(TAG, "Offence.getKey() " + keys.getKey());
                                /*OffenceDataModel offenceDataModel = new OffenceDataModel((String) keys.child(MyConstant.IMAGE_PATH).getValue(),
                                        CommonMethods.getInstance().convertTimeStampToDateTime((long)keys.child(MyConstant.REPORTED_TIME).getValue()),
                                        guardName);*/


                                OffenceDataModel imageDataModel = new OffenceDataModel((String) keys.child(MyConstant.IMAGE_PATH).getValue(),
                                        CommonMethods.getInstance().convertTimeStampToDateTime((long) keys.child(MyConstant.REPORTED_TIME).getValue()),
                                        guardName,
                                        (String) keys.child(MyConstant.DESCRIPTION).getValue(),
                                        (String) keys.child(MyConstant.SPECIESNAME).getValue(),
                                        (String) keys.child(MyConstant.LOCATION).getValue(),
                                        beatIds.getKey(),
                                        keys.getKey());

                                arrayList.add(imageDataModel);
                                // Log.e(TAG, "imagePath "+imageDataModel.getImagePath()+" Time "+imageDataModel.getReportedTime());
                            }
                            Collections.reverse(arrayList);
                            reportedOffenceListener.callback(arrayList);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Log.e(TAG, "1");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e(TAG, "2");
            }
        });
    }

    public void getAllGuards(final Interfaces.GetAllBeatListener getAllBeatListener)
    {
        root.child(MyConstant.USERS).child(MyConstant.BEAT).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<GuardDataModel> arrayList = new ArrayList<GuardDataModel>();

                for (final DataSnapshot child : dataSnapshot.getChildren())
                {


                    Log.e(TAG, "" + child.child(MyConstant.USER_NAME).getValue());

                    GuardDataModel beatDataModel = new GuardDataModel();
                    beatDataModel.setBeatId(child.getKey());
                    beatDataModel.setBeatEmail((String) child.child(MyConstant.USER_EMAIL).getValue());
                    beatDataModel.setBeatName((String) child.child(MyConstant.USER_NAME).getValue());
                    beatDataModel.setBeatPhoneNumber((String) child.child(MyConstant.USER_PHONE).getValue());

                    beatDataModel.setBeatRange((String) child.child(MyConstant.RANGE).getValue());
                    beatDataModel.setBeatBlock((String) child.child(MyConstant.BLOCK).getValue());
                    beatDataModel.setBeatBeat((String) child.child(MyConstant.BEAT).getValue());
                    beatDataModel.setBeatHeadquater((String) child.child(MyConstant.HEADQUATER).getValue());

                    if (child.hasChild(MyConstant.USER_PHOTO))
                    {
                        beatDataModel.setBeatPhoto((String) child.child(MyConstant.USER_PHOTO).getValue());
                    }

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

    public void getBeatsByCategory(String range, String block, String beat, final Interfaces.GetAllBeatListener getAllBeatListener)
    {
        root.child(MyConstant.USERS).child(MyConstant.BEAT)/*.orderByChild(MyConstant.RANGE).equalTo(range)*/
                //.orderByChild(MyConstant.BLOCK).equalTo(block)
                .orderByChild(MyConstant.BEAT).equalTo(beat).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<GuardDataModel> arrayList = new ArrayList<GuardDataModel>();

                for (final DataSnapshot child : dataSnapshot.getChildren())
                {


                    Log.e(TAG, "" + child.child(MyConstant.USER_NAME).getValue());

                    GuardDataModel beatDataModel = new GuardDataModel();
                    beatDataModel.setBeatId(child.getKey());
                    beatDataModel.setBeatEmail((String) child.child(MyConstant.USER_EMAIL).getValue());
                    beatDataModel.setBeatName((String) child.child(MyConstant.USER_NAME).getValue());
                    beatDataModel.setBeatPhoneNumber((String) child.child(MyConstant.USER_PHONE).getValue());

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

    public void getBeatDateLocationData(String beatId, final Interfaces.GetBeatDateLocationListener getBeatDateLocationListener)
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

                    ArrayList<ArrayList<BeatLocationModel.DateLocation>> dateLocationArray = new ArrayList<>();
                    for (final DataSnapshot startedBranch : child.getChildren())
                    {
                        ArrayList<BeatLocationModel.DateLocation> dateLocationArrayList = new ArrayList<BeatLocationModel.DateLocation>();
                        for (final DataSnapshot dataSnapshot1 : startedBranch.getChildren())
                        {

                            BeatLocationModel.DateLocation dateLocation = new BeatLocationModel.DateLocation();
                            dateLocation.setLocation((String) dataSnapshot1.child(MyConstant.LOCATION).getValue());
                            Object objectTime = dataSnapshot1.child(MyConstant.TIME).getValue();
                            if (objectTime != null)
                            {
                                dateLocation.setTime(CommonMethods.getInstance().convertTimeStampToDateTime((long) objectTime));
                            }

                            dateLocationArrayList.add(dateLocation);
                        }
                        dateLocationArray.add(dateLocationArrayList);

                    }

                    beatLocationModel.setDateLocations(dateLocationArray);

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


    public void deleteOffence(String beatID, String offenceID, final Interfaces.DeleteOffenceListener deleteOffenceListener)
    {
        root.child(MyConstant.REPORTED_OFFENCE).child(beatID).child(offenceID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    deleteOffenceListener.onSuccess();
                }
                else
                {
                    deleteOffenceListener.onUnSuccess();
                }
            }
        });
    }


    public void deleteReportedImage(String beatID, String reportedImageID, final Interfaces.DeleteReportedImageListener deleteReportedImageListener)
    {
        root.child(MyConstant.REPORTED_IMAGES).child(beatID).child(reportedImageID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    deleteReportedImageListener.onSuccess();
                }
                else
                {
                    deleteReportedImageListener.onUnSuccess();
                }
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
                    CommonMethods.showToast(context, "User Name already exists");
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

                        CommonMethods.showToast(context, "Login successfull");
//
                        context.startActivity(new Intent(context, MainActivity.class));

                        ((Activity) context).finish();
                    }
                    else
                    {
                        CommonMethods.showToast(context, "Password did not match");
                    }


                }

                if (!isChildThere)
                {
                    CommonMethods.showToast(context, "User Name does not exists");
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
