package com.apfinal.service;

import com.apfinal.model.Cart;
import com.apfinal.model.Product;
import com.apfinal.model.User;

import java.util.HashMap;
import java.util.Map;


public class CartService {
    private final Cart cart = new Cart();

    public Cart getCart() { return cart; }

    // افزودن محصول به سبد خرید با بررسی شرایط
    public boolean addToCart(Product p, int qty) {
        if (p == null || qty <= 0) return false;
        if (!p.isAvailableForClient()) return false;
        if (p.getStock() < qty) return false;
        cart.addItem(p.getId(), qty);
        return true;
    }

    // حذف محصول از سبد خرید
    public void removeFromCart(String productId, int qty) { cart.removeItem(productId, qty); }
    // تنظیم تعداد یک محصول در سبد خرید
    public void setItem(String productId, int qty) { cart.setItem(productId, qty); }

    // محاسبه قیمت کل سبد خرید
    public long total(Map<String, Product> productMap) {
        long total = 0L;
        for (Map.Entry<String, Integer> e : cart.getItems().entrySet()) {
            Product p = productMap.get(e.getKey());
            if (p != null) total += p.getPrice() * e.getValue();
        }
        return total;
    }

    // پرداخت نهایی و تکمیل سفارش
    public boolean checkout(User user, Map<String, Product> productMap) {
        if (user == null) return false;
        long total = total(productMap);
        if (user.getBalance() < total) return false;

        // بررسی موجودی کافی برای تمام محصولات
        for (Map.Entry<String, Integer> e : cart.getItems().entrySet()) {
            Product p = productMap.get(e.getKey());
            if (p == null || p.getStock() < e.getValue()) return false;
        }

        // کاهش موجودی محصولات و کم کردن مبلغ از پول کاربر
        for (Map.Entry<String, Integer> e : cart.getItems().entrySet()) {
            Product p = productMap.get(e.getKey());
            p.setStock(p.getStock() - e.getValue());
        }
        user.setBalance(user.getBalance() - total);
        cart.clear();
        return true;
    }

    // تبدیل داده ی محصولات به اشیا
    public Map<Product, Integer> resolveItems(Map<String, Product> productMap) {
        Map<Product, Integer> out = new HashMap<>();
        for (Map.Entry<String, Integer> e : cart.getItems().entrySet()) {
            Product p = productMap.get(e.getKey());
            if (p != null) out.put(p, e.getValue());
        }
        return out;
    }
}
