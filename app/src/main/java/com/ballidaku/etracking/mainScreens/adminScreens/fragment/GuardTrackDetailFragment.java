package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.GuardTrackDetailsAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.SimpleDividerItemDecoration;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 27/09/17.
 */

public class GuardTrackDetailFragment extends Fragment implements View.OnClickListener
{

    String TAG = GuardTrackDetailFragment.class.getSimpleName();

    View view = null;

    Context context;

    public String beatName;
    String beatID;


    RecyclerView recycleViewGuardTrackDates;
    GuardTrackDetailsAdapter beatDateLocationAdapter;

    ArrayList<BeatLocationModel> arrayList  = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_guard_track_details, container, false);

            context = getActivity();

            setUpViews();

        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity)getActivity()).toolbar.setTitle(beatName+" Track Dates");
    }

    private void setUpViews()
    {
        beatName = getArguments().getString(MyConstant.BEAT_NAME);
        beatID = getArguments().getString(MyConstant.BEAT_ID);

        recycleViewGuardTrackDates = (RecyclerView) view.findViewById(R.id.recycleViewGuardTrackDates);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewGuardTrackDates.setLayoutManager(layoutManager);
        recycleViewGuardTrackDates.setItemAnimator(new DefaultItemAnimator());
        recycleViewGuardTrackDates.addItemDecoration(new SimpleDividerItemDecoration(context));

        beatDateLocationAdapter = new GuardTrackDetailsAdapter(this,context, arrayList);

        recycleViewGuardTrackDates.setAdapter(beatDateLocationAdapter);

        getGuardTrackDetails();

    }


    void getGuardTrackDetails()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getBeatDateLocationData(beatID, new Interfaces.GetBeatDateLocationListener()
        {
            @Override
            public void callback(ArrayList<BeatLocationModel> arrayList)
            {
                /*for (int i = 0; i <arrayList.size() ; i++)
                {
                    Log.e(TAG,"Date "+arrayList.get(i).getDate());
                    Log.e(TAG,"Location "+arrayList.get(i).getDateLocations().get(0).getLocation());
                }*/

               // CommonDialogs.getInstance().selectBeatDateDialog(context,beatName,arrayList);

                refreshAdapter(arrayList);

            }
        });
    }

    void refreshAdapter(ArrayList<BeatLocationModel> arrayList)
    {
        CommonDialogs.getInstance().dialog.dismiss();

        this.arrayList = arrayList;
        beatDateLocationAdapter.refresh(arrayList);
        beatDateLocationAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {


        }
    }
}
