package com.apfinal.service;

import com.apfinal.model.Role;
import com.apfinal.model.User;
import com.apfinal.persistence.PersistenceService;

import java.util.Map;

/**
 * سرویس احراز هویت: signIn / signUp
 * - ادمین‌ها از قبل در لیست مشخص‌اند (هاردکد)
 * - ثبت‌نام فقط برای CUSTOMER انجام می‌شود
 */
public class AuthService {
    private final PersistenceService persistence;
    private Map<String, User> users;

    private static final String[] ADMIN_USERNAMES = {"admin"};

    public AuthService(PersistenceService persistence) {
        this.persistence = persistence;
        this.users = persistence.loadUsers();

        // تضمین نقش ADMIN برای کاربران از پیش تعریف شده
        for (String adminUsername : ADMIN_USERNAMES) {
            User u = users.get(adminUsername);
            if (u != null) u.setRole(Role.ADMIN);
        }
        persistence.saveUsers(users.values());
    }

    public User signIn(String username, String password) {
        User u = users.get(username);
        if (u != null && u.getPassword().equals(password)) {
            // Ensure role correctness
            for (String a : ADMIN_USERNAMES) {
                if (a.equals(username)) { u.setRole(Role.ADMIN); return u; }
            }
            u.setRole(Role.CUSTOMER);
            return u;
        }
        return null;
    }

    // ثبت‌نام کاربران جدید به عنوان CUSTOMER
    public boolean signUp(String username, String password) {
        if (users.containsKey(username)) return false;
        User u = new User(username, password, Role.CUSTOMER, 2000000L);
        users.put(username, u);
        persistence.saveUsers(users.values());
        return true;
    }

    public Map<String, User> getAllUsers() { return users; }
    public void saveUsers() { persistence.saveUsers(users.values()); }
}
