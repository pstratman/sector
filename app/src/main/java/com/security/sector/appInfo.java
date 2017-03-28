package com.security.sector;
import android.graphics.drawable.Drawable;


class appInfo {
    String appName;
    Drawable appIcon;
    String packageName;

    appInfo(String name, Drawable icon, String packName) {
        appName = name;
        appIcon = icon;
        packageName = packName;
    }

}
