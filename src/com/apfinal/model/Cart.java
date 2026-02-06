package com.apfinal.model;

import java.util.HashMap;
import java.util.Map;

/**
 * مدل سبد خرید
 */
public class Cart {
    private Map<String, Integer> items = new HashMap<>();

    public Map<String, Integer> getItems() { return items; }

    public void addItem(String productId, int qty) {
        if (qty <= 0) return;
        items.put(productId, items.getOrDefault(productId, 0) + qty);
    }

    public void removeItem(String productId, int qty) {
        if (!items.containsKey(productId) || qty <= 0) return;
        int cur = items.get(productId);
        int next = cur - qty;
        if (next <= 0) items.remove(productId);
        else items.put(productId, next);
    }

    public void setItem(String productId, int qty) {
        if (qty <= 0) items.remove(productId);
        else items.put(productId, qty);
    }

    public void clear() { items.clear(); }
}
