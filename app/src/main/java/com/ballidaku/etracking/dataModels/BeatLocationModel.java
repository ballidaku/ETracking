package com.ballidaku.etracking.dataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 22/09/17.
 */

public class BeatLocationModel
{
    String date;
    ArrayList<DateLocation> dateLocations;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public ArrayList<DateLocation> getDateLocations()
    {
        return dateLocations;
    }

    public void setDateLocations(ArrayList<DateLocation> dateLocations)
    {
        this.dateLocations = dateLocations;
    }



    public static class DateLocation implements Parcelable
    {
        String location;
        String time;

        public DateLocation()
        {
        }

        public DateLocation(Parcel in)
        {
            location = in.readString();
            time = in.readString();
        }

        public static final Creator<DateLocation> CREATOR = new Creator<DateLocation>()
        {
            @Override
            public DateLocation createFromParcel(Parcel in)
            {
                return new DateLocation(in);
            }

            @Override
            public DateLocation[] newArray(int size)
            {
                return new DateLocation[size];
            }
        };

        public String getLocation()
        {
            return location;
        }

        public void setLocation(String location)
        {
            this.location = location;
        }

        public String getTime()
        {
            return time;
        }

        public void setTime(String time)
        {
            this.time = time;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(location);
            dest.writeString(time);
        }
    }
}
