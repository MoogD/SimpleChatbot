package com.example.simplechatbot.main

interface WuWListener {
    fun setWuWListener(listener: ResultListener)
    fun armWuW()
    fun awaitEndOfSpeech()

    interface ResultListener {
        fun onWuW()
        fun onSpeechEnd()
    }
}