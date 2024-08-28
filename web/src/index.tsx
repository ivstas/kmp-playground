import React from 'react'
import {createRoot} from 'react-dom/client';
import {Messages} from './Messages.tsx'
import { WithKtorClient } from "./WithKtorClient.tsx";

const rootEl = document.getElementById('root');
if (rootEl == null) {
    throw new Error("Element #root not found")
}

createRoot(rootEl).render(
    <WithKtorClient>
        {client => (
            <Messages rpcClient={client}/>
        )}
    </WithKtorClient>
);
