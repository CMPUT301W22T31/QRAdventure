package com.example.qradventure;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class to trial adding a QR code to your account.
 * Tests login, scan, and viewing the scanned qr code from your account.
 * *These tests assume the test device already has an associated account for login.*
 * Robotium test framework is used.
 */
@RunWith(AndroidJUnit4.class)
public class IntentScanTest {
    private Solo solo;

    // rule attribute allows functional testing of activities
    // MAINTENANCE: change MainActivity to whatever the startup activity is.
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);


    /**
     * Runs before all tests; initializes solo object.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // instrumentation allows programmatic control of UI/events (buttons, etc).
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Closes activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /**
     * Checks the startup activity is correct
     * @throws Exception
     */
    @Test
    public void testStartup() throws Exception {
        // assert launch activity is correct
        solo.assertCurrentActivity("Wrong Startup Activity!", MainActivity.class);

        // test that we make it to AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // test we have logged in correctly
        String username = CurrentAccount.getAccount().getUsername();
        assertTrue(solo.waitForText(username, 1, 2000));
    }



    /**
     * Tests the basic app flow for adding a QR code
     * TODO: Adjust when ScanActivity is revised.
     * @throws Exception
     */
    @Test
    public void testAddQR() throws Exception {
        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // click on ScanActivity
        View scanButton = solo.getView("scan");
        solo.clickOnView(scanButton);
        solo.assertCurrentActivity("Scan button failed!", ScanActivity.class);

        // backdoor into PostScanActivity with an intent extra (dummy qr content)
        Intent intent = new Intent(solo.getCurrentActivity(), PostScanActivity.class);
        intent.putExtra("com.example.qradventure.QR_CONTENT", "test silly goose");
        ActivityScenario.launch(intent);

        // assert we made it to PostScanActivity
        solo.assertCurrentActivity("Failed to reach PostScanActivity!", PostScanActivity.class);

        // close dialogue and click add
        // TODO: revise if PostScanActivity changes
        solo.clickOnText("QR code scanned");
        solo.clickOnText("ADD");

        // assert navigation to AccountActivity worked
        solo.assertCurrentActivity
                ("AccountActivity Failed after clicking Add!", AccountActivity.class);

        // View codes, a QR page, and the QR comments
        solo.clickOnText("My Codes");
        solo.assertCurrentActivity("My Codes failed!", MyCodesActivity.class);
        solo.clickOnText("pts");
        solo.assertCurrentActivity("QR Page Failed!", QRPageActivity.class);
        solo.clickOnText("View Comments");
        solo.assertCurrentActivity("View Comments failed!", CommentsActivity.class);
    }


}
