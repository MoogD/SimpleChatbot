package com.example.simplechatbot.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.GsonBuilder
import javax.inject.Inject

class PreferenceHelperImpl @Inject constructor(val context: Context) : PreferenceHelper {

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
        sharedPreferences.contains(ACCOUUNT_KEY)

    override fun getAccount(): GoogleSignInAccount {
        val account = sharedPreferences.getString(ACCOUUNT_KEY, null)
        return GsonBuilder()
            .create()
            .fromJson(account, GoogleSignInAccount::class.java)
    }

    override fun setAccount(account: GoogleSignInAccount) {
        val account = GsonBuilder()
            .create()
            .toJson(account)
        sharedPreferences
            .edit()
            .putString(ACCOUUNT_KEY, account)
            .apply()
    }

    companion object {
        const val APP_SETTINGS_KEY = "APP_SETTINGS_KEY"

        const val ONBOARDING_DONE_KEY = "ONBOARDING_DONE_KEY"
        const val ACCOUUNT_KEY = "ACCOUNT_KEY"
    }
}