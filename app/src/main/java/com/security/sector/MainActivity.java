package com.security.sector;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    appInfo[] appNames;
    PackageManager pm;
    static int listViewID = R.id.list_view;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pm = getPackageManager();
        // Define the list of application names if you haven't already and then create the view.
        if (appNames == null)
            appNames = getFormattedAppList();
        createListView();
    }

    public void createListView(){
        listView = (ListView) getView(listViewID);
        // Define and assign the adapter
        appInfoArrayAdapter adapter = new appInfoArrayAdapter(this, appNames);
        listView.setAdapter(adapter);
        // Define on click event listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Intent appInfoIntent = new Intent(MainActivity.this, appInfoActivity.class);
                appInfoIntent.putExtra("selectedPackageName", (
                        (appInfo) parent.getItemAtPosition(position)).packageName);
                appInfoIntent.setFlags(appInfoIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(appInfoIntent);
            }
        });
    }

    public View getView(int id) {
        return findViewById(id);
    }

    public appInfo[] getFormattedAppList() {
        // Get application list
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<appInfo> tempNameList = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            // If it's a system app, just skip it.
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            // Otherwise, add it to the list.
            String newName = (String) pm.getApplicationLabel(packageInfo);
            String newPackageName = packageInfo.packageName;
            tempNameList.add(new appInfo(newName, newPackageName));
        }
        return tempNameList.toArray(new appInfo[tempNameList.size()]);
    }

}