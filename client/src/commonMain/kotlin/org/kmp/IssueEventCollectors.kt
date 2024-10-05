package org.kmp

@JsExport
external interface IssueChangedEventCollector {
    fun onTitleChanged(title: String)
    fun onIsCompletedChanged(isCompleted: Boolean)
    fun onAssigneeIdChanged(assigneeId: Int)
}

