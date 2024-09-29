import { connectToServerPromise, KtorRPCClient } from 'kmp-playground-client';
import { useRequest } from './hooks.ts';
import {  withLoader } from './Loader.tsx';
import { ReactNode } from 'react';

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => ReactNode
}

export function WithKtorClient({ children }: WithKtorClientProps) {
   const clientLoadingResource = useRequest(scope => connectToServerPromise(scope))

   return withLoader(clientLoadingResource, children)
}