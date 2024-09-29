import { withLoader } from '../Loader.tsx';
import { useRequest } from '../hooks.ts';
import type { KtorRPCClient } from 'kmp-playground-client';
import { Issue, IssueApiWrapper } from 'kmp-playground-client';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages } from '../Router.tsx';
import { useState } from 'react';

export function IssuesSingle(props: { rpcClient: KtorRPCClient, issueId: number }) {
   const api = new IssueApiWrapper(props.rpcClient)
   const issueResource = useRequest(scope => api.getIssue(props.issueId, scope))

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues', href: pages.issues }, { text: 'Issue' }]}>
         <div className="mx-6 my-3">
            {withLoader(issueResource, (issue) => issue
               ? <IssueRenderer issue={issue} api={api}/>
               : <div>Issue {props.issueId} not found</div>,
            )}
         </div>
      </PageLayout>
   )
}

function IssueRenderer({ issue, api }: { issue: Issue, api: IssueApiWrapper }) {
   const [title, setTitle] = useState(issue.title)

   const hasTitleChanged = title !== issue.title;

   return (
      <div>
         <div className="flex gap-2">
            <input
               type="text"
               placeholder={issue.title}
               className="input w-full max-w-xs input-ghost"
               value={title}
               onChange={e => {
                  setTitle(e.target.value)
               }}
            />
            {hasTitleChanged && (
               <div className="join">
                  <button
                     className="btn join-item btn-warning"
                     onClick={() => {
                        setTitle(issue.title)
                     }}
                  >Reset
                  </button>
                  <button
                     className="btn join-item btn-success"
                     onClick={() => {
                        api.setTitle(issue.id, title)
                     }}
                  >Save
                  </button>
               </div>
            )}
         </div>

         <div className="form-control w-48">
            <label className="label cursor-pointer">
               <span className="label-text text-lg">Resolved</span>
               <input type="checkbox" className="toggle" checked={issue.isCompleted} onChange={e => {
                  const isChecked = e.target.checked;
                  api.setIsCompleted(issue.id, isChecked)
               }}/>
            </label>
         </div>

         <div>Assignee: {issue.assigneeId}</div>
      </div>
   )
}