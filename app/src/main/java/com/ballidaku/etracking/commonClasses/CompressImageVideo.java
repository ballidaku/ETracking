package com.ballidaku.etracking.commonClasses;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.ballidaku.etracking.R;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.loader.LoadJNI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompressImageVideo
{

    private String TAG=CompressImageVideo.class.getSimpleName();

    private static CompressImageVideo instance = new CompressImageVideo();

    public static CompressImageVideo getInstance()
    {
        return instance;
    }


    Context context;
    private Interfaces.CompressionInterface compressionInterface;

    //*****************************************************************************************************
    //*****************************************************************************************************
    // Compress Video
    //*****************************************************************************************************
    //*****************************************************************************************************


    private final int STOP_TRANSCODING_MSG = -1;
    private final int FINISHED_TRANSCODING_MSG = 0;

    private LoadJNI vk = null;

    String workFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

    public void compressVideo(final Context context, final String videoPath, Interfaces.CompressionInterface compressionInterface)
    {
        this.context = context;
        this.compressionInterface=compressionInterface;

        new Thread()
        {
            public void run()
            {
                Log.e("TAG", "Worker started");
                try
                {
                    //    sleep(5000);
                    runTranscodingUsingLoader(context,videoPath);
                    handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);
                }
                catch (Exception e)
                {
                    Log.e("TAG", "threadmessage " + e.getMessage());
                }
            }

        }.start();
    }

    String outputVideoPath;
    private void runTranscodingUsingLoader(Context context,String videoPath)
    {


//         File file = getVideoImagePath(context,MEDIA_TYPE_VIDEO);
         File file = getVideoName(context);

        Log.e("TAG", "runTranscodingUsingLoader started...");
        Log.e("TAG", videoPath + "----" + file.getAbsolutePath());

        outputVideoPath = file.getAbsolutePath();

        vk = new LoadJNI();

        String commandStr = "ffmpeg -y -i " + videoPath + " -strict experimental -r 30 -ab 48000 -ac 2 -ar 48000 -vcodec mpeg4 -b 4097152 " + outputVideoPath;
//        String commandStr = "ffmpeg -y -i "+videoPath+" -codec:v libx264 -crf 23 -preset medium -codec:a libfdk_aac -vbr 4 -vf scale=-1:640,format=yuv420p "+outputVideoPath;

        Log.e("TAG", commandStr);
        try
        {
            Log.e("TAG", "vk.going to run.");
            // running regular command with validation
            vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, context.getApplicationContext());
            Log.e("TAG", "vk.run finished.");


            countDownTimer.start();

        }
        catch (Throwable e)
        {
            Log.e("TAG", "vk run exeption.", e);
            //  ShowDialog.hideDialog();
        }


    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Log.e("TAG", "Handler got message");
            //            if (progressDialog != null)
            //            {//                progressDialog.dismiss();
            // stopping the transcoding native


            if (msg.what == STOP_TRANSCODING_MSG)
            {
                Log.e("TAG", "Got cancel message, calling fexit");
                vk.fExit(context.getApplicationContext());
            }
        }
    };

    private CountDownTimer countDownTimer = new CountDownTimer(200, 100)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {
        }

        @Override
        public void onFinish()
        {
            compressionInterface.onCompressionCompleted(outputVideoPath);
        }
    };





    //*****************************************************************************************************
    //*****************************************************************************************************
    // Compress Image
    //*****************************************************************************************************
    //*****************************************************************************************************

    public synchronized String compressImage( Context context,Uri imageUri)
    {

        String filePath = getRealPathFromURI(context,imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio)
            {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            }
            else if (imgRatio > maxRatio)
            {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            }
            else
            {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try
        {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        }
        catch (OutOfMemoryError exception)
        {
            exception.printStackTrace();

        }
        try
        {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        }
        catch (OutOfMemoryError exception)
        {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try
        {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6)
            {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            }
            else if (orientation == 3)
            {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            }
            else if (orientation == 8)
            {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getImageName(context);
        try
        {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        return filename;

    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
        {
            inSampleSize++;
        }

        return inSampleSize;
    }



 /*   public synchronized void deleteDirctory()
    {
//        File dir = new File(Environment.getExternalStorageDirectory().getPath(), context.getString(R.string.app_name)+"/Images");
        File dir = getTempraryImagesDirectoryPath();
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }

    }*/


    //**********************************************************************************************
    // Get real path from uri
    //**********************************************************************************************

    @SuppressLint("NewApi")
    public String getRealPathFromURI(Context context, Uri uri)
    {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{ split[1] };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //**********************************************************************************************
    //**********************************************************************************************

    private String getImageName(Context context)
    {
        return (getTempraryImagesDirectoryPath(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }


    public Uri getTempraryImageUri(Context context)
    {
        return Uri.fromFile(getTempraryImageFile(context));
    }

    private File getTempraryImagesDirectoryPath(Context context)
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), context.getString(R.string.app_name)+"/Images");
        if (!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    public File getTempraryImageFile(Context context)
    {
        return new File(getTempraryImagesDirectoryPath(context).getAbsolutePath(), "IMG_Temp.jpg");
    }

    public void deleteTempraryImage(Context context)
    {
        deleteRecursive(getTempraryImageFile(context));
    }

    public void deleteTempraryImageDirectory(Context context)
    {
        deleteRecursive(getTempraryImagesDirectoryPath(context));
    }

    private void deleteRecursive(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    //*******************************************************************************************
    // Video
    //*******************************************************************************************

    private File getTempraryVideoDirectoryPath(Context context)
    {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), context.getString(R.string.app_name)+"/Videos");
        if (!file.exists())
        {
            file.mkdirs();
        }
        return file;
    }

    private File getVideoName(Context context)
    {
        return new File(getTempraryVideoDirectoryPath(context).getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp4");
    }

    public void deleteTempraryVideoDirectory(Context context)
    {
        deleteRecursive(getTempraryVideoDirectoryPath(context));
    }

    public void deletePath(String path)
    {
        deleteRecursive(new File(path));
    }

    /*private boolean isDirectoryCreated;
    private File mediaFile = null;
    public File getVideoImagePath(Context context,int type)
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), context.getString(R.string.app_name));

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Log.e(TAG, "failed to create directory");
                return null;
            }
            else
            {
                if (!isDirectoryCreated)
                {
                    isDirectoryCreated = true;
                    getVideoImagePath(context,type);
                }
            }
        }
        else
        {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

            if (type == MEDIA_TYPE_IMAGE)
            {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            }
            else if (type == MEDIA_TYPE_VIDEO)
            {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
                Log.e("videoPath", mediaFile + "");
            }
        }
        return mediaFile;
    }*/
}
