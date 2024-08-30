import './index.css';
import {Messages} from './Messages.tsx'
import { WithKtorClient } from "./WithKtorClient.tsx";

import { render } from 'solid-js/web';

const rootEl = document.getElementById('root');
if (rootEl == null) {
    throw new Error("Element #root not found")
}

render(
    () => (
        <WithKtorClient>
            {client => (
                <Messages rpcClient={client}/>
            )}
        </WithKtorClient>
    ),
    rootEl,
);
