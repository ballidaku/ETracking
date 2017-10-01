package com.ballidaku.etracking.dataModels;

/**
 * Created by sharanpalsingh on 17/09/17.
 */

public class ImageDataModel
{

    String image_path;
    String reported_time;

    public ImageDataModel()
    {

    }

    public ImageDataModel(String image_path, String reported_time)
    {
        this.image_path=image_path;
        this.reported_time=reported_time;
    }

    public String getImagePath()
    {
        return image_path;
    }

    public void setImagePath(String imagePath)
    {
        this.image_path = imagePath;
    }

    public String getReportedTime()
    {
        return reported_time;
    }

    public void setReportedTime(String reportedTime)
    {
        this.reported_time = reportedTime;
    }
}
