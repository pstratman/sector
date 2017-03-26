package com.security.sector;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;


public class appInfoActivity extends AppCompatActivity {

    private static final String TAG = "appInfoActivity";
    String packageName;
    String appName;
    String requestedPerms = "";
    int PID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_layout);
        packageName = bundle.getString("selectedPackageName");
        configureInfo();
        String getProcInfoCommand = "ls -l /proc/" + PID + "/fd";
        String lsProcCommand = "ls -l /proc";
        if (PID == 0) {
            Log.d(TAG, "I didn't find Shit!");
        } else {
            doCommand(getProcInfoCommand, true);
            Log.d(TAG, "PID: " + PID);
            doCommand(lsProcCommand, true);
        }
    }

    private String doCommand(String command, Boolean debugFlag) {
        StringBuilder output = new StringBuilder();
        Process proc;
        try{
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        if (debugFlag)
            Log.d(TAG, output.toString());
        return output.toString();
    }


    private void configureInfo() {
        // Get managers
        PackageManager pm = getPackageManager();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

        // Get text views
        TextView textViewAppName = (TextView) findViewById(R.id.appName);
        TextView textViewReqPerms = (TextView) findViewById(R.id.requestedResourcesContent);

        try {
            ApplicationInfo currentApp = pm.getApplicationInfo(packageName, 0);
            PackageInfo currentAppPackInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
            appName = (String) pm.getApplicationLabel(currentApp);
            String[] permissions = currentAppPackInfo.requestedPermissions;
            if (permissions != null) {
                for (int i = 0; i < permissions.length; i++ ){
                    requestedPerms += permissions[i] + "\n";
                }
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "I was Unable to find the pacakge " + packageName);
        }
        // Find the process ID of the package
        for (int i = 0; i < runningAppProcessInfo.size(); i++) {
            String runningProc = runningAppProcessInfo.get(i).processName;
            int runningProcID = runningAppProcessInfo.get(i).pid;
            Log.d(TAG, runningProc + " is running.");
            if(runningAppProcessInfo.get(i).processName.equals(packageName)) {
                Log.d(TAG, "I found " + runningProc);
                Log.d(TAG, "with process ID: " + runningProcID);
                PID = runningProcID;
                break;
            }
        }
        // Set textViews
        textViewAppName.setText(appName);
        if (requestedPerms == "") {
            requestedPerms = "No requested permissions on file.";
        }
        textViewReqPerms.setText(requestedPerms);
    }

    // Clean up the activity when you go back to the main activity.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
