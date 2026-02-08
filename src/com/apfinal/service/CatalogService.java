package com.apfinal.service;

import com.apfinal.model.Catalog;
import com.apfinal.model.Product;
import com.apfinal.persistence.PersistenceService;
import com.apfinal.session.DataChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * منطق کاتالوگ
 */
public class CatalogService {
    private final PersistenceService persistence;
    // کاتالوگ محصولات
    private Catalog catalog;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    // بارگذاری کاتالوگ از ذخیره‌سازی
    public CatalogService(PersistenceService persistence) {
        this.persistence = persistence;
        this.catalog = persistence.loadCatalog();
    }

    // دریافت تمام محصولات
    public List<Product> getAll() { return catalog.getProducts(); }
    // یافتن محصول بر اساس آی دی
    public Product findById(String id) { return catalog.findById(id); }

    // افزودن محصول جدید با تولید خودکار آی دی
    public void addProduct(Product p) {
        try {
            if (p != null) {
                String id = null;
                try {
                    id = p.getId();
                } catch (Exception ignore) {}
                if (id == null || id.trim().isEmpty()) {
                    // تلاش برای setId از طریق متد اگر موجود باشه
                    try {
                        p.setId(UUID.randomUUID().toString());
                    } catch (Throwable t) {
                        // اگر setId وجود نداشت یا هر خطای دیگری رخ داد، نادیده می‌گیریم
                    }
                }
            }
        } catch (Exception ignore) {}

        catalog.addProduct(p);
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

    // حذف محصول بر اساس آی دی
    public void removeProduct(String id) {
        catalog.removeProductById(id);
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

    // به‌روزرسانی کامل اطلاعات محصول
    public void updateProduct(Product p) {
        Product exist = catalog.findById(p.getId());
        if (exist != null) {
            exist.setName(p.getName());
            exist.setCategory(p.getCategory());
            exist.setPrice(p.getPrice());
            exist.setStock(p.getStock());
            exist.setDescription(p.getDescription());
            exist.setImagePath(p.getImagePath());
            exist.setAvailableForClient(p.isAvailableForClient());
            exist.setRating(p.getRating());
            exist.setRatingCount(p.getRatingCount());
            persistence.saveCatalog(catalog);
            notifyListeners();
        }
    }

    // جستجوی محصول بر اساس نام یا دسته‌بندی
    public List<Product> search(String q) {
        String qq = q == null ? "" : q.trim().toLowerCase();
        return catalog.getProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(qq) || p.getCategory().toLowerCase().contains(qq))
                .collect(Collectors.toList());
    }

    // مرتب‌سازی محصولات بر اساس معیارهای مختلف
    public List<Product> sortBy(String key) {
        return catalog.getProducts().stream()
                .sorted((a,b) -> {
                    switch (key) {
                        case "price": return Long.compare(a.getPrice(), b.getPrice());
                        case "name": return a.getName().compareToIgnoreCase(b.getName());
                        case "rating": return Double.compare(b.getRating(), a.getRating());
                        case "stock": return Integer.compare(b.getStock(), a.getStock());
                        default: return 0;
                    }
                }).collect(Collectors.toList());
    }

    // ذخیره کاتالوگ
    public void save() {
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

    //  سرویس ذخیره‌سازی
    public PersistenceService getPersistence() { return persistence; }

    public void addListener(DataChangeListener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(DataChangeListener l) { listeners.remove(l); }

    // اطلاع‌رسانی وقتی که محصول اضافه شد
    private void notifyListeners() {
        for (DataChangeListener l : new ArrayList<>(listeners)) {
            try { l.onDataChanged(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
