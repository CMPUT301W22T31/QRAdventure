package com.example.qradventure;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for a basic tour of the app
 * Tests login, navigation to primary activities, scan, and viewing the scanned code.
 * *These tests assume the test device already has an associated account for login.*
 * Robotium test framework is used.
 */
@RunWith(AndroidJUnit4.class)
public class IntentTestScan {
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
     * Checks the startup activity is correct
     * @throws Exception
     */
    @Test
    public void testStartup() throws Exception {
        // assert launch activity is correct
        Activity activity = rule.getActivity();
        solo.assertCurrentActivity("Wrong Startup Activity!", MainActivity.class);

        // test that we make it to AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // test we have logged in correctly
        String username = CurrentAccount.getAccount().getUsername();
        assertTrue(solo.waitForText(username, 1, 2000));
    }

    /**
     * Tests the navbar, which should be present between all* 4 primary activities (-Scan)
     * TODO: Adjust when ScanActivity is revised.
     * @throws Exception
     */
    @Test
    public void testNavbar() throws Exception {
        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        /*
        // test leaderboard button
        // TODO: leaderboard activity needs the navbar. *part 4*
        *View lbButton = solo.getView("leaderboards");
        *solo.clickOnView(lbButton);
        *solo.assertCurrentActivity("Leaderboard button failed!", LeaderboardActivity.class);
        */

        // test search players button
        View spButton = solo.getView("search_players");
        solo.clickOnView(spButton);
        solo.assertCurrentActivity("Search Players button failed!", SearchPlayersActivity.class);

        /*
        // test scan button
        * TODO: ScanActivity does not have a navbar.
        * TODO: ScanActivity is subject to change. Adjust this as well.
        View scanButton = solo.getView("scan");
        solo.clickOnView(scanButton);
        solo.assertCurrentActivity("Scan button failed!", ScanActivity.class);
        */

        // test map button
        View mapButton = solo.getView("map");
        solo.clickOnView(mapButton);
        solo.assertCurrentActivity("Map button failed!", MapActivity.class);

        // test my_account button
        View accButton = solo.getView("my_account");
        solo.clickOnView(accButton);
        solo.assertCurrentActivity("Account button failed!", AccountActivity.class);
    }

    /**
     * Tests the basic app flow for adding a QR code
     * TODO: Adjust when ScanActivity is revised.
     * @throws Exception
     */
    @Test
    public void testAddQR() throws Exception {

        /*
        // set up intent???
        Intent intent = new Intent();
        intent.putExtra("com.example.qradventure.QR_CONTENT", "silly goose");
        rule.launchActivity(intent);
         */


        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // click on ScanActivity
        View scanButton = solo.getView("scan");
        solo.clickOnView(scanButton);
        solo.assertCurrentActivity("Scan button failed!", ScanActivity.class);

        // need to go directly to postactivity with intent extra:
        // intent.putExtra("com.example.qradventure.QR_CONTENT", "silly goose");


    }
}
