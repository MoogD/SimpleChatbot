package com.example.simplechatbot.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface PreferenceHelper {

    fun getOnboardingState(): Boolean
    fun setOnboardingState(onboardingDone: Boolean)

    fun isSignedIn(): Boolean
    fun getAccount(): GoogleSignInAccount
    fun setAccount(account: GoogleSignInAccount)
}