package com.apfinal.model;

import java.util.ArrayList;
import java.util.List;

/**
 * یک wrapper ساده برای لیست محصولات
 */
public class Catalog {
    private List<Product> products = new ArrayList<>();

    public Catalog() {}

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }

    public void addProduct(Product p) { products.add(p); }
    public void removeProductById(String id) { products.removeIf(p -> p.getId().equals(id)); }

    public Product findById(String id) {
        for (Product p : products) if (p.getId().equals(id)) return p;
        return null;
    }
}
