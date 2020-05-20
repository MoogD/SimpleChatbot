package com.example.simplechatbot.utils

import com.example.simplechatbot.assistant.AssistantIntent
import com.google.cloud.language.v1beta2.AnalyzeSyntaxResponse
import com.google.cloud.language.v1beta2.DependencyEdge
import com.google.cloud.language.v1beta2.PartOfSpeech
import com.google.cloud.language.v1beta2.Token

object IntentMatcher {
    fun matchIntent(syntax: AnalyzeSyntaxResponse?): AssistantIntent {
        val intentList = mutableListOf<Pair<Token, Int>>()
        val entityList = mutableListOf<Token>()
        syntax?.tokensList?.forEach {
            when (it.partOfSpeech.tag) {
                PartOfSpeech.Tag.VERB -> intentList.add(Pair(it, syntax.tokensList.indexOf(it)))
                PartOfSpeech.Tag.NOUN -> {
                    if (it.partOfSpeech.proper == PartOfSpeech.Proper.PROPER) {
                        entityList.add(it)
                    }
                }
                else -> {}
            }
        }
        intentList.forEach {
            when (it.first.lemma) {
                AssistantIntent.CallIntent.kind -> {
                    entityList.forEach { token ->
                        if (token.dependencyEdge.label == DependencyEdge.Label.ATTR &&
                            token.dependencyEdge.headTokenIndex == it.second) {
                            return AssistantIntent.CallIntent(
                                token.lemma
                            )
                        }
                    }
                    return AssistantIntent.CallIntent()
                }
            }
        }
        return AssistantIntent.UnknownIntent()
    }
}