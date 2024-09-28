import './index.css';
import { WithKtorClient } from './WithKtorClient.tsx';

import React, { memo } from 'react';
import { createRoot } from 'react-dom/client';
import { Issues } from './pages/Issues.tsx';
import { Page, Router } from './Router.tsx';
import { NotFound } from './pages/NotFound.tsx';
import { IssuesSingle } from './pages/IssuesSingle.tsx';
import { KtorRPCClient } from 'kmp-playground-client';


const rootEl = document.getElementById('root');
if (rootEl == null) {
   throw new Error('Element #root not found')
}

const root = createRoot(rootEl);

root.render(
   <WithKtorClient>
      {client => (
         <Router>
            {page => <App page={page} rpcClient={client}/>}
         </Router>
      )}
   </WithKtorClient>,
);

const App = memo(function App({ rpcClient, page } : { rpcClient: KtorRPCClient, page: Page }) {
   switch (page.page) {
   case 'issues-all':
      return <Issues rpcClient={rpcClient}/>
   case 'issues-single':
      return <IssuesSingle rpcClient={rpcClient} issueId={page.issueId}/>
   default:
      return <NotFound/>
   }
})

