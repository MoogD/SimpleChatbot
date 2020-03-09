package com.example.simplechatbot.onboarding

import com.example.simplechatbot.onboarding.fragments.OnboadringStartFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OnboardingActivityModule {

    @ContributesAndroidInjector(modules = [(OnboardingStartFragmentModule::class)])
    abstract fun bindStartFragment(): OnboadringStartFragment
}