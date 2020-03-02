package com.example.simplechatbot

import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recorder = prepareRecorder()
        var listening = false

        listeningButton.setOnClickListener {
            if (!listening) {
                recorder.start()
                listeningButton.text = "listening"
            }
            else {
                recorder.stop()
                listeningButton.text = getString(R.string.listening_button)
            }
            listening = !listening
        }
    }



    fun prepareRecorder(): MediaRecorder {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        return recorder
    }

}
