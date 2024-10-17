package org.rsp

import kotlinx.serialization.Serializable
import org.kmp.Issue


@Serializable
sealed interface IterableModificationEvent<out ID, out T>
/** Client should be able to find out where to put new item in the list */
@Serializable
data class IterableModificationEventAdded<out T>(val item: Issue): IterableModificationEvent<Nothing, T>
@Serializable
data class IterableModificationEventRemoved<out ID>(val id: Int): IterableModificationEvent<ID, Nothing>
