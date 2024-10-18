import {  withLoader } from '../Loader.tsx';
import { useRequest, useScopeEffect } from '../hooks.ts';
import { UserApiWrapper, IssueApiWrapper, Issue, InitializedEventFlow } from 'kmp-playground-client';
import type { KtorRPCClient } from 'kmp-playground-client';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages, useNavigateToHref } from '../Router.tsx';
import { useState } from 'react';

export function UsersSingle(props: { rpcClient: KtorRPCClient, userId: number }) {
   const [userApi] = useState(() => new UserApiWrapper(props.rpcClient))
   const loadingUser = useRequest(scope => userApi.getUser(props.userId, scope), [props.userId])

   const [issuesApi] = useState(() => new IssueApiWrapper(props.rpcClient))
   const loadingIssues = useRequest(scope => issuesApi.subscribeToAssigneeIssues(scope, props.userId), [props.userId])

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All users', href: pages.issues }, { text: 'User' }]}>
         <div className="mx-6 my-3">
            {withLoader(loadingUser, (user) => user
               ? <h2>{user.name}</h2>
               : <div>User {props.userId} not found</div>,
            )}
         </div>
         <div className="mx-6 my-3">
            {withLoader(loadingIssues, (issuesInitializedUpdateFlow) => (
               <UserIssues issuesInitializedUpdateFlow={issuesInitializedUpdateFlow} />
            ))}
         </div>
      </PageLayout>
   )
}

function UserIssues({ issuesInitializedUpdateFlow }: { issuesInitializedUpdateFlow: InitializedEventFlow<KtList<Issue>> }) {
   const navigateToHref = useNavigateToHref()

   const [issueList, setIssueList] = useState(() => issuesInitializedUpdateFlow.initialValue)
   useScopeEffect(() => {
      issuesInitializedUpdateFlow.listenToUpdates(setIssueList)
   }, [])

   const issues: Issue[] = issueList.toArray()

   if (issues.length === 0) {
      return (
         <div>No issues found</div>
      )
   }

   return (
      <ul>
         {issues.map(issue => (
            <li key={issue.id} className="my-2">
               <a className="link" href={pages.issue(issue.id.toString())} onClick={navigateToHref}>{issue.title}</a>
            </li>
         ))}
      </ul>
   )
}