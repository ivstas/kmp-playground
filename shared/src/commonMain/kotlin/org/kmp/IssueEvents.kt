package org.kmp

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.rsp.*
import kotlin.js.JsExport

// should be generated

@Serializable sealed interface IssueChangedEvent
@Serializable data class TitleChanged(val title: String): IssueChangedEvent
@Serializable data class IsCompletedChanged(val isCompleted: Boolean): IssueChangedEvent
@Serializable data class AssigneeIdChanged(val assigneeId: Long): IssueChangedEvent

@JsExport class IssueChangedEventListener(
    private val onTitleChanged: (title: String) -> Unit,
    private val onIsCompletedChanged: (isCompleted: Boolean) -> Unit,
    private val onAssigneeIdChanged: (assigneeId: Long) -> Unit,
) {
    @Suppress("NON_EXPORTABLE_TYPE")
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

@Serializable sealed interface IssuesModificationEvent {
    @Serializable data class ListModification(val listEvent: IterableModificationEvent<Int, Issue>): IssuesModificationEvent
    @Serializable data class ElementModification(val id: Int, val issueChangedEvent: IssueChangedEvent): IssuesModificationEvent
}

@JsExport class IssuesModificationEventListener (
    private val onListChanged: IterableModificationEventListener<Int, Issue>,
    private val getOnElementChangedListener: (Int) -> IssueChangedEventListener,
) {
    @Suppress("NON_EXPORTABLE_TYPE")
    fun collector(event: IssuesModificationEvent) {
        when (event) {
            is IssuesModificationEvent.ListModification -> {
                onListChanged.collector(event.listEvent)
            }
            is IssuesModificationEvent.ElementModification -> {
                getOnElementChangedListener(event.id).collector(event.issueChangedEvent)
            }
        }

    }
}
