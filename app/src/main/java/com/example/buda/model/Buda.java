package com.example.buda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
    @SerializedName("buda_comments")
    public ArrayList<Comment> comments;

    protected Buda(Parcel in) {
        id = in.readInt();
        title = in.readString();
        body = in.readString();
        photo = in.readString();
        comments = in.createTypedArrayList(Comment.CREATOR);
        long tmpDate = in.readLong();
        this.created = tmpDate == -1 ? null : new Date(tmpDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(photo);
        dest.writeTypedList(comments);
        dest.writeLong(created != null ? created.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
