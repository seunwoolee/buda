package com.example.buda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Board {
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("body")
    public String body;
    @SerializedName("created")
    public Date created;
    @SerializedName("board_comments")
    public ArrayList<Comment> comments;

}
