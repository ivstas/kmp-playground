import { connectToServerPromise, KtorRPCClient } from 'kmp-playground-client';
import { useCoroutineScope } from './hooks.ts';
import { ResourceState } from './Loader.tsx';
import type { Component, JSX } from 'solid-js';
import { createResource } from 'solid-js';

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => JSX.Element
}

export const WithKtorClient: Component<WithKtorClientProps> = (props) => {
   const scope = useCoroutineScope()

   const [clientLoadingResource] = createResource(() => connectToServerPromise(scope))

   return (
      <ResourceState resource={clientLoadingResource}>
         {(rpcClient) => props.children(rpcClient)}
      </ResourceState>
   )
}