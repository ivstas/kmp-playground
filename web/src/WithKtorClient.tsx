import { KtorRPCClient, ScopeProxy, connectToServerPromise } from "kmp-playground-client";
import { useDisposable, useLoading } from "./hooks.ts";
import { Loader } from "./Loader.tsx";
import type { Component, JSX } from 'solid-js';

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => JSX.Element
}

export const WithKtorClient: Component<WithKtorClientProps> = (props) => {
    const scope = useDisposable(() => new ScopeProxy()).scope

    const ktorClientLoading = useLoading(() => connectToServerPromise(scope),)

    return (
        <Loader loadingState={ktorClientLoading()}>
            {(rpcClient) => props.children(rpcClient)}
        </Loader>
    )
}