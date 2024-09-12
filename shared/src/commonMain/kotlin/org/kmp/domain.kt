package org.kmp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.rsp.IterableModificationEvent
import kotlin.js.JsExport

@JsExport
@Serializable
data class IssueIn(
    val title: String,
    val assigneeId: Long? = null,
)

@JsExport
@Serializable
data class Issue(
    val id: Int,
    val title: String,
    val assigneeId: Int?,
    val isCompleted: Boolean,
)

@Serializable
data class TagIn(
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