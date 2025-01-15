package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retry

class ChatRepository(
    private val api: ChatApi = ChatApi(),
) {

    fun getReplyMessage(): Flow<String> {
        return api.getReply().retry { e ->
            var currentDelay = 1000L

            if (e is Exception) {
                delay(currentDelay)
                currentDelay *= DELAY_FACTOR
                true
            } else {
                false
            }
        }
    }
    private companion object {
        const val DELAY_FACTOR = 2
    }
}