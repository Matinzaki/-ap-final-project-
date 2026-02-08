package com.apfinal.model;

import java.util.ArrayList;
import java.util.List;
public class Catalog {
    // لیست محصولات موجود در کاتالوگ
    private List<Product> products = new ArrayList<>();
    public Catalog() {}

    // دریافت لیست کامل محصولات
    public List<Product> getProducts() { return products; }
    
    // تنظیم لیست محصولات
    public void setProducts(List<Product> products) { this.products = products; }

    // اضافه کردن یک محصول جدید به کاتالوگ
    public void addProduct(Product p) { products.add(p); }
    
    // حذف محصول 
    public void removeProductById(String id) { products.removeIf(p -> p.getId().equals(id)); }

    // جستجوی محصول 
    public Product findById(String id) {
        for (Product p : products) if (p.getId().equals(id)) return p;
        return null;
    }
}
