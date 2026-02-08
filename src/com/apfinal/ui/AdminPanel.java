package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.session.DataChangeListener;
import com.apfinal.session.SessionManager;
import com.apfinal.service.CatalogService;
import com.apfinal.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;

/**
 * پنل مدیریت:
 */
public class AdminPanel extends JPanel implements DataChangeListener {
    private MainFrame parent;
    private SessionManager session;
    private CatalogService catalogService;
    private JPanel listPanel;

    //  پنل ادمین
    public AdminPanel(MainFrame parent, SessionManager session) {
        this.parent = parent;
        this.session = session;
        this.catalogService = session.getCatalogService();

        catalogService.addListener(this);

        build();
        refreshList();
    }

    // ساختار اصلی رابط کاربری
    private void build() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Product");
        JButton btnLogout = new JButton("خروج");
        JButton btnSearch = new JButton("Search"); // دکمهٔ سرچ (امتیازی)
        top.add(btnAdd);
        top.add(btnLogout);
        top.add(btnSearch);
        add(top, BorderLayout.NORTH);

        // پنل نمایش لیست محصولات
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(listPanel), BorderLayout.CENTER);

        // رویدادهای دکمه‌ها
        btnAdd.addActionListener(e -> openAddDialog());
        btnLogout.addActionListener(e -> parent.showPanel("login"));

        // بخش امتیازی: باز کردن دیالوگ جستجو برای محصولات
        btnSearch.addActionListener(e -> showProductSearchDialog());
    }

    /* بخش امتیازی - دیالوگ جستجوی محصولات */
    private void showProductSearchDialog() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Search Products", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(600, 400);
        dlg.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new BorderLayout(6,6));
        JTextField tf = new JTextField();
        JButton bSearch = new JButton("Search");
        top.add(new JLabel("Search (name or category): "), BorderLayout.WEST);
        top.add(tf, BorderLayout.CENTER);
        top.add(bSearch, BorderLayout.EAST);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        JScrollPane sc = new JScrollPane(resultList);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bClose = new JButton("Close");
        bottom.add(bClose);

        root.add(top, BorderLayout.NORTH);
        root.add(sc, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        dlg.setContentPane(root);

        // تابع جستجو
        Runnable doSearch = () -> {
            String q = tf.getText().trim().toLowerCase();
            listModel.clear();
            if (q.isEmpty()) return;
            List<Product> allProducts = catalogService.getAll();
            List<Product> matches = allProducts.stream()
                .filter(p -> {
                    String name = p.getName() == null ? "" : p.getName().toLowerCase();
                    String cat  = p.getCategory() == null ? "" : p.getCategory().toLowerCase();
                    return name.contains(q) || cat.contains(q);
                })
                .collect(Collectors.toList());
            if (matches.isEmpty()) {
                listModel.addElement("هیچ محصولی پیدا نشد.");
            } else {
                for (Product p : matches) {
                    String line = String.format("%s  |  %s  | قیمت: %d  | موجودی: %d",
                        p.getName(), p.getCategory(), p.getPrice(), p.getStock());
                    listModel.addElement(line);
                }
            }
        };

        // رویدادهای جستجو
        bSearch.addActionListener(ev -> doSearch.run());
        tf.addActionListener(ev -> doSearch.run());

        // دابل‌کلیک برای نمایش جزئیات
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    int idx = resultList.locationToIndex(me.getPoint());
                    if (idx < 0) return;
                    String sel = listModel.getElementAt(idx);
                    if (sel.equals("هیچ محصولی پیدا نشد.")) return;
                    JOptionPane.showMessageDialog(dlg, sel, "Product details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        bClose.addActionListener(ev -> dlg.dispose());
        dlg.setVisible(true);
    }

    // متد واکنش به تغییرات داده
    @Override
    public void onDataChanged() {
        SwingUtilities.invokeLater(this::refreshList);
    }

    // رفرش لیست محصولات
    public void refreshList() {
        listPanel.removeAll();
        for (Product p : catalogService.getAll()) {
            // ایجاد سطر برای هر محصول
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // نمایش تصویر محصول اگر وجود داشته باشد
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                File img = new File(p.getImagePath());
                if (img.exists()) {
                    ImageIcon ic = ImageUtils.loadImage(img.getAbsolutePath(), 100, 100);
                    if (ic != null) {
                        JLabel lbl = new JLabel(ic);
                        row.add(lbl, BorderLayout.WEST);
                    }
                }
            }

            // پنل اطلاعات محصول
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.add(new JLabel("Title: " + p.getName()));
            info.add(new JLabel("Category: " + p.getCategory()));
            info.add(new JLabel("Price: " + p.getPrice()));
            info.add(new JLabel("Stock: " + p.getStock()));
            info.add(new JLabel("Available for client: " + p.isAvailableForClient()));

            // پنل دکمه‌های عملیاتی
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnModify = new JButton("Modify");
            JButton btnDelete = new JButton("Delete");
            actions.add(btnModify);
            actions.add(btnDelete);

            row.add(info, BorderLayout.CENTER);
            row.add(actions, BorderLayout.EAST);

            // رویداد ویرایش محصول
            btnModify.addActionListener(e -> openEditDialog(p));
            
            // رویداد حذف محصول با تأیید و منطق fallback
            btnDelete.addActionListener(e -> {
                int ok = JOptionPane.showConfirmDialog(this, "آیا از حذف این محصول مطمئن هستید؟", "تأیید حذف", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    boolean removed = false;
                    try {
                        // اگر id موجوده، از متد رسمی استفاده کن
                        if (p.getId() != null && !p.getId().trim().isEmpty()) {
                            catalogService.removeProduct(p.getId());
                            removed = true;
                        } else {
                            Product toRemove = null;
                            for (Product q : catalogService.getAll()) {
                                boolean same =
                                        safeEquals(q.getName(), p.getName()) &&
                                        q.getPrice() == p.getPrice() &&
                                        q.getStock() == p.getStock() &&
                                        safeEquals(q.getImagePath(), p.getImagePath());
                                if (same) { toRemove = q; break; }
                            }
                            if (toRemove != null) {
                                // اگر id داشتند، از removeProduct استفاده کن، در غیر این صورت مستقیم از لیست حذف کن و save
                                if (toRemove.getId() != null && !toRemove.getId().trim().isEmpty()) {
                                    catalogService.removeProduct(toRemove.getId());
                                } else {
                                    catalogService.getAll().remove(toRemove);
                                    catalogService.save();
                                }
                                removed = true;
                            }
                        }
                    } catch (Exception ex) {
                        removed = false;
                    }

                    if (removed) {
                        refreshList();
                        JOptionPane.showMessageDialog(this, "محصول حذف شد.");
                    } else {
                        JOptionPane.showMessageDialog(this, "حذف انجام نشد.");
                    }
                }
            });

            listPanel.add(row);
            listPanel.add(Box.createRigidArea(new Dimension(0,6)));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    // متد کمکی برای مقایسه رشته‌ها
    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // دیالوگ اضافه کردن محصول جدید
    private void openAddDialog() {
        // کد موجود باز گذاشته شد — همان UI قبلی (اضافی نکردم)
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Please enter products details", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(500, 500);
        d.setLayout(new GridLayout(0,2,6,6));

        JTextField tfName = new JTextField();
        JTextField tfCategory = new JTextField();
        JTextField tfPrice = new JTextField();
        JTextField tfStock = new JTextField();
        JTextArea taDesc = new JTextArea();
        JTextField tfImagePath = new JTextField();
        JButton btnChooseImage = new JButton("Choose image");
        JCheckBox cbAvailable = new JCheckBox("available for client", true);

        d.add(new JLabel("Title:")); d.add(tfName);
        d.add(new JLabel("Category:")); d.add(tfCategory);
        d.add(new JLabel("Price (ریال):")); d.add(tfPrice);
        d.add(new JLabel("Stock:")); d.add(tfStock);
        d.add(new JLabel("Description:")); d.add(new JScrollPane(taDesc));
        d.add(new JLabel("Image path:")); d.add(tfImagePath);
        d.add(new JLabel("")); d.add(btnChooseImage);
        d.add(new JLabel("")); d.add(cbAvailable);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Add");
        JButton btnBack = new JButton("Back");
        bottom.add(btnAdd);
        bottom.add(btnBack);
        d.add(new JLabel("")); d.add(bottom);

        btnChooseImage.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            int r = fc.showOpenDialog(d);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                tfImagePath.setText(f.getAbsolutePath());
            }
        });

        btnAdd.addActionListener(ev -> {
            try {
                Product p = new Product();
                p.setName(tfName.getText().trim());
                p.setCategory(tfCategory.getText().trim());
                p.setPrice(Long.parseLong(tfPrice.getText().trim()));
                p.setStock(Integer.parseInt(tfStock.getText().trim()));
                p.setDescription(taDesc.getText());
                p.setImagePath(tfImagePath.getText().trim());
                p.setAvailableForClient(cbAvailable.isSelected());
                catalogService.addProduct(p); // حالا CatalogService تضمین می‌کند که آی دی تولید می‌شود اگر لازم باشد
                JOptionPane.showMessageDialog(d, "Product added!");
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "مشکلی پیش اومد، ورودی‌ها رو چک کن.");
            }
        });

        btnBack.addActionListener(ev -> d.dispose());
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    // دیالوگ ویرایش محصول موجود
    private void openEditDialog(Product p) {
        // از دیالوگ قدیمی استفاده می‌کنیم (همان UI موجود)
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit product", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(500, 500);
        d.setLayout(new GridLayout(0,2,6,6));

        JTextField tfName = new JTextField(p.getName());
        JTextField tfCategory = new JTextField(p.getCategory());
        JTextField tfPrice = new JTextField(String.valueOf(p.getPrice()));
        JTextField tfStock = new JTextField(String.valueOf(p.getStock()));
        JTextArea taDesc = new JTextArea(p.getDescription());
        JTextField tfImagePath = new JTextField(p.getImagePath());
        JButton btnChooseImage = new JButton("Choose image");
        JCheckBox cbAvailable = new JCheckBox("available for client", p.isAvailableForClient());

        d.add(new JLabel("Title:")); d.add(tfName);
        d.add(new JLabel("Category:")); d.add(tfCategory);
        d.add(new JLabel("Price (ریال):")); d.add(tfPrice);
        d.add(new JLabel("Stock:")); d.add(tfStock);
        d.add(new JLabel("Description:")); d.add(new JScrollPane(taDesc));
        d.add(new JLabel("Image path:")); d.add(tfImagePath);
        d.add(new JLabel("")); d.add(btnChooseImage);
        d.add(new JLabel("")); d.add(cbAvailable);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnBack = new JButton("Back");
        bottom.add(btnSave);
        bottom.add(btnBack);
        d.add(new JLabel("")); d.add(bottom);

        btnChooseImage.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            int r = fc.showOpenDialog(d);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                tfImagePath.setText(f.getAbsolutePath());
            }
        });

        btnSave.addActionListener(ev -> {
            try {
                p.setName(tfName.getText().trim());
                p.setCategory(tfCategory.getText().trim());
                p.setPrice(Long.parseLong(tfPrice.getText().trim()));
                p.setStock(Integer.parseInt(tfStock.getText().trim()));
                p.setDescription(taDesc.getText());
                p.setImagePath(tfImagePath.getText().trim());
                p.setAvailableForClient(cbAvailable.isSelected());

                // اگر محصول در کاتالوگ با آی دی شناخته میشه، updateProduct کافیست.
                // اگر آی دی نداشت یا با آی دی نتونست پیدا کنه (محصول تازه اضافه‌شده از طریق دیالوگ بدون آی دی)،
                // با فراخوانی سیو() تغییرات را ذخیره می‌کنیم.
                if (catalogService.findById(p.getId()) != null) {
                    catalogService.updateProduct(p);
                } else {
                    // فقط مطمئن شو که آیتم موجود در لیست کاتالوگ همان شیء عه و سپس سیو کن
                    // (به این ترتیب محصول‌های بدون id هم ویرایش می‌شن)
                    List<Product> list = catalogService.getAll();
                    boolean found = false;
                    for (Product q : list) {
                        if (q == p) { // همون شی در لیست
                            catalogService.save();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // تلاش برای پیدا کردن براساس مشخصات 
                        String oid = p.getId();
                        for (int i = 0; i < list.size(); i++) {
                            Product q = list.get(i);
                            if (q.getName().equals(p.getName()) && q.getPrice() == p.getPrice() && q.getStock() == p.getStock()) {
                                list.set(i, p);
                                catalogService.save();
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            catalogService.updateProduct(p);
                        }
                    }
                }

                JOptionPane.showMessageDialog(d, "ذخیره شد!");
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "مشکلی پیش اومد، ورودی‌ها رو چک کن.");
            }
        });

        btnBack.addActionListener(ev -> d.dispose());
        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
