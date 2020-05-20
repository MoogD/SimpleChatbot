package com.example.simplechatbot.onboarding

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.example.simplechatbot.R
import com.example.simplechatbot.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val APP_SETTINGS_KEY = "APP_SETTINGS_KEY"

private const val ONBOARDING_DONE_KEY = "ONBOARDING_DONE_KEY"

@RunWith(AndroidJUnit4::class)
class OnboardingActivityTest {

    private lateinit var context: Context

    @get:Rule
    val mainActivityIntentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()
        intending(
            toPackage("com.google.android.gms")
        ).respondWith(Instrumentation.ActivityResult(
            Activity.RESULT_OK,
            Intent().putExtra(
                "googleSignInAccount",
                GoogleSignInAccount.createDefault()
            )
        ))
    }

    @Test
    fun onboardingEndToEnd() {
        val onboardingActivityScenario = ActivityScenario.launch(OnboardingActivity::class.java)

        checkOnboardingStartView()

        checkStartFragment()

        onboardingActivityScenario.onActivity { activity: OnboardingActivity? ->
            activity?.let {
                ViewModelProviders.of(
                    it,
                    it.factory
                )[OnboardingViewModel::class.java]
                    .signedIn.value = true
            }
        }
        onView(withId(R.id.nextButton))
            .check(matches(isEnabled()))
            .perform(click())

        checkPermissionFragment()

        checkOnboardingDone()

        Assert.assertEquals(Lifecycle.State.DESTROYED, onboardingActivityScenario.state)
        onboardingActivityScenario.close()
    }

    private fun checkOnboardingDone() {
        assertTrue(
            context.getSharedPreferences(
                APP_SETTINGS_KEY,
                Context.MODE_PRIVATE
            ).getBoolean(ONBOARDING_DONE_KEY, false)
        )
        intended(
            hasComponent(
                MainActivity::class.qualifiedName
            )
        )
    }

    private fun checkPermissionFragment() {
        checkPermissionFragmentView()

        checkPermissions()
    }

    private fun checkPermissions() {
        onView(withId(R.id.nextButton))
            .perform(click())
        intended(
            toPackage("com.google.android.permissioncontroller")
        )
        allowPermissions()
    }

    private fun allowPermissions() {
        val allowPermissions = UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        ).findObject(UiSelector().text("Allow"))
        if (allowPermissions.exists()) {
            try {
                allowPermissions.click()
            } catch (e: UiObjectNotFoundException) {
            }
        }
    }

    private fun checkPermissionFragmentView() {
        onView(withId(R.id.onboardingPermissionLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.onboardingPermissionText))
            .check(matches(isDisplayed()))
        onView(withId(R.id.nextButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
            .check(matches(withText(R.string.next)))
    }

    private fun checkStartFragment() {

        checkStartFragmentView()

        checkLogin()
    }

    private fun checkLogin() {
        onView(withId(R.id.signInButton))
            .perform(click())
        intended(
            toPackage("com.google.android.gms")
        )
    }

    private fun checkStartFragmentView() {
        onView(withId(R.id.onboardingLoginLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.onboardingStartText))
            .check(matches(isDisplayed()))
            .check(matches(withText(context.getString(R.string.onboarding_start_text))))
        onView(withId(R.id.signInButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        onView(withId(R.id.nextButton))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.next)))
            .check(matches(not(isEnabled())))
    }

    private fun checkOnboardingStartView() {
        onView(withId(R.id.onboardingLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.container))
            .check(matches(isDisplayed()))
    }
}