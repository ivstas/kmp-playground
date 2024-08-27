import React, { StrictMode, useEffect, useState, ReactNode } from 'react'
import {createRoot} from 'react-dom/client';
import {App} from './App.js'
import { Client, Api } from 'kmp-playground-client';

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

interface ClientConnectionProps {
    children: (client: Api) => ReactNode
}

function ClientConnection({children}: ClientConnectionProps) {
    const [api, setApi] = useState<false | Api>(false);

    useEffect(() => {
        const client = new Client()

        client.api.then(setApi)

        return () =>{
            client.close()
        }
    }, [])

    return (
        <div>
            {(api === false)
                ? (<h1>connecting...</h1>)
                : children(api)
            }
        </div>
    )
}
