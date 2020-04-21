package com.example.simplechatbot.assistant

import com.google.auth.oauth2.GoogleCredentials

interface CredentialProvider {
    fun provideCredentials(): GoogleCredentials?
}