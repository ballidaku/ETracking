package com.ballidaku.etracking.dataModels;

public class VideoDataModel
{
    private String videoPath="";
    private String videoName="";
    private String videoThumbnailPath="";
    private String reportedTime="";
    private String reportedBy="";
    private String beatID="";
    private String reportedVideoID="";

    public String getVideoName()
    {
        return videoName;
    }

    public void setVideoName(String videoName)
    {
        this.videoName = videoName;
    }

    public String getVideoThumbnailPath()
    {
        return videoThumbnailPath;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath)
    {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    public String getVideoPath()
    {
        return videoPath;
    }

    public void setVideoPath(String videoPath)
    {
        this.videoPath = videoPath;
    }

    public String getReportedTime()
    {
        return reportedTime;
    }

    public void setReportedTime(String reportedTime)
    {
        this.reportedTime = reportedTime;
    }

    public String getReportedBy()
    {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy)
    {
        this.reportedBy = reportedBy;
    }

    public String getBeatID()
    {
        return beatID;
    }

    public void setBeatID(String beatID)
    {
        this.beatID = beatID;
    }

    public String getReportedVideoID()
    {
        return reportedVideoID;
    }

    public void setReportedVideoID(String reportedVideoID)
    {
        this.reportedVideoID = reportedVideoID;
    }
}
