package com.ballidaku.etracking.commonClasses;

import com.ballidaku.etracking.dataModels.GuardDataModel;
import com.ballidaku.etracking.dataModels.BeatLocationModel;
import com.ballidaku.etracking.dataModels.ImageDataModel;
import com.ballidaku.etracking.dataModels.NotificationModel;
import com.ballidaku.etracking.dataModels.OffenceDataModel;
import com.ballidaku.etracking.dataModels.VideoDataModel;

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

    public interface ReportedVideosListener {
        public void callback(ArrayList<VideoDataModel> arrayList);
    }

    public interface ReportedOffenceListener {
        public void callback(ArrayList<OffenceDataModel> arrayList);
    }

    public interface GetAllBeatListener {
        public void callback(ArrayList<GuardDataModel> arrayList);
    }


    public interface GetBeatDateLocationListener {
        public void callback(ArrayList<BeatLocationModel> arrayList);
    }

    public interface GetNotificationListener {
        public void callback(ArrayList<NotificationModel> arrayList);
    }

    public interface OnMessageUpdateListener {
        public void onUpdated();
    }

    public interface DeleteOffenceListener {
        public void onSuccess();
        public void onUnSuccess();
    }

    public interface DeleteReportedImageListener {
        public void onSuccess();
        public void onUnSuccess();
    }

    public interface DrawPathListener {
        public void drawPath(ArrayList<BeatLocationModel.DateLocation>  arrayList);
    }

    public interface CompressionInterface {
     public void onCompressionCompleted(String path);
    }

    public interface onImageUpload
    {
        void imagePathAfterUpload(String path);
    }

    public interface OnDownloadCompleted
    {
        void onCompleted();
    }

}
