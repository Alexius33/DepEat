package com.alexiusdev.depeat.datamodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product implements Serializable {
    private String name, ingredients, imageUrl, id;
    private int quantity;
    private double price;

    public Product() {
    }

    public Product(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Product(JSONObject jsonProduct) throws JSONException {
        this.id = jsonProduct.getString("id");
        this.name = jsonProduct.getString("name");
        this.ingredients = jsonProduct.getString("ingredients");
        this.imageUrl = jsonProduct.getString("imageUrl");
        this.quantity = 0;
        this.price = jsonProduct.getDouble("price");
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getIngredients() {
        return ingredients;
    }

    public Product setIngredients(String ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Product setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getId() {
        return id;
    }

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }

    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (quantity > 0) {
            this.quantity--;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }

    public JSONObject toJSONObject() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("quantity", String.valueOf(quantity));
        return new JSONObject(map);
    }
}
