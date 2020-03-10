package com.example.simplechatbot.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.example.simplechatbot.BaseActivity
import com.example.simplechatbot.MainActivity
import com.example.simplechatbot.R
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.fragments.OnboardingPermissionFragment
import com.example.simplechatbot.onboarding.fragments.OnboardingStartFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

import kotlinx.android.synthetic.main.activity_onboarding.*
import timber.log.Timber
import javax.inject.Inject


private const val RC_SIGN_IN = 1

class OnboardingActivity : BaseActivity(), HasAndroidInjector,
    OnOnboardingActivityInteractionListener {


    @field :[Inject ApplicationContext]
    internal lateinit var context: Context


    private var currentStepIndex: Int = 0
    private lateinit var onboardingSteps: Array<OnboardingStep>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var signedIn = false

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    init {
        // Enable VectorDrawables on devices below Android 5
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setSupportActionBar(toolbar)

        setupOnboardingSteps()

        if (savedInstanceState != null) {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)

            onboardingSteps.forEachIndexed { i, step ->
                if (fragment?.tag == step.tag) {
                    currentStepIndex = i
                }
            }
        }

        mGoogleSignInClient = configureSignIn()

        loadCurrentStepFragment()
        Timber.i("onCreate Finished")
    }


    fun setupOnboardingSteps() {
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
                fragment = OnboardingPermissionFragment.newInstance(R.string.onboarding_start_title,
                    R.string.onboarding_start_text)
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
            Timber.i("tag: ${ currentStep.tag }")
            commitAllowingStateLoss()

            Timber.i("Visibility: ${ window.decorView.systemUiVisibility }")
            Timber.i("currentFragment loaded")
        }
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

    override fun onSignIn() {
        signIn(mGoogleSignInClient)
    }

    fun configureSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }

    private fun signIn(mGoogleSignInClient: GoogleSignInClient): Intent {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
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
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
//            updateUI(account)
            signedIn = true
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.w("signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
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
