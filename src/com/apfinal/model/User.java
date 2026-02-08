package com.apfinal.model;

/**
 * - هم ادمین و هم مشتری با این کلاس نمایش داده می شه.
 * -
 */
public class User {
    // فیلدهای کلاس، اطلاعات کاربر
    private String username;
    private String password;
    private Role role;
    private long balance; // ریال

    public User() {}

    public User(String username, String password, Role role, long balance) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = balance;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public long getBalance() { return balance; }
    public void setBalance(long balance) { this.balance = balance; }
}
