package com.samedtevin.bagcilarapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.samedtevin.bagcilarapp.model.ChatMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.chatDataStore by preferencesDataStore(
    name = "chat_prefs"
)

class ChatDataStore @Inject constructor(@ApplicationContext val context: Context) {

    companion object{
        private val CHAT_MESSAGES = stringPreferencesKey("chat_messages")
        private val PENDING_QUESTION = stringPreferencesKey("pending_question")
    }

    private val json = Json{
        ignoreUnknownKeys = true
    }

    val messages: Flow<List<ChatMessage>> = context.chatDataStore.data.map { preferences ->

        val data = preferences[CHAT_MESSAGES]

        if(data.isNullOrEmpty()){
            emptyList()
        }
        else{
            json.decodeFromString(data)
        }
    }

    val pendingQuestion: Flow<String?> =
        context.chatDataStore.data.map{ preferences ->
            preferences[PENDING_QUESTION]
        }

    suspend fun saveMessage(messages: List<ChatMessage>){
        context.chatDataStore.edit { preferences ->
            preferences[CHAT_MESSAGES] = json.encodeToString(messages)
        }
    }

    suspend fun savePendingQuestion(question: String) {
        context.chatDataStore.edit { preferences ->
            preferences[PENDING_QUESTION] = question
        }
    }

    suspend fun getPendingQuestion(): String? {
        return context.chatDataStore.data.first()[PENDING_QUESTION]
    }

    suspend fun clearPendingQuestion() {
        context.chatDataStore.edit { preferences ->
            preferences.remove(PENDING_QUESTION)
        }
    }


    suspend fun clearChat() {
        context.chatDataStore.edit {
            preferences -> preferences.remove(CHAT_MESSAGES)
            preferences.remove(PENDING_QUESTION)
        }
    }
}