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
import com.ballidaku.etracking.adapters.ReportedImagesAdapter;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.ImageDataModel;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 20/09/17.
 */

public class ReportedImagesFragment extends Fragment
{

    String TAG = ReportedImagesFragment.class.getSimpleName();

    View view = null;

    Context context;

    RecyclerView recyclerViewReportedImages;
    ReportedImagesAdapter reportedImagesAdapter;

    ArrayList<ImageDataModel> arrayListImages=new ArrayList<>();


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

    private void setUpViews()
    {
        recyclerViewReportedImages=(RecyclerView)view.findViewById(R.id.recyclerViewReportedImages);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 4);
        recyclerViewReportedImages.setLayoutManager(layoutManager);
        recyclerViewReportedImages.setItemAnimator(new DefaultItemAnimator());

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewReportedImages.getContext(),
//                layoutManager.getOrientation());

//        recyclerViewReportedImages.addItemDecoration(dividerItemDecoration);

        reportedImagesAdapter=new ReportedImagesAdapter(context,arrayListImages);
        recyclerViewReportedImages.setAdapter(reportedImagesAdapter);
    }


    private void getFirebaseImages()
    {

        MyFirebase.getInstance().getReportedImagesByAdmin(new Interfaces.ReportedImagesListener()
        {
            @Override
            public void callback(ArrayList<ImageDataModel> arrayList)
            {
                reportedImagesAdapter.refresh(arrayList);
            }
        });

    }
}
