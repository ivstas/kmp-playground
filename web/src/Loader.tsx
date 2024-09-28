import { ReactNode } from 'react';

export type Loading<T> = {
    isLoading: true,
} | {
    isLoading: false,
    data: T,
}


export function withLoader<T>(
   loading: Loading<T>,
   renderLoaded: (loaded: T) => ReactNode,
): ReactNode {
   return loading.isLoading
      ? <span className="loading loading-dots loading-lg"></span>
      : renderLoaded(loading.data)
}

