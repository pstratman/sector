package com.security.sector;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * appInfoActivity()
 * This is the second activity that is only ever launched from the MainActivity. It fills out the
 * requested permissions and the open file descriptors.
 */
public class appInfoActivity extends AppCompatActivity {

    PackageManager pm;   // The Package Manager
    ActivityManager am;  // The Activity Manager
    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo; // The list of running apps

    String packageName = ""; // The package name of the target application
    String appName = "";     // The display name of the target application
    String requestedPerms = ""; // The default value of the requested permissions
    String requestedUsesPerms = "";
    String openFDs = "PID not found running on device.";  // The default value of the apps open fd's
    int PID = 0; // The process identifier for the targeted application
    int versionCode = 0; // The version code of the application or 0 if not defined.
    String versionName = ""; // The version name of the application if defined in the manifest.
    Date installedDate; // The Date that the application was installed on the device.
    Date updatedDate; // The Date that the application was last updated on the device.
    int appUID = -1; // The user ID that the application runs under.
    String packageSigHash = ""; // The signature hash or hashes for the package
    String sharedLibFiles = ""; // The shared library files for the package if any.

    /**
     * onCreate()
     * This method is called when the activity is created. It associates the activity with
     * the layout activity_main.
     * @param savedInstanceState - The saved state of the activity, the state of sector isn't saved,
     *                           but this variable is required.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.app_info_layout);
        packageName = bundle.getString("selectedPackageName");
        doInstantiate();
    }

    /**
     * doInstantiate()
     * This method is used to organize the function calls so that all of them are called in a
     * logical order. It was partly created so I could test the functionality of the onCreate
     * method without having to do some unit test gymnastics.
     */
    public void doInstantiate() {
        // Instantiate managers
        pm = getPackageManager();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningAppProcessInfo = am.getRunningAppProcesses();
        appName = packageName;
        gatherApplicationInfo();
        setPackageSigHash();
        setPID();
        if (PID != 0)
            openFDs = getUsing();
        setupTabViews();
        setTextViews();
    }

    /**
     * getUsing()
     * This method sets up the command which is used to get the open file descriptors and handles
     * the system call's output.
     * @return (String) - The list of open file descriptors for a particular PID
     */
    public String getUsing() {
        String command = "ls -l /proc/" + PID + "/fd | sort";
        List<String> sortList = new ArrayList<>();
        List<String> output = doCommand(command);
        StringBuilder retString = new StringBuilder();
        if (output.size() != 0) {
            for (String lineItem : output) {
                String[] tempTokens = lineItem.split(" ");
                if (tempTokens.length > 2) {
                    String addItem = tempTokens[tempTokens.length - 1];
                    sortList.add(addItem.trim());
                }
            }
            Collections.sort(sortList.subList(1, sortList.size()));
            for (String fd : sortList) {
                retString.append(fd).append("\n");
            }
        }
        return retString.toString();
    }

    /**
     * doCommand()
     * This method handles making the system call and returning the output.
     * @param command - the Command to execute on the device.
     * @return (List) - The output as a list of strings
     */
    public List<String> doCommand(String command) {
        Process proc;
        List<String> output = new ArrayList<>();
        try{
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                output.add(line);
            }
        } catch (Exception e){
            if (e.toString().contains("SELinux"))
                openFDs = "You do not have permissions to view this information";
        }
        return output;
    }

    /**
     * setRequestedAndName()
     * This method sets the requested resources and appName strings by getting the information from
     * the PackageManager.
     */
    public void gatherApplicationInfo() {
        try {
            // Get App Info
            ApplicationInfo currentApp = pm.getApplicationInfo(packageName, 0);
            // Get shared libs
            String[] libFiles = currentApp.sharedLibraryFiles;
            if (libFiles != null) {
                for (String libFile : libFiles) {
                    sharedLibFiles += libFile + "\n";
                }
            } else {
                sharedLibFiles = "No shared library files found for package";
            }
            // Set UID and app name
            appUID = currentApp.uid;
            appName = (String) pm.getApplicationLabel(currentApp);

            // Get Pack info
            PackageInfo currentAppPackInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
            // Set version info
            versionCode = currentAppPackInfo.versionCode;
            versionName = currentAppPackInfo.versionName;
            if (versionName == null)
                versionName = "No version name found.";
            // Set installation/update info
            installedDate = new Date(currentAppPackInfo.firstInstallTime);
            updatedDate = new Date(currentAppPackInfo.lastUpdateTime);
            // Get and set requested permissions from manifest
            String[] requestedPermissions = currentAppPackInfo.requestedPermissions;
            if (requestedPermissions != null) {
                for (String permission : requestedPermissions) {
                    if (permission != null)
                        requestedUsesPerms += permission + "\n";
                }
            } else {
                requestedUsesPerms = "No requested permissions listed in application manifest.";
            }
            PermissionInfo[] permissions = currentAppPackInfo.permissions;
            if (permissions != null){
                for (PermissionInfo permission : permissions) {
                    if (permission != null) {
                        if (permission.loadDescription(pm) != null) {
                            requestedPerms += permission.loadDescription(pm);
                            requestedPerms += "\n";
                        }
                    }
                }
            } else {
                requestedPerms = "No other permissions listed in application manifest.";
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            requestedPerms = "Unable to find application package on device";
            requestedUsesPerms = "Unable to find application package on device";
            openFDs = "Unable to find application package on device";
            sharedLibFiles = "Unable to find application package on device";
        }
    }

    /**
     * setPackageSigHash()
     * This method gets the package hash information from the package manager and sets the class
     * variable with that information if it can.
     */
    public void setPackageSigHash() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            Signature[] currentPackageSigs = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES).signatures;

            for (Signature sig : currentPackageSigs) {
                packageSigHash += sig.hashCode() + "\n";
            }
        } catch (PackageManager.NameNotFoundException e) {
            packageSigHash = "I couldn't find the package on the system";
        }
    }

    /**
     * setTextViews()
     * This method sets all the text that is displayed in the test views on the additional
     * information tab.
     */
    public void setTextViews() {
        // Set app name
        ((TextView) findViewById(R.id.appName)).setText(appName);

        // Set using resources
        ((TextView) findViewById(R.id.openFDSContent)).setText(openFDs);
        ((TextView) findViewById(R.id.sharedLibsContent)).setText(sharedLibFiles);

        // Set requested
        ((TextView) findViewById(R.id.requestedResourcesContent)).setText(requestedUsesPerms);
        ((TextView) findViewById(R.id.otherResourcesContent)).setText(requestedPerms);

        // Additional info
        // Set UID
        String appUIDDisplay = "Application runtime UID:      " + appUID;
        ((TextView) findViewById(R.id.application_uid_label)).setText(appUIDDisplay);
        // Version Info
        String versionCodeInfo = getString(R.string.version_code_label) +
                "     " + String.valueOf(versionCode);
        String versionNameInfo = getString(R.string.version_name_label) +
                "    " + versionName;
        ((TextView) findViewById(R.id.versionCodeLabel)).setText(String.valueOf(versionCodeInfo));
        ((TextView) findViewById(R.id.versionNameLabel)).setText(versionNameInfo);

        // Installation Info
        String installedDateString = DateFormat.getDateTimeInstance().format(installedDate);
        String updatedDateString = DateFormat.getDateTimeInstance().format(updatedDate);
        String firstInstDisplay = getString(R.string.first_install_label) +
                "       " + installedDateString;
        String lastUpdateDisplay = getString(R.string.last_update_label) +
                "    " + updatedDateString;
        ((TextView) findViewById(R.id.firstInstallLabel)).setText(firstInstDisplay);
        ((TextView) findViewById(R.id.lastUpdateLabel)).setText(lastUpdateDisplay);

        // Signature Hash info
        ((TextView) findViewById(R.id.sigHashContent)).setText(packageSigHash);
    }

    /**
     * setupTabViews()
     * This method sets up the Tab host on the activity and adds our tabs to it.
     */
    public void setupTabViews() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHostMain);
        tabHost.setup();

        // Tab 1
        TabHost.TabSpec usingTab = tabHost.newTabSpec("Used Resources");
        usingTab.setContent(R.id.tab1);
        usingTab.setIndicator("Used Resources");
        tabHost.addTab(usingTab);

        // Tab 2
        TabHost.TabSpec requestedTab = tabHost.newTabSpec("Requested Resources");
        requestedTab.setContent(R.id.tab2);
        requestedTab.setIndicator("Requested Resources");
        tabHost.addTab(requestedTab);

        // Tab 3
        TabHost.TabSpec additionalInfoTab = tabHost.newTabSpec("Additional Info");
        additionalInfoTab.setContent(R.id.tab3);
        additionalInfoTab.setIndicator("Additional Info");
        tabHost.addTab(additionalInfoTab);
    }

    /**
     * setPID()
     * This method gets the process ID for the target application if it can.
     */
    public void setPID() {
        // Find the process ID of the package
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            String runningProc = runningAppProcessInfo.get(i).processName;
            if (runningProc.equals(packageName)) {
                PID = runningAppProcessInfo.get(i).pid;
                break;
            }
        }
    }

    /**
     * onDestroy()
     * This method does the activity cleanup when the user navigates away from it. This is a
     * recommended practice to save on the amount of memory an application needs to run.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        pm = null;
        am = null;
        // - Other
        runningAppProcessInfo = null;
        packageName = null;
        appName = null;
        requestedPerms = null;
        openFDs = null;
        System.gc();
    }
}
