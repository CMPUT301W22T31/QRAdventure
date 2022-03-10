package com.example.qradventure;


import static org.junit.Assert.assertTrue;

import android.app.Activity;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;



public class CameraTest {

    private Solo solo;
    QR testQR;

    @Rule
    public ActivityTestRule<AccountActivity> rule = new ActivityTestRule<>(AccountActivity.class, true, true);



    @Before
    public void setUp()throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }


     //TODO: Not sure if testing this is going to be possible without adding a bunch of globals?
//    // Test to scan a QR code. Times out after 20 Seconds
//    @Test(timeout = 30000)
//    public void testScanQR(){
//        solo.assertCurrentActivity("Wrong", AccountActivity.class);
//
//        solo.clickOnImage(9);
//
//        // wait to scan QR code
//        AccountActivity camera = (AccountActivity)solo.getCurrentActivity();
//
//        while (camera.scannedQR == null);
//
//        assertTrue(camera.globalQRData.contains(camera.scannedQR));
//
//    }


}
