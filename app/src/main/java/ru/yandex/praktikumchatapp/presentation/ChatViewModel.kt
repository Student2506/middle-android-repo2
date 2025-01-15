package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository
import java.util.concurrent.CopyOnWriteArrayList

class ChatViewModel(
    val isWithReplies: Boolean = true,
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow(State.StateMessage(CopyOnWriteArrayList()))
    val messages = _messages.asStateFlow()

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collect { response ->
                    _messages.update {
                        _messages.value.copy(
                            messages = it.messages + Message.OtherMessage(response)
                        )
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _messages.update {
            _messages.value.copy(
                messages = it.messages + Message.MyMessage(messageText)
            )
        }
    }
}