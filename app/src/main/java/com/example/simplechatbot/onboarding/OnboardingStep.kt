package com.example.simplechatbot.onboarding

import com.example.simplechatbot.onboarding.fragments.OnboardingBaseFragment

data class OnboardingStep(
    val tag: String,
    val fragment: OnboardingBaseFragment
)