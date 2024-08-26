import React, {StrictMode} from 'react'
import {createRoot} from 'react-dom/client';
import {App} from './App.js'
import { printServerFlow } from 'kmp-playground-client';

const rootEl = document.getElementById('root');
if (rootEl == null) {
    throw new Error("Element #root not found")
}

printServerFlow()

createRoot(rootEl).render(
    <StrictMode>
        <App/>
    </StrictMode>
);
