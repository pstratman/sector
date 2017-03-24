package com.security.sector;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;


public class appInfoActivity extends AppCompatActivity {

    private static final String TAG = "appInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_layout);
        configureInfo(bundle.getString("selectedPackageName"));
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
