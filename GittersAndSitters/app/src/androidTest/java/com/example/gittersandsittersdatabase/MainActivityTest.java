package com.example.gittersandsittersdatabase;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //Build the result by setting up an email

    String fakeEmail = "abc@gmail.com";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testUserEmailScenario() {

        //Input some text in the email field
        //Launch activity to be returned and displayed

        Espresso.onView(withId(R.id.login_emailAddress)).perform(typeText(fakeEmail));

        //Assert that the data we set up above is shown

        Espresso.onView(withId(R.id.activity_main_id)).check(matches(withText(fakeEmail)));

    }

    @Test
    public void testPasswordScenario(){

        //Build the result

        Intent result = new Intent();
        String fakePassword = "fakePassword";
        result.putExtra("password", fakePassword);

       //Input some text in the email field
       //Launch activity to be returned and displayed

       Espresso.onView(withId(R.id.login_password)).perform(typeText(fakePassword));

       //Assert that the data we set up above is returned

       Espresso.onView(withId(R.id.activity_main_id)).check(matches(withText(fakePassword)));

    }

    @Test
    public void testLoginButton(){
        //Click on log in button

        Espresso.onView(withId(R.id.login_button)).perform(click());

        //Test to see if user is logged in

        Espresso.onView(withId(R.id.activity_main_id)).check(matches(isDisplayed()));

    }


    @After
    public void tearDown() throws Exception {

    }
}