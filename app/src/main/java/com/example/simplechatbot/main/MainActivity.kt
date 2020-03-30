package com.example.simplechatbot.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity(), MainView {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context
    private var isListening = false

    private val presenter = MainPresenterImpl(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        listeningButton.setOnClickListener(::onListeningClicked)
        Timber.i("onCreate called")
    }

    override fun onStart() {
        super.onStart()

        presenter.bindView(this)
    }

    override fun onStop() {
        super.onStop()

        presenter.unbindView()
    }

    fun onListeningClicked(view: View) {
        presenter.listeningPressed()
    }

    override fun startOnboarding() {
        startActivity(OnboardingActivity.intent(context))
        finish()
    }

    fun setAdapter(adapter: ListAdapter<ChatItem, RecyclerView.ViewHolder>) {
        utteranceList.adapter = adapter
    }

    override fun updateChat(chatItems: List<ChatItem>) {

    }

    override fun stopListening() {
        isListening = false
        listeningButton.text = getString(R.string.listening_button)
        Timber.i("Stop listening")
    }

    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }
}
