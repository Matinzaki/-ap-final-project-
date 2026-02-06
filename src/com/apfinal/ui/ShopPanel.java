package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.model.User;
import com.apfinal.service.CatalogService;
import com.apfinal.service.CartService;
import com.apfinal.session.DataChangeListener;
import com.apfinal.session.SessionManager;
import com.apfinal.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.text.Collator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * ShopPanel: پنل اصلی مشتری
 *
 * - نمایش فهرست محصولات (وسط)
 * - نوار سمت چپ حاوی اطلاعات کاربر: نوع یوزر، welcome، username، balance
 *    و دکمه‌های: Cart، Profile، Logout
 * - جستجو و سورت (بالای وسط) — حالا گزینه‌های سورت فارسی اضافه شدند
 *
 * تغییرات (طبق درخواست):
 *  - به سِرت‌بای گزینه‌های فارسی "نام (الف تا ی)" و "دسته‌بندی (الف تا ی)" اضافه شد
 *  - مرتب‌سازی فارسی با Collator و Locale("fa") انجام می‌شه
 *  - کارت محصول (نمایش) شامل Category هم هست
 */
public class ShopPanel extends JPanel implements DataChangeListener {

    private final MainFrame mainFrame;
    private final SessionManager session;

    private final CatalogService catalogService;
    private final CartService cartService;

    // بخش‌های UI که نیاز به آپدیت داینامیک دارن
    private JLabel lblUserType;
    private JLabel lblWelcome;
    private JLabel lblUsername;
    private JLabel lblBalance;
    private JPanel productsContainer; // جایی که کارت محصولات قرار می‌گیره
    private JTextField tfSearch;
    private JComboBox<String> cbSort;

    // لیبل‌های فارسی سورت
    private static final String SORT_DEFAULT = "پیش‌فرض";
    private static final String SORT_PRICE = "قیمت";
    private static final String SORT_NAME_FA = "نام (الف تا ی)";
    private static final String SORT_CATEGORY_FA = "دسته‌بندی (الف تا ی)";
    private static final String SORT_RATING = "رتبه";
    private static final String SORT_STOCK = "موجودی";

    public ShopPanel(MainFrame mainFrame, SessionManager session) {
        this.mainFrame = mainFrame;
        this.session = session;
        this.catalogService = session.getCatalogService();
        this.cartService = session.getCartService();

        // عضو شدن به عنوان listener برای تغییر داده‌ها (وقتی user یا کاتالوگ تغییر کنه)
        this.session.addListener(this);
        this.catalogService.addListener(this); // وقتی محصولات تغییر کردند هم آپدیت می‌شه

        setLayout(new BorderLayout(10, 10));
        initUI();
        refreshProducts();
        refreshUserInfo();
    }

    // ساختار اصلی UI
    private void initUI() {
        // سمت چپ: اطلاعات کاربر + دکمه‌ها
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(260, 0));
        left.setLayout(new GridBagLayout());
        left.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(6,6,6,6);

        lblUserType = new JLabel("User Type: -");
        lblWelcome = new JLabel("Welcome!");
        lblUsername = new JLabel("Your username is: -");
        lblBalance = new JLabel("Balance: 0 ریال");

        // دکمه‌ها
        JButton btnCart = new JButton("Basket");
        JButton btnProfile = new JButton("Profile");
        JButton btnLogout = new JButton("Log out"); // <-- این دکمه اضافه شد (بخش امتیازی)

        // چیدمان سمت چپ
        left.add(lblUserType, g); g.gridy++;
        left.add(lblWelcome, g); g.gridy++;
        left.add(lblUsername, g); g.gridy++;
        left.add(lblBalance, g); g.gridy++;

        // فاصله
        g.weighty = 1.0;
        left.add(Box.createVerticalGlue(), g);
        g.weighty = 0; g.gridy++;

        left.add(btnCart, g); g.gridy++;
        left.add(btnProfile, g); g.gridy++;
        left.add(btnLogout, g); g.gridy++;

        add(left, BorderLayout.WEST);

        // بالای وسط: جستجو و سورت
        JPanel topCenter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        tfSearch = new JTextField(24);
        cbSort = new JComboBox<>(new String[]{SORT_DEFAULT, SORT_PRICE, SORT_NAME_FA, SORT_CATEGORY_FA, SORT_RATING, SORT_STOCK});
        JButton btnSearch = new JButton("Search");
        JButton btnSort = new JButton("Sort");

        topCenter.add(new JLabel("Search:"));
        topCenter.add(tfSearch);
        topCenter.add(btnSearch);
        topCenter.add(new JLabel("Sort by:"));
        topCenter.add(cbSort);
        topCenter.add(btnSort);

        add(topCenter, BorderLayout.NORTH);

        // مرکز: لیست محصولات (اسکرول)
        productsContainer = new JPanel();
        productsContainer.setLayout(new BoxLayout(productsContainer, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(productsContainer);
        add(scroll, BorderLayout.CENTER);

        // اکشن‌ها
        btnSearch.addActionListener(e -> doSearch());
        btnSort.addActionListener(e -> doSort());

        btnCart.addActionListener(e -> {
            // باز کردن دیالوگ سبد خرید
            CartDialog dialog = new CartDialog(
                mainFrame,         // پنجره مادر
                catalogService,    // برای دسترسی به محصولات
                cartService,       // منطق سبد خرید
                session            // اطلاعات کاربر لاگین شده
            );
            dialog.setLocationRelativeTo(mainFrame);
            dialog.setVisible(true); // باز کردن دیالوگ سبد
        });

        btnProfile.addActionListener(e -> {
            // ساده: نمایش اطلاعات پروفایل در dialog
            User u = session.getCurrentUser();
            if (u == null) {
                JOptionPane.showMessageDialog(this, "ابتدا وارد شوید");
                return;
            }
            String msg = "Username: " + u.getUsername() + "\nRole: " + u.getRole() + "\nBalance: " + u.getBalance() + " ریال";
            JOptionPane.showMessageDialog(this, msg, "Profile", JOptionPane.INFORMATION_MESSAGE);
        });

        // دکمه Log out — بخش امتیازی
        btnLogout.addActionListener(e -> {
            int ans = JOptionPane.showConfirmDialog(this, "آیا می‌خواهید خارج شوید؟", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) {
                session.setCurrentUser(null); // پاک کردن کاربر جاری
                // بازگشت به پنل لاگین
                mainFrame.showPanel("login");
                JOptionPane.showMessageDialog(this, "شما با موفقیت خارج شدید.");
            }
        });
    }

    // وقتی دیتا تغییر کرد (سیستم notify می‌کنه)، این فراخوانی می‌شه
    @Override
    public void onDataChanged() {
        SwingUtilities.invokeLater(() -> {
            refreshProducts();
            refreshUserInfo();
        });
    }

    // آپدیت اطلاعات سمت چپ براساس session.getCurrentUser()
    private void refreshUserInfo() {
        User u = session.getCurrentUser();
        if (u == null) {
            lblUserType.setText("User Type: -");
            lblWelcome.setText("Welcome!");
            lblUsername.setText("Your username is: -");
            lblBalance.setText("Balance: 0 ریال");
        } else {
            lblUserType.setText("User Type: " + u.getRole().name());
            lblWelcome.setText("Welcome, " + u.getUsername() + "!");
            lblUsername.setText("Your username is: " + u.getUsername());
            lblBalance.setText("Balance: " + u.getBalance() + " ریال");
        }
    }

    // بارگذاری و نمایش محصولات (میانجی با CatalogService)
    private void refreshProducts() {
        productsContainer.removeAll();

        List<Product> list = catalogService.getAll();
        if (list == null || list.isEmpty()) {
            productsContainer.add(new JLabel("No products available."));
        } else {
            for (Product p : list) {
                // استفاده از ProductCard (نمایش دسته‌بندی هم داخل کارت هست)
                productsContainer.add(new ProductCard(p, catalogService, cartService, session));
                productsContainer.add(Box.createRigidArea(new Dimension(0,8)));
            }
        }

        productsContainer.revalidate();
        productsContainer.repaint();
    }

    // جستجو
    private void doSearch() {
        String q = tfSearch.getText().trim();
        List<Product> res = catalogService.search(q);
        productsContainer.removeAll();
        if (res == null || res.isEmpty()) {
            productsContainer.add(new JLabel("هیچ محصولی پیدا نشد"));
        } else {
            for (Product p : res) {
                productsContainer.add(new ProductCard(p, catalogService, cartService, session));
                productsContainer.add(Box.createRigidArea(new Dimension(0,8)));
            }
        }
        productsContainer.revalidate();
        productsContainer.repaint();
    }

    // مرتب‌سازی فارسی/انگلیسی ترکیبی
    private void doSort() {
        String selected = (String) cbSort.getSelectedItem();
        List<Product> list = new ArrayList<>(catalogService.getAll()); // کپی لیست اولیه

        // Collator فارسی برای مرتب‌سازی الفبایی
    Collator collatorFa = Collator.getInstance(Locale.forLanguageTag("fa"));

        if (SORT_NAME_FA.equals(selected)) {
            list.sort((p1, p2) -> collatorFa.compare(p1.getName() == null ? "" : p1.getName(), p2.getName() == null ? "" : p2.getName()));
        } else if (SORT_CATEGORY_FA.equals(selected)) {
            list.sort((p1, p2) -> collatorFa.compare(p1.getCategory() == null ? "" : p1.getCategory(), p2.getCategory() == null ? "" : p2.getCategory()));
        } else if (SORT_PRICE.equals(selected)) {
            // از متد سرویس استفاده می‌کنیم تا رفتاری که قبلا بوده حفظ شود (قیمت صعودی)
            list = catalogService.sortBy("price");
        } else if (SORT_RATING.equals(selected)) {
            list = catalogService.sortBy("rating");
        } else if (SORT_STOCK.equals(selected)) {
            list = catalogService.sortBy("stock");
        } else {
            // پیش‌فرض: ترتیب اولیه را نگه می‌داریم
            list = catalogService.getAll();
        }

        productsContainer.removeAll();
        for (Product p : list) {
            productsContainer.add(new ProductCard(p, catalogService, cartService, session));
            productsContainer.add(Box.createRigidArea(new Dimension(0,8)));
        }
        productsContainer.revalidate();
        productsContainer.repaint();
    }
}
