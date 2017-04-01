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

/**
 * MainActivity()
 * This is the activity that is run when Sector is started. It sets up the List of installed
 * applications and allows for the creation of the appInfoActivity by event.
 */
public class MainActivity extends AppCompatActivity {

    appInfo[] appNames;                      // The list of non-system applications on the device
    PackageManager pm;                       // The package manager
    static int listViewID = R.id.list_view;  // The Layout ID of the ListView.
    ListView listView;                       // The actual ListView object.

    /**
     * onCreate()
     * This method is called when the activity is created. It associates the activity with
     * the layout activity_main.
     * @param savedInstanceState - The saved state of the activity, the state of sector isn't saved,
     *                           but this variable is required.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * onStart()
     * This method is called directly after the onCreate method. In MainActivity, this method
     * instantiates our variables if necessary and then calls the method to create the list view.
     */
    @Override
    protected void onStart() {
        super.onStart();
        pm = getPackageManager();
        // Define the list of application names if you haven't already and then create the view.
        if (appNames == null)
            appNames = getFormattedAppList();
        createListView();
    }

    /**
     * createListView()
     * This method creates the ListView for MainActivity and assigns it an adapter and an
     * onItemClickLister which is used to start the appInfoActivity.
     */
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

    /**
     * getView()
     * @param id - The layout ID of the view that we want to get.
     * @return (View) - the view that was found by the findViewByID builtin method.
     */
    public View getView(int id) {
        return findViewById(id);
    }

    /**
     * getFormattedAppList()
     * This method is used to create the array of appInfo objects that is used by the
     * appInfoArrayAdapter in the ListView. The method gets the list of installed applications and
     * filters out the system applications.
     * @return (appInfo[]) - the array of appInfo objects created in the method.
     */
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