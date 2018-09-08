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
import com.ballidaku.etracking.adapters.ReportedOffenceAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.OffenceDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sharanpalsingh on 27/11/17.
 */

public class ReportedOffenceFragment extends Fragment
{

    String TAG = ReportedOffenceFragment.class.getSimpleName();

    View view = null;

    Context context;

    RecyclerView recyclerViewReportedImages;
    public ReportedOffenceAdapter reportedOffenceAdapter;

    ArrayList<OffenceDataModel> arrayListImages=new ArrayList<>();


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

        ((MainActivity)getActivity()).toolbar.setTitle("Reported Offence");
        ((MainActivity)getActivity()).refreshMenu(3);
    }

    private void setUpViews()
    {
        recyclerViewReportedImages=(RecyclerView)view.findViewById(R.id.recyclerViewReportedImages);

        RecyclerView.LayoutManager layoutManager =new LinearLayoutManager(context);
        recyclerViewReportedImages.setLayoutManager(layoutManager);
        recyclerViewReportedImages.setItemAnimator(new DefaultItemAnimator());

        reportedOffenceAdapter=new ReportedOffenceAdapter(context,arrayListImages);
        recyclerViewReportedImages.setAdapter(reportedOffenceAdapter);
    }


    private void getFirebaseImages()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getReportedOffenceByAdmin(new Interfaces.ReportedOffenceListener()
        {
            @Override
            public void callback(ArrayList<OffenceDataModel> arrayList)
            {

                Collections.sort(arrayList, new Comparator<OffenceDataModel>() {
                    public int compare(OffenceDataModel o1, OffenceDataModel o2) {
                        if (o1.getReportedTime() == null || o2.getReportedTime() == null)
                            return 0;
                        return CommonMethods.getInstance().stringToDate(o2.getReportedTime()).compareTo(CommonMethods.getInstance().stringToDate(o1.getReportedTime()));
                    }
                });

               // Collections.reverse(arrayList);

                reportedOffenceAdapter.refresh(arrayList);

                CommonDialogs.getInstance().dialog.dismiss();
            }
        });

    }


    public void showShareOption()
    {
        reportedOffenceAdapter.showShareIcon=true;
        reportedOffenceAdapter.showDeleteIcon=false;
        reportedOffenceAdapter.notifyDataSetChanged();
    }

    public void showDeleteOption()
    {
        reportedOffenceAdapter.showShareIcon=false;
        reportedOffenceAdapter.showDeleteIcon=true;
        reportedOffenceAdapter.notifyDataSetChanged();
    }

    public void refreshMenus()
    {
        reportedOffenceAdapter.showShareIcon=false;
        reportedOffenceAdapter.showDeleteIcon=false;
        reportedOffenceAdapter.notifyDataSetChanged();
    }
}