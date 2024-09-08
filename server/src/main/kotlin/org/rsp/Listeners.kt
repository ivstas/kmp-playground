package org.rsp

typealias Listener<T> = (T) -> Unit
typealias LifetimedListener<T> = (T, Lifetime) -> Unit

class ListenerGroup<E> {
    private val listeners = mutableListOf<LifetimedListener<E>>()

    private var lt = LifetimeSource()

    val untilNextEventLifetime: Lifetime get() = lt

    fun addListener(lifetime: Lifetime, listener: LifetimedListener<E>) {
        listeners.add(listener)

        lifetime.callWhenTerminated {
            listeners.remove(listener)
        }
    }

    fun notifyAll(e: E) {
        lt.terminate()
        lt = LifetimeSource()
        listeners.forEach { listener ->
            listener(e, lt)
        }
    }
}
