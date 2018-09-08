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
import com.ballidaku.etracking.dataModels.GuardDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.GuardTrackDetailFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.SearchGuardByCategoryFragment;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 21/09/17.
 */

public class GuardsSelectionAdapter extends RecyclerView.Adapter<GuardsSelectionAdapter.MyViewHolder>
{

    String TAG="GuardsSelectionAdapter";

    private ArrayList<GuardDataModel> arrayList;
    private Context context;
    SearchGuardByCategoryFragment searchGuardByCategoryFragment;


    public GuardsSelectionAdapter(SearchGuardByCategoryFragment searchGuardByCategoryFragment, Context context, ArrayList<GuardDataModel> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.searchGuardByCategoryFragment=searchGuardByCategoryFragment;
    }


    public void refresh(ArrayList<GuardDataModel> arrayList)
    {
        this.arrayList = arrayList;
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
                .inflate(R.layout.custom_guard_selection, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {

        holder.textViewBeatName.setText(arrayList.get(position).getBeatName());
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewBeatName;

        public MyViewHolder(View view)
        {
            super(view);

            textViewBeatName = (TextView) view.findViewById(R.id.textViewBeatName);


            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    Log.e(TAG,"arrayList.get(getAdapterPosition()).getBeatId()  "+arrayList.get(getAdapterPosition()).getBeatId());
                    getBeatDateLocation(arrayList.get(getAdapterPosition()).getBeatName(),arrayList.get(getAdapterPosition()).getBeatId());

                }
            });

        }
    }


    public void getBeatDateLocation(final String beatName, String beatId)
    {
        Bundle bundle=new Bundle();
        bundle.putString(MyConstant.BEAT_NAME,beatName);
        bundle.putString(MyConstant.BEAT_ID,beatId);

        GuardTrackDetailFragment guardTrackDetails=new GuardTrackDetailFragment();
        guardTrackDetails.setArguments(bundle);

        CommonMethods.getInstance().switchfragment(searchGuardByCategoryFragment,guardTrackDetails);

        searchGuardByCategoryFragment.getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }





}
