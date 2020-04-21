package com.example.simplechatbot.assistant

import android.content.Context
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject


class CredentialProviderImpl @Inject constructor(
    private val context: Context
) : CredentialProvider {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun provideCredentials(): GoogleCredentials? {
        var token: AccessToken? = null
        uiScope.launch {
            val stream: InputStream =
                context.assets.open("simplechatbot-b9313-8f52c1b81876.json")
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            token = credentials.refreshAccessToken()
        }
        return GoogleCredentials(token)
    }
}