package com.security.sector;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    PackageManager pm;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pm = getPackageManager();
        List<String> appNames = getAppNames();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Define and assign the adapter
        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.list_item, appNames);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        // Define on click event listener


    }

    private List<String> getAppNames() {
        // Get application list
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> tempNameList = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            String newName = (String) pm.getApplicationLabel(packageInfo);
            tempNameList.add(newName);
            Log.d(TAG, "Adding: " + newName);
        }
        return tempNameList;
    }

}
