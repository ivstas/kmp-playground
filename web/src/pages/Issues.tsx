import { Issue as ApiIssue, IssuesModificationEventListener, ScopeProxy } from 'kmp-playground-client';
import {
   IssueApiWrapper,
   IssueChangedEventListener,
   IterableModificationEventListener,
   KtorRPCClient,
} from 'kmp-playground-client';
import { pages, useNavigateToHref } from '../Router.tsx';
import { useEffect, useState } from 'react';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';


export function Issues(props: { rpcClient: KtorRPCClient }) {
   const api = new IssueApiWrapper(props.rpcClient)
   const issues = useIssues(api)

   const navigateToHref = useNavigateToHref()

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues' }]}>
         <div className="mx-2 my-3">
            <table className="table">
               <thead>
                  <tr>
                     <th>Issue</th>
                     <th>Is completed</th>
                     <th>assignee</th>
                  </tr>
               </thead>
               <tbody>
                  {issues.map((issue) => (
                     <tr key={issue.id}>
                        <th>
                           <a href={pages.issue(issue.id.toString())} onClick={navigateToHref} className="link">{issue.title}</a>
                        </th>
                        <th>
                           <div className="form-control">
                              <label className="label cursor-pointer">
                                 <input type="checkbox" className="toggle" checked={issue.isCompleted} onChange={e => {
                                    const isChecked = e.target.checked;
                                    api.setIsCompleted(issue.id, isChecked)
                                 }}/>
                              </label>
                           </div>
                        </th>
                        <th>
                           {issue.assigneeId
                              ? (
                                 <a href={pages.user(issue.assigneeId.toString())} onClick={navigateToHref} className="link">{issue.assigneeId}</a>
                              ) : (
                                 <a>-</a>
                              )}
                        </th>
                     </tr>
                  ))}
               </tbody>
            </table>

         </div>
      </PageLayout>
   )
}

interface Issue {
   id: number
   title: string
   assigneeId: number | null | undefined
   isCompleted: boolean
}

function useIssues(api: IssueApiWrapper): Issue[] {
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