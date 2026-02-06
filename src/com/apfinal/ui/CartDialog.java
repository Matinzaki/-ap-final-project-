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
 * - نمایش محصولات داخل سبد
 * - افزایش/کاهش تعداد
 * - checkout با بررسی balance و stock (استفاده از CartService)
 *
 * توضیح: پس از checkout، کاتالوگ و users ذخیره میشن تا تغییرات پایدار باشن.
 */
public class CartDialog extends JDialog {
    private CatalogService catalogService;
    private CartService cartService;
    private SessionManager session;
    private JPanel listPanel;
    private JLabel lblTotal;
    private JLabel lblBalance;

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

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

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

        btnCheckout.addActionListener(e -> doCheckout());
        btnClose.addActionListener(e -> dispose());

        refreshList();
    }

    // ساخت map از productId->Product برای محاسبات
    private Map<String, Product> buildProductMap() {
        Map<String, Product> map = new java.util.HashMap<>();
        for (Product p : catalogService.getAll()) map.put(p.getId(), p);
        return map;
    }

    // رفرش لیست اقلام داخل سبد
    public void refreshList() {
        listPanel.removeAll();
        Map<String, Product> productMap = buildProductMap();
        Map<Product, Integer> items = cartService.resolveItems(productMap);

        for (Map.Entry<Product, Integer> e : items.entrySet()) {
            Product p = e.getKey();
            int qty = e.getValue();

            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            JLabel lbl = new JLabel("<html><b>Title:</b> " + p.getName() + "  &nbsp;&nbsp; <b>Price:</b> " + p.getPrice() + "  &nbsp;&nbsp; <b>In basket:</b> " + qty + "</html>");

            JPanel actions = new JPanel(new FlowLayout());
            JButton inc = new JButton("Increase count");
            JButton dec = new JButton("Decrease count");
            actions.add(inc);
            actions.add(dec);

            row.add(lbl, BorderLayout.CENTER);
            row.add(actions, BorderLayout.SOUTH);

            inc.addActionListener(ev -> {
                // چک موجودی واقعی
                if (p.getStock() < qty + 1) {
                    JOptionPane.showMessageDialog(this, "موجودی کافی نیست.");
                    return;
                }
                cartService.addToCart(p, 1);
                refreshList();
            });

            dec.addActionListener(ev -> {
                cartService.removeFromCart(p.getId(), 1);
                refreshList();
            });

            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(8));
        }

        long total = cartService.total(productMap);
        lblTotal.setText("Total: " + total + " ریال");
        User user = session.getCurrentUser();
        if (user != null) lblBalance.setText("Balance: " + user.getBalance() + " ریال");
        else lblBalance.setText("Balance: -");

        listPanel.revalidate();
        listPanel.repaint();
    }

    // انجام پرداخت
    private void doCheckout() {
        User user = session.getCurrentUser();
        Map<String, Product> productMap = buildProductMap();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "ابتدا باید وارد شوید.");
            return;
        }

        // Checkout با استفاده از CartService
        boolean ok = cartService.checkout(user, productMap);
        if (ok) {
            // ذخیره تغییرات: کاتالوگ (stock جدید) و کاربران (balance)
            catalogService.save();
            session.getAuthService().saveUsers();
            JOptionPane.showMessageDialog(this, "خرید با موفقیت انجام شد!");
            // نوتیفای سراسری (کتالوگ تغییر کرده)
            session.getCatalogService().save(); // این متد هم notify میکنه
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "پرداخت انجام نشد — موجودی یا استوک کافی نیست.");
        }
    }
}
