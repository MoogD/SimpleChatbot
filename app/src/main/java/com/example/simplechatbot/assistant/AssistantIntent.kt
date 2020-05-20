package com.example.simplechatbot.assistant

abstract class AssistantIntent {
    abstract val entity: String?

    class CallIntent(override val entity: String? = null) : AssistantIntent() {
        companion object {
            const val kind = "call"
        }
    }

    class UnknownIntent : AssistantIntent() {
        override val entity: String? = null
        companion object {
            const val kind = "unknown"
        }
    }
}
