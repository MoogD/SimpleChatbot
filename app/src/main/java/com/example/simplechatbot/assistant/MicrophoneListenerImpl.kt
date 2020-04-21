package com.example.simplechatbot.assistant

import android.media.MediaRecorder
import timber.log.Timber
import java.io.File

class MicrophoneListenerImpl : MicrophoneListener {
    private val mediaRecorder = MediaRecorder()
    private var isPrepared = false

    private fun prepareRecorder(path: String) {
        val file = prepareAudioFile(path)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        mediaRecorder.setOutputFile(file)
        mediaRecorder.prepare()
        isPrepared = true
    }

    private fun prepareAudioFile(path: String): File {
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    override fun startListening(path: String) {
        try {
            if (isPrepared) {
                mediaRecorder.start()
            } else {
                prepareRecorder(path)
                mediaRecorder.start()
            }
        } catch (e: IllegalStateException) {
            Timber.w("Preparing listening before starting to record audio failed!")
        }
    }

    override fun stopListening() {
        try {
            mediaRecorder.stop()
        } catch (e: IllegalStateException) {
            Timber.w("Recording has to be started before calling stopListening")
        }
    }

    override fun destroy() {
        mediaRecorder.release()
    }
}