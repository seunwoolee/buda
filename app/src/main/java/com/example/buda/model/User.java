package com.example.buda.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class User extends RealmObject {
    public String key;
    public String username;
    @SerializedName("first_name")
    public String name;
}
