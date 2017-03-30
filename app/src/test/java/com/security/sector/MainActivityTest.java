package com.security.sector;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    private appInfo[] expectedOutputAll = new appInfo[2];
    private appInfo[] expectedOutputSingle = new appInfo[1];
    private MainActivity mockMain = new MainActivity();

    @Mock
    Context mockContext;


    @Before
    public void setup() {
        expectedOutputAll[0] = new appInfo("Test Package 1", "testPackage1");
        expectedOutputAll[1] = new appInfo("Test Package 2", "testPackage2");
        expectedOutputSingle[0] = new appInfo("Test Package 1", "testPackage1");
    }

    @Test
    public void shouldGetAppInfoList(){
        final PackageManager mockManager = Mockito.mock(PackageManager.class);
        final ApplicationInfo testPackage1 = new ApplicationInfo();
        final ApplicationInfo testPackage2 = new ApplicationInfo();
        testPackage1.packageName = "testPackage1";
        testPackage1.flags = 0;
        testPackage2.packageName = "testPackage2";
        testPackage2.flags = 0;
        List<ApplicationInfo> testApplicationList = new ArrayList<>();
        testApplicationList.add(testPackage1);
        testApplicationList.add(testPackage2);

        mockMain.pm = mockManager;
        Mockito.when(mockMain.pm.getInstalledApplications(PackageManager.GET_META_DATA))
                .thenReturn(testApplicationList);

        Mockito.when(mockMain.pm.getApplicationLabel(testPackage1))
                .thenReturn("Test Package 1");
        Mockito.when(mockMain.pm.getApplicationLabel(testPackage2))
                .thenReturn("Test Package 2");

        appInfo[] actual = mockMain.getFormattedAppList();
        Assert.assertTrue("Application List item 0's appName should be what I expect",
                actual[0].appName.equals(expectedOutputAll[0].appName));
        Assert.assertTrue("Application List item 1's appName should be what I expect",
                actual[1].appName.equals(expectedOutputAll[1].appName));
        Assert.assertTrue("Application List item 0's packageName should be what I expect",
                actual[0].packageName.equals(expectedOutputAll[0].packageName));
        Assert.assertTrue("Application List item 1's packageName should be what I expect",
                actual[1].packageName.equals(expectedOutputAll[1].packageName));
    }

    @Test
    public void shouldIgnoreSystemPackages(){
        final PackageManager mockManager = Mockito.mock(PackageManager.class);
        final ApplicationInfo testPackage1 = new ApplicationInfo();
        final ApplicationInfo testPackage2 = new ApplicationInfo();
        testPackage1.packageName = "testPackage1";
        testPackage1.flags = 0;
        testPackage2.packageName = "testPackage2";
        testPackage2.flags = 1;
        List<ApplicationInfo> testApplicationList = new ArrayList<>();
        testApplicationList.add(testPackage1);
        testApplicationList.add(testPackage2);

        mockMain.pm = mockManager;
        Mockito.when(mockMain.pm.getInstalledApplications(PackageManager.GET_META_DATA))
                .thenReturn(testApplicationList);

        Mockito.when(mockMain.pm.getApplicationLabel(testPackage1))
                .thenReturn("Test Package 1");

        appInfo[] actual = mockMain.getFormattedAppList();
        Assert.assertTrue("Application List should only contain one of the items passed",
                actual.length == 1);
        Assert.assertTrue("Application List item 0's appName should be what I expect",
                actual[0].appName.equals(expectedOutputSingle[0].appName));
        Assert.assertTrue("Application List item 0's packageName should be what I expect",
                actual[0].packageName.equals(expectedOutputSingle[0].packageName));
    }

}
