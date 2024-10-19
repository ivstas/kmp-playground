import {
   IssueApiWrapper,
   InitializedEventFlow,
   Issue,
   KtorRPCClient,
   IssueIn,
} from 'kmp-playground-client';
import { pages, useNavigateToHref } from '../Router.tsx';
import { useState } from 'react';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { useRequest, useScopeEffect } from '../hooks.ts';
import { withLoader } from '../Loader.tsx';


export function Issues(props: { rpcClient: KtorRPCClient }) {
   const [api] = useState(() => new IssueApiWrapper(props.rpcClient))
   const loadingIssueUpdateFlow = useRequest(scope => api.subscribeToAllIssues(scope))

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues' }]}>
         <div className="mx-2 my-3">
            {withLoader(loadingIssueUpdateFlow, (loadingIssueUpdateFlow) => (
               <IssueList loadingIssueUpdateFlow={loadingIssueUpdateFlow} api={api}/>
            ))}
         </div>
      </PageLayout>
   )
}

function IssueList({ loadingIssueUpdateFlow, api }: {loadingIssueUpdateFlow: InitializedEventFlow<KtList<Issue>>, api: IssueApiWrapper}) {
   const navigateToHref = useNavigateToHref()

   const [issueList, setIssueList] = useState(() => loadingIssueUpdateFlow.initialValue)
   useScopeEffect(() => {
      loadingIssueUpdateFlow.listenToUpdates(setIssueList)
   }, [])

   const issues: Issue[] = issueList.toArray()

   return (
      <>
         <table className="table">
            <thead>
               <tr>
                  <th>Issue</th>
                  <th>Is completed</th>
                  <th>assignee</th>
                  <th></th>
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
                              <a href={pages.user(issue.assigneeId.toString())} onClick={navigateToHref}
                                 className="link">{issue.assigneeId}</a>
                           ) : (
                              <a>-</a>
                           )}
                     </th>
                     <th>
                        <button className="btn" onClick={() => {
                           api.removeIssue(issue.id)
                        }}>
                           remove
                           <svg
                              xmlns="http://www.w3.org/2000/svg"
                              className="h-3 w-3"
                              fill="none"
                              viewBox="0 0 24 24"
                              stroke="currentColor">
                              <path
                                 strokeLinecap="round"
                                 strokeLinejoin="round"
                                 strokeWidth="2"
                                 d="M6 18L18 6M6 6l12 12"/>
                           </svg>
                        </button>
                     </th>
                  </tr>
               ))}
            </tbody>
         </table>
         <NewIssueForm api={api}/>
      </>
   )
}

function NewIssueForm({ api }: { api: IssueApiWrapper }) {
   const [title, setTitle] = useState('')

   return (
      <div className="form-control">
         <label className="label">
            <span className="label-text">Create new issue</span>
         </label>
         <div className="flex gap-2 w-full">
            <input
               type="text"
               placeholder="issue title"
               className="input input-bordered flex-1"
               value={title}
               onChange={e => setTitle(e.target.value)}
            />
            <button
               className="btn btn-primary"
               disabled={title.trim().length === 0}
               onClick={() => {
                  api.addIssue(new IssueIn(title))
                  setTitle('')
               }}
            >Create issue
            </button>
         </div>

      </div>
   )
}
