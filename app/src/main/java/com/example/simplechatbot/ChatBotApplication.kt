package com.example.simplechatbot

import android.app.Application
import android.content.Context
import com.example.simplechatbot.utils.AppStateManager
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class ChatBotApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var appManager: AppStateManager

    open val component: ChatBotApplicationComponent by lazy {
        DaggerChatBotApplicationComponent
            .builder()
            .androidModule(
                AndroidModule(applicationContext)
            )
            .build()
    }

    companion object {
        operator fun get(context: Context): ChatBotApplication {
            return context.applicationContext as ChatBotApplication
        }
    }
}