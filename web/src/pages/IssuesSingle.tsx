import { withLoader } from '../Loader.tsx';
import { useRequest, useScopeEffect } from '../hooks.ts';
import {
   InitializedEventFlow,
   Issue,
   IssueApiWrapper,
   KtorRPCClient,
   Nullable,
   User,
   UserApiWrapper,
} from 'kmp-playground-client';
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
               ? <IssueRenderer collector={loadingIssue} api={api} client={props.rpcClient} />
               : <div>Issue {props.issueId} not found</div>,
            )}
         </div>
      </PageLayout>
   )
}

function IssueRenderer({ collector, api, client }: { collector: InitializedEventFlow<Issue>, api: IssueApiWrapper, client: KtorRPCClient }) {
   const [issue, setIssue] = useState(() => collector.initialValue)
   useScopeEffect(() => {
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

         <AssigneeLoader
            client={client}
            assigneeId={issue.assigneeId}
            changeAssignee={id => api.setAssigneeId(issue.id, id)}
         />
      </div>
   )
}

function AssigneeLoader({ client, assigneeId, changeAssignee }: { client: KtorRPCClient, assigneeId: Nullable<number>, changeAssignee: (id: number) => void, }) {
   const [api] = useState(() => new UserApiWrapper(client))

   const request = useRequest(scope => api.subscribeToAllUsers(scope), [])

   return withLoader(request, (initializedEventFlow) => (
      <AssigneeSelector
         initializedEventFlow={initializedEventFlow}
         assigneeId={assigneeId}
         changeAssignee={changeAssignee}
      />
   ))
}

function AssigneeSelector({
   initializedEventFlow,
   assigneeId,
   changeAssignee,
}: {
    initializedEventFlow: InitializedEventFlow<KtList<User>>,
    assigneeId: Nullable<number>,
    changeAssignee: (id: number) => void,
}) {
   const [userList, setUserList] = useState(() => initializedEventFlow.initialValue)
   useScopeEffect(() => {
      initializedEventFlow.listenToUpdates(setUserList)
   }, [])

   const users: User[] = userList.toArray()

   return (
      <select
         value={assigneeId || ''}
         className="select select-bordered w-full max-w-xs"
         onChange={e => {
            const id = Number.parseInt(e.currentTarget.value);
            if (isNaN(id)) {
               return; // todo: set assignee to null
            }
            changeAssignee(id)
         }}
      >
         {users.map(user => (
            <option
               key={user.id}
               value={user.id.toString()}
            >{user.name}</option>
         ))}
      </select>
   )
}