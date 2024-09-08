package org.rsp

import kotlin.js.JsExport


@JsExport
sealed interface IterableModificationEvent<out ID, out T>
data class IterableModificationEventReset<T>(val list: Array<T>): IterableModificationEvent<Nothing, T>
/** Client should be able to find out where to put new item in the list */
data class IterableModificationEventAdded<T>(val item: T): IterableModificationEvent<Nothing, T>
data class IterableModificationEventRemoved<ID>(val id: ID): IterableModificationEvent<ID, Nothing>


@JsExport
class IterableModificationEventListener<in ID, in T>(
    private val onReset: (list: Array<T>) -> Unit,
    private val onAdded: (item: T) -> Unit,
    private val onRemoved: (id: ID) -> Unit
) {
    fun collector(event: IterableModificationEvent<ID, T>) {
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
