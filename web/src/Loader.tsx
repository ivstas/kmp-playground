import React, { ReactNode } from "react";
import { Loading } from "./hooks.ts";

interface LoaderProps<T> {
    loadingState: Loading<T>
    children: (loaded: T) => ReactNode
}

export function Loader<T>({ loadingState, children }: LoaderProps<T>) {
    if (loadingState.isLoading) {
        return <h1>loading...</h1>
    }

    return children(loadingState.value)
}