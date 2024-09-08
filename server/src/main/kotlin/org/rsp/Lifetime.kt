package org.rsp


interface Lifetime {
    val isTerminated: Boolean
    fun callWhenTerminated(listener: () -> Unit)
}

class LifetimeSource: Lifetime {
    override var isTerminated = false
    private val listeners = mutableListOf<() -> Unit>()

    override fun callWhenTerminated(listener: () -> Unit) {
        if (isTerminated) {
            listener()
        } else {
            listeners.add(listener)
        }
    }

    fun terminate() {
        if (isTerminated) {
            throw IllegalStateException("Lifetime already terminated")
        }

        isTerminated = true
        listeners.forEach { it() }
        listeners.clear()
    }
}

inline fun <T> withLifetime(block: (Lifetime) -> T): T {
    val lifetime = LifetimeSource()
    return try {
        block(lifetime)
    } finally {
        lifetime.terminate()
    }
}

