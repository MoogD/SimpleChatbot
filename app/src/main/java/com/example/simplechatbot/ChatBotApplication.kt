package com.example.simplechatbot

import android.app.Application
import android.content.Context
import com.example.simplechatbot.utils.AppStateManager
import com.example.simplechatbot.utils.AppStateManagerImpl
import com.example.simplechatbot.utils.LocaleHelper
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
                AndroidModule(applyLocaleToApplicationContext(applicationContext))
            )
            .build()
    }

    private fun applyLocaleToApplicationContext(context: Context): Context {
        return LocaleHelper.onAttach(
            context,
            if (::appManager.isInitialized) appManager.locale else AppStateManagerImpl(
                context
            ).locale
        )
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