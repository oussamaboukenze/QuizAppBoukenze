package com.example.quizapp_boukenze

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val container: LinearLayout = view.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.tvMessage.text = message.content
        
        val context = holder.itemView.context
        
        if (message.isUser) {
            holder.container.gravity = Gravity.END
            holder.tvMessage.background = ContextCompat.getDrawable(context, R.drawable.bg_bubble_user)
            holder.tvMessage.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        } else {
            holder.container.gravity = Gravity.START
            holder.tvMessage.background = ContextCompat.getDrawable(context, R.drawable.bg_bubble_bot)
            holder.tvMessage.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }
    }

    override fun getItemCount() = messages.size
}
