package com.security.sector;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class appInfoActivity extends AppCompatActivity {

    // Get managers
    PackageManager pm;
    ActivityManager am;
    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo;

    private static final String TAG = "appInfoActivity";
    String packageName;
    String appName;
    String requestedPerms = "No requested permissions on file.";
    String usingResources = "PID not found running on device.";
    int PID = 0;

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
        setRequestedAndName();
        setPID();
        if (PID != 0)
            usingResources = getUsing();
        setTextViews();
    }

    private String getUsing() {
        String command = "ls -l /proc/" + PID + "/fd | sort";
        StringBuilder output = new StringBuilder();
        List<String> sortList = new ArrayList<>();
        Process proc;
        try{
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                String[] tempSplit = line.split(" ");
                if (tempSplit.length > 2) {
                    String resourceToAdd = tempSplit[tempSplit.length - 1];
                    sortList.add(resourceToAdd.trim());
                }
            }
            Collections.sort(sortList.subList(1, sortList.size()));
            for (String fd : sortList) {
                output.append(fd).append("\n");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return output.toString();
    }


    private void setRequestedAndName() {
        try {
            ApplicationInfo currentApp = pm.getApplicationInfo(packageName, 0);
            PackageInfo currentAppPackInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
            appName = (String) pm.getApplicationLabel(currentApp);
            String[] permissions = currentAppPackInfo.requestedPermissions;
            if (permissions != null) {
                requestedPerms = "";
                for (String permission : permissions) {
                    requestedPerms += permission + "\n";
                }
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "I was Unable to find the package " + packageName);
        }
    }

    private void setTextViews() {
        // Set textViews
        ((TextView) findViewById(R.id.appName)).setText(appName);
        ((TextView) findViewById(R.id.usingResourcesContent)).setText(usingResources);
        ((TextView) findViewById(R.id.requestedResourcesContent)).setText(requestedPerms);
    }

    private void setPID() {
        // Find the process ID of the package
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            String runningProc = runningAppProcessInfo.get(i).processName;
            int runningProcID = runningAppProcessInfo.get(i).pid;
            Log.d(TAG, runningProc + " is running.");
            if (runningAppProcessInfo.get(i).processName.equals(packageName)) {
                PID = runningProcID;
                break;
            }
        }
    }

    // Clean up the activity when you go back to the main activity.
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
        usingResources = null;
        System.gc();
    }
}
