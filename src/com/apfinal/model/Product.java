package com.apfinal.model;

import java.util.UUID;

/**
 * مدل محصول: نام، دسته، قیمت (ریال)، استوک، توضیح، مسیر تصویر، امتیاز
 */
public class Product {
    // فیلدهای کلاس - اطلاعات محصول
    private String id;
    private String name;
    private String category;
    private long price;
    private int stock;
    private String description;
    private String imagePath;
    private double rating;
    private int ratingCount;
    private boolean availableForClient;

    public Product() {}

    //  پارامترهای اصلی
    public Product(String name, String category, long price, int stock, String description, String imagePath, boolean availableForClient) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.imagePath = imagePath;
        this.availableForClient = availableForClient;
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getPrice() { return price; }
    public void setPrice(long price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public boolean isAvailableForClient() { return availableForClient; }
    public void setAvailableForClient(boolean availableForClient) { this.availableForClient = availableForClient; }

    // متد اضافه کردن امتیاز جدید
    public void addRating(double r) {
        if (r < 0) r = 0;
        double total = rating * ratingCount;
        ratingCount++;
        total += r;
        rating = total / ratingCount;
    }

    // متد نمایش محصول به صورت متنی
    @Override
    public String toString() {
        return name + " | " + category + " | " + price + " ریال | stock: " + stock;
    }
}
