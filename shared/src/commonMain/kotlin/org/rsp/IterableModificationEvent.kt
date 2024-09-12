package org.rsp

import kotlinx.serialization.Serializable
import org.kmp.Issue
import kotlin.js.JsExport


@Serializable
sealed interface IterableModificationEvent<out ID, out T>
@Serializable
data class IterableModificationEventReset<out T>(val list: List<Issue>): IterableModificationEvent<Nothing, T>
/** Client should be able to find out where to put new item in the list */
@Serializable
data class IterableModificationEventAdded<out T>(val item: Issue): IterableModificationEvent<Nothing, T>
@Serializable
data class IterableModificationEventRemoved<out ID>(val id: Int): IterableModificationEvent<ID, Nothing>


@JsExport
class IterableModificationEventListener(
    private val onReset: (list: List<Issue>) -> Unit,
    private val onAdded: (item: Issue) -> Unit,
    private val onRemoved: (id: Int) -> Unit
) {
    @Suppress("NON_EXPORTABLE_TYPE")
    fun collector(event: IterableModificationEvent<Int, Issue>) {
        when (event) {
            is IterableModificationEventAdded -> {
                onAdded(event.item)
            }

            is IterableModificationEventRemoved -> {
                onRemoved(event.id)
            }

            is IterableModificationEventReset -> {
                onReset(event.list)
            }
        }
    }
}
