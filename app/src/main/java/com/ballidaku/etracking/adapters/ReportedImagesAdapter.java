package com.ballidaku.etracking.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.ImageDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 17/09/17.
 */

public class ReportedImagesAdapter extends RecyclerView.Adapter<ReportedImagesAdapter.MyViewHolder>
{

    String TAG = "ReportedImagesAdapter";

    private ArrayList<ImageDataModel> mImagesList;
    private Context context;

    public boolean showDeleteIcon;
    public boolean showShareIcon;

    CardView cardView;


    public ReportedImagesAdapter(Context context, ArrayList<ImageDataModel> imageList)
    {
        this.context = context;
        this.mImagesList = imageList;

    }



    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_reported_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {

        String imageUrl = mImagesList.get(position).getImagePath();
        String[] dateTime = mImagesList.get(position).getReportedTime().split(" ");


        CommonMethods.getInstance().showImageGlide2(context,holder.imageViewReported,imageUrl);


        String reportedBy=mImagesList.get(position).getReportedBy();
        if(reportedBy.isEmpty())
        {
            holder.linearLayoutReportedBy.setVisibility(View.GONE);
        }
        else
        {
            holder.textViewReportedBy.setText(reportedBy);
        }

        holder.textViewDate.setText(CommonMethods.getInstance().convertDateToDateFormat(dateTime[0]));
        holder.textViewTime.setText(CommonMethods.getInstance().convertTimeToTimeFormat(dateTime[1]+" "+dateTime[2]));


        if (!showDeleteIcon && !showShareIcon)
        {
            holder.linearLayoutShareDelete.setVisibility(View.GONE);
        }
        else
        {
            holder.linearLayoutShareDelete.setVisibility(View.VISIBLE);

            if (showDeleteIcon)
            {
                holder.imageViewShareDelete.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_delete, null));
            }
            else
            {
                holder.imageViewShareDelete.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_share, null));
            }
        }




    }

   public void  refresh(ArrayList<ImageDataModel> arrayList)
    {
        mImagesList=arrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return mImagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageViewReported;
        ImageView imageViewShareDelete;

        TextView textViewDate;
        TextView textViewTime;
        TextView textViewReportedBy;

        LinearLayout linearLayoutReportedBy;
        LinearLayout linearLayoutShareDelete;
        LinearLayout linearLayoutDownload;

        CardView cardViewLocal;

        public MyViewHolder(View view)
        {
            super(view);

            imageViewReported = view.findViewById(R.id.imageViewReported);
            textViewDate =  view.findViewById(R.id.textViewDate);
            textViewTime =  view.findViewById(R.id.textViewTime);
            textViewReportedBy =  view.findViewById(R.id.textViewReportedBy);

            linearLayoutReportedBy = view.findViewById(R.id.linearLayoutReportedBy);
            linearLayoutShareDelete = view.findViewById(R.id.linearLayoutShareDelete);
            linearLayoutDownload = view.findViewById(R.id.linearLayoutDownload);
            linearLayoutDownload.setVisibility(View.GONE);

            cardViewLocal =  view.findViewById(R.id.cardView);


            imageViewShareDelete = view.findViewById(R.id.imageViewShareDelete);


            imageViewReported.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    //CommonDialogs.getInstance().zoomableImageViewDialog(context,mImagesList.get(getAdapterPosition()).getImagePath());
                    String reportedBy=textViewReportedBy.getText().toString().trim();
                    CommonDialogs.getInstance().showImage(context,mImagesList.get(getAdapterPosition()).getImagePath(),reportedBy.isEmpty()?"Image":reportedBy);
                }
            });


            imageViewShareDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String shareDelete = showDeleteIcon ? "Delete" : "Share";


                    Log.e(TAG, "BeatId " + mImagesList.get(getAdapterPosition()).getBeatID());
                    Log.e(TAG, "ReportedImageID " + mImagesList.get(getAdapterPosition()).getReportedImageID());

                    if(showDeleteIcon)
                    {
                        delete(getAdapterPosition(), mImagesList.get(getAdapterPosition()).getBeatID(), mImagesList.get(getAdapterPosition()).getReportedImageID());
                    }
                    else
                    {

                        linearLayoutShareDelete.setVisibility(View.GONE);
                        cardView=cardViewLocal;

                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ((MainActivity)context).requestAppPermissions(permission, R.string.permission, 54);

                    }
                }
            });

        }
    }



    public void getBitmapOfView()
    {
        showShareIcon=false;
        notifyDataSetChanged();
        Bitmap balliBitmap = getBitmapOfView((View)cardView);

        saveFile("temprary",balliBitmap,100);
    }

    public void delete(final int adapterPosition, String beatID, String reportedImageID)
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().deleteReportedImage(beatID, reportedImageID, new Interfaces.DeleteReportedImageListener()
        {
            @Override
            public void onSuccess()
            {
                mImagesList.remove(adapterPosition);
                showShareIcon=false;
                showDeleteIcon=false;
                notifyDataSetChanged();
                CommonDialogs.getInstance().dialog.dismiss();
                CommonMethods.getInstance().showToast(context, "Image deleted successfully");
            }

            @Override
            public void onUnSuccess()
            {
                CommonDialogs.getInstance().dialog.dismiss();
            }
        });
    }


    public  Bitmap getBitmapOfView(final View view) {
        view.destroyDrawingCache();
        view.buildDrawingCache(false);
        Bitmap orig = view.getDrawingCache();
        Bitmap.Config config = null;

        if (orig != null) {
            config = orig.getConfig();
        }

        if (config == null) {
            //config = Bitmap.Config.ARGB_8888;
            config = Bitmap.Config.RGB_565;
        }
        //Bitmap b = orig.copy(config, false);
        Bitmap b = null;

        try {
            if (orig != null) {
                b = orig.copy(config, true);
                orig.recycle();
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            try {
                b = convertToMutable(orig);
                orig.recycle();
            } catch (OutOfMemoryError exception1) {
                exception1.printStackTrace();
            }
        }


        view.destroyDrawingCache();


        return b;
    }


    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();


            if (type == null) {
                type = Bitmap.Config.ARGB_8888;
            }


            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    String tempraryDirectory = Environment.getExternalStorageDirectory() + "/ETracking";

    private void saveFile(String name, Bitmap b, int quality) {
        FileOutputStream fos;// = null;
        //String fileName = getFileName(name);
        String fileName = name + ".jpg";

        //File directory = new File(Environment.getExternalStorageDirectory() + "/vokiBalli/");




        File directory = new File(tempraryDirectory);
        directory.mkdir();

        File fileToSave = new File(directory, fileName);
        try {
            fos = new FileOutputStream(fileToSave);
            if (!b.compress(Bitmap.CompressFormat.JPEG, quality, fos))
                Log.d("BALLI", "Compress/Write failed");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.d("BALLI", "Can't save the screenshot! Requires write permission (android.permission.WRITE_EXTERNAL_STORAGE) in AndroidManifest.xml of the application under test.");
            e.printStackTrace();
        }

        b.recycle();

        Intent share = new Intent(Intent.ACTION_SEND);
        //share.setType("image/jpeg");
        share.setType("application/image");
        //share.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileToSave.getAbsolutePath()));
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToSave));
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share Image"));


    }





}
