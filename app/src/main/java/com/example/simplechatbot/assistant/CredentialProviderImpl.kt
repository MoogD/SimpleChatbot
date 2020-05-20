package com.example.simplechatbot.assistant

import android.content.Context
import com.google.api.gax.core.CredentialsProvider
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class CredentialProviderImpl @Inject constructor(
    private val context: Context
) : CredentialsProvider {
    var token: AccessToken? = null

    override fun getCredentials(): Credentials {
        val stream: InputStream =
            context.assets.open("simplechatbot-b9313-8f52c1b81876.json")
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        try {
            token = credentials.refreshAccessToken()
        } catch (e: IllegalStateException) {
            Timber.w("Error refreshing token: $e")
        }
        return GoogleCredentials(token)
    }
}
