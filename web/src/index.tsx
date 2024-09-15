import './index.css';
import { WithKtorClient } from './WithKtorClient.tsx';

import { render } from 'solid-js/web';
import { MainPage } from './pages/main.tsx';
import { Issues } from './pages/Issues.tsx';
import { routerSignal } from './Router.tsx';
import { NotFound } from './pages/NotFound.tsx';
import { IssuesSingle } from './pages/IssuesSingle.tsx';
import { KtorRPCClient } from 'kmp-playground-client';


const rootEl = document.getElementById('root');
if (rootEl == null) {
   throw new Error('Element #root not found')
}

function matchRoute(client: KtorRPCClient) {
   const page = routerSignal()

   switch (page?.page) {
   case 'issues-all':
      return <Issues rpcClient={client}/>
   case 'issues-single':
      return <IssuesSingle rpcClient={client} issueId={Number.parseInt(page.issueId)}/>
   default:
      return <NotFound/>
   }
}

render(
   () => (
      <WithKtorClient>
         {client => (
            <MainPage>
               {matchRoute(client)}
            </MainPage>
         )}
      </WithKtorClient>
   ),
   rootEl,
);
