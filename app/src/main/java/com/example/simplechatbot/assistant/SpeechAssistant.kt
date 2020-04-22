package com.example.simplechatbot.assistant

import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult

interface SpeechAssistant {

    fun translateFile(path: String): List<SpeechRecognitionResult>?
    fun prepareSpeechAssistant(credentialProvider: CredentialProvider)
}