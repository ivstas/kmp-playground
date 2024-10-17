package org.kmp

import kotlinx.serialization.Serializable

// should be generated
@Serializable sealed interface IssueChangedEvent
@Serializable data class TitleChanged(val title: String): IssueChangedEvent
@Serializable data class IsCompletedChanged(val isCompleted: Boolean): IssueChangedEvent
@Serializable data class AssigneeIdChanged(val assigneeId: Int?): IssueChangedEvent
