package com.example.simplechatbot.utils

interface PathProvider {
    fun provideAudioInputFilePath(): String
    fun provideAudioOutputFilePath(): String
}