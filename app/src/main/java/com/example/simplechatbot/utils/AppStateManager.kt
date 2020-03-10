package com.example.simplechatbot.utils

import android.accounts.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.*

interface AppStateManager {
    var account: GoogleSignInAccount?

    val app: App

    var locale: Locale

    interface App {
        val onboardingIsDone: Boolean?
    }
}
