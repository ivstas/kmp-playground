import { Loading } from "./hooks.ts";
import type { JSX } from "solid-js";


interface LoaderProps<T> {
    loadingState: Loading<T>
    children: (loaded: T) => JSX.Element
}

export const Loader = <T,>(props: LoaderProps<T>) => {
    return (
        <div>
            {props.loadingState.isLoading
                ? (<span class="text-3xl font-bold underline">Loading...</span>)
                : props.children(props.loadingState.value)
            }
        </div>
    );
};