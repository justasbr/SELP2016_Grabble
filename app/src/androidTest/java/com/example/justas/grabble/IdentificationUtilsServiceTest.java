package com.example.justas.grabble;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.justas.grabble.utils.IdentificationUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class IdentificationUtilsServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void returnsAndroidDeviceId() {
        String id = IdentificationUtils.getAndroidId(mContext);
        assertNotNull(id);
    }

    @Test
    public void idHasLengthOf16() {
        String id = IdentificationUtils.getAndroidId(mContext);
        assertEquals(id.length(), 16);
    }

}
