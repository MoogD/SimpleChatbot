package com.example.simplechatbot

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasAndroidInjector {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context
    private var isListening = false

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        listeningButton.setOnClickListener(::onListeningClicked)
    }

    override fun onResume() {
        super.onResume()
        if (!appManager.app.onboardingIsDone) {
            startActivity(OnboardingActivity.intent(context))
            finish()
        }
    }

    fun onListeningClicked(view: View) {

    }


    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

}
