package com.example.simplechatbot

import android.app.Application
import android.content.Context
import com.example.simplechatbot.utils.AndroidUtils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class ChatBotApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private val component: ChatBotApplicationComponent by lazy {
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

    private fun initApplication() {
        AndroidUtils.setupTimber()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    companion object {
        operator fun get(context: Context): ChatBotApplication {
            return context.applicationContext as ChatBotApplication
        }
    }
}
