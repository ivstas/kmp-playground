import React from 'react';
import { shareServerPort } from 'kmp-playground-client';

export function App() {
    return (
        <div>
            <h1>Server is listening on port {shareServerPort()}</h1>
        </div>
    )
}