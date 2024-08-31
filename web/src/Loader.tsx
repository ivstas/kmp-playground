import type { JSX } from "solid-js";
import { Resource, Show } from "solid-js";


interface ResourceStateProps<T> {
    resource: Resource<T>
    children: (loaded: T) => JSX.Element
}

export function ResourceState<T>(props: ResourceStateProps<T>): JSX.Element {
    return (
        <Show when={!props.resource.loading} fallback={<span>loading...</span>}>
            {/* this would break if the resource has an "unresolved" state */}
            {props.children(props.resource()!!)}
        </Show>
    )
}
