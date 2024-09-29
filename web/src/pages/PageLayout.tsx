import { ReactNode } from 'react';
import { pages } from '../Router.tsx';

export const homePageBreadcrumb = { href: pages.home, text: 'Home' };
export interface Breadcrumb {
    href?: string
    text: string
    // todo: icon
}


export function PageLayout(props: { children: ReactNode, breadcrumbs: Breadcrumb[] }) {
   return (
      <section>
         <nav className="flex px-6 py-3 breadcrumbs text-sm">
            <ul>
               {props.breadcrumbs.map((breadcrumb) => (
                  <li key={breadcrumb.text + ':' + (breadcrumb.href || '')}>
                     {breadcrumb.href
                        ? <a href={breadcrumb.href}>{breadcrumb.text}</a>
                        : breadcrumb.text
                     }
                  </li>
               ))}
            </ul>
         </nav>

         <main className="flex flex-col px-6 py-3">
            {props.children}
         </main>
      </section>
   )
}