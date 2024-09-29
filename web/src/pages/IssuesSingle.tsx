import {  withLoader } from '../Loader.tsx';
import { useRequest } from '../hooks.ts';
import { IssueApi } from 'kmp-playground-client';
import type { KtorRPCClient } from 'kmp-playground-client';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages } from "../Router.tsx";

export function IssuesSingle(props: { rpcClient: KtorRPCClient, issueId: number }) {
   const api = new IssueApi(props.rpcClient)
   const issueResource = useRequest(scope => api.getIssue(props.issueId, scope))

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All issues', href: pages.issues }, { text: 'Issue' }]}>
         <div>
            <h1>Issue</h1>
            {withLoader(issueResource, (issue) => issue
               ? <h2>{issue.title}</h2>
               : <div>Issue {props.issueId} not found</div>,
            )}
         </div>
      </PageLayout>
   )
}