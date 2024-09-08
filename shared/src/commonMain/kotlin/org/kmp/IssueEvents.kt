package org.kmp

import org.rsp.*
import kotlin.js.JsExport

// should be generated
sealed interface IssueChangedEvent
data class TitleChanged(val title: String): IssueChangedEvent
data class IsCompletedChanged(val isCompleted: Boolean): IssueChangedEvent
data class AssigneeIdChanged(val assigneeId: Long): IssueChangedEvent

@JsExport class IssueChangedEventListener(
    private val onTitleChanged: (title: String) -> Unit,
    private val onIsCompletedChanged: (isCompleted: Boolean) -> Unit,
    private val onAssigneeIdChanged: (assigneeId: Long) -> Unit,
) {
    fun collector(event: IssueChangedEvent) {
        when (event) {
            is TitleChanged -> {
                onTitleChanged(event.title)
            }
            is IsCompletedChanged -> {
                onIsCompletedChanged(event.isCompleted)
            }
            is AssigneeIdChanged -> {
                onAssigneeIdChanged(event.assigneeId)
            }
        }
    }
}


typealias IssueListModificationEvent = IterableModificationEvent<Long, Issue>
