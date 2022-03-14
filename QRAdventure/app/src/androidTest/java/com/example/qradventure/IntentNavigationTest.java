package com.example.qradventure;


import static org.junit.Assert.assertTrue;

import android.view.View;
import android.widget.EditText;

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
 * Test class for navigating through various activities
 * *These tests assume the test device has an associated account for login*
 * Robotium test framework is used.
 *
 * NOTE: occasionally the tests crash due to an unknown security permissions issue, but running
 * be successful. Try running them individually if the problem persists.
 */
@RunWith(AndroidJUnit4.class)
public class IntentNavigationTest {
    // rule attribute allows functional testing of activities
    // MAINTENANCE: change MainActivity to whatever the startup activity is.
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    private Solo solo;

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
     * Tests the navbar, which should be present between all* 4 primary activities (-Scan?)
     * @throws Exception
     */
    @Test
    public void testNavbar() throws Exception {
        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // test search players button
        View spButton = solo.getView("search_players");
        solo.clickOnView(spButton);
        solo.assertCurrentActivity("Search Players button failed!", SearchPlayersActivity.class);

        // test map button
        View mapButton = solo.getView("map");
        solo.clickOnView(mapButton);
        solo.assertCurrentActivity("Map button failed!", MapActivity.class);

        // test my_account button
        View accButton = solo.getView("my_account");
        solo.clickOnView(accButton);
        solo.assertCurrentActivity("Account button failed!", AccountActivity.class);

        /*
        // test leaderboard button
        // TODO: Leaderboard does not have a navbar (yet, *part4*)
        View lbButton = solo.getView("leaderboards");
        solo.clickOnView(lbButton);
        solo.assertCurrentActivity("Leaderboard button failed!", LeaderboardActivity.class);
        */

        /*
        // test scan button
        * TODO: ScanActivity does not have a navbar.
        * TODO: ScanActivity is subject to change. Adjust/remove this.
        View scanButton = solo.getView("scan");
        solo.clickOnView(scanButton);
        solo.assertCurrentActivity("Scan button failed!", ScanActivity.class);
        */
    }

    /**
     * Tests the activity to search players by username
     * @throws Exception
     */
    @Test
    public void testSearchPlayers() {
        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // test search players button
        View spButton = solo.getView("search_players");
        solo.clickOnView(spButton);
        solo.assertCurrentActivity("Search Players button failed!", SearchPlayersActivity.class);

        // enter some username to search (search by first letter of current username)
        String letter = CurrentAccount.getAccount().getUsername().substring(0,1);
        solo.enterText((EditText) solo.getView(R.id.etUsername), letter);
        View searchButton = solo.getView("buttonConfirmSearch");
        solo.clickOnView(searchButton);

        // click on first result and check activity navigation
        solo.clickInList(1);
        solo.assertCurrentActivity("Profile Activity Failed!", ProfileActivity.class);

        // verify navbar to AccountActivity works
        View accButton = solo.getView("my_account");
        solo.clickOnView(accButton);
        solo.assertCurrentActivity("Account button failed!", AccountActivity.class);
    }

}
