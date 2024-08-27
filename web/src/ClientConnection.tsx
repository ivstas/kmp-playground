import { Client, KtorRPCClient } from "kmp-playground-client";
import React, { ReactNode, useEffect, useState } from "react";

interface ClientConnectionProps {
    children: (rpcClient: KtorRPCClient) => ReactNode
}

export function ClientConnection({ children }: ClientConnectionProps) {
    const [rpcClient, setRpcClient] = useState<false | KtorRPCClient>(false);

    useEffect(() => {
        const client = new Client()

        client.rpcClient.then(setRpcClient)

        return () =>{
            client.close()
        }
    }, [])

    return (
        <div>
            {(rpcClient === false)
                ? (<h1>connecting...</h1>)
                : children(rpcClient)
            }
        </div>
    )
}