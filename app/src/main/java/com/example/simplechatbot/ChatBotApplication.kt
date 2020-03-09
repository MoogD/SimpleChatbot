package com.example.simplechatbot

import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class ChatBotApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>


//    open val component: ChatBotApplicationComponent by lazy {
//        DaggerChatBotApplicationComponent
//            .builder()
//            .androidModule(
//                AndroidModule(applyLocaleToApplicationContext(applicationContext))
//            )
//            .build()
//    }
}