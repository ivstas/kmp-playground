import { Api, Client } from "kmp-playground-client";
import React, { ReactNode, useEffect, useState } from "react";

interface ClientConnectionProps {
    children: (client: Api) => ReactNode
}

export function ClientConnection({ children }: ClientConnectionProps) {
    const [api, setApi] = useState<false | Api>(false);

    useEffect(() => {
        const client = new Client()

        client.api.then(setApi)

        return () =>{
            client.close()
        }
    }, [])

    return (
        <div>
            {(api === false)
                ? (<h1>connecting...</h1>)
                : children(api)
            }
        </div>
    )
}