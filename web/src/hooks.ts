import { Disposable, CoroutineScope, ScopeProxy } from 'kmp-playground-client';
import { createSignal, onCleanup } from 'solid-js';
import type { Accessor } from 'solid-js'

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

export type Loading<T> = {
   isLoading: true
} | {
   isLoading: false
   value: T
}

export function mapLoadingState<T, R>(
   current: Loading<T>,
   mapReady: (value: T) => R,
   mapLoading: () => Loading<R> = () => ({ isLoading: true }),
): Loading<R> {
   if (current.isLoading) {
      return mapLoading()
   }

   return { isLoading: false, value: mapReady(current.value) }
}

export function useLoading<T>(promise: Promise<T>): Accessor<Loading<T>> {
   const [loadingState, setLoadingState] = createSignal<Loading<T>>({ isLoading: true })

   // todo: handle errors
   promise.then(value => {
      setLoadingState({ isLoading: false, value })
   })

   return loadingState
}
