package com.example.simplechatbot.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.injections.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel

    private var chatListAdapter = ChatListAdapter()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MainService.MainBinder
            mainViewModel.setWuWListener(binder.getService())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
        mainViewModel.checkOnboarding()
        setupListener()

        setContentView(R.layout.activity_main)
        utteranceList.adapter = chatListAdapter

        Intent(this, MainService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun setupListener() {
        mainViewModel.onboardingDone.observe(this, Observer {
            if (!it) {
                startOnboarding()
            }
        })
        mainViewModel.isListening.observe(this, Observer {
            when (it) {
                null -> startProcessing()
                true -> startListening()
                false -> stopListening()
            }
        })
        mainViewModel.conversationObservable.observe(this, Observer {
            chatListAdapter.chatItemList = it
        })
        mainViewModel.intent.observe(this, Observer {
            if (it != null) {
                stopListening()
            }
        })
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        mainViewModel.onDestroy()
        super.onDestroy()
    }

    private fun startOnboarding() {
        startActivity(OnboardingActivity.intent(context))
        finish()
    }

    private fun startListening() {
        listeningButton.text = getString(R.string.listening_button_listening)
    }

    private fun startProcessing() {
        listeningButton.text = getString(R.string.listening_button_processing)
    }

    private fun stopListening() {
        listeningButton.text = getString(R.string.listening_button)
    }

    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
    }
}
