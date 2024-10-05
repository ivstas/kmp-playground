package org.kmp

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserEvent
@Serializable data class NameChanged(val name: String): UserEvent