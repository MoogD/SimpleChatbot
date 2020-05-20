package com.example.simplechatbot.main

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.OnboardingActivity
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val APP_SETTINGS_KEY = "APP_SETTINGS_KEY"

private const val ONBOARDING_DONE_KEY = "ONBOARDING_DONE_KEY"

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var context: Context

    @get:Rule
    val onboardingIntentsTestRule = IntentsTestRule(OnboardingActivity::class.java)

    @Before
    fun init() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testOnboardingNeeded() {
        setOnboardingState(false)
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)
        intended(
            hasComponent(
                OnboardingActivity::class.qualifiedName
            )
        )
        assertEquals(Lifecycle.State.DESTROYED, mainActivityScenario.state)

        mainActivityScenario.close()
    }

    @Test
    fun testOnboardingDone() {
        setOnboardingState(true)
        var utteranceList: RecyclerView? = null
        val mainActivityScenario = ActivityScenario.launch(MainActivity::class.java)
            .onActivity { activity: MainActivity? ->
                utteranceList = activity?.findViewById(R.id.utteranceList)
            }
        checkActivityStartView(utteranceList)

        mainActivityScenario.close()
    }

    private fun checkActivityStartView(utteranceList: RecyclerView?) {
        onView(withId(R.id.mainLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.utteranceList))
            .check(matches(isDisplayed()))
        onView(withId(R.id.listeningButton))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
        assertEquals(0, utteranceList?.adapter?.itemCount ?: -1)
    }

    private fun setOnboardingState(state: Boolean) {
        val sharedPreferences = context.getSharedPreferences(
            APP_SETTINGS_KEY,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit()
            .putBoolean(ONBOARDING_DONE_KEY, state)
            .apply()
    }
}