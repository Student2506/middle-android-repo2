package ru.yandex.praktikumchatapp.presentation

sealed interface State {
    data class StateMessage(val messages: List<Message>) : State
}