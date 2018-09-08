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
import com.ballidaku.etracking.adapters.SearchGuardByNameAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.SimpleDividerItemDecoration;
import com.ballidaku.etracking.dataModels.GuardDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 02/10/17.
 */

public class SearchGuardByNameFragment extends Fragment
{

    String TAG = SearchGuardByNameFragment.class.getSimpleName();

    View view = null;

    Context context;

    RecyclerView recycleViewGuard;

    SearchGuardByNameAdapter searchGuardByNameAdapter;

    ArrayList<GuardDataModel> arrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_search_guard_by_name, container, false);

            context = getActivity();

            setUpViews();

        }

        return view;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity)getActivity()).toolbar.setTitle("Search Guard");
        ((MainActivity)getActivity()).refreshMenu(1);
    }

    private void setUpViews()
    {
        recycleViewGuard = (RecyclerView) view.findViewById(R.id.recycleViewGuard);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewGuard.setLayoutManager(layoutManager);
        recycleViewGuard.setItemAnimator(new DefaultItemAnimator());
        recycleViewGuard.addItemDecoration(new SimpleDividerItemDecoration(context));

        searchGuardByNameAdapter = new SearchGuardByNameAdapter(context, arrayList);

        recycleViewGuard.setAdapter(searchGuardByNameAdapter);


        getAllGuards();
    }


    void getAllGuards()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getAllGuards(new Interfaces.GetAllBeatListener()
        {
            @Override
            public void callback(ArrayList<GuardDataModel> arrayList)
            {
                refreshAdapter(arrayList);
            }
        });
    }


    void refreshAdapter(ArrayList<GuardDataModel> arrayList)
    {
        CommonDialogs.getInstance().dialog.dismiss();

        this.arrayList = arrayList;
        searchGuardByNameAdapter.refresh(arrayList);

    }

    public void filterList(String name)
    {
        ArrayList<GuardDataModel> guardListLocal = new ArrayList<>();

        for (int i = 0; i < arrayList.size(); i++)
        {
            if (arrayList.get(i).getBeatName().toLowerCase().contains(name.toLowerCase()))
            {
                guardListLocal.add(arrayList.get(i));
            }
        }

        if (name.trim().length() > 0)
        {
            searchGuardByNameAdapter.refresh(guardListLocal);
        }
        else
        {
            searchGuardByNameAdapter.refresh(arrayList);
        }
    }


}
