package com.example.simplechatbot.assistant

interface MicrophoneListener {
    fun startListening(path: String)
    fun stopListening()
    fun destroy()
}