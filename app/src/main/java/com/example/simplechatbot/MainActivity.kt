package com.example.simplechatbot

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import com.example.simplechatbot.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


class MainActivity : BaseActivity() {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context
    private var isListening = false

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)

        setContentView(R.layout.activity_main)
        listeningButton.setOnClickListener(::onListeningClicked)
        Timber.i("onCreate called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("${ sharedPref.getBoolean(Constants.IS_ONBOARDING_DONE, false)}")
        if (!sharedPref.getBoolean(Constants.IS_ONBOARDING_DONE, false)) {
            startActivity(OnboardingActivity.intent(context))
            finish()
        }
    }

    fun onListeningClicked(view: View) {
        isListening = !isListening
        if (isListening) {
            listeningButton.text = getString(R.string.listening_button_listening)
            Timber.i("Start listening")
        } else {
            listeningButton.text = getString(R.string.listening_button)
            Timber.i("Stop listening")
        }


    }


    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

}
