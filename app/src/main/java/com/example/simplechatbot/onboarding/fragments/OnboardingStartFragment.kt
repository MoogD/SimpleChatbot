package com.example.simplechatbot.onboarding.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.simplechatbot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.onboarding_start.*
import timber.log.Timber


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class OnboardingStartFragment : OnboardingBaseFragment() {
    private var titleRes: Int? = null
    private var textRes: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            titleRes = getInt(ARG_TITLE_RES)
            textRes = getInt(ARG_TEXT_RES)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.onboarding_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.i("onViewCreated called")
        sign_in_button.setOnClickListener {
            listener?.onSignIn()
        }
    }


    override fun updateUi() {
        if (listener?.signedIn == true) {
            signed_in_text.text = "You are signed in as ${ appManager.account?.displayName }"
            next_button.isEnabled = true
            next_button.setOnClickListener {
               listener?.onNextStep()
            }
        } else {
            signed_in_text.text = "Sign in failed"
        }
    }

    companion object {
        private const val ARG_TITLE_RES = "titleRes"
        private const val ARG_TEXT_RES = "textRes"

        @JvmStatic
        fun newInstance(
            @StringRes titleRes: Int,
            @StringRes textRes: Int
        ) =
            OnboardingStartFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TITLE_RES, titleRes)
                    putInt(ARG_TEXT_RES, textRes)
                }
            }
    }
}
