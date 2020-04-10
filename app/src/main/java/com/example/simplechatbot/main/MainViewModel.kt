package com.example.simplechatbot.main

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.utils.PreferenceHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper
): ViewModel(), AIListener {

    var onboardingDone: MutableLiveData<Boolean> = MutableLiveData(false)

    private val config: AIConfiguration = AIConfiguration(
        ACCESS_TOKEN,
        ai.api.AIConfiguration.SupportedLanguages.English,
        AIConfiguration.RecognitionEngine.System
    )
    private lateinit var aiService: AIService

    var isListening = MutableLiveData(false)
    private val conversation = mutableListOf<ChatItem>()
    val conversationObservable = MutableLiveData<MutableList<ChatItem>>()
    private var idCount = 0

    fun prepareListening(context: Context) {
        aiService = AIService.getService(context, config)
        aiService.setListener(this)
    }

    fun startListening() {
        aiService.startListening()
    }

    private fun updateConversation(chatItem: ChatItem) {
        conversation.add(chatItem)
        conversationObservable.value = conversation
    }

    fun checkOnboarding() {
        onboardingDone.value = preferenceHelper.getOnboardingState()
    }

    override fun onResult(response: AIResponse?) {
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

    override fun onListeningStarted() {
        isListening.value = true
    }

    override fun onAudioLevel(level: Float) {
    }

    override fun onError(error: AIError?) {
        Timber.w("AiService error: $error")
        isListening.value = false
    }

    override fun onListeningCanceled() {
        isListening.value = false
    }

    override fun onListeningFinished() {
        isListening.value = false
    }

    companion object {
        private const val ACCESS_TOKEN = "b3e790d591064ff79c8124aa8115ed42"
    }
    class Factory(val provider: Provider<MainViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider.get() as T
    }
}