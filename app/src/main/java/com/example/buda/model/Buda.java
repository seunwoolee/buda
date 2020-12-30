package com.example.buda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Buda implements Parcelable {
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("body")
    public String body;
    @SerializedName("photo")
    public String photo;
    @SerializedName("created")
    public Date created;

    protected Buda(Parcel in) {
        id = in.readInt();
        title = in.readString();
        body = in.readString();
        photo = in.readString();
    }

    public static final Creator<Buda> CREATOR = new Creator<Buda>() {
        @Override
        public Buda createFromParcel(Parcel in) {
            return new Buda(in);
        }

        @Override
        public Buda[] newArray(int size) {
            return new Buda[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(photo);
    }
}
