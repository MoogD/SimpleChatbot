package com.example.simplechatbot.utils

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AppStateManagerImpl(val context: Context) : AppStateManager {


    override var account: GoogleSignInAccount?
        get() = account
        set(value) {
            account = value
        }

    override val app: AppStateManager.App = object : AppStateManager.App {
        override var onboardingIsDone: Boolean?
            get() = onboardingIsDone ?: false
            set(value) {
                onboardingIsDone = value
            }
    }
}