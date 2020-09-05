package com.jignesh.streety.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Places implements Parcelable {
    public static final int PLACES_CARD = 1;

    @SerializedName("place_type")
    private String type;//holds the view type constant
    @SerializedName("place_photo")
    // TODO: 13-Mar-19 Change int to String
    private String data;    // variable is used to store the respective data that we’ll be populating.
                         // Ideally it’ll contain a drawable or raw type resource.
    @SerializedName("place_name")
    private String text; //contains the String that’ll be displayed in the TextView.
    private boolean isChecked = false;
    @SerializedName("place_lat")
    private double lat;
    @SerializedName("place_lon")
    private double lon;

    @SerializedName("place_address")
    private String address;

    @SerializedName("place_desc")
    private String desc;

    public Places(String type, String text, String data, boolean isChecked, double lat, double lon) {
        this.type = type;
        this.data = data;
        this.text = text;
        this.isChecked = isChecked;
        this.lat=lat;
        this.lon=lon;
    }

    protected Places(Parcel in) {
        type = in.readString();
        data = in.readString();
        text = in.readString();
        isChecked = in.readByte() != 0;
        lat = in.readDouble();
        lon = in.readDouble();
        address = in.readString();
        desc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(data);
        dest.writeString(text);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(address);
        dest.writeString(desc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Places> CREATOR = new Creator<Places>() {
        @Override
        public Places createFromParcel(Parcel in) {
            return new Places(in);
        }

        @Override
        public Places[] newArray(int size) {
            return new Places[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public double getLat() { return lat;}

    public void setLat(double lat) { this.lat=lat;}

    public double getLon() { return lon;}

    public void setLon(double lon) { this.lon=lon;}

    public void setAddress(String add)
    {
        address=add;
    }
    public String getAddress()
    {return address;}

    public void setDesc(String desc)
    {
this.desc=desc;
    }
    public String getDesc()
    {return desc;}


}
