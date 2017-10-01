package com.ballidaku.etracking.commonClasses;

import com.ballidaku.etracking.dataModels.BeatDataModel;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.dataModels.ImageDataModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sharanpalsingh on 20/09/17.
 */

public class Interfaces
{

    public interface GetBeatsListener {
        public void callback(ArrayList<HashMap<String,Object>> result);
    }


    public interface ForgotPasswordListener {
        public void callback(String result);
    }

    public interface MyListener {
        public void callback(boolean result);
    }

    public interface ReportedImagesListener {
        public void callback(ArrayList<ImageDataModel> arrayList);
    }

    public interface GetAllBeatListener {
        public void callback(ArrayList<BeatDataModel> arrayList);
    }


    public interface GetBeatDateLocationListener {
        public void callback(ArrayList<BeatLocationModel> arrayList);
    }


    public interface DrawPathListener {
        public void drawPath(ArrayList<BeatLocationModel.DateLocation>  arrayList);
    }

}
