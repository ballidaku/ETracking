package com.ballidaku.etracking.dataModels;

/**
 * Created by sharanpalsingh on 26/11/17.
 */

public class OffenceDataModel
{

    String image_path;
    String reported_time;
    String reportedBy;
    String description;
    String speciesName;
    String location;
    String beatID;
    String offenceID;

    public OffenceDataModel(String image_path, String reported_time,String reportedBy,
                            String description, String speciesName,String location,
                            String beatID,
                            String offenceID)
    {
        this.image_path=image_path;
        this.reported_time=reported_time;
        this.reportedBy=reportedBy;
        this.description=description;
        this.speciesName=speciesName;
        this.location=location;
        this.beatID=beatID;
        this.offenceID=offenceID;
    }

    public String getImagePath()
    {
        return image_path;
    }

    public String getReportedTime()
    {
        return reported_time;
    }

    public String getReportedBy()
    {
        return reportedBy;
    }

    public String getDescription()
    {
        return description;
    }

    public String getSpeciesName()
    {
        return speciesName;
    }

    public String getLocation()
    {
        return location;
    }

    public String getBeatID()
    {
        return beatID;
    }

    public String getOffenceID()
    {
        return offenceID;
    }
}
