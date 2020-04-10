package com.example.simplechatbot.onboarding.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.simplechatbot.R
import com.example.simplechatbot.onboarding.OnboardingViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.onboarding_start.*
import javax.inject.Inject

class OnboardingStartFragment : Fragment() {
    private var titleRes: Int? = null
    private var textRes: Int? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var onboardingViewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
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
        sign_in_button.setOnClickListener(::onSignIn)

        onboardingViewModel = ViewModelProviders.of(
            requireActivity(),
            factory
        )[OnboardingViewModel::class.java]

        setupObservers()
    }

    private fun setupObservers() {
        onboardingViewModel.signedIn.observe(this, Observer {
            if (it) {
                updateUi()
            }
        })
    }
    private fun onSignIn(view: View) {
        onboardingViewModel.initSignIn()
    }

    private fun updateUi() {
        next_button.isEnabled = true
        next_button.setOnClickListener {
            onboardingViewModel.onNextStep()
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
