package org.kmp

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.rsp.IterableModificationEvent
import kotlin.js.JsExport

@JsExport
@Serializable
data class User(
    val id: Int,
    val name: String,
)

@JsExport
@Serializable
data class IssueIn(
    val title: String,
    val assigneeId: Int? = null,
    val isCompleted: Boolean = false,
)

@JsExport
@Serializable
data class Issue(
    val id: Int,
    val title: String,
    val assigneeId: Int? = null,
    val isCompleted: Boolean = false,
)

@JsExport
@Serializable
data class TagIn(
    val title: String,
)

@JsExport
@Serializable
data class Tag(
    val id: Int,
    val title: String,
)

@Serializable
data class Wrapper<T>(
    val value: T,
)

@Serializable
data class IssueListUpdates(
    @Contextual val listChangedFlow: Flow<IterableModificationEvent<Int, Issue>>,
    @Contextual val elementChangedFlow: Flow<Pair<Int, IssueChangedEvent>>,
)

@Serializable
data class UserUpdates(
    @Contextual val userChangedFlow: Flow<UserEvent>,
)

@Serializable
data class InitializedFlow<T, E>(
    val initialValue: T,
    @Contextual val flow: Flow<E>,
)