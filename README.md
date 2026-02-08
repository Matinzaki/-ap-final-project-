# ap-final-project — Mall Shopping (Java Swing)

## تیم
- Paria Bagheri Behrouzian — 40312013
- Matin Zaki — 40313017

## توضیح کلی
این پروژه یک اپلیکیشن شبیه‌سازی فروشگاه (Mall Shopping) نوشته شده با Java است. کاربران می‌توانند محصولات را مرور کنند، به سبد خرید اضافه/حذف کنند و خرید را نهایی کنند. مدیران می‌توانند محصولات را اضافه/ویرایش/حذف کنند. داده‌ها به‌صورت JSON در پوشه‌ی `data/` ذخیره می‌شوند.

## ویژگی‌های پیاده‌سازی شده (الزامی)
- مدل محصول با فیلدهای name, category, price, stock, imagePath, description, availableForClient, rating, ratingCount
- نمایش catalog با رابط گرافیکی Swing
- سبد خرید با قابلیت add/remove و نمایش مجموع قیمت به‌صورت پویا
- checkout که تعداد موجودی را کم می‌کند و ذخیره‌سازی نهایی انجام می‌دهد
- دو نقش: CUSTOMER و ADMIN با دسترسی‌های متفاوت
- persistence با JSON (Gson) برای products و carts و users
- UI شامل: Login, Shop (برای کاربر)، Admin panel، Cart dialog، Sidebar با search و sort
## ویژگی‌های امتیازی (علامت‌گذاری شده)
- مدیریت balance برای مشتریان (کسر در زمان پرداخت)  // بخش امتیازی
- سیستم rating (می‌تواند مقدار ممیزی داشته باشد)  // بخش امتیازی
- آپلود و نمایش تصویر محصول در UI  // بخش امتیازی
- پشتیبانی PostgreSQL (معماری آماده، پیاده‌سازی اولیه حذف شده)  // بخش امتیازی


## نحوه اجرا
1. پروژه را در IDE باز کن و dependency Gson را اضافه کن.
2. پوشهٔ `data/` را کنار `src/` بساز. (کد خودکار هم آن را درست می‌کند اگر موجود نباشد.)
3. کلاس `com.apfinal.ui.Main` را اجرا کن.
4. در صورت نبود user می‌توانی از پنل Sign up استفاده کنی یا فایل `data/users.json` را ویرایش کنی.
