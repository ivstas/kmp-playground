import React, { useEffect, useState } from 'react';
import { Api, KtorRPCClient } from 'kmp-playground-client';

export function App({ rpcClient }: { rpcClient: KtorRPCClient }) {
    const [messages, setMessages] = useState([] as string[])

    // todo: unsubscribe
    useEffect(() => {
        const api = new Api(rpcClient)
        api.listenToMessageFlow((message) => {
            setMessages((messages) => [...messages, message])
        })
    }, [])

    return (
        <div>
            <h1>Received messages</h1>
            {messages.length === 0
                ? <p>no messages</p>
                : (
                    <ul>
                        {messages.map((message, i) => (
                            <li key={i}>{message}</li>
                        ))}
                    </ul>
                )
            }
        </div>
    )
}