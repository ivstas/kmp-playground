import {  withLoader } from '../Loader.tsx';
import { useRequest } from '../hooks.ts';
import { UserApiWrapper } from 'kmp-playground-client';
import type { KtorRPCClient } from 'kmp-playground-client';
import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages } from '../Router.tsx';

export function UsersSingle(props: { rpcClient: KtorRPCClient, userId: number }) {
   const api = new UserApiWrapper(props.rpcClient)
   const issueResource = useRequest(scope => api.getUser(props.userId, scope))

   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb, { text: 'All users', href: pages.issues }, { text: 'User' }]}>
         <div className="mx-6 my-3">
            {withLoader(issueResource, (user) => user
               ? <h2>{user.name}</h2>
               : <div>User {props.userId} not found</div>,
            )}
         </div>
      </PageLayout>
   )
}