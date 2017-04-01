package com.security.sector;

/**
 * appInfo
 * This class represents the data that exists in the ListView created in MainActivity.
 */
class appInfo {
    String appName;
    String packageName;

    /**
     * appInfo()
     * Constructor for the appInfo Class
     * @param name - the new application name to store
     * @param packName - the new package name to store
     */
    appInfo(String name, String packName) {
        appName = name;
        packageName = packName;
    }

}
