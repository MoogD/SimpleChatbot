package com.example.simplechatbot.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplechatbot.R


class ChatListAdapter: RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    var chatItemList: MutableList<ChatItem> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return chatItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = chatItemList[position].name
        holder.contentText.text = chatItemList[position].content

        var constraintSet = ConstraintSet()
        val chatItemLayout = holder.itemView.findViewById<ConstraintLayout>(R.id.chatItem)
        constraintSet.clone(chatItemLayout)

        if (chatItemList[position].orientation == ItemDirection.RIGHT) {
            constraintSet.connect(
                R.id.nameText,
                ConstraintSet.END,
                R.id.chatItem,
                ConstraintSet.END,
                6
            )
            constraintSet.connect(
                R.id.contentText,
                ConstraintSet.END,
                R.id.chatItem,
                ConstraintSet.END,
                6
            )
        } else {
            constraintSet.connect(
                R.id.nameText,
                ConstraintSet.START,
                R.id.chatItem,
                ConstraintSet.START,
                6
            )
            constraintSet.connect(
                R.id.contentText,
                ConstraintSet.START,
                R.id.chatItem,
                ConstraintSet.START,
                6
            )
        }
        constraintSet.applyTo(chatItemLayout)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameText)
        val contentText: TextView = itemView.findViewById(R.id.contentText)
    }
}
