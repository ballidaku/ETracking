package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.GuardsSelectionAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.SimpleDividerItemDecoration;
import com.ballidaku.etracking.dataModels.BeatDataModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 25/09/17.
 */

public class SearchGuardByCategoryFragment extends Fragment implements View.OnClickListener
{

    String TAG = SearchGuardByCategoryFragment.class.getSimpleName();

    View view = null;

    Context context;

    TextView textViewHeadquater;

    Spinner spinnerUserType;
    Spinner spinnerRange;
    Spinner spinnerBlock;
    Spinner spinnerBeat;


    RecyclerView recycleViewBeat;
    GuardsSelectionAdapter beatSelectionAdapter;

    ArrayList<BeatDataModel> arrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_search_guard_by_category, container, false);

            context = getActivity();

            setUpViews();

        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity)getActivity()).toolbar.setTitle("Track Guard");
    }

    private void setUpViews()
    {
        textViewHeadquater = (TextView) view.findViewById(R.id.textViewHeadquater);

        spinnerUserType = (Spinner) view.findViewById(R.id.spinnerUserType);
        spinnerRange = (Spinner) view.findViewById(R.id.spinnerRange);
        spinnerBlock = (Spinner) view.findViewById(R.id.spinnerBlock);
        spinnerBeat = (Spinner) view.findViewById(R.id.spinnerBeat);


        view.findViewById(R.id.textViewSearch).setOnClickListener(this);


        ArrayAdapter<String> rangeAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_view, getResources().getStringArray(R.array.rangeName));
        spinnerRange.setAdapter(rangeAdapter);


        spinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0, 0);
                TextView textViewSpinner = (TextView) view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String rangeName = spinnerRange.getSelectedItem().toString();

                String[] range;
                if (rangeName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    range = getResources().getStringArray(R.array.nagrotaBlock);
                }
                else
                {
                    range = getResources().getStringArray(R.array.dhametaBlock);
                }

                ArrayAdapter<String> blockAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_view, range);
                spinnerBlock.setAdapter(blockAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        spinnerBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0, 0);
                TextView textViewSpinner = (TextView) view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String blockName = spinnerBlock.getSelectedItem().toString();

                String[] beat;
                if (blockName.equals(MyConstant.DEHRA))
                {
                    beat = getResources().getStringArray(R.array.nagrotaDehraBeat);
                }
                else if (blockName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    beat = getResources().getStringArray(R.array.nagrotaNagrotaBeat);
                }
                else if (blockName.equals(MyConstant.DHAMETA))
                {
                    beat = getResources().getStringArray(R.array.dhametaDhametaBeat);
                }
                else
                {
                    beat = getResources().getStringArray(R.array.dhametaSansarpurBeat);
                }

                ArrayAdapter<String> beatAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item_view, beat);
                spinnerBeat.setAdapter(beatAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        spinnerBeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0, 0);
                TextView textViewSpinner = (TextView) view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String beatName = spinnerBeat.getSelectedItem().toString();

                String headquater = "";
                if (beatName.equals(MyConstant.DEHRA))
                {
                    headquater = MyConstant.DEHRA;
                }
                else if (beatName.equals(MyConstant.BHATOLI_PHAKORIAN))
                {
                    headquater = MyConstant.BHATOLI;
                }
                else if (beatName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    headquater = MyConstant.NAGROTA_SURIAN;
                }
                else if (beatName.equals(MyConstant.JAWALI))
                {
                    headquater = MyConstant.LUV;
                }
                else if (beatName.equals(MyConstant.DHAMETA))
                {
                    headquater = MyConstant.DHAMETA;
                }
                else if (beatName.equals(MyConstant.PONG_DAM))
                {
                    headquater = MyConstant.KHATIYAR;
                }
                else if (beatName.equals(MyConstant.SANSARPUR_TERRACE))
                {
                    headquater = MyConstant.SANSARPUR_TERRACE;
                }
                else if (beatName.equals(MyConstant.DADASIBA))
                {
                    headquater = MyConstant.DADASIBA;
                }


                textViewHeadquater.setText(headquater);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        recycleViewBeat = (RecyclerView) view.findViewById(R.id.recycleViewBeat);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewBeat.setLayoutManager(layoutManager);
        recycleViewBeat.setItemAnimator(new DefaultItemAnimator());
        recycleViewBeat.addItemDecoration(new SimpleDividerItemDecoration(context));

        beatSelectionAdapter = new GuardsSelectionAdapter(this,context, arrayList);

        recycleViewBeat.setAdapter(beatSelectionAdapter);
    }


    void refreshAdapter(ArrayList<BeatDataModel> arrayList)
    {
        CommonDialogs.getInstance().dialog.dismiss();

        this.arrayList = arrayList;
        beatSelectionAdapter.refresh(arrayList);
        beatSelectionAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.textViewSearch:

                String range=(String)spinnerRange.getSelectedItem();
                String block=(String)spinnerBlock.getSelectedItem();
                String beat=(String)spinnerBeat.getSelectedItem();
                CommonDialogs.getInstance().progressDialog(context);
                MyFirebase.getInstance().getBeatsByCategory(range,block,beat,new Interfaces.GetAllBeatListener()
                {
                    @Override
                    public void callback(ArrayList<BeatDataModel> arrayList)
                    {
                        refreshAdapter(arrayList);
                    }
                });

                /*MyFirebase.getInstance().getAllBeats(new Interfaces.GetAllBeatListener()
                {
                    @Override
                    public void callback(ArrayList<BeatDataModel> arrayList)
                    {
                        refreshAdapter(arrayList);
                    }
                });*/

                break;

        }
    }
}
