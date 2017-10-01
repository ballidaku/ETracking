package com.ballidaku.etracking.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.dataModels.ImageDataModel;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 17/09/17.
 */

public class ReportedImagesAdapter extends RecyclerView.Adapter<ReportedImagesAdapter.MyViewHolder>
{

    private ArrayList<ImageDataModel> mImagesList;
    private Context context;


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


        CommonMethods.getInstance().showImageGlide(context,holder.imageViewReported,imageUrl);


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

        public MyViewHolder(View view)
        {
            super(view);

            imageViewReported = (ImageView) view.findViewById(R.id.imageViewReported);


            imageViewReported.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    CommonDialogs.getInstance().zoomableImageViewDialog(context,mImagesList.get(getAdapterPosition()).getImagePath());
                }
            });

        }
    }





}
