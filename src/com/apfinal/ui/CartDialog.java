package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.model.User;
import com.apfinal.session.SessionManager;
import com.apfinal.service.CartService;
import com.apfinal.service.CatalogService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * دیالوگ سبد خرید:
 */
public class CartDialog extends JDialog {
    // متغیرهای ذخیره سرویس‌ها و کامپوننت‌های UI
    private CatalogService catalogService;
    private CartService cartService;
    private SessionManager session;
    private JPanel listPanel;
    private JLabel lblTotal;
    private JLabel lblBalance;

    //  دیالوگ
    public CartDialog(Window owner, CatalogService catalogService, CartService cartService, SessionManager session) {
        super(owner, "Your basket", ModalityType.APPLICATION_MODAL);
        this.catalogService = catalogService;
        this.cartService = cartService;
        this.session = session;
        init();
    }

    private void init() {
        setSize(800, 500);
        setLayout(new BorderLayout());

        // ایجاد پنل لیست محصولات با قابلیت اسکرول
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        // ایجاد پنل پایینی 
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: 0 ریال");
        lblBalance = new JLabel("Balance: -");
        JButton btnCheckout = new JButton("Checkout");
        JButton btnClose = new JButton("Close");
        bottom.add(lblBalance);
        bottom.add(lblTotal);
        bottom.add(btnCheckout);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        // تنظیم رویدادهای دکمه‌ها
        btnCheckout.addActionListener(e -> doCheckout());
        btnClose.addActionListener(e -> dispose());

        // بارگذاری اولیه لیست محصولات
        refreshList();
    }

    // ایجاد نقشه‌ای از شناسه محصول به شیء Product برای دسترسی سریع
    private Map<String, Product> buildProductMap() {
        Map<String, Product> map = new java.util.HashMap<>();
        for (Product p : catalogService.getAll()) map.put(p.getId(), p);
        return map;
    }

    // به‌روزرسانی لیست محصولات داخل سبد خرید
    public void refreshList() {
        // پاک کردن محتوای قبلی
        listPanel.removeAll();
        
        // ساخت نقشه محصولات و دریافت آیتم‌های سبد
        Map<String, Product> productMap = buildProductMap();
        Map<Product, Integer> items = cartService.resolveItems(productMap);

        // ایجاد یک سطر برای هر محصول در سبد
        for (Map.Entry<Product, Integer> e : items.entrySet()) {
            Product p = e.getKey();
            int qty = e.getValue();

            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            
            //ایجاد لیبل دور محصولات برای خوشگلی بیشتر
            JLabel lbl = new JLabel("<html><b>Title:</b> " + p.getName() + "  &nbsp;&nbsp; <b>Price:</b> " + p.getPrice() + "  &nbsp;&nbsp; <b>In basket:</b> " + qty + "</html>");

            // پنل دکمه‌های افزایش و کاهش
            JPanel actions = new JPanel(new FlowLayout());
            JButton inc = new JButton("Increase count");
            JButton dec = new JButton("Decrease count");
            actions.add(inc);
            actions.add(dec);

            row.add(lbl, BorderLayout.CENTER);
            row.add(actions, BorderLayout.SOUTH);

            //  افزایش تعداد - با بررسی موجودی
            inc.addActionListener(ev -> {
                // بررسی موجودی کافی قبل از افزایش
                if (p.getStock() < qty + 1) {
                    JOptionPane.showMessageDialog(this, "موجودی کافی نیست.");
                    return;
                }
                cartService.addToCart(p, 1);
                refreshList();
            });

            //  کم کردن تعداد
            dec.addActionListener(ev -> {
                cartService.removeFromCart(p.getId(), 1);
                refreshList();
            });

            // اضافه کردن سطر به لیست
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(8)); // فاصله عمودی بین سطرها
        }

        // محاسبه و نمایش قیمت کل
        long total = cartService.total(productMap);
        lblTotal.setText("Total: " + total + " ریال");
        
        // نمایش موجودی حساب کاربر
        User user = session.getCurrentUser();
        if (user != null) lblBalance.setText("Balance: " + user.getBalance() + " ریال");
        else lblBalance.setText("Balance: -");

        // اعتبارسنجی پنل
        listPanel.revalidate();
        listPanel.repaint();
    }

    // انجام عملیات پرداخت نهایی
    private void doCheckout() {
        User user = session.getCurrentUser();
        Map<String, Product> productMap = buildProductMap();
        
        // بررسی لاگین بودن کاربر
        if (user == null) {
            JOptionPane.showMessageDialog(this, "ابتدا باید وارد شوید.");
            return;
        }

        boolean ok = cartService.checkout(user, productMap);
        if (ok) {
            // ذخیره تغییرات در کاتالوگ (موجودی جدید) و کاربران (موجودی حساب)
            catalogService.save();
            session.getAuthService().saveUsers();
            JOptionPane.showMessageDialog(this, "خرید با موفقیت انجام شد!");
            
            // اطلاع‌رسانی تغییرات کاتالوگ
            session.getCatalogService().save(); // این متد هم notify می‌کند
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "پرداخت انجام نشد — موجودی یا استوک کافی نیست.");
        }
    }
}
