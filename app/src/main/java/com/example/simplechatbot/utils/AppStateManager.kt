package com.example.simplechatbot.utils

import android.accounts.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface AppStateManager {
    var account: GoogleSignInAccount?

    val app: App

    interface App {
        val onboardingIsDone: Boolean?
    }
}
