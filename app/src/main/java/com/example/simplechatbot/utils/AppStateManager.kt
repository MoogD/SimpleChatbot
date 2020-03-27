package com.example.simplechatbot.utils

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.*

interface AppStateManager {
    var account: GoogleSignInAccount?

    val app: App

    var locale: Locale

    interface App {
        var onboardingIsDone: Boolean
    }
}
