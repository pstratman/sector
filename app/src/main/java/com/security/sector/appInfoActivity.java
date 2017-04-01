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

    public String getUsing() {
        String command = "ls -l /proc/" + PID + "/fd | sort";
        List<String> sortList = new ArrayList<>();
        List<String> output = doCommand(command);
        StringBuilder retString = new StringBuilder();
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
        return retString.toString();
    }

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
            e.printStackTrace();
        }
        return output;
    }

    public void setRequestedAndName() {
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

    public void setTextViews() {
        // Set textViews
        ((TextView) findViewById(R.id.appName)).setText(appName);
        ((TextView) findViewById(R.id.usingResourcesContent)).setText(usingResources);
        ((TextView) findViewById(R.id.requestedResourcesContent)).setText(requestedPerms);
    }

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
