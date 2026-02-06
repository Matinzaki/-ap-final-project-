package com.apfinal.ui;

import com.apfinal.persistence.PersistenceService;
import com.apfinal.service.AuthService;
import com.apfinal.service.CartService;
import com.apfinal.service.CatalogService;
import com.apfinal.session.SessionManager;

import javax.swing.SwingUtilities;

/**
 * نقطهٔ ورود برنامه
 */
public class Main {
    public static void main(String[] args) {
        PersistenceService persistence = new PersistenceService();
        CatalogService catalogService = new CatalogService(persistence);
        AuthService authService = new AuthService(persistence);
        CartService cartService = new CartService();

        SessionManager session = new SessionManager(catalogService, authService, cartService);

        SwingUtilities.invokeLater(() -> {
            MainFrame mf = new MainFrame(session);
            mf.setVisible(true);
        });
    }
}
