package com.example.simplechatbot.main


import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.model.Result
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.injections.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity(), AIListener {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var mainViewModel: MainViewModel

    private var chatListAdapter = ChatListAdapter()
    private lateinit var config: AIConfiguration
    private lateinit var aiService: AIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
        mainViewModel.checkOnboarding()
        setupListener()

        setContentView(R.layout.activity_main)
        listeningButton.setOnClickListener(::onListeningClicked)
        utteranceList.adapter = chatListAdapter

        config = AIConfiguration(
            "b3e790d591064ff79c8124aa8115ed42",
            ai.api.AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System
        )
        aiService = AIService.getService(this, config)
        aiService.setListener(this)

    }

    private fun setupListener() {
        mainViewModel.onboardingDone.observe(this, Observer{
            if (!it) {
                startOnboarding()
            }
        })
        mainViewModel.isListening.observe(this, Observer {
            if (it) {
                startListening()
            } else {
                stopListening()
            }
        })
        mainViewModel.conversationObservable.observe(this, Observer {
            chatListAdapter.chatItemList = it
        })
    }

    private fun onListeningClicked(view: View) {
        mainViewModel.changeListeningState()
        listen()
    }

    private fun listen() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            aiService.startListening()
        }
    }
    private fun startOnboarding() {
        startActivity(OnboardingActivity.intent(context))
        finish()
    }

    private fun startListening() {
        listeningButton.text = getString(R.string.listening_button_listening)
        Timber.i("Start listening")
    }

    private fun stopListening() {
        listeningButton.text = getString(R.string.listening_button)
        Timber.i("Stop listening")
    }

    override fun onResult(response: AIResponse?) {
        mainViewModel.onResponse(response)
    }

    override fun onListeningStarted() {
    }

    override fun onAudioLevel(level: Float) {
    }

    override fun onError(error: AIError?) {
        Timber.w("Listening error: %s", error!!.message)
    }

    override fun onListeningCanceled() {
    }

    override fun onListeningFinished() {
        listeningButton.text = getString(R.string.listening_button)
    }

    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }
}
