package com.example.simplechatbot.main

import android.content.Context
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

interface MainPresenter {

    val context: Context
    var view: MainView?
    var isListening: Boolean
    val conversation: MutableList<ChatItem>
    val chatAdapter: ListAdapter<ChatItem, RecyclerView.ViewHolder>


    fun bindView(mainview: MainView)
    fun unbindView()

    fun listeningPressed()
}