package com.apfinal.ui;

import com.apfinal.model.Product;
import com.apfinal.service.CatalogService;

import javax.swing.*;
import java.awt.*;

public class EditProductDialog extends JDialog {

    public EditProductDialog(JFrame parent, CatalogService cs, Product original) {
        super(parent, "Edit Product", true);

        setSize(400, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextField nameField = new JTextField(original.getName());
        JTextField categoryField = new JTextField(original.getCategory());
        JTextField priceField = new JTextField(String.valueOf(original.getPrice()));
        JTextField qtyField = new JTextField(String.valueOf(original.getStock()));
        JCheckBox availableBox = new JCheckBox("Available for clients", original.isAvailableForClient());
        JLabel imageLabel = new JLabel(original.getImagePath() == null ? "" : original.getImagePath());

        form.add(new JLabel("Name")); form.add(nameField);
        form.add(new JLabel("Category")); form.add(categoryField);
        form.add(new JLabel("Price (Rial)")); form.add(priceField);
        form.add(new JLabel("Quantity")); form.add(qtyField);
        form.add(new JLabel("")); form.add(availableBox);
        form.add(new JLabel("Image")); form.add(imageLabel);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        save.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String cat = categoryField.getText();
                long price = Long.parseLong(priceField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                boolean avail = availableBox.isSelected();
                String imagePath = imageLabel.getText();

                Product updated = new Product(
                        original.getId(),
                        name,
                        price,
                        qty,
                        "", // description (اگر داری می‌تونی از فیلد جداگانه بگیری)
                        imagePath == null ? "" : imagePath,
                        avail
                );
                updated.setCategory(cat);

                // بهتر است از updateProduct استفاده کنیم (وجود دارد در CatalogService)
                cs.updateProduct(updated);
                JOptionPane.showMessageDialog(this, "Product updated.");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and quantity must be numeric.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        cancel.addActionListener(e -> dispose());

        buttons.add(save);
        buttons.add(cancel);

        add(buttons, BorderLayout.SOUTH);
    }
}
