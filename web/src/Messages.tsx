import React, { useEffect, useMemo, useState } from 'react';
import { ScopeProxy, MessageApi, KtorRPCClient } from 'kmp-playground-client';
import { useDisposable } from "./hooks.ts";

export function Messages({ rpcClient }: { rpcClient: KtorRPCClient }) {
    const scope = useDisposable(() => new ScopeProxy()).scope
    const messageApi = useMemo(() => new MessageApi(rpcClient), [rpcClient])
    const [messages, setMessages] = useState([] as string[])

    useEffect(() => {
        messageApi.listenToMessageFlow(scope, (message) => {
            setMessages((messages) => [...messages, message])
        })
    }, [messageApi, scope])

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