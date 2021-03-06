package com.ballidaku.etracking.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.GuardTrackDetailFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.GuardTrackMapFragment;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by sharanpalsingh on 22/09/17.
 */

public class GuardTrackDetailsAdapter extends RecyclerView.Adapter<GuardTrackDetailsAdapter.MyViewHolder>
{

    String TAG = "GuardTrackDetailsAdapter";

    private ArrayList<BeatLocationModel> arrayList;
    private Context context;
    GuardTrackDetailFragment guardTrackDetailFragment;

    public GuardTrackDetailsAdapter(GuardTrackDetailFragment guardTrackDetailFragment, Context context, ArrayList<BeatLocationModel> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.guardTrackDetailFragment = guardTrackDetailFragment;
    }

    public void refresh(ArrayList<BeatLocationModel> arrayList)
    {
        this.arrayList = arrayList;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public GuardTrackDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_track_guard_detail_item, parent, false);

        return new GuardTrackDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GuardTrackDetailsAdapter.MyViewHolder holder, int position)
    {
        String date = arrayList.get(position).getDate();

        holder.textViewBeatName.setText(date);

        int size = arrayList.get(position).getDateLocations().size();
        if (size > 0)
        {
            float distanceCovered=0.0f;
            int timeInSeconds=0;

            ArrayList<ArrayList<BeatLocationModel.DateLocation>> arrayListLocations = arrayList.get(position).getDateLocations();
            for (int i = 0; i < arrayListLocations.size(); i++)
            {
                ArrayList<BeatLocationModel.DateLocation> dateLocationsList = arrayListLocations.get(i);

                String firstLatLong = dateLocationsList.get(0).getLocation();
                String lastLatLong = dateLocationsList.get(dateLocationsList.size() - 1).getLocation();

                distanceCovered+= CommonMethods.getInstance().distanceBetweenLatLong(firstLatLong, lastLatLong);

                String startTime = dateLocationsList.get(0).getTime();
                String endTime = dateLocationsList.get(dateLocationsList.size() - 1).getTime();

                timeInSeconds+= CommonMethods.getInstance().getTimeTaken(startTime, endTime);

                Log.e(TAG,"timeInSeconds : "+timeInSeconds);

            }

            holder.textViewBeatKm.setText(CommonMethods.getInstance().getDistanceFormat(distanceCovered));


            holder.textViewTimeTaken.setText(CommonMethods.getInstance().getTimeFormat(timeInSeconds));
        }
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewBeatName;
        public TextView textViewBeatKm;
        public TextView textViewTimeTaken;

        public MyViewHolder(View view)
        {
            super(view);

            textViewBeatName = (TextView) view.findViewById(R.id.textViewBeatName);
            textViewBeatKm = (TextView) view.findViewById(R.id.textViewBeatKm);
            textViewTimeTaken = (TextView) view.findViewById(R.id.textViewTimeTaken);


            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // CommonDialogs.getInstance().dialog.dismiss();
                    // ((MainActivity)context).draw(arrayList.get(getAdapterPosition()).getDateLocations());

                    Bundle bundle = new Bundle();

                    Gson gson = new Gson();
                    bundle.putString(MyConstant.LOCATION, gson.toJson(arrayList.get(getAdapterPosition()).getDateLocations()));
                    bundle.putString(MyConstant.BEAT_NAME, guardTrackDetailFragment.beatName);

                    GuardTrackMapFragment guardTrackMapFragment = new GuardTrackMapFragment();
                    guardTrackMapFragment.setArguments(bundle);

                    CommonMethods.getInstance().switchfragment(guardTrackDetailFragment, guardTrackMapFragment);

                    guardTrackDetailFragment.getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            });

        }
    }


}
