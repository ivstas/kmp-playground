import { ResourceState } from '../Loader.tsx';
import { createResource, Show } from 'solid-js';
import { useCoroutineScope } from '../hooks.ts';
import { IssueApi } from 'kmp-playground-client';
import type { KtorRPCClient } from 'kmp-playground-client';
import type { JSX } from 'solid-js'

export function IssuesSingle(props: { rpcClient: KtorRPCClient, issueId: number }): JSX.Element {
   const scope = useCoroutineScope()

   const api = new IssueApi(props.rpcClient)
   const [issueResource] = createResource(() => api.getIssue(props.issueId, scope))

   return (
      <div>
         <h1>Issue</h1>
         <ResourceState resource={issueResource}>
            {(issue) => (
               <Show when={issue} fallback={<div>Issue {props.issueId} not found</div>}>
                  {(issue) => (
                     <h2>{issue().title}</h2>
                  )}
               </Show>
            )}
         </ResourceState>
      </div>
   )
}