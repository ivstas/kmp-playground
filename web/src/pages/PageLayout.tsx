import { PropsWithChildren } from 'react';
import { pages, useNavigateToHref } from '../Router.tsx';

export const homePageBreadcrumb = { href: pages.home, text: 'Home' };
export interface Breadcrumb {
    href?: string
    text: string
}


export function PageLayout({ children, breadcrumbs }: PropsWithChildren<{ breadcrumbs: Breadcrumb[] }>) {
   const navigateToHref = useNavigateToHref()

   return (
      <section>
         <nav className="flex px-6 py-3 breadcrumbs text-sm">
            <ul>
               {breadcrumbs.map((breadcrumb) => (
                  <li key={breadcrumb.text + ':' + (breadcrumb.href || '')}>
                     {breadcrumb.href
                        ? <a href={breadcrumb.href} onClick={navigateToHref}>{breadcrumb.text}</a>
                        : breadcrumb.text
                     }
                  </li>
               ))}
            </ul>
         </nav>

         <main>
            {children}
         </main>
      </section>
   )
}