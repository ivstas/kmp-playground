import { IssueApi, KtorRPCClient } from 'kmp-playground-client';
import { useCoroutineScope } from '../hooks.ts';
import { createResource, For } from 'solid-js';
import type { JSX } from 'solid-js';
import { ResourceState } from '../Loader.tsx';


export function Issues(props: { rpcClient: KtorRPCClient }): JSX.Element {
   const scope = useCoroutineScope()

   const api = new IssueApi(props.rpcClient)
   const [issuesResource] = createResource(() => api.getIssues(scope))

   return (
      <div>
         <h1>Issues</h1>
         <ResourceState resource={issuesResource}>
            {(issueKtList) => (
               <For each={issueKtList}>
                  {(issue) => (
                     <li>{issue.title}</li>
                  )}
               </For>
            )}
         </ResourceState>
      </div>
   )
}