import { MessageApi, KtorRPCClient } from 'kmp-playground-client';
import { useCoroutineScope } from '../hooks.ts';
import { createSignal, For, Show } from 'solid-js';


export function Messages(props: { rpcClient: KtorRPCClient }) {
   const scope = useCoroutineScope()

   const [messages, setMessages] = createSignal<string[]>([])

   const api = new MessageApi(props.rpcClient)
   api.listenToMessageFlow(scope, (message) => {
      setMessages((messages) => [...messages, message])
   })

   const isEmpty = () => messages().length === 0

   return (
      <div>
         <h1>Received messages</h1>
         <Show when={!isEmpty()} fallback={<p>no messages yet</p>}>
            <For each={messages()}>
               {(message) => (
                  <li>{message}</li>
               )}
            </For>
         </Show>
      </div>
   )
}