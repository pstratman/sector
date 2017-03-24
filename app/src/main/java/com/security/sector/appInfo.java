package com.security.sector;
import android.graphics.drawable.Drawable;

class appInfo {
    private String appName;
    private Drawable appIcon;
    private String packageName;

    appInfo(String name, Drawable icon, String packName) {
        appName = name;
        appIcon = icon;
        packageName = packName;
    }

    public void setAppName(String newName) { appName = newName; }

    String getAppName() { return appName; }

    public void setAppIcon(Drawable newIcon) { appIcon = newIcon; }

    Drawable getAppIcon() { return appIcon; }

    public void setPackageName(String newPackName) { packageName = newPackName; }

    String getPackageName() { return packageName; }

}
