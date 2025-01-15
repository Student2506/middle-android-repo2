package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val api: ChatApi = ChatApi(),
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
    }
}