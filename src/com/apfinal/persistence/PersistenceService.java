package com.apfinal.persistence;

import com.apfinal.model.Catalog;
import com.apfinal.model.Product;
import com.apfinal.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * خواندن/نوشتن JSON برای محصولات و کاربران
 */
public class PersistenceService {
    private static final String DATA_DIR = "data";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.json";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final String IMAGES_DIR = DATA_DIR + "/images";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //  ایجاد دایرکتوری‌
    public PersistenceService() {
        try {
            Files.createDirectories(Path.of(DATA_DIR));
            Files.createDirectories(Path.of(IMAGES_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ریختن کاتالوگ محصولات از فایل JSON
    public Catalog loadCatalog() {
        Catalog c = new Catalog();
        File f = new File(PRODUCTS_FILE);
        if (!f.exists()) return c;
        try (Reader r = new FileReader(f)) {
            Type listType = new TypeToken<List<Product>>(){}.getType();
            List<Product> list = gson.fromJson(r, listType);
            if (list != null) c.setProducts(list);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return c;
    }

    // ذخیره کاتالوگ محصولات در فایل JSON
    public void saveCatalog(Catalog catalog) {
        try (Writer w = new FileWriter(PRODUCTS_FILE)) {
            gson.toJson(catalog.getProducts(), w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ریختن کاربران از فایل JSON
    public Map<String, User> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return defaultUsers();
        try (Reader r = new FileReader(f)) {
            Type t = new TypeToken<List<User>>(){}.getType();
            List<User> list = gson.fromJson(r, t);
            Map<String, User> map = new HashMap<>();
            if (list != null) for (User u : list) map.put(u.getUsername(), u);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return defaultUsers();
        }
    }

    // ذخیره کاربران در فایل JSON
    public void saveUsers(Collection<User> users) {
        try (Writer w = new FileWriter(USERS_FILE)) {
            gson.toJson(users, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ایجاد کاربران پیش‌فرض
    private Map<String, User> defaultUsers() {
        Map<String, User> map = new HashMap<>();
        map.put("admin", new User("admin", "admin", com.apfinal.model.Role.ADMIN, 0L));
        map.put("paria", new User("paria", "1234", com.apfinal.model.Role.CUSTOMER, 5000000L));
        return map;
    }

    // دریافت مسیر تصاویر
    public String getImagesDir() { return IMAGES_DIR; }
}
