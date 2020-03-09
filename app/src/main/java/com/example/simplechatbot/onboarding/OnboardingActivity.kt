package com.example.simplechatbot.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.simplechatbot.MainActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.fragments.OnboadringStartFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector

import kotlinx.android.synthetic.main.activity_onboarding.*
import javax.inject.Inject

class OnboardingActivity : AppCompatActivity(), HasAndroidInjector,  OnOnboardingActivityInteractionListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    private var currentStepIndex: Int = 0
    private lateinit var onboardingSteps: Array<Fragment>

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)

    }

    fun setupOnboardingSteps() {
        onboardingSteps = arrayOf(
            OnboadringStartFragment.newInstance(R.string.onboarding_start_title, R.string.onboarding_start_text)
        )
    }

    override fun onNextStep(): Boolean {
        if (currentStepIndex < onboardingSteps.size - 1) {
            currentStepIndex++
        } else {
            container.visibility = View.INVISIBLE
            startActivity(MainActivity.intent(context))
        }
        return true
    }

    override fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        return true
    }

    override fun onPermissionsRequest(permissions: Array<String>) {

    }

    companion object {
        const val REQUEST_PERMISSIONS: Int = 20001
        const val REQUEST_ENABLE_BT: Int = 20002

        fun intent(context: Context) =
            Intent(context, OnboardingActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
    }

}
