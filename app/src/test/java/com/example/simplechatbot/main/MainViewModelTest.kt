package com.example.simplechatbot.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.simplechatbot.assistant.MicrophoneListener
import com.example.simplechatbot.assistant.SpeechAssistant
import com.example.simplechatbot.utils.PathProvider
import com.example.simplechatbot.utils.PreferenceHelper
import com.google.api.gax.core.CredentialsProvider
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private val preferenceHelper: PreferenceHelper = mock(PreferenceHelper::class.java)
    private val speechAssistant: SpeechAssistant = mock(SpeechAssistant::class.java)
    private val credentialProvider: CredentialsProvider = mock(CredentialsProvider::class.java)
    private val pathProvider: PathProvider = mock(PathProvider::class.java)
    private val listener: MicrophoneListener = mock(MicrophoneListener::class.java)
    private val wuwListener: WuWListener = mock(WuWListener::class.java)

    private val mainViewModel = MainViewModel(
        preferenceHelper,
        speechAssistant,
        credentialProvider,
        pathProvider,
        listener
    )

    private val testDispatcher = TestCoroutineDispatcher()

    @Rule
    @JvmField
    val testRule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mainViewModel.setWuWListener(wuwListener)
    }

    @After
    fun tearDown() {
        mainViewModel.onDestroy()
        testDispatcher.advanceUntilIdle()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
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

    @Test
    fun `test startListening() when not listening`() {
        mainViewModel.isListening.value = false
        mainViewModel.startListening()
        verify(listener, times(1))
            .startListening(pathProvider.provideAudioInputFilePath())
        verify(wuwListener, times(1)).awaitEndOfSpeech()
        assertTrue(mainViewModel.isListening.value ?: false)
    }

    @Test
    fun `test startListening() when listening`() {
        mainViewModel.isListening.value = true
        mainViewModel.startListening()
        verify(listener, times(0))
            .startListening(pathProvider.provideAudioInputFilePath())
        verify(wuwListener, times(0)).awaitEndOfSpeech()
    }

    @Test
    fun `test stopListening() when not listening`() {
        mainViewModel.isListening.value = false
        mainViewModel.stopListening()
        verify(listener, times(0))
            .stopListening()
        verify(wuwListener, times(0)).armWuW()
    }

    @Test
    fun `test stopListening() when listening`() {
        runBlockingTest {
            mainViewModel.isListening.value = true
            mainViewModel.stopListening()
            verify(listener, times(1))
                .stopListening()
            verify(speechAssistant, times(1))
                .prepareResponse(
                    pathProvider.provideAudioInputFilePath(),
                    pathProvider.provideAudioOutputFilePath()
                )
            verify(wuwListener, times(1)).armWuW()
        }
        assertNull(mainViewModel.isListening.value)
    }
}