package com.example.simplechatbot

import android.app.Application
import android.content.Context
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class ChatBotApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    open val component: ChatBotApplicationComponent by lazy {
        DaggerChatBotApplicationComponent
            .builder()
            .androidModule(
                AndroidModule(applicationContext)
            )
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        component.inject(this)

        initApplication()
    }

    abstract fun initApplication()

    companion object {
        operator fun get(context: Context): ChatBotApplication {
            return context.applicationContext as ChatBotApplication
        }
    }
}
