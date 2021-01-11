package com.example.buda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Comment implements Parcelable {
    @SerializedName("id")
    public int id;
    @SerializedName("username")
    public String username;
    @SerializedName("name")
    public String name;
    @SerializedName("comment")
    public String comment;
    @SerializedName("created")
    public Date created;

    protected Comment(Parcel in) {
        id = in.readInt();
        comment = in.readString();
        username = in.readString();
        name = in.readString();
        long tmpDate = in.readLong();
        this.created = tmpDate == -1 ? null : new Date(tmpDate);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(comment);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeLong(created != null ? created.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
