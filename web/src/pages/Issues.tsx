import { Issue as ApiIssue, IssuesModificationEventListener } from 'kmp-playground-client';
import {
   CoroutineScope,
   IssueApi,
   IssueChangedEventListener,
   IterableModificationEventListener,
   KtorRPCClient,
} from 'kmp-playground-client';
import { useCoroutineScope } from '../hooks.ts';
import type { Accessor, JSX } from 'solid-js';
import { createSignal, For } from 'solid-js';


export function Issues(props: { rpcClient: KtorRPCClient }): JSX.Element {
   const scope = useCoroutineScope()

   const api = new IssueApi(props.rpcClient)
   // const [issuesResource] = createResource(() => api.getIssues(scope))
   const issues = useIssues(api, scope)

   return (
      <div>
         <h1>Issues</h1>
         {/*<ResourceState resource={issuesResource}>*/}
         {/*   {(issueKtList) => (*/}
         <For each={issues()}>
            {(issue) => (
               <li>{issue.title}</li>
            )}
         </For>
         {/*)}*/}
         {/*</ResourceState>*/}
      </div>
   )
}

interface Issue {
   id: number
   title: string
   assigneeId: number
   isCompleted: boolean
}

function useIssues(api: IssueApi, scope: CoroutineScope): Accessor<Issue[]> {
   const [signal, setSignal] = createSignal<Issue[]>([])

   const listListener = new IterableModificationEventListener(
      (list) => { // onReset
         setSignal(() => list.toArray())
      },
      (item) => { // onAdded
         setSignal(prev => {
            return [...prev] // todo: add item
         })
      },
      (id) => { // onRemoved
         setSignal(prev => {
            return prev.filter((issue) => issue.id !== id)
         })
      },
   )

   const getElementUpdateListener = (issueId: number) => {
      function changeIssue(override: Partial<Issue>) {
         return (prev: Issue[]) => prev.map((issue) => {
            return issue.id === issueId
               ? { ...issue, ...override }
               : issue;
         })
      }

      return new IssueChangedEventListener(
         (title) => { // onTitleChanged
            setSignal(changeIssue({ title }))
         },
         (isCompleted) => { // onIsCompletedChanged
            setSignal(changeIssue({ isCompleted }))
         },
         (assigneeId /* Long */) => { // onAssigneeIdChanged
            setSignal(changeIssue({ assigneeId }))
         },
      );
   }

   const listener = new IssuesModificationEventListener(
      listListener,
      getElementUpdateListener,
   )

   api.listenToIssueEvents(scope, listener)

   return signal
}