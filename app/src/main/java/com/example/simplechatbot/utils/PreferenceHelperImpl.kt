package com.example.simplechatbot.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.GsonBuilder
import javax.inject.Inject

class PreferenceHelperImpl @Inject constructor(context: Context) : PreferenceHelper {

    private val sharedPreferences =
        context.getSharedPreferences(APP_SETTINGS_KEY, Context.MODE_PRIVATE)

    override fun getOnboardingState(): Boolean =
        sharedPreferences.getBoolean(ONBOARDING_DONE_KEY, false)

    override fun setOnboardingState(onboardingDone: Boolean) {
        sharedPreferences.edit()
            .putBoolean(ONBOARDING_DONE_KEY, onboardingDone)
            .apply()
    }

    override fun isSignedIn(): Boolean =
        sharedPreferences.contains(ACCOUNT_KEY)

    override fun getAccount(): GoogleSignInAccount {
        val account = sharedPreferences.getString(ACCOUNT_KEY, null)
        return GsonBuilder()
            .create()
            .fromJson(account, GoogleSignInAccount::class.java)
    }

    override fun setAccount(account: GoogleSignInAccount) {
        val googleAccount = GsonBuilder()
            .create()
            .toJson(account)
        sharedPreferences
            .edit()
            .putString(ACCOUNT_KEY, googleAccount)
            .apply()
    }

    companion object {
        private const val APP_SETTINGS_KEY = "APP_SETTINGS_KEY"

        private const val ONBOARDING_DONE_KEY = "ONBOARDING_DONE_KEY"
        private const val ACCOUNT_KEY = "ACCOUNT_KEY"
    }
}