package com.samedtevin.bagcilarapp.adapter.uiadapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samedtevin.bagcilarapp.databinding.ItemAiMessageBinding
import com.samedtevin.bagcilarapp.databinding.ItemUserMessageBinding
import com.samedtevin.bagcilarapp.model.ChatMessage
import io.noties.markwon.Markwon

class ChatAdapter(private val messages: MutableList<ChatMessage>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val USER_MESSAGE = 1
        private const val AI_MESSAGE = 2
        private const val LOADING_MESSAGE =3
    }

    private var isLoading = false

    override fun getItemViewType(position: Int): Int {

        if(isLoading && position == messages.size){
            return LOADING_MESSAGE
        }
        return if (messages[position].isUser){
            USER_MESSAGE
        }
        else{
            AI_MESSAGE
        }
    }

    class LoadingViewHolder(private val binding: ItemAiMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.tvAiMessage.text = "AI is typing..."
        }
    }

    class UserMessageViewHolder(private val binding: ItemUserMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(message: ChatMessage){
            binding.tvUserMessage.text = message.message
        }
    }

    class AiMessageViewHolder(private val binding: ItemAiMessageBinding): RecyclerView.ViewHolder(binding.root){

        private val markwon = Markwon.create(binding.root.context)
        fun bind(message: ChatMessage){
            markwon.setMarkdown(
                binding.tvAiMessage,
                message.message
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when(viewType){
            USER_MESSAGE -> { val binding = ItemUserMessageBinding.inflate(inflater,parent,false)
                UserMessageViewHolder(binding)
            }

            AI_MESSAGE -> { val binding = ItemAiMessageBinding.inflate(inflater,parent,false)
                AiMessageViewHolder(binding)
            }
            else -> { val binding = ItemAiMessageBinding.inflate(inflater,parent,false)
                LoadingViewHolder(binding)
            }

        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        if(holder is LoadingViewHolder){
            holder.bind()
            return
        }

        val message = messages[position]

        when(holder){
            is UserMessageViewHolder -> holder.bind(message)

            is AiMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size + if(isLoading) 1 else 0
    }

    fun addMessage(message: ChatMessage){
        messages.add(message)
        notifyItemInserted(messages.lastIndex)
    }

    fun showLoading(){
        if(!isLoading){
            isLoading = true
            notifyItemInserted(messages.size)
        }
    }

    fun hideLoading(){
        if(isLoading){
            isLoading = false
            notifyItemRemoved(messages.size)
        }
    }

    fun submitMessages(newMessages: List<ChatMessage>){
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }


}