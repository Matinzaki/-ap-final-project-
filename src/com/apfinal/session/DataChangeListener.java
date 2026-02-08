package com.apfinal.session;

/**
 * یه مکانیسم ساده برای اطلاع رسانی تغییرات داده ها به بخش های دیگه ی برنامه درحقیقت یه رابط یا همون اینترفیس هستش
 */
public interface DataChangeListener {
    void onDataChanged();
}
