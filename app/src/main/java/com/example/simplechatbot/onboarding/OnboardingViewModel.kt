package com.example.simplechatbot.onboarding

import android.Manifest
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragment
import com.example.simplechatbot.utils.PreferenceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class OnboardingViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : ViewModel() {


    private val onboardingSteps: Array<OnboardingStep> = setupOnboardingSteps()

    private var currentStepIndex: Int = 0

    val currentStep: MutableLiveData<OnboardingStep> =
        MutableLiveData<OnboardingStep>(onboardingSteps[currentStepIndex])

    val googleSignInClient: MutableLiveData<GoogleSignInClient> = MutableLiveData()

    val signedIn: MutableLiveData<Boolean> = MutableLiveData()

    private fun setupOnboardingSteps(): Array<OnboardingStep>{
        val onboardingStartFragment by lazy {
            OnboardingStep(
                tag = LOGIN_FRAGMENT_TAG,
                fragment = OnboardingStartFragment.newInstance(
                    R.string.onboarding_start_title,
                    R.string.onboarding_start_text
                )
            )
        }
        val onboadringPermissionFragment by lazy {
            OnboardingStep(
                tag = PERMISSION_FRAGMENT_TAG,
                fragment = OnboardingPermissionFragment.newInstance(
                    R.string.onboarding_permission_title,
                    R.string.onboarding_permission_text,
                    permissions = createPermissionsList()
                )
            )
        }
        return arrayOf(
            onboardingStartFragment,
            onboadringPermissionFragment
        )
    }

    fun startSignIn(context: Context) {
        if (signedIn.value == true) {
            googleSignInClient.value = null
        } else {
            val googleSignInOption =
                GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.server_client_id))
                    .requestEmail()
                    .requestScopes(Scope(Scopes.APP_STATE), Scope(Scopes.PROFILE))
                    .build()
            googleSignInClient.value =
                GoogleSignIn.getClient(context, googleSignInOption)
        }
    }

    fun onNextStep() {
        if (currentStepIndex < onboardingSteps.size -1 ) {
            currentStep.value = onboardingSteps[++currentStepIndex]
        } else {
            currentStep.value = null
        }
    }

    private fun createPermissionsList(): ArrayList<String> = arrayListOf(
        Manifest.permission.RECORD_AUDIO
    )

    fun finishFTU() {
        preferenceHelper.setOnboardingState(true)
    }

    fun setAccount(completedTask: Task<GoogleSignInAccount>) {
        try {
            preferenceHelper.setAccount(
                completedTask.getResult(ApiException::class.java) as GoogleSignInAccount
            )
            signedIn.value = true
        } catch (e: ApiException) {
            Timber.w("signIn failed: code=%s", e.statusCode)
        }
    }

    fun initSignIn() {
        signedIn.value = preferenceHelper.isSignedIn()
    }

    companion object {
        const val LOGIN_FRAGMENT_TAG = "Login"
        const val PERMISSION_FRAGMENT_TAG = "Permission"
    }

    class Factory(val provider: Provider<OnboardingViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider.get() as T
    }
}