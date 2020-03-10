package com.example.simplechatbot.utils

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.*

class AppStateManagerImpl(val context: Context) : AppStateManager {

    var isOnboardingInitialized = false
    override var account: GoogleSignInAccount?
        get() = account
        set(value) {
            account = value
        }

    override val app: AppStateManager.App = object : AppStateManager.App {
        override var onboardingIsDone: Boolean
            get() = if (isOnboardingInitialized) onboardingIsDone else false
            set(value) {
                onboardingIsDone = value
                isOnboardingInitialized = true
            }
    }
    override var locale: Locale
        get() = Locale.US
        set(value) {
            locale = value
        }
}