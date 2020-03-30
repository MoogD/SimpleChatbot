package com.example.simplechatbot.main

data class ChatItem(
    val name: String,
    val orientation: ItemDirection,
    val content: String
)

enum class ItemDirection {
    LEFT, RIGHT
}