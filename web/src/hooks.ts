import { Disposable } from "kmp-playground-client";
import { createSignal, onCleanup } from "solid-js";
import type { Accessor } from "solid-js";

export function useDisposable<T extends Disposable>(createDisposable: () => T): T {
    const disposable = createDisposable()

    onCleanup(() => {
        disposable.dispose()
    })

    return disposable
}

export type Loading<T> = {
    isLoading: true
} | {
    isLoading: false
    value: T
}

export function useLoading<T>(load: () => Promise<T>): Accessor<Loading<T>> {
    const [loadingState, setLoadingState] = createSignal<Loading<T>>({ isLoading: true })

    // todo: handler errors
    load().then(value => {
        setLoadingState({ isLoading: false, value })
    })

    return loadingState
}