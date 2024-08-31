package org.kmp.domain

import kotlinx.serialization.Serializable

@Serializable
data class IssueIn(
    val title: String,
    val assigneeId: Long? = null,
)

@Serializable
data class TagIn(
    val title: String,
)