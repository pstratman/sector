package com.security.sector;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class appInfoActivityTest {

    private appInfoActivity mockActivity = new appInfoActivity();

    @Mock
    Context mockContext;

    @Test
    public void shouldMakeSystemCallForFileDescriptors() throws IOException {
        Process mockProc = Mockito.spy(Runtime.getRuntime().exec(""));

        mockActivity.getUsing();
    }

}
