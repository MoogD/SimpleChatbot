package com.example.simplechatbot.main

import ai.api.model.AIResponse
import ai.api.model.Fulfillment
import ai.api.model.Status
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.OnboardingActivity
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

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

//        mainActivityScenario.onActivity{ activity: MainActivity? ->
//            activity?.let {
//                ViewModelProviders.of(it, it.factory)[MainViewModel::class.java]
//                    .onResult(provideAiResponse())
//            }
//        }
        checkViewAfterConversation(utteranceList)

        mainActivityScenario.close()
    }

    private fun checkViewAfterConversation(utteranceList: RecyclerView?) {
        assertEquals(2, utteranceList?.adapter?.itemCount)
        onView(withText("User"))
            .check(matches(isDisplayed()))
        onView(withText("hello"))
            .check(matches(isDisplayed()))
        onView(withText("Assistant"))
            .check(matches(isDisplayed()))
        onView(withText(
            "Hi! I can show you examples of helper intents. We recommend trying this sample on a phone so you can see all helper intents."
        )).check(matches(isDisplayed()))
    }

    private fun checkActivityStartView(utteranceList: RecyclerView?) {
        onView(withId(R.id.mainLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.utteranceList))
            .check(matches(isDisplayed()))
        onView(withId(R.id.listeningButton))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
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

//    private fun provideAiResponse(): AIResponse {
//        val date = Calendar.getInstance().time
//        val result = ai.api.model.Result()
//        result.action = "input.welcome"
//        result.resolvedQuery = "hello"
//        result.fulfillment = Fulfillment()
//        result.fulfillment.speech = "Hi! I can show you examples of helper intents. We recommend trying this sample on a phone so you can see all helper intents."
//        val status = Status()
//        status.code = 200
//        status.errorType = "success"
//        status.errorDetails = null
//        val response = AIResponse()
//        response.id = "ac2f20fd-830f-4976-ac13-4dccf4009bf7-266f04e0"
//        response.timestamp = date
//        response.result = result
//        response.status = status
//        response.sessionId = "89c81e1f-5728-4d6e-8ae0-1a07c097a211"
//        return  response
//    }
}