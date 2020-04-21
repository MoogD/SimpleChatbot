package com.example.simplechatbot.assistant

import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import java.nio.file.Files
import java.nio.file.Paths

class SpeechAssistantImpl(private val credentialProvider: CredentialProvider) : SpeechAssistant {
    private val speechClient = SpeechClient.create(
        SpeechSettings.newBuilder()
            .setCredentialsProvider {credentialProvider.provideCredentials()}
            .build()
    )

    override fun translateFile(pathString: String): List<SpeechRecognitionResult> {
        val path = Paths.get(pathString)
        val data = Files.readAllBytes(path)
        val audioBytes = ByteString.copyFrom(data)
        val config = RecognitionConfig.newBuilder()
            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
            .setSampleRateHertz(16000)
            .setLanguageCode("en-US")
            .build()
        val audio = RecognitionAudio.newBuilder()
            .setContent(audioBytes)
            .build()
        val response = speechClient.recognize(config, audio)
        return response.resultsList
    }

}