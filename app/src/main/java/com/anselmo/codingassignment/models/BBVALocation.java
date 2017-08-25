package com.anselmo.codingassignment.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chemo on 8/24/17.
 */
public class BBVALocation implements Parcelable {
    private String formatted_address;
    private String name;
    private String icon;
    private String rating;
    private String lat;
    private String lng;
    private int open_now;
    private ArrayList<String> types;

    public BBVALocation() {}

    public BBVALocation(String formatted_address, String name, String icon, String rating, String lat, String lng, int open_now, ArrayList<String> types) {
        this.formatted_address = formatted_address;
        this.name = name;
        this.icon = icon;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.open_now = open_now;
        this.types = types;
    }

    public BBVALocation(Parcel in){
        this.formatted_address = in.readString();
        this.name =  in.readString();
        this.icon =  in.readString();
        this.rating =  in.readString();
        this.lat =  in.readString();
        this.lng =  in.readString();
        this.open_now =  in.readInt();
        this.types =  in.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.formatted_address);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeString(this.rating);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeInt(this.open_now);
        dest.writeList(this.types);
    }

    @Override
    public String toString() {
        return "BBVALocation{" +
                "formatted_address='" + formatted_address + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", rating='" + rating + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", open_now=" + open_now +
                ", types=" + types +
                '}';
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BBVALocation createFromParcel(Parcel in) {
            return new BBVALocation(in);
        }

        public BBVALocation[] newArray(int size) {
            return new BBVALocation[size];
        }
    };


    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getOpen_now() {
        return open_now;
    }

    public void setOpen_now(int open_now) {
        this.open_now = open_now;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }
}