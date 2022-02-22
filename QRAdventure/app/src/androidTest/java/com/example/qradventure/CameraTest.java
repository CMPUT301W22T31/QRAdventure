package com.example.qradventure;


import android.app.Activity;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

public class CameraTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<Camera> rule = new ActivityTestRule<>(Camera.class, true, true);



    @Before
    public void setUp()throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void test(){
        solo.assertCurrentActivity("Wrong", Camera.class);

        solo.clickOnButton("qr_button");
    }


}
