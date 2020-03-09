package com.example.simplechatbot

import com.example.simplechatbot.annotationclasses.PerActivity
import com.example.simplechatbot.onboarding.OnboardingActivity
import com.example.simplechatbot.onboarding.OnboardingActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatBotApplicationBuilderModule  {

    @PerActivity
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    abstract fun bindMainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [(OnboardingActivityModule::class)])
    abstract fun bindOnboardingActivity(): OnboardingActivity

}