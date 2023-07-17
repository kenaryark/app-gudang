package com.pnj.gudang.chat

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.pnj.gudang.R
import java.io.File

class ChatAdapter(private val chatList: ArrayList<Chat>):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var email: TextView = itemView.findViewById(R.id.TVLChatEmail)
        var time: TextView = itemView.findViewById(R.id.TVLChatTime)
        var message: TextView = itemView.findViewById(R.id.TVLChatMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_layout, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat : Chat = chatList[position]
        holder.email.text = chat.email
        holder.time.text = chat.time
        holder.message.text = chat.message
    }
}