package com.example.qradventure;

import static org.junit.Assert.*;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qradventure.activity.AccountActivity;
import com.example.qradventure.activity.CommentsActivity;
import com.example.qradventure.activity.MainActivity;
import com.example.qradventure.activity.MapsActivity;
import com.example.qradventure.activity.MockAccountActivity;
import com.example.qradventure.activity.MockPostScan;
import com.example.qradventure.activity.MyCodesActivity;
import com.example.qradventure.activity.OwnerActivity;
import com.example.qradventure.activity.PostScanActivity;
import com.example.qradventure.activity.ProfileActivity;
import com.example.qradventure.activity.QRPageActivity;
import com.example.qradventure.activity.ScannedByActivity;
import com.example.qradventure.activity.StatsActivity;
import com.example.qradventure.activity.ViewCodesActivity;
import com.example.qradventure.model.CurrentAccount;
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
     * Tests the basic app flow for adding a QR code and other actions
     * Must already have an account registed on the device
     * This includes adding a QR, adding a geolocation, Viewing the QR, viewing the comments, and deleting a QR
     *
     * TODO: Adjust when ScanActivity is revised.
     * @throws Exception
     */
    @Test
    public void testAddQR() throws Exception {
        // wait for launch to reach AccountActivity
        solo.waitForActivity("AccountActivity", 5000);

        // click on ScanActivity
        View scanButton = solo.getView("scan");
        Thread.sleep(100);
        solo.clickOnView(scanButton);

        // backdoor into PostScanActivity with an intent extra (dummy qr content)
        Intent intent = new Intent(solo.getCurrentActivity(), PostScanActivity.class);
        intent.putExtra("com.example.qradventure.QR_CONTENT", "test silly goose");
        ActivityScenario.launch(intent);

        // assert we made it to PostScanActivity

        View scannedBy = solo.getView(R.id.see_people);

        solo.clickOnView(scannedBy);

        solo.assertCurrentActivity("Failed to reach ScannedByActivity!", ScannedByActivity.class);

        solo.goBack();

        // adding geolocation
        View map = solo.getView(R.id.add_geo);
        solo.clickOnView(map);

        View confirm = solo.getView(R.id.confirm_geo);

        solo.clickOnView(confirm);




        // close dialogue and click add
        // TODO: revise if PostScanActivity changes
        View add = solo.getView(R.id.button);

        solo.clickOnView(add);

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

        solo.enterText( (EditText)solo.getView(R.id.editText_comment), "This is a comment" );

        //add our own comment

        solo.clickOnText("Add");

        assertTrue(solo.waitForText("This is a comment", 1, 2000));

        solo.goBack();
        solo.goBack();
        solo.clickLongInList(0);
        solo.clickOnText("Yes");

    }

    /**
     * Test for adding an image to a QR. Uses a mock of PostScanActivity to manually set the image
     * bitmap.
     * The image shows up as a black or white square for some reason, but it does work
     * @throws Exception
     */
    @Test
    public void testPicture() throws Exception {
        solo.waitForActivity("AccountActivity", 5000);

        Intent intent = new Intent(solo.getCurrentActivity(), MockPostScan.class);
        intent.putExtra("com.example.qradventure.QR_CONTENT", "TESTQR");
        ActivityScenario.launch(intent);
        solo.waitForActivity("AccountActivity", 5000);
        solo.clickOnText("My Codes");
        solo.assertCurrentActivity("My Codes failed!", MyCodesActivity.class);
        solo.clickOnText("pts");
        solo.assertCurrentActivity("QR Page Failed!", QRPageActivity.class);
        solo.goBack();
        solo.clickLongInList(0);
        solo.clickOnText("Yes");

    }



    /**
     * Test for the status QR
     * @throws Exception
     */
    @Test
    public void testStatusQR() throws Exception {
        solo.waitForActivity("AccountActivity", 5000);

        // backdoor into PostScanActivity with an intent extra (dummy qr content)
        Intent intent = new Intent(solo.getCurrentActivity(), MockAccountActivity.class);
        intent.putExtra("TEST", "QRSTATS-test-1-2-3-4");
        ActivityScenario.launch(intent);
        solo.assertCurrentActivity("Stats test failed", StatsActivity.class);

    }

    /**
     * Test for taking the login QR. Quickly logs into a second account. Will reset the previous account
     * afterwards
     * @throws Exception
     */
    @Test
    public void testLoginQR() throws Exception {
        solo.waitForActivity("AccountActivity", 5000);

        // backdoor into PostScanActivity with an intent extra (dummy qr content)
        Intent intent = new Intent(solo.getCurrentActivity(), MockAccountActivity.class);
        intent.putExtra("TEST", "QRLOGIN-b4048ce9c5919adf");
        ActivityScenario.launch(intent);
        solo.waitForText("otherjack", 1, 2000);



    }

    /**
     * Tests the leaderboard. Makes sure we can enter the leaderboard and interact with the
     * players listed. Also counts as testing the player rankings, since this is located on the leaderboard
     * @throws Exception
     */
    @Test
    public void testLeaderBoard() throws Exception{


        solo.waitForActivity("AccountActivity", 5000);
        solo.clickOnView(solo.getView(R.id.leaderboards));
        solo.clickInList(3);
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.goBack();
        solo.clickOnText("Largest CODE");
        solo.clickInList(3);
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.goBack();
        solo.clickOnText("Total Scans");
        solo.clickInList(3);
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);

    }

    /**
     * tests map. enters the map and checks out nearby QRs
     * @throws Exception
     */
    @Test
    public void testMap() throws Exception{
        solo.waitForActivity("AccountActivity", 5000);
        solo.clickOnView(solo.getView(R.id.map));
        solo.assertCurrentActivity("Wrong Activity", MapsActivity.class);
        solo.clickOnButton(0);
        solo.waitForText("QRs Nearby", 1, 2000);

    }

    /**
     * Test for deleting a QR of a player
     * The account which it deletes from is hardcoded, and will have a number of QRs to start.
     * Once the account is out of QRs this test will not work
     * @throws Exception
     */
    @Test
    public void testOwnerQR() throws Exception{
        solo.waitForActivity("LoginActivity", 5000);

        Intent profileIntent = new Intent(solo.getCurrentActivity(), ProfileActivity.class);
        profileIntent.putExtra("com.example.qradventure.USERNAME", "otherjack");
        profileIntent.putExtra("Owner", "Owner");
        ActivityScenario.launch(profileIntent);
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnText("View Codes");
        solo.assertCurrentActivity("Wrong Activity", ViewCodesActivity.class);
        solo.clickOnText("pts");
        solo.assertCurrentActivity("Wrong Activity", QRPageActivity.class);
        solo.clickOnText("Delete");
        solo.assertCurrentActivity("Wrong Activity", OwnerActivity.class);


    }

    /**
     * Test for deleting a players account
     * The account is hardcoded, so running this test once succesfully ensure other runs will
     * fail unless the account is re-created. DO NOT RUN THIS TEST UNLESS YOU ARE SURE YOU
     * WANT TO DELETE THE ACCOUNT, AS IT WILL ALSO MAKE testOwnerQR NOT WORK.
     * @throws Exception
     */
    @Test
    public void testOwnerAcc() throws Exception{
        solo.waitForActivity("LoginActivity", 5000);

        Intent profileIntent = new Intent(solo.getCurrentActivity(), ProfileActivity.class);
        profileIntent.putExtra("com.example.qradventure.USERNAME", "otherjack");
        profileIntent.putExtra("Owner", "Owner");
        ActivityScenario.launch(profileIntent);
        solo.waitForActivity("ProfileActivity", 5000);
        solo.clickOnText("Delete");


    }


}
