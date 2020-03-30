package com.example.simplechatbot.main

interface MainView {

    fun startOnboarding()

    fun updateChat(chatItems: List<ChatItem>)
    fun stopListening()
}
