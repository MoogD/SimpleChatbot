package com.example.simplechatbot.main

import android.content.Context
import com.example.simplechatbot.utils.Constants

class MainPresenterImpl(override val context: Context): MainPresenter {

    override var view: MainView? = null
    override var isListening = false
    override val conversation: MutableList<ChatItem> = mutableListOf<ChatItem>()

    val sharedPref = context.getSharedPreferences(Constants.APP_SETTINGS, Context.MODE_PRIVATE)

    override fun bindView(mainView: MainView) {
        view = mainView
        if (!sharedPref.getBoolean(Constants.IS_ONBOARDING_DONE, false)) {
            view?.startOnboarding()
        }
    }

    override fun unbindView() {
        view = null
    }

    override fun listeningPressed() {
        conversation.add(ChatItem("User", ItemDirection.LEFT, "Hello"))
        conversation.add(ChatItem("Assistant", ItemDirection.RIGHT, "Hello, How are you?"))
    }
}