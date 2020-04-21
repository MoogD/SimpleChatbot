package com.example.simplechatbot.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.assistant.MicrophoneListener
import com.example.simplechatbot.assistant.MicrophoneListenerImpl
import com.example.simplechatbot.assistant.SpeechAssistant
import com.example.simplechatbot.utils.PreferenceHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
    private val speechAssistant: SpeechAssistant
): ViewModel() {

    var onboardingDone: MutableLiveData<Boolean> = MutableLiveData(false)

    var isListening = MutableLiveData(false)
    private val conversation = mutableListOf<ChatItem>()
    val conversationObservable = MutableLiveData<MutableList<ChatItem>>()
    private var idCount = 0

    private val listener: MicrophoneListener = MicrophoneListenerImpl()

//    fun prepareListening(context: Context) {
//
//    }

    fun startListening(path: String) {
        if (isListening.value == true) {
            listener.stopListening()
            val results = speechAssistant.translateFile(path)
            Timber.i("Results: $results")
            results.forEach { result ->
                Timber.i("$result")
            }
            isListening.value = false
        } else {
            listener.startListening(path)
            isListening.value = true
        }
    }

    private fun updateConversation(chatItem: ChatItem) {
        conversation.add(chatItem)
        conversationObservable.value = conversation
    }

    fun checkOnboarding() {
        onboardingDone.value = preferenceHelper.getOnboardingState()
    }

    companion object {
        private const val ACCESS_TOKEN = "b3e790d591064ff79c8124aa8115ed42"
    }
    @Suppress("UNCHECKED_CAST")
    class Factory(val provider: Provider<MainViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider.get() as T
    }
}