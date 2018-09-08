package com.ballidaku.etracking.dataModels;

/**
 * Created by sharanpalsingh on 17/09/17.
 */

public class ImageDataModel
{

    String image_path;
    String reported_time;
    String reportedBy;
    String beatID;
    String reportedImageID;

    public String getReportedBy()
    {
        return reportedBy;
    }

    public ImageDataModel()
    {
    }

    public ImageDataModel(String image_path, String reported_time,String reportedBy,
                          String beatID,
                          String reportedImageID)
    {
        this.image_path=image_path;
        this.reported_time=reported_time;
        this.reportedBy=reportedBy;
        this.beatID=beatID;
        this.reportedImageID=reportedImageID;
    }

    public String getImagePath()
    {
        return image_path;
    }


    public String getReportedTime()
    {
        return reported_time;
    }


    public String getBeatID()
    {
        return beatID;
    }

    public String getReportedImageID()
    {
        return reportedImageID;
    }
}
