package com.alexiusdev.depeat.datamodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Restaurant {
    private String name, address, description, imageUrl;
    private int rating;
    private double minOrder;
    private ArrayList<Product> products;

    public Restaurant(String name, String address, String description, String imageUrl, int rating, float minOrder/*, ArrayList<Product> products*/) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.minOrder = minOrder;
        //this.products = products;
        products = new ArrayList<>();
    }

    public Restaurant(JSONObject jsonRestaurant) throws JSONException {
        name = jsonRestaurant.getString("name");
        address = jsonRestaurant.getString("address");
        //description =
        imageUrl = jsonRestaurant.getString("image_url");
        //rating = rating;
        minOrder = Double.parseDouble(jsonRestaurant.getString("min_order"));
        //products = products;
    }

    public void setMinOrder(float minOrder) {
        this.minOrder = minOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getRatingFloat() {
        return rating/10F;
    }

    public int getRatingInt() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMinOrder() {
        return minOrder;
    }

    public int getRating() {
        return rating;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
