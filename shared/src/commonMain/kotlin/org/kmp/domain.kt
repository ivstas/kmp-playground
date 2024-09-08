package org.kmp

import kotlinx.serialization.Serializable
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
    val id: Long,
    val title: String,
    val assigneeId: Long?,
    val isCompleted: Boolean,
)

@Serializable
data class TagIn(
    val title: String,
)