package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.session.SessionManager;
import com.apfinal.service.CartService;
import com.apfinal.service.CatalogService;
import com.apfinal.util.ImageUtils;

import javax.swing.*;
import java.awt.*;


public class ProductCard extends JPanel {
    private Product product;
    private CatalogService catalogService;
    private CartService cartService;
    private SessionManager session;

    public ProductCard(Product product, CatalogService catalogService, CartService cartService, SessionManager session) {
        this.product = product;
        this.catalogService = catalogService;
        this.cartService = cartService;
        this.session = session;
        build();
    }

    private void build() {
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        // تصویر سمت چپ
        JLabel lblImg = new JLabel();
        ImageIcon icon = ImageUtils.loadImage(product.getImagePath(), 130, 110);
        if (icon != null) lblImg.setIcon(icon);
        else lblImg.setText("No Image");

        // اطلاعات محصول
        JPanel info = new JPanel(new GridLayout(0,2));
        info.add(new JLabel("<html><b>Title:</b> " + product.getName() + "</html>"));
        info.add(new JLabel("<html><b>Category:</b> " + (product.getCategory() == null ? "-" : product.getCategory()) + "</html>"));
        info.add(new JLabel("<html><b>Price:</b> " + product.getPrice() + " ریال</html>"));
        info.add(new JLabel("<html><b>Stock:</b> " + product.getStock() + "</html>"));
        info.add(new JLabel("Average rating: " + String.format("%.1f", product.getRating())));
        info.add(new JLabel("Rating count: " + product.getRatingCount()));
        info.add(new JLabel("Available: " + (product.isAvailableForClient() ? "Yes" : "No")));

        // دکمه‌ها
        JPanel actions = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add to basket");
        JButton btnRemove = new JButton("Remove from basket");
        actions.add(btnAdd);
        actions.add(btnRemove);

        add(lblImg, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);
        add(actions, BorderLayout.EAST);

        // Add handler
        btnAdd.addActionListener(e -> {
            if (!product.isAvailableForClient()) {
                JOptionPane.showMessageDialog(this, "این کالا فعلا قابل خرید نیست.");
                return;
            }
            if (product.getStock() <= 0) {
                JOptionPane.showMessageDialog(this, "متأسفانه این کالا فعلا موجود نیست — بهتون خبر می‌دیم.");
                return;
            }
            boolean ok = cartService.addToCart(product, 1);
            if (ok) {
                JOptionPane.showMessageDialog(this, "به سبد اضافه شد!");
            } else {
                JOptionPane.showMessageDialog(this, "اضافه نشد (موجودی کافی نیست).");
            }
        });

        // Remove handler (کاهش ۱ واحد)
        btnRemove.addActionListener(e -> {
            cartService.removeFromCart(product.getId(), 1);
            JOptionPane.showMessageDialog(this, "یک عدد از سبد حذف شد (اگر بود).");
        });
    }
}
