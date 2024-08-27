import React, { StrictMode } from 'react'
import {createRoot} from 'react-dom/client';
import {App} from './App.js'
import { ClientConnection } from "./ClientConnection.tsx";

const rootEl = document.getElementById('root');
if (rootEl == null) {
    throw new Error("Element #root not found")
}

createRoot(rootEl).render(
    <StrictMode>
        <ClientConnection>
            {api => (
                <App api={api}/>
            )}
        </ClientConnection>
    </StrictMode>
);
