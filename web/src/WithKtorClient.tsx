import { connectToServerPromise, KtorRPCClient } from 'kmp-playground-client';
import { useRequest } from './hooks.ts';
import {  withLoader } from './Loader.tsx';

interface WithKtorClientProps {
    children: (rpcClient: KtorRPCClient) => JSX.Element
}

export function WithKtorClient({ children }: WithKtorClientProps) {
   const clientLoadingResource = useRequest(scope => connectToServerPromise(scope))

   return withLoader(clientLoadingResource, children)
}