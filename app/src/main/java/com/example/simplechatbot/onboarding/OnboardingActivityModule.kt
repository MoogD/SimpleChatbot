package com.example.simplechatbot.onboarding

import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragmentModule
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class OnboardingActivityModule {

    @ContributesAndroidInjector(modules = [(OnboardingStartFragmentModule::class)])
    abstract fun bindStartFragment(): OnboardingStartFragment

    @ContributesAndroidInjector(modules = [(OnboardingPermissionFragmentModule::class)])
    abstract fun bindPermissionFragment(): OnboardingPermissionFragment
}