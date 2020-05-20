package com.example.simplechatbot.utils

import com.example.simplechatbot.assistant.AssistantIntent
import junit.framework.Assert.assertEquals
import org.junit.Test

class PromptProviderTest {
    private val callIntentWithEntity = AssistantIntent.CallIntent("Test")
    private val callIntent = AssistantIntent.CallIntent()

    @Test
    fun `test provideCallPrompt for call intent with entity`() {
        val prompt = PromptProvider.provideCallPrompt(callIntentWithEntity, "Test utterance")
        assertEquals(
            prompt,
            "I understood as Test utterance. The Intent is ${AssistantIntent.CallIntent.kind}" +
                    " with entity ${callIntentWithEntity.entity}"
        )
    }

    @Test
    fun `test provideCallPrompt for call intent without entity`() {
        val prompt = PromptProvider.provideCallPrompt(callIntent, "Test utterance")
        assertEquals(
            prompt,
            "I understood as Test utterance. The Intent is" +
                    " ${AssistantIntent.CallIntent.kind}. I could not understand the entity."
        )
    }

    @Test
    fun `test provideCallPrompt for unknown intent`() {
        val prompt = PromptProvider.provideUnknownPrompt()
        assertEquals(
            prompt,
            "I could not get an intent from this utterance."
        )
    }
}