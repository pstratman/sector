package com.security.sector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.Signature;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class appInfoActivityTest {

    private appInfoActivity mockActivity = new appInfoActivity();
    private List<String> mockCommandOutput = new ArrayList<>();
    private String[] mockPerms = new String[2];
    private StringBuilder expected = new StringBuilder();

    @Mock
    Context mockContext;

    @Before
    public void setup() {
        String out1 = "lr-x------ 1 u0_a72 u0_a72 64 2017-03-30 20:31 40 -> /data/app/com.security.sector-2/split_lib_slice_6_apk.apk";
        String out2 = "lr-x------ 1 u0_a72 u0_a72 64 2017-03-30 20:31 41 -> /data/app/com.security.sector-2/split_lib_slice_7_apk.apk";
        String out3 = "lrwx------ 1 u0_a72 u0_a72 64 2017-03-30 20:31 27 -> socket:[27665]";
        String out4 = "length 0";
        mockCommandOutput.add(out1);
        mockCommandOutput.add(out2);
        mockCommandOutput.add(out3);
        mockCommandOutput.add(out4);

        String str1 = "/data/app/com.security.sector-2/split_lib_slice_6_apk.apk";
        String str2 = "/data/app/com.security.sector-2/split_lib_slice_7_apk.apk";
        String str3 = "socket:[27665]";
        expected.append(str1).append("\n");
        expected.append(str2).append("\n");
        expected.append(str3).append("\n");

        mockPerms[0] = "CAN_EAT";
        mockPerms[1] = "CAN_SLEEP_ON_COUCH";
    }


    @Test
    public void shouldMakeSystemCallForFileDescriptors() throws IOException {
        appInfoActivity appInfoSpy = Mockito.spy(new appInfoActivity());
        Mockito.doReturn(mockCommandOutput).when(appInfoSpy).doCommand(Mockito.anyString());

        String actual = appInfoSpy.getUsing();
        Assert.assertTrue("It should return a list of open file descriptors when getUsing is called",
                expected.toString().equals(actual));
    }

    @Test
    public void shouldSetRequestedPermsAndName() {

        mockActivity.packageName = "testPackage";
        String appName = "test Package 1";
        PackageManager mockManager = Mockito.mock(PackageManager.class);
        ApplicationInfo mockAppInfoObj = Mockito.mock(ApplicationInfo.class);
        PackageInfo mockPackInfoObj = Mockito.mock(PackageInfo.class);
        mockPackInfoObj.requestedPermissions = mockPerms;
        mockActivity.pm = mockManager;
        String expectedPerms = "";
        expectedPerms += mockPerms[0] + "\n";
        expectedPerms += mockPerms[1] + "\n";

        PermissionInfo mockPermObj = Mockito.mock(PermissionInfo.class);
        PermissionInfo mockPermObj2 = Mockito.mock(PermissionInfo.class);
        PermissionInfo[] mockOtherPermsArray = new PermissionInfo[2];
        mockOtherPermsArray[0] = mockPermObj;
        mockOtherPermsArray[1] = mockPermObj2;
        Mockito.when(mockPermObj.loadDescription(mockManager)).thenReturn(mockPerms[0] + "\n");
        Mockito.when(mockPermObj2.loadDescription(mockManager)).thenReturn(mockPerms[1] + "\n");
        mockPackInfoObj.permissions = mockOtherPermsArray;

        try {
            Mockito.doReturn(mockAppInfoObj).when(mockManager)
                    .getApplicationInfo(mockActivity.packageName, 0);
            Mockito.doReturn(mockPackInfoObj).when(mockManager)
                    .getPackageInfo(mockActivity.packageName, PackageManager.GET_PERMISSIONS);
            Mockito.doReturn(appName).when(mockManager).getApplicationLabel(mockAppInfoObj);
        } catch (Exception e) {

        }
        mockActivity.gatherApplicationInfo();

        Assert.assertTrue("Should configure the permissions if they exist",
                expectedPerms.equals(mockActivity.requestedUsesPerms));
        Assert.assertTrue("Should configure the permissions if they exist",
                expectedPerms.equals(mockActivity.requestedPerms));
        Assert.assertTrue("Should have set the appName",
                mockActivity.appName.equals(appName));
    }

    @Test
    public void shouldSetDates() throws PackageManager.NameNotFoundException {
        mockActivity.packageName = "testPackage";
        String appName = "test Package 1";
        PackageManager mockManager = Mockito.mock(PackageManager.class);
        ApplicationInfo mockAppInfoObj = Mockito.mock(ApplicationInfo.class);
        PackageInfo mockPackInfoObj = Mockito.mock(PackageInfo.class);
        long beginningOfTime = 0;
        Date testDate = new Date(beginningOfTime * 1000);
        mockPackInfoObj.firstInstallTime = beginningOfTime;
        mockPackInfoObj.lastUpdateTime = beginningOfTime;
        mockActivity.pm = mockManager;

        Mockito.doReturn(mockAppInfoObj).when(mockManager)
                .getApplicationInfo(mockActivity.packageName, 0);
        Mockito.doReturn(mockPackInfoObj).when(mockManager)
                .getPackageInfo(mockActivity.packageName, PackageManager.GET_PERMISSIONS);
        Mockito.doReturn(appName).when(mockManager).getApplicationLabel(mockAppInfoObj);

        mockActivity.gatherApplicationInfo();

        Assert.assertTrue("First installation date should be set correctly",
                mockActivity.installedDate.toString().equals(testDate.toString()));
        Assert.assertTrue("Last update date should be set correctly",
                mockActivity.updatedDate.toString().equals(testDate.toString()));
    }

    @Test
    public void shouldSetSharedLibs() throws PackageManager.NameNotFoundException {
        mockActivity.packageName = "testPackage";
        String appName = "test Package 1";
        PackageManager mockManager = Mockito.mock(PackageManager.class);
        ApplicationInfo mockAppInfoObj = Mockito.mock(ApplicationInfo.class);
        PackageInfo mockPackInfoObj = Mockito.mock(PackageInfo.class);
        String sharedLib1 = "Shared Library 1";
        String sharedLib2 = "Shared Library 2";
        String sharedLib3 = "Shared Library 3";
        String[] sharedLibsTest = new String[3];
        sharedLibsTest[0] = sharedLib1;
        sharedLibsTest[1] = sharedLib2;
        sharedLibsTest[2] = sharedLib3;
        mockAppInfoObj.sharedLibraryFiles = sharedLibsTest;
        mockActivity.pm = mockManager;
        String sharedLibsExpected = sharedLib1 + "\n" + sharedLib2 + "\n" + sharedLib3 + "\n";

        Mockito.doReturn(mockAppInfoObj).when(mockManager)
                .getApplicationInfo(mockActivity.packageName, 0);
        Mockito.doReturn(mockPackInfoObj).when(mockManager)
                .getPackageInfo(mockActivity.packageName, PackageManager.GET_PERMISSIONS);
        Mockito.doReturn(appName).when(mockManager).getApplicationLabel(mockAppInfoObj);

        mockActivity.gatherApplicationInfo();

        Assert.assertTrue("Should combine the array of libs into one cohesive string",
                mockActivity.sharedLibFiles.equals(sharedLibsExpected));
    }

    @Test
    public void shouldInstantiateClassVarsAfterOnCreate() {
        appInfoActivity mockActivitySpy = Mockito.spy(new appInfoActivity());
        PackageManager mockPackMan = Mockito.mock(PackageManager.class);
        ActivityManager mockActiveMan = Mockito.mock(ActivityManager.class);
        mockActivitySpy.PID = 0;
        mockActivitySpy.packageName = "Test Pack";
        ActivityManager.RunningAppProcessInfo testRunningAppProcInfoObj = new ActivityManager.RunningAppProcessInfo();
        List<ActivityManager.RunningAppProcessInfo> expectedList = new ArrayList<>();
        expectedList.add(testRunningAppProcInfoObj);

        Mockito.doReturn(mockPackMan).when(mockActivitySpy).getPackageManager();
        Mockito.doReturn(mockActiveMan).when(mockActivitySpy)
                .getSystemService(Context.ACTIVITY_SERVICE);
        Mockito.doNothing().when(mockActivitySpy).gatherApplicationInfo();
        Mockito.doNothing().when(mockActivitySpy).setPackageSigHash();
        Mockito.doNothing().when(mockActivitySpy).setPID();
        Mockito.doReturn("").when(mockActivitySpy).getUsing();
        Mockito.doNothing().when(mockActivitySpy).setupTabViews();
        Mockito.doNothing().when(mockActivitySpy).setTextViews();
        Mockito.when(mockActiveMan.getRunningAppProcesses()).thenReturn(expectedList);

        mockActivitySpy.doInstantiate();
        Assert.assertTrue("Should set the package manager", mockActivitySpy.pm == mockPackMan);
        Assert.assertTrue("Should set the activity manager", mockActivitySpy.am == mockActiveMan);
        Assert.assertTrue("Should set the list of running apps",
                mockActivitySpy.runningAppProcessInfo == expectedList);
        Assert.assertTrue("Should set the app name to the package name initially",
                mockActivitySpy.appName.equals(mockActivitySpy.packageName));
        mockActivitySpy.PID = 15;
        mockActivitySpy.doInstantiate();
        Mockito.verify(mockActivitySpy, Mockito.times(2)).gatherApplicationInfo();
        Mockito.verify(mockActivitySpy, Mockito.times(2)).setPackageSigHash();
        Mockito.verify(mockActivitySpy, Mockito.times(2)).setPID();
        Mockito.verify(mockActivitySpy, Mockito.times(1)).getUsing();
        Mockito.verify(mockActivitySpy, Mockito.times(2)).setupTabViews();
        Mockito.verify(mockActivitySpy, Mockito.times(2)).setTextViews();
    }

    @Test
    public void shouldSetDefaultsIfPackageNotFound() throws PackageManager.NameNotFoundException {

        PackageManager mockManager = Mockito.mock(PackageManager.class);
        mockActivity.packageName = "testPackage";
        mockActivity.pm = mockManager;

        Mockito.doThrow(new PackageManager.NameNotFoundException())
                .when(mockManager).getApplicationInfo(mockActivity.packageName, 0);
        mockActivity.gatherApplicationInfo();
        Assert.assertTrue(
                "Should set descriptive text on requested permissions if the package wasn't found",
                mockActivity.requestedPerms.equals("Unable to find application package on device"));
        Assert.assertTrue(
                "requestedUsesPerms should be empty since it was never set",
                mockActivity.requestedUsesPerms.equals("Unable to find application package on device"));
        Assert.assertTrue(
                "Should set descriptive text on open FDs if the package wasn't found",
                mockActivity.openFDs.equals("Unable to find application package on device"));
        Assert.assertTrue(
                "Should set descriptive text on sharedLibs if the package wasn't found",
                mockActivity.sharedLibFiles.equals("Unable to find application package on device"));
        Assert.assertTrue(
                "Should leave the UID as -1", mockActivity.appUID == -1);
        Assert.assertTrue(
                "appName should be empty since it was never set", mockActivity.appName.equals(""));

    }

    @Test
    public void shouldLeaveRequestedTagIfNoneOnFile() {
        mockActivity.packageName = "testPackage";
        String appName = "test Package 1";
        PackageManager mockManager = Mockito.mock(PackageManager.class);
        ApplicationInfo mockAppInfoObj = Mockito.mock(ApplicationInfo.class);
        PackageInfo mockPackInfoObj = Mockito.mock(PackageInfo.class);
        mockPackInfoObj.requestedPermissions = null;
        mockActivity.pm = mockManager;
        String expectedPerms = "No requested permissions listed in application manifest.";
        try {
            Mockito.doReturn(mockAppInfoObj).when(mockManager)
                    .getApplicationInfo(mockActivity.packageName, 0);
            Mockito.doReturn(mockPackInfoObj).when(mockManager)
                    .getPackageInfo(mockActivity.packageName, PackageManager.GET_PERMISSIONS);
            Mockito.doReturn(appName).when(mockManager).getApplicationLabel(mockAppInfoObj);
        } catch (Exception e) {

        }
        mockActivity.gatherApplicationInfo();

        Assert.assertTrue("Should leave the permissions tag if they don't exist",
                expectedPerms.equals(mockActivity.requestedUsesPerms));
        Assert.assertTrue("Should have set the appName",
                mockActivity.appName.equals(appName));

    }

    @Test
    public void shouldSetTextViews(){
        appInfoActivity mockActivitySpy = Mockito.spy(new appInfoActivity());
        TextView mockTextView = Mockito.mock(TextView.class);
        Mockito.doReturn(mockTextView).when(mockActivitySpy).findViewById(Mockito.anyInt());
        Mockito.doReturn("").when(mockActivitySpy).getString(Mockito.anyInt());
        Date mockDate = Mockito.mock(Date.class);
        mockActivitySpy.versionName = "1.0.0";
        mockActivitySpy.versionCode = 1;
        mockActivitySpy.installedDate = mockDate;
        mockActivitySpy.updatedDate = mockDate;
        Mockito.when(mockDate.toString()).thenReturn("");
        mockActivitySpy.setTextViews();
        Mockito.verify(mockActivitySpy, Mockito.times(11)).findViewById(Mockito.anyInt());
    }

    @Test
    public void shouldSetThePID() {
        // Setup running proc info
        List<ActivityManager.RunningAppProcessInfo> mockRunningProcs = new ArrayList<>();
        ActivityManager.RunningAppProcessInfo mockRunningProc1 = Mockito.mock(ActivityManager.RunningAppProcessInfo.class);
        ActivityManager.RunningAppProcessInfo mockRunningProc2 = Mockito.mock(ActivityManager.RunningAppProcessInfo.class);
        mockRunningProc1.processName = "Test Process 1";
        mockRunningProc1.pid = 1234;
        mockRunningProc2.processName = "Test Process 2";
        mockRunningProc2.pid = 5678;
        mockRunningProcs.add(mockRunningProc1);
        mockRunningProcs.add(mockRunningProc2);

        // Assign to appInfoActivity and set name
        mockActivity.runningAppProcessInfo = mockRunningProcs;
        mockActivity.packageName = "Test Process 2";

        // Run
        mockActivity.setPID();

        Assert.assertTrue("Sector should set the process id by appName", mockActivity.PID == mockRunningProc2.pid);

    }

    @Test
    public void shouldSetTabViews() {
        appInfoActivity mockActivitySpy = Mockito.spy(new appInfoActivity());
        TabHost mockTabHost = Mockito.mock(TabHost.class);
        TabHost.TabSpec mockTabSpec = Mockito.mock(TabHost.TabSpec.class);

        Mockito.doReturn(mockTabHost).when(mockActivitySpy).findViewById(Mockito.anyInt());
        Mockito.when(mockTabHost.newTabSpec(Mockito.anyString())).thenReturn(mockTabSpec);

        mockActivitySpy.setupTabViews();
        Mockito.verify(mockTabHost, Mockito.times(3)).addTab(Mockito.any(TabHost.TabSpec.class));
    }

}
