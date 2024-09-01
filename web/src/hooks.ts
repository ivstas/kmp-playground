import { Disposable, CoroutineScope, ScopeProxy } from 'kmp-playground-client';
import { onCleanup } from 'solid-js';

export function useDisposable<T extends Disposable>(createDisposable: () => T): T {
   const disposable = createDisposable()

   onCleanup(() => {
      disposable.dispose()
   })

   return disposable
}

export function useCoroutineScope(): CoroutineScope {
   return useDisposable(() => new ScopeProxy()).scope
}
