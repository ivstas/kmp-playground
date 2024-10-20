package org.rsp

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.SealedClassSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = IterableModificationEventSerializer::class)
sealed interface IterableModificationEvent<out ID, out T>
/** Client should be able to find out where to put new item in the list */
@Serializable
data class IterableModificationEventAdded<out ID, out T>(val id: ID, val item: T): IterableModificationEvent<ID, T>
@Serializable
data class IterableModificationEventRemoved<out ID>(val id: ID): IterableModificationEvent<ID, Nothing>

// https://github.com/Kotlin/kotlinx.serialization/issues/1252#issuecomment-1780935921
@OptIn(InternalSerializationApi::class)
class IterableModificationEventSerializer<ID, T>(idSerializer: KSerializer<ID>, tSerializer: KSerializer<T>): KSerializer<IterableModificationEvent<ID, T>> {
    private val serializer = SealedClassSerializer(
        IterableModificationEvent::class.simpleName!!,
        IterableModificationEvent::class,
        arrayOf(IterableModificationEventAdded::class, IterableModificationEventRemoved::class),
        arrayOf(IterableModificationEventAdded.serializer(idSerializer, tSerializer), IterableModificationEventRemoved.serializer(idSerializer))
    )

    override val descriptor: SerialDescriptor = serializer.descriptor
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): IterableModificationEvent<ID, T> { return serializer.deserialize(decoder) as IterableModificationEvent<ID, T> }
    override fun serialize(encoder: Encoder, value: IterableModificationEvent<ID, T>) { serializer.serialize(encoder, value) }
}