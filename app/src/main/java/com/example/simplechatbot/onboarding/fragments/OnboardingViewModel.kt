package com.example.simplechatbot.onboarding.fragments

import android.Manifest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.OnboardingStep

class OnboardingViewModel : ViewModel() {

    private val onboardingSteps: Array<OnboardingStep> = setupOnboardingSteps()

    private var currentStepIndex: Int = 0

    val currentStep: MutableLiveData<OnboardingStep> = MutableLiveData<OnboardingStep>()

    init {
        currentStep.value = onboardingSteps[currentStepIndex]
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


}