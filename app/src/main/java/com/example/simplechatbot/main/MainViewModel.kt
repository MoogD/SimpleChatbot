package com.example.simplechatbot.main

import ai.api.model.AIResponse
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.injections.ApplicationContext
import com.example.simplechatbot.utils.PreferenceHelper
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel @Inject constructor(private val preferenceHelper: PreferenceHelper): ViewModel() {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    var onboardingDone: MutableLiveData<Boolean> = MutableLiveData(false)

    var isListening = MutableLiveData(false)
    private val conversation = mutableListOf<ChatItem>()
    val conversationObservable = MutableLiveData<MutableList<ChatItem>>()
    private var idCount = 0

    fun changeListeningState() {
        isListening.value = !(isListening.value ?: false)

    }

    private fun updateConversation(chatItem: ChatItem) {
        conversation.add(chatItem)
        conversationObservable.value = conversation
    }

    fun checkOnboarding() {
        onboardingDone.value = preferenceHelper.getOnboardingState()
    }

    fun onResponse(response: AIResponse?) {
        updateConversation(
            ChatItem(
                idCount++,
                "User",
                ItemDirection.LEFT,
                response?.result?.resolvedQuery ?: "..."
            )
        )
        updateConversation(
            ChatItem(
                idCount++,
                "Assistant",
                ItemDirection.RIGHT,
                response?.result?.fulfillment?.speech
                    ?: "I could not get that. Please try again."
            )
        )

    }
    class Factory(val provider: Provider<MainViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider.get() as T
    }
}