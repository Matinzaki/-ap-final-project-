package com.apfinal.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * متدهای کمکی برای تصاویر:
 * - loadImage: لود و مقیاس‌دهی
 * - copyToImagesDir: کپی تصویر به data/images و بازگرداندن مسیر جدید
 */
public class ImageUtils {

    public static ImageIcon loadImage(String path, int w, int h) {
        if (path == null || path.isEmpty()) return null;
        File f = new File(path);
        if (!f.exists()) return null;
        ImageIcon icon = new ImageIcon(f.getAbsolutePath());
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static String copyToImagesDir(File srcFile, String imagesDir) {
        try {
            Path dst = Path.of(imagesDir, srcFile.getName());
            int i = 1;
            String base = srcFile.getName();
            String name = base;
            while (Files.exists(dst)) {
                int dot = base.lastIndexOf('.');
                String nm = dot > 0 ? base.substring(0, dot) : base;
                String ext = dot > 0 ? base.substring(dot) : "";
                name = nm + "_" + i + ext;
                dst = Path.of(imagesDir, name);
                i++;
            }
            Files.copy(srcFile.toPath(), dst);
            return dst.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return srcFile.getAbsolutePath();
        }
    }
}
