package com.security.sector;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    PackageManager pm;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pm = getPackageManager();
        appInfo[] appNames = getFormattedAppList();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define and assign the adapter
        appInfoArrayAdapter adapter = new appInfoArrayAdapter(this, appNames);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        // Define on click event listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent appInfoIntent = new Intent(MainActivity.this, appInfoActivity.class);
                appInfoIntent.putExtra("selectedPackageName", (
                        (appInfo) parent.getItemAtPosition(position)).getPackageName());
                startActivity(appInfoIntent);
            }
        });
    }

    private appInfo[] getFormattedAppList() {
        // Get application list
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int index = 0;
        appInfo[] tempNameList = new appInfo[packages.size()];
        for (ApplicationInfo packageInfo : packages) {
            String newName = (String) pm.getApplicationLabel(packageInfo);
            Drawable newIcon = pm.getApplicationIcon(packageInfo);
            String newPackageName = packageInfo.packageName;
            tempNameList[index] = new appInfo(newName, newIcon, newPackageName);
            Log.d(TAG, "Adding: " + newName);
            Log.d(TAG, "With package name: " + newPackageName);
            index++;
        }
        return tempNameList;
    }

}