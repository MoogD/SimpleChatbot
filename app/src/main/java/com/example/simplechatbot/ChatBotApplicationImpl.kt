package com.example.simplechatbot

import com.example.simplechatbot.utils.AndroidUtils
import dagger.android.AndroidInjector

open class ChatBotApplicationImpl : ChatBotApplication() {
    override fun initApplication() {
        AndroidUtils.setupTimber()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

}