package com.security.sector;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        final ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.list_item, appNames);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        // Define on click event listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
            }
        });
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
