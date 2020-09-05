package com.jignesh.streety.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trips implements Parcelable {
    @SerializedName("trip_name")
    String trips_name;
@SerializedName("trip_date")
    String trip_date;

    public Trips(String text,String date)
    {
        trips_name=text;
        trip_date=date;
    }
    protected Trips(Parcel in) {

        trips_name = in.readString();
        trip_date=in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trips_name);
        dest.writeString(trip_date);


    }
    public static final Creator<Trips> CREATOR = new Creator<Trips>() {
        @Override
        public Trips createFromParcel(Parcel in) {
            return new Trips(in);
        }

        @Override
        public Trips[] newArray(int size) {
            return new Trips[size];
        }
    };
    public String getText() {
        return trips_name;
    }

    public void setText(String text) {
        trips_name = text;
    }

    public String getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }
}
