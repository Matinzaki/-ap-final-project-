package com.apfinal.ui;

import com.apfinal.model.User;
import com.apfinal.session.SessionManager;
import com.apfinal.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * پنل لاگین/ثبت‌نام.
 * ثبت‌نام فقط برای کاربران عادی (CUSTOMER) است.
 */
public class LoginPanel extends JPanel {
    private MainFrame parent;
    private SessionManager session;
    private AuthService authService;

    public LoginPanel(MainFrame parent, SessionManager session) {
        this.parent = parent;
        this.session = session;
        this.authService = session.getAuthService();
        build();
    }

    private void build() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);

        JLabel lblTitle = new JLabel("Please enter your username and password");
        JTextField tfUser = new JTextField(20);
        JPasswordField pf = new JPasswordField(20);
        JButton btnSignIn = new JButton("Sign in");
        JButton btnSignUp = new JButton("Sign up");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        gbc.gridx = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(tfUser, gbc);

        gbc.gridy++; gbc.gridx = 0; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(pf, gbc);

        gbc.gridy++; gbc.gridx = 0; add(btnSignUp, gbc);
        gbc.gridx = 1; add(btnSignIn, gbc);

        btnSignIn.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String p = new String(pf.getPassword());
            User user = authService.signIn(u, p);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "نام‌کاربری یا رمز اشتباهه!");
                return;
            }
            parent.setCurrentUser(user);
            if (user.getRole().name().equals("ADMIN")) parent.showPanel("admin");
            else parent.showPanel("shop");
        });

        btnSignUp.addActionListener(e -> {
            String u = tfUser.getText().trim(), p = new String(pf.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "هر دو فیلد باید پر بشن!");
                return;
            }
            if (u.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "این نام کاربری رزرو شده است.");
                return;
            }
            boolean ok = authService.signUp(u, p);
            if (ok) JOptionPane.showMessageDialog(this, "ثبت‌نام انجام شد. حالا وارد شو.");
            else JOptionPane.showMessageDialog(this, "این نام کاربری قبلاً وجود داره.");
        });
    }
}
