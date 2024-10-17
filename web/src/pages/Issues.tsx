import {
   IssueApiWrapper,
   InitializedEventFlow,
   Issue,
   KtorRPCClient,
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
                           <a href={pages.user(issue.assigneeId.toString())} onClick={navigateToHref}
                              className="link">{issue.assigneeId}</a>
                        ) : (
                           <a>-</a>
                        )}
                  </th>
               </tr>
            ))}
         </tbody>
      </table>
   )
}
