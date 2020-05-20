package com.example.simplechatbot.onboarding

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
import com.example.simplechatbot.main.MainActivity
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_onboarding.*
import javax.inject.Inject

class OnboardingActivity : BaseActivity() {

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    private var currentStep: OnboardingStep? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var onboardingViewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onboardingViewModel = ViewModelProviders.of(
            this,
            factory
        )[OnboardingViewModel::class.java]

        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)

        setupObservers()

        loadCurrentStepFragment()
    }

    private fun setupObservers() {
        onboardingViewModel.currentStep.observe(this, Observer { newStep ->
            if (newStep != null) {
                currentStep = newStep
                loadCurrentStepFragment()
            } else {
                container.visibility = View.INVISIBLE
                onboardingViewModel.finishFTU()
                startActivity(MainActivity.intent(context))
                finish()
            }
        })

        onboardingViewModel.signedIn.observe(this, Observer {
            if (!it) {
                onboardingViewModel.startSignIn(context)
            }
        })

        onboardingViewModel.googleSignInClient.observe(this, Observer {
            it?.let {
                signIn(it)
            }
        })
    }

    private fun loadCurrentStepFragment() {

        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.findFragmentById(R.id.container)?.let { previousFragment ->
                hide(previousFragment)
                detach(previousFragment)
            }

            supportFragmentManager.findFragmentByTag(currentStep?.tag)?.let { newFragment ->
                attach(newFragment)
                show(newFragment)
            } ?: currentStep?.fragment?.let { add(R.id.container, it, currentStep?.tag) }
            commitAllowingStateLoss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSIONS ->
                currentStep?.fragment.let {
                    (it as? OnboardingPermissionFragment)?.onPermissionsResult(grantResults)
                }
        }
    }

    private fun signIn(googleSignInClient: GoogleSignInClient) {
        startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            onboardingViewModel.setAccount(
                GoogleSignIn.getSignedInAccountFromIntent(data)
            )
        }
    }

    companion object {
        const val REQUEST_PERMISSIONS: Int = 20001
        const val RC_SIGN_IN: Int = 20002

        fun intent(context: Context) =
            Intent(context, OnboardingActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
    }
}
