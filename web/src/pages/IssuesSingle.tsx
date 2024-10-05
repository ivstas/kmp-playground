import { withLoader } from '../Loader.tsx';
import { useRequest, useScopeEffect } from '../hooks.ts';
import { InitializedCollector, KtorRPCClient } from 'kmp-playground-client';
import { IssueApiWrapper, ScopeProxy } from 'kmp-playground-client';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages } from '../Router.tsx';
import { useState } from 'react';

export function IssuesSingle(props: { rpcClient: KtorRPCClient, issueId: number }) {
   const [api] = useState(() => new IssueApiWrapper(props.rpcClient))

   const loadingIssue = useRequest(scope => api.subscribeToIssue(scope, props.issueId))

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues', href: pages.issues }, { text: 'Issue' }]}>
         <div className="mx-6 my-3">
            {withLoader(loadingIssue, (loadingIssue) => loadingIssue
               ? <IssueRenderer collector={loadingIssue} api={api} />
               : <div>Issue {props.issueId} not found</div>,
            )}
         </div>
      </PageLayout>
   )
}

function IssueRenderer({ collector, api }: { collector: InitializedCollector, api: IssueApiWrapper }) {
   const [issue, setIssue] = useState(() => collector.initialValue)
   useScopeEffect(scope => {
      collector.listenToUpdates(setIssue)
   }, [])
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