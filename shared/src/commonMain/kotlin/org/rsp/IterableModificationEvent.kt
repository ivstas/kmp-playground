package org.rsp

import kotlinx.serialization.Serializable
import kotlin.js.JsExport


@Serializable
sealed interface IterableModificationEvent<out ID, out T>
@Serializable
data class IterableModificationEventReset<T>(val list: List<T>): IterableModificationEvent<Nothing, T>
/** Client should be able to find out where to put new item in the list */
@Serializable
data class IterableModificationEventAdded<T>(val item: T): IterableModificationEvent<Nothing, T>
@Serializable
data class IterableModificationEventRemoved<ID>(val id: ID): IterableModificationEvent<ID, Nothing>


@JsExport
class IterableModificationEventListener<in ID, in T>(
    private val onReset: (list: List<T>) -> Unit,
    private val onAdded: (item: T) -> Unit,
    private val onRemoved: (id: ID) -> Unit
) {
    @Suppress("NON_EXPORTABLE_TYPE")
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
