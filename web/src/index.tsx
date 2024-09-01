import './index.css';
import { Messages } from './pages/Messages.tsx'
import { WithKtorClient } from './WithKtorClient.tsx';

import { render } from 'solid-js/web';
import { MainPage } from './pages/main.tsx';
import { Issues } from './pages/Issues.tsx';

const rootEl = document.getElementById('root');
if (rootEl == null) {
   throw new Error('Element #root not found')
}

render(
   () => (
      <WithKtorClient>
         {client => (
            <MainPage>
               <Messages rpcClient={client}/>
               <Issues rpcClient={client}/>
            </MainPage>
         )}
      </WithKtorClient>
   ),
   rootEl,
);
