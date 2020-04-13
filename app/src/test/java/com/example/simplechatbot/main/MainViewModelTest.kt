package com.example.simplechatbot.main

import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.model.Fulfillment
import ai.api.model.Status
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.simplechatbot.utils.PreferenceHelper
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private val preferenceHelper: PreferenceHelper = mock(PreferenceHelper::class.java)

    private val mainViewModel = MainViewModel(preferenceHelper)
    private val error: AIError = mock(AIError::class.java)

    @Rule
    @JvmField
    val testRule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `test onListeningStarted`() {
        mainViewModel.onListeningStarted()
        assertTrue(mainViewModel.isListening.value ?: false)
    }

    @Test
    fun `test onError`() {
        mainViewModel.isListening.value = true
        mainViewModel.onError(error)
        assertFalse(mainViewModel.isListening.value ?: true)
    }

    @Test
    fun `test onListeningCanceled`() {
        mainViewModel.isListening.value = true
        mainViewModel.onListeningCanceled()
        assertFalse(mainViewModel.isListening.value ?: true)
    }

    @Test
    fun `test onListeningFinished`() {
        mainViewModel.isListening.value = true
        mainViewModel.onListeningFinished()
        assertFalse(mainViewModel.isListening.value ?: true)
    }

    @Test
    fun `test onResult`() {
        val size = mainViewModel.conversationObservable.value?.size ?: 0
        val response = provideAiResponse()
        mainViewModel.onResult(response)
        assertEquals(size + 2, mainViewModel.conversationObservable.value?.size)
        mainViewModel.conversationObservable.value?.let {
            assertEquals(
                response.result.resolvedQuery,
                it.get(it.size -2).content
            )
            assertEquals(
                response.result.fulfillment.speech,
                it.get(it.size -1).content
            )
        } ?: assert(false)
    }

    @Test
    fun `test checkOnboarding before onboarding`() {
        `when`(preferenceHelper.getOnboardingState()).thenReturn(false)
        mainViewModel.checkOnboarding()
        assertFalse(mainViewModel.onboardingDone.value ?: true)
    }

    @Test
    fun `test checkOnboarding after onboarding`() {
        `when`(preferenceHelper.getOnboardingState()).thenReturn(true)
        mainViewModel.checkOnboarding()
        assertTrue(mainViewModel.onboardingDone.value ?: false)
    }

    private fun provideAiResponse(): AIResponse {
        val date = Calendar.getInstance().time
        val result = ai.api.model.Result()
        result.action = "input.welcome"
        result.resolvedQuery = "hello"
        result.fulfillment = Fulfillment()
        result.fulfillment.speech = "Hi! I can show you examples of helper intents. We recommend trying this sample on a phone so you can see all helper intents."
        val status = Status()
        status.code = 200
        status.errorType = "success"
        status.errorDetails = null
        val response = AIResponse()
        response.id = "ac2f20fd-830f-4976-ac13-4dccf4009bf7-266f04e0"
        response.timestamp = date
        response.result = result
        response.status = status
        response.sessionId = "89c81e1f-5728-4d6e-8ae0-1a07c097a211"
        return  response
    }
}