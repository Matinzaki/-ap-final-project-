package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.service.CatalogService;
import com.apfinal.persistence.PersistenceService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class AddProductDialog extends JDialog {
    private JTextField idField = new JTextField(20);
    private JTextField nameField = new JTextField(20);
    private JTextField categoryField = new JTextField(20);
    private JTextField priceField = new JTextField(20);
    private JTextField qtyField = new JTextField(20);
    private JCheckBox availBox = new JCheckBox("Available for clients", true);
    private JTextField imagePathField = new JTextField(20);
    private File selectedImageFile = null;
    public AddProductDialog(JFrame owner, CatalogService cs, PersistenceService ps) {
        super(owner, "Add Product", true);
        setLayout(new BorderLayout());
        
        // ایجاد پنل  با چیدمان شبکه‌ ای
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        form.add(new JLabel("ID (leave empty to auto)")); form.add(idField);
        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Category")); form.add(categoryField);
        form.add(new JLabel("Price (Rial)")); form.add(priceField);
        form.add(new JLabel("Quantity")); form.add(qtyField);
        form.add(new JLabel("")); form.add(availBox);
        
        // بخش انتخاب تصویر
        form.add(new JLabel("Image"));
        JPanel imRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
        imagePathField.setEditable(false);
        imRow.add(imagePathField);
        JButton choose = new JButton("Choose...");
        
        //  انتخاب تصویر
        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int r = fc.showOpenDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fc.getSelectedFile();
                imagePathField.setText(selectedImageFile.getAbsolutePath());
            }
        });
        imRow.add(Box.createHorizontalStrut(6));
        imRow.add(choose);
        form.add(imRow);

        add(form, BorderLayout.CENTER);

        // درست کردن دکمه‌های سیو و کنسل
        JPanel buttons = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        buttons.add(save); buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);

        //  دکمه ی سیو
        save.addActionListener(e -> {
            try {
                // خواندن داده
                String id = idField.getText().trim();
                if (id.isEmpty()) id = UUID.randomUUID().toString();
                String name = nameField.getText().trim();
                String cat = categoryField.getText().trim();
                long price = Long.parseLong(priceField.getText().trim()); // ریال
                int qty = Integer.parseInt(qtyField.getText().trim());
                boolean avail = availBox.isSelected();

                // پردازش تصویر
                String imageRelPath = null;
                if (selectedImageFile != null) {
                    File imagesDir = new File("data/images");
                    imagesDir.mkdirs();
                    String ext = selectedImageFile.getName().contains(".") ?
                            selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf('.')) : ".jpg";
                    String destName = UUID.randomUUID().toString() + ext;
                    File dest = new File(imagesDir, destName);
                    Files.copy(selectedImageFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    imageRelPath = "data/images/" + destName;
                }

                // ایجاد محصول و ذخیره آن
                Product p = new Product(id, name, price, qty, "", imageRelPath == null ? "" : imageRelPath, avail);
                p.setCategory(cat);
                cs.addProduct(p); // افزودن و ذخیره کردن
                JOptionPane.showMessageDialog(this, "Product added.");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and quantity must be numeric.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving product: " + ex.getMessage());
            }
        });

        //  دکمه ی کنسل
        cancel.addActionListener(e -> dispose());

        // تنظیمات پنجره
        pack();
        setLocationRelativeTo(owner);
        setVisible(true);
    }

}
