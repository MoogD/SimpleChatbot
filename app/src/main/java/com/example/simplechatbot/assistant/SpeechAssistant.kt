package com.example.simplechatbot.assistant

import com.google.api.gax.core.CredentialsProvider

interface SpeechAssistant {

    suspend fun prepareSpeechAssistant(credentialProvider: CredentialsProvider)
    suspend fun prepareResponse(inputPath: String, outputPath: String): AssistantIntent
}
