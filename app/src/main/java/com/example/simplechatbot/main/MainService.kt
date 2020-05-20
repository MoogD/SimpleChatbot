package com.example.simplechatbot.main

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import edu.cmu.pocketsphinx.Assets
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class MainService : Service(), RecognitionListener, WuWListener, HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private val binder = MainBinder()
    private var listener: WuWListener.ResultListener? = null

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.IO + job)

    private var recognizer: SpeechRecognizer? = null

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (recognizer == null) {
            uiScope.launch {
                prepare()
            }
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        job.cancel()
        listener = null
        return super.onUnbind(intent)
    }

    override fun setWuWListener(listener: WuWListener.ResultListener) {
        this.listener = listener
    }

    private fun prepare() {
        val assetDir = Assets(this@MainService)
            .syncAssets()
        recognizer = SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(File(assetDir, "en-us-ptm"))
            .setDictionary(File(assetDir, "cmudict-en-us.dict"))
            .recognizer
        recognizer?.addListener(this@MainService)
        recognizer?.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE)
        recognizer?.addKeyphraseSearch(END_SEARCH, ENDPHRASE)
        recognizer?.startListening(KWS_SEARCH)
    }

    override fun armWuW() {
        recognizer?.startListening(KWS_SEARCH)
    }

    override fun awaitEndOfSpeech() {
        recognizer?.startListening(END_SEARCH)
    }

    override fun onResult(p0: Hypothesis?) {}

    override fun onPartialResult(p0: Hypothesis?) {
        if (recognizer?.searchName == KWS_SEARCH && p0?.hypstr == KEYPHRASE) {
            Timber.i("WakeWord detected")
            recognizer?.stop()
            listener?.onWuW()
        }
    }

    override fun onTimeout() {}

    override fun onBeginningOfSpeech() {}

    override fun onEndOfSpeech() {
        if (recognizer?.searchName == END_SEARCH) {
            Timber.i("end of speechrecognition ")
            recognizer?.stop()
            listener?.onSpeechEnd()
        } else {
            recognizer?.stop()
            recognizer?.startListening(KWS_SEARCH)
        }
    }

    override fun onError(p0: Exception?) {}

    companion object {
        private const val KWS_SEARCH = "wakeup"
        private const val END_SEARCH = "end"
        private const val KEYPHRASE = "hello assistant"

        // Some phrase that should not be recognized to end listening state
        private const val ENDPHRASE = "aösjdfasdfmasdfasdf"
    }

    inner class MainBinder : Binder() {
        fun getService(): MainService = this@MainService
    }
}
