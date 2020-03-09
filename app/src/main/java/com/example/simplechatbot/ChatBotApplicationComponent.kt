package com.example.simplechatbot

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
    AndroidModule::class,
    ChatBotApplicationBuilderModule::class,
    ChatBotApplicationModule::class
    ]
)
interface ChatBotApplicationComponent {

    fun inject(application: ChatBotApplication)
}