import { KtorRPCClient, ScopeProxy, connectToServerPromise } from "kmp-playground-client";
import React, { ReactNode } from "react";
import { useDisposable, useLoading } from "./hooks.ts";
import { Loader } from "./Loader.tsx";

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => ReactNode
}

export function WithKtorClient({children}: WithKtorClientProps) {
    const scope = useDisposable(() => new ScopeProxy()).scope

    const ktorClientLoading = useLoading(() => connectToServerPromise(scope), [scope])

    return (
        <Loader loadingState={ktorClientLoading}>
            {(rpcClient) => children(rpcClient)}
        </Loader>
    )
}