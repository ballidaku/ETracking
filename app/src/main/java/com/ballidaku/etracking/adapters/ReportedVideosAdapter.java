package com.ballidaku.etracking.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.ballidaku.etracking.commonClasses.CompressImageVideo;
import com.ballidaku.etracking.commonClasses.DownloadTask;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.VideoDataModel;

import java.io.File;
import java.util.ArrayList;

public class ReportedVideosAdapter extends RecyclerView.Adapter<ReportedVideosAdapter.MyViewHolder>
{

    private String TAG = "ReportedVideosAdapter";

    private ArrayList<VideoDataModel> videoDataModelArrayList;
    private Context context;

    public boolean showDeleteIcon;
    public boolean showShareIcon;

//    private CardView cardView;


    public ReportedVideosAdapter(Context context, ArrayList<VideoDataModel> videoDataModelArrayList)
    {
        this.context = context;
        this.videoDataModelArrayList = videoDataModelArrayList;
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_reported_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {

        String imageUrl = videoDataModelArrayList.get(position).getVideoThumbnailPath();
        String videoName = videoDataModelArrayList.get(position).getVideoName();
        String[] dateTime = videoDataModelArrayList.get(position).getReportedTime().split(" ");


        CommonMethods.getInstance().showImageGlide2(context, holder.imageViewReported, imageUrl);


        String reportedBy = videoDataModelArrayList.get(position).getReportedBy();
        if (reportedBy.isEmpty())
        {
            holder.linearLayoutReportedBy.setVisibility(View.GONE);
        }
        else
        {
            holder.textViewReportedBy.setText(reportedBy);
        }

        holder.textViewDate.setText(CommonMethods.getInstance().convertDateToDateFormat(dateTime[0]));
        holder.textViewTime.setText(CommonMethods.getInstance().convertTimeToTimeFormat(dateTime[1] + " " + dateTime[2]));

        //Check video in Download
        File file = new File(CompressImageVideo.getInstance().getDownloadVideoDirectoryPath(context), videoName);
        holder.linearLayoutDownload.setVisibility(file.exists() ? View.GONE : View.VISIBLE);


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

            holder.linearLayoutDownload.setVisibility(View.GONE);
        }


    }

    public void refresh(ArrayList<VideoDataModel> arrayList)
    {
        videoDataModelArrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return videoDataModelArrayList.size();
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

        MyViewHolder(View view)
        {
            super(view);

            imageViewReported = view.findViewById(R.id.imageViewReported);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewTime = view.findViewById(R.id.textViewTime);
            textViewReportedBy = view.findViewById(R.id.textViewReportedBy);

            linearLayoutReportedBy = view.findViewById(R.id.linearLayoutReportedBy);
            linearLayoutShareDelete = view.findViewById(R.id.linearLayoutShareDelete);
            linearLayoutDownload = view.findViewById(R.id.linearLayoutDownload);


            cardViewLocal = view.findViewById(R.id.cardView);


            imageViewShareDelete = view.findViewById(R.id.imageViewShareDelete);


            imageViewReported.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    String videoName = videoDataModelArrayList.get(getAdapterPosition()).getVideoName();

                    File file = new File(CompressImageVideo.getInstance().getDownloadVideoDirectoryPath(context), videoName);

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    Uri data = Uri.parse(file.getAbsolutePath());
                    intent.setDataAndType(data, "video/m4a");
                    context.startActivity(intent);

                }
            });

            linearLayoutDownload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String videoPath = videoDataModelArrayList.get(getAdapterPosition()).getVideoPath();
                    String videoName = videoDataModelArrayList.get(getAdapterPosition()).getVideoName();
                    File file = new File(CompressImageVideo.getInstance().getDownloadVideoDirectoryPath(context), videoName);

                    new DownloadTask(context, file, new Interfaces.OnDownloadCompleted()
                    {
                        @Override
                        public void onCompleted()
                        {
                            notifyDataSetChanged();
                        }
                    }).execute(videoPath);
                }
            });


            imageViewShareDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String shareDelete = showDeleteIcon ? "Delete" : "Share";


                    Log.e(TAG, "BeatId " + videoDataModelArrayList.get(getAdapterPosition()).getBeatID());
                    Log.e(TAG, "ReportedImageID " + videoDataModelArrayList.get(getAdapterPosition()).getReportedVideoID());

                    if (showDeleteIcon)
                    {
                        delete(getAdapterPosition(), videoDataModelArrayList.get(getAdapterPosition()).getBeatID(), videoDataModelArrayList.get(getAdapterPosition()).getReportedVideoID());
                    }
                    else
                    {
                        CommonMethods.getInstance().shareVideoLink(context,videoDataModelArrayList.get(getAdapterPosition()).getVideoPath());
                        //linearLayoutShareDelete.setVisibility(View.GONE);
//                        cardView = cardViewLocal;
                        //String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                       // ((MainActivity) context).requestAppPermissions(permission, R.string.permission, 54);
                    }
                }
            });

        }
    }


    public void delete(final int adapterPosition, String beatID, String reportedImageID)
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().deleteReportedImage(beatID, reportedImageID, new Interfaces.DeleteReportedImageListener()
        {
            @Override
            public void onSuccess()
            {
                videoDataModelArrayList.remove(adapterPosition);
                showShareIcon = false;
                showDeleteIcon = false;
                notifyDataSetChanged();
                CommonDialogs.getInstance().dialog.dismiss();
                CommonMethods.getInstance().showToast(context, "Video deleted successfully");
            }

            @Override
            public void onUnSuccess()
            {
                CommonDialogs.getInstance().dialog.dismiss();
            }
        });
    }


}
