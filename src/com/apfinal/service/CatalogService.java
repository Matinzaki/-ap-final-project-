package com.apfinal.service;

import com.apfinal.model.Catalog;
import com.apfinal.model.Product;
import com.apfinal.persistence.PersistenceService;
import com.apfinal.session.DataChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * منطق کاتالوگ: CRUD، جستجو، سورت و نوتیفای listener ها
 */
public class CatalogService {
    private final PersistenceService persistence;
    private Catalog catalog;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public CatalogService(PersistenceService persistence) {
        this.persistence = persistence;
        this.catalog = persistence.loadCatalog();
    }

    public List<Product> getAll() { return catalog.getProducts(); }
    public Product findById(String id) { return catalog.findById(id); }

    public void addProduct(Product p) {
        catalog.addProduct(p);
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

    public void removeProduct(String id) {
        catalog.removeProductById(id);
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

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

    public List<Product> search(String q) {
        String qq = q == null ? "" : q.trim().toLowerCase();
        return catalog.getProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(qq) || p.getCategory().toLowerCase().contains(qq))
                .collect(Collectors.toList());
    }

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

    public void save() {
        persistence.saveCatalog(catalog);
        notifyListeners();
    }

    public PersistenceService getPersistence() { return persistence; }

    public void addListener(DataChangeListener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(DataChangeListener l) { listeners.remove(l); }

    private void notifyListeners() {
        for (DataChangeListener l : new ArrayList<>(listeners)) {
            try { l.onDataChanged(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
