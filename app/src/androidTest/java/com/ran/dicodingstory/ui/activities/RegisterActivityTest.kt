package com.ran.dicodingstory.ui.activities

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
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
class RegisterActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testRegisterValidation() {
        onView(withId(R.id.rsButton)).perform(click())
        onView(withId(R.id.ed_register_name)).check(matches(hasErrorText("Name is required")))
        onView(withId(R.id.ed_register_email)).check(matches(hasErrorText("Email is required")))
        onView(withId(R.id.ed_register_password)).check(matches(hasErrorText("Password is required")))
    }

    @Test
    fun testRegisterInvalid() {
        onView(withId(R.id.ed_register_email)).perform(typeText("emailsalah"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.ed_register_password)).perform(typeText("kureng12345"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.rsButton)).perform(click())
        onView(withId(R.id.ed_register_name)).check(matches(hasErrorText("Name is required")))
        onView(withId(R.id.ed_register_email)).check(matches(hasErrorText("Email is required")))
    }

    @Test
    fun testRegister(){
        onView(withId(R.id.ed_register_email))
            .perform(ViewActions.replaceText("baruuu12334ff@gmail.com"))
        onView(withId(R.id.ed_register_name))
            .perform(ViewActions.replaceText("Kucing Baru"))
        onView(withId(R.id.ed_register_password))
            .perform(ViewActions.replaceText("Bulat1234!"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.rsButton)).perform(click())
        activity.scenario.moveToState(Lifecycle.State.DESTROYED)
    }
}