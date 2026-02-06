package com.apfinal.session;

import com.apfinal.model.User;
import com.apfinal.service.AuthService;
import com.apfinal.service.CartService;
import com.apfinal.service.CatalogService;

import java.util.ArrayList;
import java.util.List;

/**
 * نگهدارنده سرویس‌ها و کاربر جاری؛ مکان مرکزی برای اشتراک سرویس‌ها
 */
public class SessionManager {
    private final CatalogService catalogService;
    private final AuthService authService;
    private final CartService cartService;

    private User currentUser;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    public SessionManager(CatalogService catalogService, AuthService authService, CartService cartService) {
        this.catalogService = catalogService;
        this.authService = authService;
        this.cartService = cartService;
    }

    public CatalogService getCatalogService() { return catalogService; }
    public AuthService getAuthService() { return authService; }
    public CartService getCartService() { return cartService; }

    public void setCurrentUser(User u) {
        this.currentUser = u;
        notifyDataChanged();
    }

    public User getCurrentUser() { return currentUser; }

    public void addListener(DataChangeListener l) {
        if (l != null && !listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(DataChangeListener l) { listeners.remove(l); }

    public void notifyDataChanged() {
        for (DataChangeListener l : new ArrayList<>(listeners)) {
            try { l.onDataChanged(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
