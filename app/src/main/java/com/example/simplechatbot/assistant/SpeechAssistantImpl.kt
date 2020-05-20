package com.example.simplechatbot.assistant

import com.example.simplechatbot.utils.IntentMatcher
import com.example.simplechatbot.utils.PromptProvider
import com.google.api.gax.core.CredentialsProvider
import com.google.cloud.language.v1beta2.Document
import com.google.cloud.language.v1beta2.LanguageServiceClient
import com.google.cloud.language.v1beta2.LanguageServiceSettings
import com.google.cloud.speech.v1p1beta1.RecognitionAudio
import com.google.cloud.speech.v1p1beta1.RecognitionConfig
import com.google.cloud.speech.v1p1beta1.SpeechClient
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative
import com.google.cloud.speech.v1p1beta1.SpeechSettings
import com.google.cloud.texttospeech.v1beta1.AudioConfig
import com.google.cloud.texttospeech.v1beta1.AudioEncoding
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender
import com.google.cloud.texttospeech.v1beta1.SynthesisInput
import com.google.cloud.texttospeech.v1beta1.TextToSpeechClient
import com.google.cloud.texttospeech.v1beta1.TextToSpeechSettings
import com.google.cloud.texttospeech.v1beta1.VoiceSelectionParams
import com.google.protobuf.ByteString
import timber.log.Timber
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

class SpeechAssistantImpl @Inject constructor(
    private val intentMatcher: IntentMatcher,
    private val promptProvider: PromptProvider
) : SpeechAssistant {
    private var speechClient: SpeechClient? = null
    private var languageClient: LanguageServiceClient? = null
    private var ttsClient: TextToSpeechClient? = null

    override suspend fun prepareResponse(
        inputPath: String,
        outputPath: String
    ): AssistantIntent {
        val utterance = translateFile(inputPath)
        val intent = getSpeechResult(utterance)
        prepareResponsePrompt(utterance?.transcript, intent, outputPath)
        return intent
    }

    override suspend fun prepareSpeechAssistant(credentialProvider: CredentialsProvider) {
        speechClient = SpeechClient.create(
            SpeechSettings.newBuilder()
                .setCredentialsProvider(credentialProvider)
                .build()
        )
        val languageSettings = LanguageServiceSettings.newBuilder()
            .setCredentialsProvider(credentialProvider)
            .build()
        languageClient = LanguageServiceClient.create(languageSettings)
        val ttsSetting = TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(credentialProvider)
            .build()
        ttsClient = TextToSpeechClient.create(ttsSetting)
    }

    private fun translateFile(path: String): SpeechRecognitionAlternative? {
        val pathString = Paths.get(path)
        val data = Files.readAllBytes(pathString)
        val audioBytes = ByteString.copyFrom(data)
        val config = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
            .setSampleRateHertz(16000)
            .setLanguageCode("en-US")
            .build()
        val audio = RecognitionAudio.newBuilder()
            .setContent(audioBytes)
            .build()
        val response = speechClient?.recognize(config, audio)
        return response?.resultsList?.get(0)?.alternativesList?.maxBy { it.confidence }
    }

    private fun prepareResponsePrompt(
        utterance: String?,
        intent: AssistantIntent,
        outputPath: String
    ): ByteString? {
        if (utterance == null) return null
        val prompt = when (intent) {
            is AssistantIntent.CallIntent -> promptProvider.provideCallPrompt(intent, utterance)
            else -> promptProvider.provideUnknownPrompt()
        }
        Timber.d("Prompt prepared: $prompt")
        try {
            val input = SynthesisInput.newBuilder()
                .setText(prompt)
                .build()
            val voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build()
            val audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build()
            val response = ttsClient?.synthesizeSpeech(input, voice, audioConfig)
            val audioContents = response!!.audioContent

            FileOutputStream(outputPath).use { out ->
                out.write(audioContents.toByteArray())
                return audioContents
            }
        } catch (e: Exception) {
            Timber.w("Creating TTS client failed because of: $e")
        }
        return null
    }

    private fun getSpeechResult(result: SpeechRecognitionAlternative?): AssistantIntent {
        val doc = Document.newBuilder()
            .setContent(result?.transcript)
            .setType(Document.Type.PLAIN_TEXT)
            .build()
        val syntax = languageClient?.analyzeSyntax(doc)
        return intentMatcher.matchIntent(syntax)
    }
}
