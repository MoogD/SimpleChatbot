package com.example.simplechatbot

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val REQUEST_MICROPHONE = 0
private const val REQUEST_WRITE_STORAGE = 1
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listening = false
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_MICROPHONE
            )
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_STORAGE
            )
        }
        val recorder = prepareRecorder()

        listeningButton.setOnClickListener {
            if (!listening) {
                recorder.start()
                listeningButton.text = getString(R.string.listening_button_listening)
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
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)

        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC).absolutePath,
                "AudioRecording.3gp"
        )
        file.createNewFile()
        recorder.setOutputFile(file)
        recorder.prepare()
        return recorder
    }

}
