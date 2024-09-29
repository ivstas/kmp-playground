import { Issue as ApiIssue, IssuesModificationEventListener, ScopeProxy } from 'kmp-playground-client';
import {
   IssueApi,
   IssueChangedEventListener,
   IterableModificationEventListener,
   KtorRPCClient,
} from 'kmp-playground-client';
import { useNavigateToHref } from '../Router.tsx';
import { useEffect, useState } from 'react';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';


export function Issues(props: { rpcClient: KtorRPCClient }) {
   const api = new IssueApi(props.rpcClient)
   const issues = useIssues(api)

   const navigateToHref = useNavigateToHref()

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues' }]}>
         <div>
            <h1>Issues</h1>
            <ul className="menu bg-base-200 rounded-box w-96">
               {issues.map((issue) => (
                  <li key={issue.id}>
                     <a href={`/issues/${issue.id}`} onClick={navigateToHref}>{issue.title}</a>
                  </li>
               ))}
            </ul>
         </div>
      </PageLayout>
   )
}

interface Issue {
   id: number
   title: string
   assigneeId: number
   isCompleted: boolean
}

function useIssues(api: IssueApi): Issue[] {
   const [signal, setSignal] = useState<Issue[]>([])

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

   useEffect(() => {
      const scopeProxy = new ScopeProxy()

      api.listenToIssueEvents(scopeProxy.scope, listener)

      return () => {
         scopeProxy.dispose()
      }
   }, []);

   return signal
}