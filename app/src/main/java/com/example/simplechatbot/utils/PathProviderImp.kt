package com.example.simplechatbot.utils

import android.content.Context
import javax.inject.Inject

class PathProviderImp @Inject constructor(private val context: Context) : PathProvider {

    override fun provideAudioInputFilePath(): String =
        context.applicationInfo.dataDir + "/$AUDIO_INPUT_FILE_PATH"

    override fun provideAudioOutputFilePath(): String =
        context.applicationInfo.dataDir + "/$AUDIO_OUTPUT_FILE_PATH"

    companion object {
        private const val AUDIO_INPUT_FILE_PATH = "AudioFile.wav"
        private const val AUDIO_OUTPUT_FILE_PATH = "OutputFile.wav"
    }
}
