import { DependencyList, useEffect, useState } from "react";
import { Disposable } from "kmp-playground-client";

export function useDisposable<T extends Disposable>(createDisposable: () => T): T {
    const [state] = useState<T>(createDisposable)

    useEffect(() => {
        return () => {
            state.dispose()
        }
    }, [])

    return state
}

export type Loading<T> = {
    isLoading: true
} | {
    isLoading: false
    value: T
}

export function useLoading<T>(load: () => Promise<T>, deps: DependencyList = []): Loading<T> {
    const [loadingState, setLoadingState] = useState<Loading<T>>({ isLoading: true })

    useEffect(() => {
        // todo: handler errors
        load().then(value => {
            setLoadingState({ isLoading: false, value })
        })
    }, deps); // todo: should I include load in deps?

    return loadingState
}