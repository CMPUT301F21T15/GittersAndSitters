package com.example.gittersandsittersdatabase;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.regex.Pattern.matches;

import android.content.Intent;
import android.view.View;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

//    private String fakeEmail = "abcd@gmail.com";
    private String fakePassword = "1379246";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testUserInputScenario() {

        //Build the result

        Intent resultData = new Intent();
        String fakeEmail = "abc@gmail.com";
        resultData.putExtra("email", fakeEmail);

        //Input some text in the email field
        //Launch activity to be returned and displayed
        Espresso.onView(withId(R.id.login_emailAddress)).perform(typeText(fakeEmail));

        //Assert that the data we set up above is shown
        Espresso.onView(withId(R.id.login_emailAddress)).check(matches(withText(fakeEmail)));

    }

    @Test
    public void testPasswordScenario(){

       //Input some text in the email field
       //Launch activity to be returned and displayed
       Espresso.onView(withId(R.id.login_password)).perform(typeText(fakePassword));

       //Assert that the data we set up above is returned
       Espresso.onView(withId(R.id.login_password)).check(matches(withText(fakePassword)));

    }


    @After
    public void tearDown() throws Exception {
    }
}