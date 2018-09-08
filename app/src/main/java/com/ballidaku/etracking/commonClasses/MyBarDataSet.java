package com.ballidaku.etracking.commonClasses;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;


public class MyBarDataSet extends BarDataSet {

    public MyBarDataSet(List<BarEntry> yVals, String label)
    {
        super(yVals, label);
    }


    @Override
    public int getColor(int index)
    {
        if(getEntryForXIndex(index).getVal() > 85)
            return mColors.get(0);
        else if(getEntryForXIndex(index).getVal() >= 50 & getEntryForXIndex(index).getVal() < 85)
            return mColors.get(1);
        else if(getEntryForXIndex(index).getVal() >=0 & getEntryForXIndex(index).getVal() < 50)
            return mColors.get(2);
        else
            return 0;
    }
}
