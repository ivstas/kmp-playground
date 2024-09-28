import { ReactNode } from 'react';


export function PageLayout(props: { children: ReactNode }) {
   return (
      <section>
         <nav className="flex px-6 py-3">
            <span>navigation</span>
         </nav>

         <main className="flex flex-col px-6 py-3">
            {props.children}
         </main>
      </section>
   )
}