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
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.ImageDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sharanpalsingh on 20/09/17.
 */

public class ReportedImagesFragment extends Fragment
{

    String TAG = ReportedImagesFragment.class.getSimpleName();

    View view = null;

    Context context;

    RecyclerView recyclerViewReportedImages;
    public ReportedImagesAdapter reportedImagesAdapter;

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

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity)getActivity()).toolbar.setTitle("Reported Images");
        ((MainActivity)getActivity()).refreshMenu(2);
    }

    private void setUpViews()
    {
        recyclerViewReportedImages=(RecyclerView)view.findViewById(R.id.recyclerViewReportedImages);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerViewReportedImages.setLayoutManager(layoutManager);
        recyclerViewReportedImages.setItemAnimator(new DefaultItemAnimator());


        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;

        recyclerViewReportedImages.addItemDecoration( CommonMethods.getInstance().new SpacesItemDecoration(spanCount,spacing,includeEdge));


//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewReportedImages.getContext(),
//                layoutManager.getOrientation());

//        recyclerViewReportedImages.addItemDecoration(dividerItemDecoration);

        reportedImagesAdapter=new ReportedImagesAdapter(context,arrayListImages);
        recyclerViewReportedImages.setAdapter(reportedImagesAdapter);
    }


    private void getFirebaseImages()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getReportedImagesByAdmin(new Interfaces.ReportedImagesListener()
        {
            @Override
            public void callback(ArrayList<ImageDataModel> arrayList)
            {

                Collections.sort(arrayList, new Comparator<ImageDataModel>() {
                    public int compare(ImageDataModel o1, ImageDataModel o2) {
                        if (o1.getReportedTime() == null || o2.getReportedTime() == null)
                            return 0;
                        return CommonMethods.getInstance().stringToDate(o1.getReportedTime()).compareTo(CommonMethods.getInstance().stringToDate(o2.getReportedTime()));
                    }
                });

                Collections.reverse(arrayList);

                reportedImagesAdapter.refresh(arrayList);
                CommonDialogs.getInstance().dialog.dismiss();
            }
        });

    }


    public void showShareOption()
    {
        reportedImagesAdapter.showShareIcon=true;
        reportedImagesAdapter.showDeleteIcon=false;
        reportedImagesAdapter.notifyDataSetChanged();
    }

    public void showDeleteOption()
    {
        reportedImagesAdapter.showShareIcon=false;
        reportedImagesAdapter.showDeleteIcon=true;
        reportedImagesAdapter.notifyDataSetChanged();
    }

    public void refreshMenus()
    {
        reportedImagesAdapter.showShareIcon=false;
        reportedImagesAdapter.showDeleteIcon=false;
        reportedImagesAdapter.notifyDataSetChanged();
    }
}
