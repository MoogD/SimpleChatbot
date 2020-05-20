package com.example.simplechatbot.utils

import com.example.simplechatbot.assistant.AssistantIntent

object PromptProvider {
    fun provideCallPrompt(intent: AssistantIntent, utterance: String): String {
        val prompt = "I understood as $utterance. "
        val promptExtension = if (intent.entity != null) {
            "The Intent is ${AssistantIntent.CallIntent.kind} with entity ${intent.entity}"
        } else {
            "The Intent is ${AssistantIntent.CallIntent.kind}. I could not understand the entity."
        }
        return prompt + promptExtension
    }

    fun provideUnknownPrompt(): String =
        "I could not get an intent from this utterance."
}