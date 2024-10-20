package org.kmp

import kotlinx.serialization.Serializable

// should be generated
@Serializable sealed interface IssueChangedEvent {
    @Serializable data class Title(val title: String): IssueChangedEvent
    @Serializable data class IsCompleted(val isCompleted: Boolean): IssueChangedEvent
    @Serializable data class AssigneeId(val assigneeId: Int?): IssueChangedEvent
}
