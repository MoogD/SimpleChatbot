package com.example.simplechatbot.utils

import com.example.simplechatbot.assistant.AssistantIntent
import com.google.cloud.language.v1beta2.AnalyzeSyntaxResponse
import com.google.cloud.language.v1beta2.DependencyEdge
import com.google.cloud.language.v1beta2.PartOfSpeech
import com.google.cloud.language.v1beta2.Token
import org.junit.Test

class IntentMatcherTest {

    @Test
    fun `test matchIntent for call utterance with entity`() {
        val intent = IntentMatcher.matchIntent(provideCallWithEntityResponse())
        assert(intent is AssistantIntent.CallIntent)
        assert(intent.entity == CALL_ENTITY_TEXT)
    }

    @Test
    fun `test matchIntent for call utterance without entity`() {
        val intent = IntentMatcher.matchIntent(provideCallWithoutEntityResponse())
        assert(intent is AssistantIntent.CallIntent)
        assert(intent.entity == null)
    }

    @Test
    fun `test matchIntent for unknown utterance`() {
        val intent = IntentMatcher.matchIntent(provideUnknwonIntentResponse())
        assert(intent is AssistantIntent.UnknownIntent)
        assert(intent.entity == null)
    }

    @Test
    fun `test matchIntent for empty response`() {
        val intent = IntentMatcher.matchIntent(provideEmptyResponse())
        assert(intent is AssistantIntent.UnknownIntent)
        assert(intent.entity == null)
    }

    private fun provideCallWithEntityResponse(): AnalyzeSyntaxResponse {
        val tokens = listOf<Token>(
            Token.newBuilder()
                .setLemma(CALL_INTENT_TEXT)
                .setPartOfSpeech(
                    PartOfSpeech.newBuilder()
                        .setTag(PartOfSpeech.Tag.VERB)
                ).build(),
            Token.newBuilder()
                .setLemma(CALL_ENTITY_TEXT)
                .setPartOfSpeech(
                    PartOfSpeech.newBuilder()
                        .setTag(PartOfSpeech.Tag.NOUN)
                        .setProper(PartOfSpeech.Proper.PROPER)
                ).setDependencyEdge(
                    DependencyEdge.newBuilder()
                        .setLabel(DependencyEdge.Label.ATTR)
                        .setHeadTokenIndex(0)
                )
                .build()
        )
        return AnalyzeSyntaxResponse.newBuilder()
            .addAllTokens(tokens)
            .build()
    }

    private fun provideCallWithoutEntityResponse(): AnalyzeSyntaxResponse {
        val tokens = listOf<Token>(
            Token.newBuilder()
                .setLemma(CALL_INTENT_TEXT)
                .setPartOfSpeech(
                    PartOfSpeech.newBuilder()
                        .setTag(PartOfSpeech.Tag.VERB)
                ).build(),
            Token.newBuilder()
                .setLemma(CALL_ENTITY_TEXT)
                .setPartOfSpeech(
                    PartOfSpeech.newBuilder()
                        .setTag(PartOfSpeech.Tag.VERB)
                ).build()
        )
        return AnalyzeSyntaxResponse.newBuilder()
            .addAllTokens(tokens)
            .build()
    }

    private fun provideUnknwonIntentResponse(): AnalyzeSyntaxResponse {
        val tokens = listOf<Token>(
            Token.newBuilder()
                .setLemma(UNKNOWN_INTENT_TEXT)
                .setPartOfSpeech(
                    PartOfSpeech.newBuilder()
                        .setTag(PartOfSpeech.Tag.VERB)
                ).build()
        )
        return AnalyzeSyntaxResponse.newBuilder()
            .addAllTokens(tokens)
            .build()
    }

    private fun provideEmptyResponse(): AnalyzeSyntaxResponse {
        val tokens = listOf<Token>()
        return AnalyzeSyntaxResponse.newBuilder()
            .addAllTokens(tokens)
            .build()
    }

    companion object {
        private const val CALL_ENTITY_TEXT = "name"
        private const val CALL_INTENT_TEXT = "call"
        private const val UNKNOWN_INTENT_TEXT = "write"
    }
}