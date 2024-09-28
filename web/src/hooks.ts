import { CoroutineScope, ScopeProxy } from 'kmp-playground-client';
import { DependencyList, useEffect, useState } from 'react';
import { Loading } from './Loader.tsx';

export function useRequest<T>(loadData: (scope: CoroutineScope) => Promise<T>, deps: DependencyList = []): Loading<T> {
   const [data, setData] = useState<Loading<T>>({ isLoading: true })

   useEffect(() => {
      async function fetchData(scope: CoroutineScope) {
         try {
            const data = await loadData(scope)
            setData({ isLoading: false, data })
         } catch (error: unknown) {
            console.error(error)
         }
      }

      const scopeProxy = new ScopeProxy()

      fetchData(scopeProxy.scope)

      return () => {
         // when hook finishes, scope will be terminated
         // (by default (with empty dependencies) this will be called on component unmount
         // even if the request is still pending, loadData should depend on the passed scope and terminate the request
         scopeProxy.dispose()
      }
   }, deps);

   return data;
}
