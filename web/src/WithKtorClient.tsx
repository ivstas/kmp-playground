import { connectToServerPromise, KtorRPCClient } from "kmp-playground-client";
import { useCoroutineScope, useLoading } from "./hooks.ts";
import { Loader } from "./Loader.tsx";
import type { Component, JSX } from 'solid-js';

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => JSX.Element
}

export const WithKtorClient: Component<WithKtorClientProps> = (props) => {
    const scope = useCoroutineScope()

    const ktorClientLoading = useLoading(connectToServerPromise(scope))

    return (
        <Loader loadingState={ktorClientLoading()}>
            {(rpcClient) => props.children(rpcClient)}
        </Loader>
    )
}