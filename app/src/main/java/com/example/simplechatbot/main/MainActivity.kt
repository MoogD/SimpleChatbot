package com.example.simplechatbot.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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


class MainActivity : BaseActivity() {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel

    private var chatListAdapter = ChatListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
        mainViewModel.checkOnboarding()
        setupListener()

        setContentView(R.layout.activity_main)
        listeningButton.setOnClickListener(::onListeningClicked)
        utteranceList.adapter = chatListAdapter
        mainViewModel.prepareListening(context)
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
        Timber.i("$view was clicked")
        mainViewModel.startListening()
    }

    private fun startOnboarding() {
        startActivity(OnboardingActivity.intent(context))
        finish()
    }

    private fun startListening() {
        listeningButton.text = getString(R.string.listening_button_listening)
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
