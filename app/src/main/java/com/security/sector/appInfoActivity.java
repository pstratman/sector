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

    String packageName; // The package name of the target application
    String appName;     // The display name of the target application
    String requestedPerms = ""; // The default value of the requested permissions
    String requestedUsesPerms = "";
    String openFDs = "PID not found running on device.";  // The default value of the apps open fd's
    int PID = 0; // The process identifier for the targeted application
    int versionCode = 0;
    String versionName = "";
    Date installedDate;
    Date updatedDate;
    int appUID = -1;
    String packageSigHash;
    String sharedLibFiles;

    /**
     * onCreate()
     * This method is called when the activity is created. It associates the activity with
     * the layout activity_main.
     * @param savedInstanceState - The saved state of the activity, the state of sector isn't saved,
     *                           but this variable is required.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instantiate managers
        pm = getPackageManager();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        runningAppProcessInfo = am.getRunningAppProcesses();

        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_layout);
        packageName = bundle.getString("selectedPackageName");

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
            installedDate = new Date(currentAppPackInfo.firstInstallTime * 1000);
            updatedDate = new Date(currentAppPackInfo.lastUpdateTime * 1000);
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
                    if (permission != null)
                        requestedPerms += permission.loadDescription(pm);
                }
            } else {
                requestedPerms = "No other permissions listed in application manifest.";
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            requestedPerms = "Unable to find application package on device";
            openFDs = "Unable to find application package on device";
        }
    }

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
        String firstInstDisplay = getString(R.string.first_install_label) +
                "       " + installedDate.toString();
        String lastUpdateDisplay = getString(R.string.last_update_label) +
                "    " + updatedDate.toString();
        ((TextView) findViewById(R.id.firstInstallLabel)).setText(firstInstDisplay);
        ((TextView) findViewById(R.id.lastUpdateLabel)).setText(lastUpdateDisplay);

        // Signature Hash info
        ((TextView) findViewById(R.id.sigHashContent)).setText(packageSigHash);
    }

    /**
     *
     *
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
            int runningProcID = runningAppProcessInfo.get(i).pid;
            if (runningProc.equals(packageName)) {
                PID = runningProcID;
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
