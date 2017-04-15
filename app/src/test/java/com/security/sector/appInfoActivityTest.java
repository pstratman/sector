package com.security.sector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
        Assert.assertTrue("Should have set the appName",
                mockActivity.appName.equals(appName));

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
