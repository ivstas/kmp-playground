import { JSX } from 'solid-js';


export function RootContainer(props: { children: JSX.Element }): JSX.Element {
   return (
      <section>
         <nav class="flex px-6 py-3">
            <span>navigation</span>
         </nav>

         <main class="flex flex-col px-6 py-3">
            {props.children}
         </main>
      </section>
   )
}