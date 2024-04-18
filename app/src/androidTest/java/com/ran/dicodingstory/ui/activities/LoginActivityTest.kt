package com.ran.dicodingstory.ui.activities

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ran.dicodingstory.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/// BEFORE RUN THIS TESTING PLEASE COMMENT ONDRAW FUNCTION IN CUSTOMVIEWS
@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)
    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
    @Test
    fun testLoginValidation() {
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.ed_login_email)).check(matches(ViewMatchers.hasErrorText("Email is required")))
        onView(withId(R.id.ed_login_password)).check(matches(ViewMatchers.hasErrorText("Password is required")))
    }

    @Test
    fun testLoginInvalid() {
        onView(withId(R.id.ed_login_email)).perform(replaceText("kucingsri123"))
        onView(withId(R.id.ed_login_password)).perform(replaceText("12345678"), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.ed_login_email)).check(matches(ViewMatchers.hasErrorText("Email is not valid")))
    }

    @Test
    fun testLoginSuccess() {
        Intents.init()
        onView(withId(R.id.ed_login_email)).perform(replaceText("kucingsri123@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(replaceText("Bulat1234!"), closeSoftKeyboard())

        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(2000)
        activity.scenario.moveToState(Lifecycle.State.DESTROYED)
    }

}