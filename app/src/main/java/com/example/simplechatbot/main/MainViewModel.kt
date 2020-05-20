package com.example.simplechatbot.main

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simplechatbot.assistant.AssistantIntent
import com.example.simplechatbot.assistant.MicrophoneListener
import com.example.simplechatbot.assistant.SpeechAssistant
import com.example.simplechatbot.utils.PathProvider
import com.example.simplechatbot.utils.PreferenceHelper
import com.google.api.gax.core.CredentialsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

class MainViewModel @Inject constructor(
    private val preferenceHelper: PreferenceHelper,
    private val speechAssistant: SpeechAssistant,
    private val credentialProvider: CredentialsProvider,
    private val pathProvider: PathProvider,
    private val listener: MicrophoneListener
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + job)

    var onboardingDone: MutableLiveData<Boolean> = MutableLiveData(false)

    var isListening: MutableLiveData<Boolean?> = MutableLiveData(false)
    val conversationObservable = MutableLiveData<MutableList<ChatItem>>()
    private var wuwListener: WuWListener? = null
    var intent = MutableLiveData<AssistantIntent>()

    private val wuwResponseListener = object : WuWListener.ResultListener {
        override fun onWuW() {
            startListening()
        }

        override fun onSpeechEnd() {
            stopListening()
        }
    }

    init {
        uiScope.launch {
            speechAssistant.prepareSpeechAssistant(credentialProvider)
        }
    }

    fun startListening() {
        if (isListening.value == false) {
            listener.startListening(pathProvider.provideAudioInputFilePath())
            isListening.value = true
            wuwListener?.awaitEndOfSpeech()
        }
    }

    fun stopListening() {
        if (isListening.value == true) {
            listener.stopListening()
            Timber.i("Stop listening called!")
            CoroutineScope(Dispatchers.Main).launch {
                intent.value = uiScope.async sync@{
                    return@sync getAssistantResponse()
                }.await()
            }
            isListening.value = null
            wuwListener?.armWuW()
        }
    }

    private suspend fun getAssistantResponse(): AssistantIntent {
        Timber.i("GetAssistantResponse called")
        val result = speechAssistant
            .prepareResponse(
                pathProvider.provideAudioInputFilePath(),
                pathProvider.provideAudioOutputFilePath()
            )
        Timber.i("Assistant result")
        val player = MediaPlayer()
        player.setDataSource(pathProvider.provideAudioOutputFilePath())
        player.prepare()
        player.start()
        return result
    }

    fun setWuWListener(wuWListener: WuWListener) {
        this.wuwListener = wuWListener
        wuwListener?.setWuWListener(wuwResponseListener)
    }

    fun onDestroy() {
        job.cancel()
        wuwListener = null
        listener.destroy()
    }

    fun checkOnboarding() {
        onboardingDone.value = preferenceHelper.getOnboardingState()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(val provider: Provider<MainViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            provider.get() as T
    }
}