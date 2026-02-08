# ap-final-project — Mall Shopping (Java Swing)

## تیم
- Paria Bagheri Behrouzian — 40312013
- Matin Zaki — 40313017

## توضیح کلی
این پروژه یک اپلیکیشن شبیه‌سازی فروشگاه (Mall Shopping) نوشته شده با Java و Swing است. کاربران می‌توانند محصولات را مرور کنند، به سبد خرید اضافه/حذف کنند و خرید را نهایی کنند. مدیران می‌توانند محصولات را اضافه/ویرایش/حذف کنند. داده‌ها به‌صورت JSON در پوشه‌ی `data/` ذخیره می‌شوند.

## ویژگی‌های پیاده‌سازی شده (الزامی)
- مدل محصول با فیلدهای name, category, price, stock, imagePath, description, availableForClient, rating, ratingCount
- نمایش catalog با رابط گرافیکی Swing
- سبد خرید با قابلیت add/remove و نمایش مجموع قیمت به‌صورت پویا
- checkout که تعداد موجودی را کم می‌کند و ذخیره‌سازی نهایی انجام می‌دهد
- دو نقش: CUSTOMER و ADMIN با دسترسی‌های متفاوت
- persistence با JSON (Gson) برای products و carts و users
- UI شامل: Login, Shop (برای کاربر)، Admin panel، Cart dialog، Sidebar با search و sort
- رعایت اصول طراحی modular و استفاده از سرویس‌ها برای منطق کسب‌وکار

## ویژگی‌های امتیازی (علامت‌گذاری شده)
- مدیریت balance برای مشتریان (کسر در زمان پرداخت)  // بخش امتیازی
- سیستم rating (می‌تواند مقدار ممیزی داشته باشد)  // بخش امتیازی
- آپلود و نمایش تصویر محصول در UI  // بخش امتیازی
- پشتیبانی PostgreSQL (معماری آماده، پیاده‌سازی اولیه حذف شده)  // بخش امتیازی

## پیش‌نیازها
- Java 11+
- Gson library (add `com.google.code.gson:gson:2.10.1` or similar)

## ساختار پروژه
ap-final-project/
├─ src/
│ └─ com/apfinal/
│ ├─ model/
│ │ ├─ Product.java
│ │ ├─ Catalog.java
│ │ ├─ Cart.java
│ │ ├─ User.java
│ │ └─ Role.java
│ ├─ persistence/
│ │ └─ PersistenceService.java
│ ├─ service/
│ │ ├─ AuthService.java
│ │ ├─ CatalogService.java
│ │ └─ CartService.java
│ ├─ ui/
│ │ ├─ Main.java
│ │ ├─ MainFrame.java
│ │ ├─ LoginPanel.java
│ │ ├─ ShopPanel.java
│ │ ├─ ProductCard.java
│ │ ├─ CartDialog.java
│ │ └─ AdminPanel.java
│ └─ util/
│ └─ ImageUtils.java
├─ data/
│ ├─ products.json
│ ├─ users.json
│ └─ carts/
└─ README.md


## نحوه اجرا
1. پروژه را در IDE باز کن و dependency Gson را اضافه کن.
2. پوشهٔ `data/` را کنار `src/` بساز. (کد خودکار هم آن را درست می‌کند اگر موجود نباشد.)
3. کلاس `com.apfinal.ui.Main` را اجرا کن.
4. در صورت نبود user می‌توانی از پنل Sign up استفاده کنی یا فایل `data/users.json` را ویرایش کنی.

## نحوه کار با Git
- نام ریپو: `ap-final-project`
- شاخه‌ها: `main` (پایدار)، `feature/*` برای توسعه
- هر عضو وظایفش را در کامیت‌ها ثبت کند

## مستند داخلی کلاس‌ها
در کد، هر کلاس و متد به‌صورت نام‌گذاری شده و با ساختار تمیز نوشته شده تا مسئولیت‌ها واضح باشند.
