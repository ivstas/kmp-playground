import './index.css';
import { WithKtorClient } from './WithKtorClient.tsx';

import { render } from 'solid-js/web';
import { RootContainer } from './pages/RootContainer.tsx';
import { Issues } from './pages/Issues.tsx';
import { routerSignal } from './Router.tsx';
import { NotFound } from './pages/NotFound.tsx';
import { IssuesSingle } from './pages/IssuesSingle.tsx';
import { KtorRPCClient } from 'kmp-playground-client';
import { MainPage } from './pages/Main.tsx';


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
   case 'main':
      return <MainPage />
   default:
      return <NotFound/>
   }
}

render(
   () => (
      <WithKtorClient>
         {client => (
            <RootContainer>
               {matchRoute(client)}
            </RootContainer>
         )}
      </WithKtorClient>
   ),
   rootEl,
);
