package com.apfinal.ui;

// Product: مدل محصول برای نمایش و ویرایش اطلاعات
// CatalogService: سرویس کاتالوگ برای عملیات ذخیره و بازیابی محصولات
import com.apfinal.model.Product;
import com.apfinal.service.CatalogService;

import javax.swing.*;
import java.awt.*;
// پنجره ی ویرایش محصول
public class EditProductDialog extends JDialog {

    public EditProductDialog(JFrame parent, CatalogService cs, Product original) {
        super(parent, "Edit Product", true);

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextField nameField = new JTextField(original.getName());
        // فیلد دسته‌بندی با مقدار فعلی
        JTextField categoryField = new JTextField(original.getCategory());
        // فیلد قیمت - تبدیل عدد به رشته برای نمایش
        JTextField priceField = new JTextField(String.valueOf(original.getPrice()));
        // فیلد موجودی - تبدیل عدد به رشته برای نمایش
        JTextField qtyField = new JTextField(String.valueOf(original.getStock()));
        // چک‌باکس وضعیت نمایش به مشتری - با مقدار فعلی
        JCheckBox availableBox = new JCheckBox("Available for clients", original.isAvailableForClient());
        // لیبل نمایش مسیر تصویر - اگر مسیر null باشد رشته خالی نمایش داده می‌شود
        JLabel imageLabel = new JLabel(original.getImagePath() == null ? "" : original.getImagePath());

        // اضافه کردن برچسب و فیلدها به فرم
        // هر ردیف شامل یک برچسب در ستون اول و فیلد مربوطه در ستون دوم
        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Category")); form.add(categoryField);
        form.add(new JLabel("Price (Rial)")); form.add(priceField);
        form.add(new JLabel("Quantity")); form.add(qtyField);
        form.add(new JLabel("")); form.add(availableBox); 
        form.add(new JLabel("Image")); form.add(imageLabel);

        // اضافه کردن پنل فرم به بخش مرکزی پنجره
        add(form, BorderLayout.CENTER);

        // ایجاد پنل برای دکمه‌های پایین پنجره
        JPanel buttons = new JPanel();
        // ایجاد دکمه ذخیره
        JButton save = new JButton("Save");
        // ایجاد دکمه کنسل
        JButton cancel = new JButton("Cancel");

        save.addActionListener(e -> {
            try {
                // خواندن مقادیر از فیلدهای ورودی
                // خواندن نام از فیلد متن
                String name = nameField.getText();
                // خواندن دسته‌بندی از فیلد متن
                String cat = categoryField.getText();
                // خواندن قیمت و تبدیل به عدد long - ممکن است NumberFormatException بدهد
                long price = Long.parseLong(priceField.getText());
                // خواندن تعداد و تبدیل به عدد int - ممکن است NumberFormatException بدهد
                int qty = Integer.parseInt(qtyField.getText());
                // خواندن وضعیت چک‌باکس (انتخاب شده یا نه)
                boolean avail = availableBox.isSelected();
                // خواندن مسیر تصویر از لیبل
                String imagePath = imageLabel.getText();

                // ایجاد یک شیء محصول جدید با مقادیر ویرایش شده
                // پارامترها: شناسه، نام، قیمت، موجودی، توضیحات، مسیر تصویر، وضعیت نمایش
                Product updated = new Product(
                        original.getId(), // استفاده از شناسه محصول اصلی
                        name,             // نام جدید
                        price,            // قیمت جدید
                        qty,              // موجودی جدید
                        "",              
                        imagePath == null ? "" : imagePath, 
                        avail            
                );
                // تنظیم دسته‌بندی محصول 
                updated.setCategory(cat);


                if (cs.findById(original.getId()) != null) {
                    cs.updateProduct(updated);
                } else {
                    boolean handled = false; 
                    
                    // گشتن تمام محصولات کاتالوگ
                    for (Product q : cs.getAll()) {
                        if (q == original) {
                            // اگر همان شیء پیدا شد، مقادیر جدید را روی آن تنظیم می‌کنیم
                            q.setName(name);
                            q.setCategory(cat);
                            q.setPrice(price);
                            q.setStock(qty);
                            q.setDescription("");
                            q.setImagePath(imagePath == null ? "" : imagePath);
                            q.setAvailableForClient(avail);
                            // ذخیره تغییرات
                            cs.save();
                            handled = true; 
                            break; 
                        }
                    }
                    
                    // اگر با مقایسه مرجع پیدا نشد
                    if (!handled) {
                        if (updated.getId() != null && !updated.getId().trim().isEmpty()) {
                            cs.updateProduct(updated);
                        } else {
                            // سعی می‌کنیم محصول رو بر اساس مشخصات (نام، قیمت، موجودی) پیدا کنیم
                            for (int i = 0; i < cs.getAll().size(); i++) {
                                Product q = cs.getAll().get(i);
                                // مقایسه بر اساس مشخصات محصول
                                if (q.getName().equals(original.getName()) && 
                                    q.getPrice() == original.getPrice() && 
                                    q.getStock() == original.getStock()) {
                                    // جایگزینی محصول قدیمی با محصول به‌روزشده در لیست
                                    cs.getAll().set(i, updated);
                                    cs.save(); 
                                    handled = true;
                                    break;
                                }
                            }
                            
                            if (!handled) {
                                cs.updateProduct(updated);
                            }
                        }
                    }
                }

                JOptionPane.showMessageDialog(this, "محصول ویرایش شد.");
                // بستن پنجره دیالوگ
                dispose();
            } 
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "قیمت و تعداد باید عددی باشند.");
            } 
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "مشکلی پیش اومد، ورودی‌ها رو چک کن.");
            }
        });

        // وقتی کاربر کلیک می‌کند، پنجره بسته می‌شود
        cancel.addActionListener(e -> dispose());

        // اضافه کردن دکمه‌ها به پنل دکمه‌ها
        buttons.add(save);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);
    }
}
