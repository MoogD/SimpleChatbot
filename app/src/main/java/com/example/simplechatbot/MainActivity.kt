package com.example.simplechatbot

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simplechatbot.MainActivity.Companion.intent
import com.example.simplechatbot.annotationclasses.ApplicationContext
import com.example.simplechatbot.onboarding.OnboardingActivity
import com.example.simplechatbot.utils.AppStateManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import javax.inject.Inject

private const val REQUEST_MICROPHONE = 0
private const val REQUEST_WRITE_STORAGE = 1
class MainActivity : AppCompatActivity(), HasAndroidInjector {
//
//    lateinit var recorder: MediaRecorder

    @field :[Inject ApplicationContext]
    internal lateinit var context: Context

    @Inject
    lateinit var appManager: AppStateManager

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        // Fix bolean? to get rid of boolean equality check
        if (appManager.app.onboardingIsDone == null || !appManager.app.onboardingIsDone!!) {
            startActivity(OnboardingActivity.intent(context))
            finish()
        }

        setContentView(R.layout.activity_main)

//        var listening = false
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.RECORD_AUDIO),
//                REQUEST_MICROPHONE
//            )
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                REQUEST_WRITE_STORAGE
//            )
//        }
//
//        listeningButton.setOnClickListener {
//            if (!listening) {
//                recorder = prepareRecorder()
//                recorder.start()
//                listeningButton.text = getString(R.string.listening_button_listening)
//            }
//            else {
//                recorder.stop()
//                recorder.release()
//                listeningButton.text = getString(R.string.listening_button)
//            }
//            listening = !listening
//        }
    }

//    Move to BaseActivity
//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(newBase)
//    }


    fun prepareRecorder(): MediaRecorder {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)

        val file = File(
            "/data/data/com.example.simplechatbot/",
                "AudioRecording.3gp"
        )
        file.createNewFile()
        recorder.setOutputFile(file)
        recorder.prepare()
        return recorder
    }


    companion object {
        fun intent(context: Context) =
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

}
