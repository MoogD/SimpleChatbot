package com.example.simplechatbot.onboarding.fragments

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingStep
import com.example.simplechatbot.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import timber.log.Timber
import javax.inject.Inject

class OnboardingViewModel : ViewModel() {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    private val onboardingSteps: Array<OnboardingStep> = setupOnboardingSteps()

    private var currentStepIndex: Int = 0

    private var sharedPreferences: SharedPreferences

    val currentStep: MutableLiveData<OnboardingStep> = MutableLiveData<OnboardingStep>()

    val signedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        currentStep.value = onboardingSteps[currentStepIndex]
        sharedPreferences = context.getSharedPreferences(Constants.APP_SETTINGS, Context.MODE_PRIVATE)
        signedIn.value = sharedPreferences.contains(Constants.ACCOUUNT)
    }

    private fun setupOnboardingSteps(): Array<OnboardingStep>{
        val onboardingStartFragment by lazy {
            OnboardingStep(
                tag = "start",
                fragment = OnboardingStartFragment.newInstance(
                    R.string.onboarding_start_title,
                    R.string.onboarding_start_text)
            )
        }
        val onboadringPermissionFragment by lazy {
            OnboardingStep(
                tag = "permission",
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

    fun onNextStep() {
        if (currentStepIndex < onboardingSteps.size -1 ) {
            currentStep.value = onboardingSteps[++currentStepIndex]
        } else {
            currentStep.value = null
        }
    }


    private fun createPermissionsList(): ArrayList<String> = arrayListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun finishFTU() {
        sharedPreferences
            .edit()
            .putBoolean(Constants.IS_ONBOARDING_DONE, true)
            .apply()
    }

    fun setAccount(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = GsonBuilder()
                .create()
                .toJson(completedTask.getResult(ApiException::class.java))
            sharedPreferences
                .edit()
                .putString(Constants.ACCOUUNT, account)
                .apply()
            signedIn.value = true
        } catch (e: ApiException) {
            Timber.w("signInResult failed: code=" + e.statusCode)
        }
    }
}