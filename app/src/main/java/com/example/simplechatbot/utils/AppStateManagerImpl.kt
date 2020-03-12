package com.example.simplechatbot.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.util.*

class AppStateManagerImpl(val context: Context) : AppStateManager {

    override var account: GoogleSignInAccount? = null
        get() = field
        set(value) {
            field = value
        }

    override var app: AppStateManager.App = object : AppStateManager.App {
        override var onboardingIsDone: Boolean = false
            get() = field
            set(value) {
                field = value
            }
    }
    override var locale: Locale
        get() = Locale.US
        set(value) {
            locale = value
        }
}