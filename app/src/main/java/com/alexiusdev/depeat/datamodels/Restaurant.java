package com.alexiusdev.depeat.datamodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String address;
    private String imageUrl;
    private String id;
    private String phoneNum;
    private int rating;
    private double minOrder;
    private boolean isFavourite;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String imageUrl, String phoneNum, int rating, double minOrder) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.minOrder = minOrder;
        this.phoneNum = phoneNum;
    }

    public Restaurant(JSONObject jsonRestaurant) throws JSONException {
        id = jsonRestaurant.getString("id");
        name = jsonRestaurant.getString("name");
        address = jsonRestaurant.getString("address");
        imageUrl = jsonRestaurant.getString("imageUrl");
        phoneNum = jsonRestaurant.getString("phoneNum");
        rating = Integer.parseInt(jsonRestaurant.getString("rating"));
        minOrder = Double.parseDouble(jsonRestaurant.getString("minOrder"));
    }

    public Restaurant(String id, String name, String address, String imageUrl, double minOrder) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.minOrder = minOrder;
    }

    public String getName() {
        return name;
    }

    public Restaurant setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Restaurant setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Restaurant setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getId() {
        return id;
    }

    public Restaurant setId(String id) {
        this.id = id;
        return this;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public Restaurant setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        return this;
    }

    public Float getRatingFloat() {
        return rating / 10F;
    }

    public int getRatingInt() {
        return rating;
    }

    public Restaurant setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public double getMinOrder() {
        return minOrder;
    }

    public Restaurant setMinOrder(double minOrder) {
        this.minOrder = minOrder;
        return this;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public Restaurant setFavourite(boolean favourite) {
        isFavourite = favourite;
        return this;
    }
}
