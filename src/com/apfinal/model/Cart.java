package com.apfinal.model;

import java.util.HashMap;
import java.util.Map;

/**
 * مدل سبد خرید
 */
public class Cart {
    // ذخیره محصولات و تعداد آنها در سبد خرید
    private Map<String, Integer> items = new HashMap<>();

    // دریافت تمام آیتم‌های سبد خرید
    public Map<String, Integer> getItems() { return items; }

    // اضافه کردن محصول به سبد خرید
    public void addItem(String productId, int qty) {
        if (qty <= 0) return;
        items.put(productId, items.getOrDefault(productId, 0) + qty);
    }

    // حذف تعداد مشخصی از یک محصول
    public void removeItem(String productId, int qty) {
        if (!items.containsKey(productId) || qty <= 0) return;
        int cur = items.get(productId);
        int next = cur - qty;
        if (next <= 0) items.remove(productId);
        else items.put(productId, next);
    }

    // تنظیم تعداد یک محصول
    public void setItem(String productId, int qty) {
        if (qty <= 0) items.remove(productId);
        else items.put(productId, qty);
    }

    // پاک کردن کامل سبد خرید
    public void clear() { items.clear(); }
}
