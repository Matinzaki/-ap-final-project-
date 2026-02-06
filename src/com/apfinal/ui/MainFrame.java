package com.apfinal.ui;

import com.apfinal.model.User;
import com.apfinal.session.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * فریم اصلی و مدیریت پنل‌ها
 */
public class MainFrame extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel container = new JPanel(cards);
    private SessionManager session;

    public MainFrame(SessionManager session) {
        this.session = session;
        initUI();
    }

    private void initUI() {
        setTitle("Shopping Mall - AP Final Project");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        LoginPanel login = new LoginPanel(this, session);
        ShopPanel shop = new ShopPanel(this, session);
        AdminPanel admin = new AdminPanel(this, session);

        container.add(login, "login");
        container.add(shop, "shop");
        container.add(admin, "admin");

        add(container);
        showPanel("login");
    }

    public void showPanel(String name) { cards.show(container, name); }

    public SessionManager getSession() { return session; }

    public void setCurrentUser(User u) {
        session.setCurrentUser(u);
    }
}
