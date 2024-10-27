package org.kmp

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserChangedEvent {
    @Serializable
    data class NameChanged(val name: String) : UserChangedEvent
}