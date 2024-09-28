
import Navigo from 'navigo';
import { BaseSyntheticEvent, createContext, ReactNode, useContext, useEffect, useState } from 'react'; // When using ES modules.


export type Page = {
    page: 'issues-all',
} | {
    page: 'issues-single',
    issueId: number,
} | {
    page: 'not-found',
}

const RouterContext = createContext<Navigo | undefined>(undefined);

export function Router({ children }: {children: (page: Page) => ReactNode}) {
   const [page, setPage] = useState<Page | undefined>(undefined)
   const [routerState, setRouterState] = useState<Navigo | undefined>(undefined)

   useEffect(() => {
      const router = new Navigo('/');

      router
         .on('/issues', () => {
            setPage({ page: 'issues-all' })
         })
         .on('/issues/:id', (match) => {
            const issueIdAsString = match?.data?.id
            if (issueIdAsString === undefined) {
               throw new Error('issueId not found')
            } else {
               const issueId = Number.parseInt(issueIdAsString)
               if (Number.isNaN(issueId)) {
                  throw new Error(`issue id ${issueIdAsString} is not a number`)
               }
               setPage({ page: 'issues-single', issueId })
            }
         })
         .on('*', () => {
            setPage({  page: 'not-found' })
         })

      router.resolve()
      setRouterState(router)

      return () => {
         router.destroy()
      }
   }, []);

   return page !== undefined && routerState !== undefined && (
      <RouterContext.Provider value={routerState}>
         {children(page)}
      </RouterContext.Provider>
   )
}

export function useRouter() {
   const router = useContext(RouterContext)
   if (router === undefined) {
      throw new Error('useRouter must be used inside Router')
   }

   return router
}


export function useNavigateToHref() {
   const router = useRouter()

   return (e: BaseSyntheticEvent<object, Element>) => {
      e.preventDefault(); // otherwise browser will reload the page

      // if (e.currentTarget instanceof HTMLElement) {
      const currentTarget = e.currentTarget;
      const href = currentTarget.getAttribute('href')
      if (href != null) {
         router.navigate(href)
      }
   }
}
