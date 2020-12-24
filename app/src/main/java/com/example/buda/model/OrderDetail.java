package com.example.buda.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetail {
    @SerializedName("orderNumber")
    public String orderNumber;
    @SerializedName("name")
    public String name;
    @SerializedName("day")
    public String day;
    @SerializedName("productName")
    public String productName;
    @SerializedName("count")
    public int count;
    @SerializedName("price")
    public int price;
}
