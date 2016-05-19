package com.stadgent.mobiliteitapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityTestCase;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ItemType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 17-May-16.
 */

@RunWith(JUnit4.class)
@LargeTest
public class LoginActivityTest{


    @Test
    public void loginpressedNoName(){
        Espresso.onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

}
