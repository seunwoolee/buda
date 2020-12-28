package com.example.buda.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Buda {
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
}
