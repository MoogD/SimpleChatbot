package com.example.simplechatbot

import com.example.simplechatbot.injections.PerActivity
import com.example.simplechatbot.main.MainActivity
import com.example.simplechatbot.main.MainActivityModule
import com.example.simplechatbot.main.MainService
import com.example.simplechatbot.main.MainServiceModule
import com.example.simplechatbot.onboarding.OnboardingActivity
import com.example.simplechatbot.onboarding.OnboardingActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatBotApplicationBuilderModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    abstract fun bindMainActivity(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [(OnboardingActivityModule::class)])
    abstract fun bindOnboardingActivity(): OnboardingActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [(MainServiceModule::class)])
    abstract fun bindMainService(): MainService
}
