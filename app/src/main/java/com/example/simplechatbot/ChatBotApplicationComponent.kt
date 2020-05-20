package com.example.simplechatbot

import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
    AndroidSupportInjectionModule::class,
    AndroidModule::class,
    ChatBotApplicationModule::class,
    ChatBotApplicationBuilderModule::class
    ]
)
interface ChatBotApplicationComponent {

    fun inject(application: ChatBotApplication)
}
