package com.example.simplechatbot.onboarding.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.simplechatbot.onboarding.OnOnboardingActivityInteractionListener
import com.example.simplechatbot.utils.AppStateManager
import dagger.android.support.AndroidSupportInjection
import java.lang.RuntimeException
import javax.inject.Inject

abstract class OnboardingBaseFragment: Fragment() {

    internal var listener: OnOnboardingActivityInteractionListener? = null

    @Inject
    lateinit var appManager: AppStateManager

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

        if (context is OnOnboardingActivityInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnOnboardingActivityInteractionListener")
        }
    }

    override fun onStart() {
        super.onStart()

        if (getContext() is OnOnboardingActivityInteractionListener) {
            listener = getContext() as OnOnboardingActivityInteractionListener
        } else {
            throw RuntimeException(context.toString() + " must implement OnOnboardingActivityInteractionListener")
        }
    }

    override fun onStop() {
        super.onStop()
        listener = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    open fun updateUi() {

    }
}