package com.example.simplechatbot.assistant

import com.google.cloud.speech.v1.SpeechRecognitionResult

interface SpeechAssistant {

    fun translateFile(path: String): List<SpeechRecognitionResult>
}