package com.example.simplechatbot.onboarding.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.simplechatbot.onboarding.OnOnboardingActivityInteractionListener
import dagger.android.support.AndroidSupportInjection
import java.lang.RuntimeException

abstract class OnboardingBaseFragment: Fragment() {

    protected var listener: OnOnboardingActivityInteractionListener? = null


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OnOnboardingActivityInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnOnboardingActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    abstract fun updateUi()
}