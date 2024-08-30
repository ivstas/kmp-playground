import { ScopeProxy, MessageApi, KtorRPCClient } from 'kmp-playground-client';
import { useDisposable } from "./hooks.ts";
import { Component, createSignal, For, Show } from "solid-js";

interface MessagesProps {
    rpcClient: KtorRPCClient
}

export const Messages: Component<MessagesProps> = (props) => {
    const scope = useDisposable(() => new ScopeProxy()).scope

    const [messages, setMessages] = createSignal<string[]>([])

    const api = new MessageApi(props.rpcClient)
    api.listenToMessageFlow(scope, (message) => {
        setMessages((messages) => [...messages, message])
    })

    const isEmpty = () => messages().length === 0

    return (
        <div>
            <h1>Received messages</h1>
            <Show when={!isEmpty()} fallback={<p>no messages</p>}>
                <For each={messages()}>
                    {(message) => (
                        <li>{message}</li>
                    )}
                </For>
            </Show>
        </div>
    )
}