package com.example.simplechatbot.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.MainActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragment
import com.example.simplechatbot.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.activity_onboarding.*
import timber.log.Timber
import javax.inject.Inject

class OnboardingActivity : BaseActivity(), OnOnboardingActivityInteractionListener {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    private lateinit var sharedPreferences: SharedPreferences
    private var currentStepIndex: Int = 0
    private lateinit var onboardingSteps: Array<OnboardingStep>
    private lateinit var googleSignInClient: GoogleSignInClient
    override var signedIn = false

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)
        sharedPreferences = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE)
        setupOnboardingSteps()
        Timber.i("onboarding activity")
        currentStepIndex = savedInstanceState?.getInt("CURRENT_STEP_INDEX") ?: 0

        googleSignInClient = configureSignIn()

        loadCurrentStepFragment()
        Timber.i("onCreate Finished")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.run {
            putInt("CURRENT_STEP_INDEX", currentStepIndex)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setupOnboardingSteps() {
        val onboardingStartFragment by lazy {
            OnboardingStep(
                tag = "start",
                fragment = OnboardingStartFragment.newInstance(R.string.onboarding_start_title,
                    R.string.onboarding_start_text)
            )
        }
        val onboadringPermissionFragment by lazy {
            OnboardingStep(
                tag = "permission",
                fragment = OnboardingPermissionFragment.newInstance(
                    R.string.onboarding_permission_title,
                    R.string.onboarding_permission_text,
                    permissions = createPermissionsList()
                )
            )
        }
        onboardingSteps = arrayOf(
            onboardingStartFragment,
            onboadringPermissionFragment
        )
    }

    private fun loadCurrentStepFragment() {
        val currentStep = onboardingSteps[currentStepIndex]

        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.findFragmentById(R.id.container)?.let { previousFragment ->
                hide(previousFragment)
                detach(previousFragment)
            }

            supportFragmentManager.findFragmentByTag(currentStep.tag)?.let { newFragment ->
                attach(newFragment)
                show(newFragment)
            } ?: add(R.id.container, currentStep.fragment, currentStep.tag)
            commitAllowingStateLoss()
        }
    }

    override fun onNextStep(): Boolean {
        if (currentStepIndex < onboardingSteps.size - 1) {
            currentStepIndex++
            loadCurrentStepFragment()
        } else {
            container.visibility = View.INVISIBLE
            sharedPreferences.edit().putBoolean(Constants.IS_ONBOARDING_DONE, true).apply()
            startActivity(MainActivity.intent(context))
            finish()
        }
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
                onboardingSteps[currentStepIndex].fragment.let {
                    (it as? OnboardingPermissionFragment)?.onPermissionsResult(grantResults)
                }
        }
    }

    private fun createPermissionsList(): ArrayList<String> = arrayListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onSignIn() {
        if (!sharedPreferences.contains("ACCOUNT")) {
            signIn(googleSignInClient)
        } else {
            Timber.i("Allready logged in!")
            signedIn = true
            onboardingSteps[currentStepIndex].fragment.updateUi()
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
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
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
        try {
            val account = GsonBuilder()
                .create()
                .toJson(completedTask.getResult(ApiException::class.java))
            sharedPreferences.edit().putString("ACCOUNT", account).apply()
            signedIn = true
        } catch (e: ApiException) {
            Timber.w("signInResult:failed code=" + e.statusCode)
        }
        if (onboardingSteps[currentStepIndex].tag == "start") {
            onboardingSteps[currentStepIndex].fragment.updateUi()
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
