package com.example.simplechatbot.onboarding

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.simplechatbot.utils.PreferenceHelper
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OnboardingViewModelTest {

    private val context: Context = mock(Context::class.java)

    private val preferenceHelper: PreferenceHelper = mock(PreferenceHelper::class.java)
    private val task: Task<GoogleSignInAccount> = mock(Task::class.java) as Task<GoogleSignInAccount>
    private val account: GoogleSignInAccount = mock(GoogleSignInAccount::class.java)

    private val onboardingViewModel = OnboardingViewModel(preferenceHelper)

    @Before
    fun setup() {
        whenever(task.getResult(ApiException::class.java)).thenReturn(account)
    }

    @Rule
    @JvmField
    val testRule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `test startSignIn signed in`() {
        onboardingViewModel.signedIn.value = true
        onboardingViewModel.startSignIn(context)
        assertEquals(
            null,
            onboardingViewModel.googleSignInClient.value
        )
    }

    @Test
    fun `test onNextStep`() {
        assertEquals(
            OnboardingViewModel.LOGIN_FRAGMENT_TAG,
            onboardingViewModel.currentStep.value?.tag
        )
        onboardingViewModel.onNextStep()
        assertEquals(
            OnboardingViewModel.PERMISSION_FRAGMENT_TAG,
            onboardingViewModel.currentStep.value?.tag
        )
        onboardingViewModel.onNextStep()
        assertEquals(onboardingViewModel.currentStep.value, null)
    }

    @Test
    fun `test finishFTU`() {
        onboardingViewModel.finishFTU()
        verify(preferenceHelper, times(1)).setOnboardingState(true)
    }

    @Test
    fun `test setAccount`() {
        onboardingViewModel.setAccount(task)
        verify(preferenceHelper, times(1)).setAccount(account)
        assertTrue(onboardingViewModel.signedIn.value ?: false)
    }

    @Test
    fun `test initSignIn not signed in`() {
        `when`(preferenceHelper.isSignedIn()).thenReturn(false)
        onboardingViewModel.initSignIn()
        assertFalse(onboardingViewModel.signedIn.value ?: true)
    }

    @Test
    fun `test initSignIn signed in`() {
        `when`(preferenceHelper.isSignedIn()).thenReturn(true)
        onboardingViewModel.initSignIn()
        assertTrue(onboardingViewModel.signedIn.value ?: false)
    }
}