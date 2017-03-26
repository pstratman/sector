package com.security.sector;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
    int PID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_layout);
        packageName = bundle.getString("selectedPackageName");
        configureInfo(packageName);
        configureVars();
        if (PID == 0) {
            Log.d(TAG, "I didn't find Shit!");
        } else {
            String getProcInfoCommand = "ls -l /proc/" + PID + "/fd";
            doCommand(getProcInfoCommand, true);
        }

    }

    private void configureVars() {
        ActivityManager am = (ActivityManager) getSystemService(appInfoActivity.this.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

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
    }

    private String doCommand(String command, Boolean debugFlag) {
        StringBuffer output = new StringBuffer();
        Process proc;
        try{
            proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                output.append(line + "\n");
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        if (debugFlag)
            Log.d(TAG, output.toString());
        return output.toString();
    }


    private void configureInfo(String packageName) {
        // Get package manager
        PackageManager pm = getPackageManager();

        // Get text views
        TextView textViewAppName = (TextView) findViewById(R.id.appName);


        try {
            ApplicationInfo currentApp = pm.getApplicationInfo(packageName, 0);
            textViewAppName.setText(pm.getApplicationLabel(currentApp));

        }
        catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "I was Unable to find the pacakge " + packageName);
        }
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
