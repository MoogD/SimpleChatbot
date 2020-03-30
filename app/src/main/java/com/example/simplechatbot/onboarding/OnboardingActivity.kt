package com.example.simplechatbot.onboarding

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.main.MainActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task

import kotlinx.android.synthetic.main.activity_onboarding.*
import timber.log.Timber
import javax.inject.Inject

class OnboardingActivity : BaseActivity(), OnOnboardingActivityInteractionListener {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    private lateinit var googleSignInClient: GoogleSignInClient
    override var signedIn = false

    private var currentStep: OnboardingStep? = null

    val onboardingViewModel: OnboardingViewModel by lazy {
        ViewModelProviders.of(this).get(OnboardingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)

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

        onboardingViewModel.signedIn.observe(this, Observer { newBoolean ->
            signedIn = newBoolean
        })

        Timber.i("onboarding activity")

        googleSignInClient = configureSignIn()

        loadCurrentStepFragment()
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

    override fun onNextStep(): Boolean {
        onboardingViewModel.onNextStep()
        return true
    }

    override fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onPermissionsRequest(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS)
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

    override fun onSignIn() {
        if (!signedIn) {
            signIn(googleSignInClient)
        } else {
            Timber.i("Allready logged in!")
            currentStep?.fragment?.updateUi()
        }
    }

    fun configureSignIn(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .requestScopes(Scope(Scopes.APP_STATE),Scope(Scopes.PROFILE))
            .build()
        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    private fun signIn(googleSignInClient: GoogleSignInClient): Intent {
        val signInIntent: Intent = googleSignInClient.getSignInIntent()

        startActivityForResult(signInIntent, RC_SIGN_IN)
        return signInIntent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
       onboardingViewModel.setAccount(completedTask)
        if (currentStep?.tag == "start") {
            currentStep?.fragment?.updateUi()
        }
    }

    companion object {
        const val REQUEST_PERMISSIONS: Int = 20001
        const val RC_SIGN_IN : Int = 20002

        fun intent(context: Context) =
            Intent(context, OnboardingActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
    }

}
