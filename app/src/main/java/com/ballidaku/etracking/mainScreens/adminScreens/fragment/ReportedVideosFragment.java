package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.ReportedVideosAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.VideoDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReportedVideosFragment extends Fragment
{

    String TAG = ReportedVideosFragment.class.getSimpleName();

    View view = null;

    Context context;

    RecyclerView recyclerViewReportedVideos;
    public ReportedVideosAdapter reportedVideosAdapter;

    ArrayList<VideoDataModel> arrayListVideos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_reported_images, container, false);

            context = getActivity();


            setUpViews();

            getFirebaseImages();


        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity) getActivity()).toolbar.setTitle("Reported Videos");
        ((MainActivity) getActivity()).refreshMenu(3);
    }

    private void setUpViews()
    {
        recyclerViewReportedVideos =view.findViewById(R.id.recyclerViewReportedImages);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerViewReportedVideos.setLayoutManager(layoutManager);
        recyclerViewReportedVideos.setItemAnimator(new DefaultItemAnimator());


        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;

        recyclerViewReportedVideos.addItemDecoration(CommonMethods.getInstance().new SpacesItemDecoration(spanCount, spacing, includeEdge));
        reportedVideosAdapter = new ReportedVideosAdapter(context, arrayListVideos);
        recyclerViewReportedVideos.setAdapter(reportedVideosAdapter);
    }


    private void getFirebaseImages()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getReportedVideosByAdmin(new Interfaces.ReportedVideosListener()
        {
            @Override
            public void callback(ArrayList<VideoDataModel> arrayList)
            {

                Collections.sort(arrayList, new Comparator<VideoDataModel>()
                {
                    public int compare(VideoDataModel o1, VideoDataModel o2)
                    {
                        if (o1.getReportedTime() == null || o2.getReportedTime() == null)
                            return 0;
                        return CommonMethods.getInstance().stringToDate(o1.getReportedTime()).compareTo(CommonMethods.getInstance().stringToDate(o2.getReportedTime()));
                    }
                });

                Collections.reverse(arrayList);

                reportedVideosAdapter.refresh(arrayList);
                CommonDialogs.getInstance().dialog.dismiss();
            }
        });

    }


    public void showShareOption()
    {
        reportedVideosAdapter.showShareIcon = true;
        reportedVideosAdapter.showDeleteIcon = false;
        reportedVideosAdapter.notifyDataSetChanged();
    }

    public void showDeleteOption()
    {
        reportedVideosAdapter.showShareIcon = false;
        reportedVideosAdapter.showDeleteIcon = true;
        reportedVideosAdapter.notifyDataSetChanged();
    }

    public void refreshMenus()
    {
        reportedVideosAdapter.showShareIcon = false;
        reportedVideosAdapter.showDeleteIcon = false;
        reportedVideosAdapter.notifyDataSetChanged();
    }
}
