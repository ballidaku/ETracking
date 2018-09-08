package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MyBarDataSet;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by sharanpalsingh on 08/10/17.
 */

public class GraphFragment extends Fragment implements View.OnClickListener
{

    String TAG = GraphFragment.class.getSimpleName();

    View view = null;

    Context context;

    BarChart barChart;
    BarData barData;
    MyBarDataSet barDataSet;

    public String beatName;

    ImageView imageViewPrevious;
    ImageView imageViewForward;

    TextView textViewMonthYear;


    ArrayList<BeatLocationModel> arrayList = new ArrayList<>();

    Calendar calendar = Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_graph, container, false);

            context = getActivity();

            setUpViews();

            calendar.setTime(new Date());

        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity) getActivity()).toolbar.setTitle(beatName + " Graph");
    }

    private void setUpViews()
    {

        imageViewPrevious = (ImageView) view.findViewById(R.id.imageViewPrevious);
        imageViewForward = (ImageView) view.findViewById(R.id.imageViewForward);

        imageViewPrevious.setOnClickListener(this);
        imageViewForward.setOnClickListener(this);

        textViewMonthYear = (TextView) view.findViewById(R.id.textViewMonthYear);



        beatName = getArguments().getString(MyConstant.BEAT_NAME);
        String listAsString = getArguments().getString(MyConstant.LIST);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<BeatLocationModel>>(){}.getType();
        arrayList= gson.fromJson(listAsString, type);

        //arrayList = getArguments().getParcelableArrayList(MyConstant.LIST);

     /*   Collections.sort(arrayList, new Comparator<BeatLocationModel>() {
            public int compare(BeatLocationModel o1, BeatLocationModel o2) {
                if (o1.getDate() == null || o2.getDate() == null)
                    return 0;
                return CommonMethods.getInstance().stringToDate(o1.getDate()).compareTo(CommonMethods.getInstance().stringToDate(o2.getDate()));
            }
        });*/


        barChart = (BarChart) view.findViewById(R.id.barChart);

        barChart.setDescription("");



        textViewMonthYear.setText(CommonMethods.getInstance().getCurrentMonthYear());
        ArrayList<String> monthDatesList = CommonMethods.getInstance().getMonthDates(calendar);
        refreshData(monthDatesList, getBarEntryNew(monthDatesList));


    }

    public void refreshData(ArrayList<String> barEntryLabelsLoacal, ArrayList<BarEntry> barEntryLocal)
    {

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinValue(0f);
        yAxis.setAxisMaxValue(100f);
        yAxis.setSpaceBottom(10);


        YAxis yAxis1 = barChart.getAxisRight();
        yAxis1.setAxisMinValue(0f);
        yAxis1.setAxisMaxValue(100f);
        yAxis1.setSpaceBottom(10);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setAxisMaxValue(100f);

        barDataSet = new MyBarDataSet(barEntryLocal, "KM");
        barDataSet.setColors(new int[]{ContextCompat.getColor(context, R.color.colorGreenDark),
                ContextCompat.getColor(context, R.color.colorGreen),
                ContextCompat.getColor(context, R.color.colorRed)});

        barData = new BarData(barEntryLabelsLoacal, barDataSet);

        barChart.setData(barData);
        barChart.setDrawValueAboveBar(false);
        barChart.invalidate();
    }


//    public ArrayList<BarEntry> getBarEntry()
//    {
//        ArrayList<BarEntry> barEntryList = new ArrayList<>();
//
//        for (int i = 0; i < arrayList.size(); i++)
//        {
//
//            float value = 0f;
//
//            int size = arrayList.get(i).getDateLocations().size();
//            if (size > 0)
//            {
//                String firstLatLong = arrayList.get(i).getDateLocations().get(0).getLocation();
//                String lastLatLong = arrayList.get(i).getDateLocations().get(size - 1).getLocation();
//                value = Float.parseFloat(CommonMethods.getInstance().distanceBetweenLatLong(firstLatLong, lastLatLong).replace("Km", "").trim());
//            }
//
//            barEntryList.add(new BarEntry(value, i));
//        }
//
//        return barEntryList;
//    }

    public ArrayList<BarEntry> getBarEntryNew(ArrayList<String> monthDatesList)
    {
        ArrayList<BarEntry> barEntryList = new ArrayList<>();

        HashMap<String,ArrayList<ArrayList<BeatLocationModel.DateLocation>>> hashMap=new HashMap<>();

        for (int i = 0; i < arrayList.size(); i++)
        {
            String date = arrayList.get(i).getDate();
            boolean b= CommonMethods.getInstance().checkDateLiesInPresentMonth(calendar,date);
            if(b)
            {
               hashMap.put(arrayList.get(i).getDate(),arrayList.get(i).getDateLocations());
            }
        }


        for (int i = 0; i < monthDatesList.size(); i++)
        {

            float value = 0f;

            String date=monthDatesList.get(i)+new SimpleDateFormat("-MM-yyyy", Locale.US).format(calendar.getTime());
            Log.e(TAG,"date "+date);
            if(hashMap.containsKey(date))
            {
                int size = hashMap.get(date).size();
                if (size > 0)
                {
                    ArrayList<ArrayList<BeatLocationModel.DateLocation>> arrayLists=  hashMap.get(date);
                    for (int j = 0; j < arrayLists.size(); j++)
                    {
                        ArrayList<BeatLocationModel.DateLocation> dateLocationList=arrayLists.get(j);
                        String firstLatLong = dateLocationList.get(0).getLocation();
                        String lastLatLong = dateLocationList.get(dateLocationList.size() - 1).getLocation();
                        value += CommonMethods.getInstance().distanceBetweenLatLong(firstLatLong, lastLatLong);
                    }
                }
            }

            barEntryList.add(new BarEntry(value, i));
        }

        return barEntryList;
    }

   /* public ArrayList<String> getBarEntryLabels()
    {
        ArrayList<String> barEntryLabelList = new ArrayList<>();


        for (int i = 0; i < arrayList.size(); i++)
        {
            //Log.e(TAG,arrayList.get(i).getDate());
            barEntryLabelList.add(arrayList.get(i).getDate());
        }

        return barEntryLabelList;
    }*/


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageViewPrevious:

                previous();

                break;

            case R.id.imageViewForward:

                next();

                break;
        }
    }


    public void previous()
    {
        textViewMonthYear.setText(CommonMethods.getInstance().getPreviousMonthYear(calendar));
        ArrayList<String> monthDatesList = CommonMethods.getInstance().getMonthDates(calendar);
        refreshData(monthDatesList, getBarEntryNew(monthDatesList));
    }

    public void next()
    {
        textViewMonthYear.setText(CommonMethods.getInstance().getNextMonthYear(calendar));
        ArrayList<String> monthDatesList = CommonMethods.getInstance().getMonthDates(calendar);
        refreshData(monthDatesList, getBarEntryNew(monthDatesList));
    }
}
