
import Navigo from 'navigo';
import { BaseSyntheticEvent, createContext, ReactNode, useContext, useEffect, useState } from 'react'; // When using ES modules.


export type Page = {
    page: 'home',
} | {
    page: 'issues-all',
} | {
    page: 'issues-single',
    issueId: number,
} | {
    page: 'users-single',
    userId: number,
} | {
    page: 'not-found',
}

export const pages = {
   home: '/',
   issues: '/issues',
   issue: (issueId: string) => `/issues/${issueId}`,
   user: (userId: string) => `/users/${userId}`,
}

const RouterContext = createContext<Navigo | undefined>(undefined);

export function Router({ children }: {children: (page: Page) => ReactNode}) {
   const [page, setPage] = useState<Page | undefined>(undefined)
   const [routerState, setRouterState] = useState<Navigo | undefined>(undefined)

   useEffect(() => {
      const router = new Navigo('/');

      router
         .on(pages.home, () => {
            setPage({ page: 'home' })
         })
         .on(pages.user(':id'), (match) => {
            const userIdAsString = match?.data?.id;
            if (userIdAsString === undefined) {
               throw new Error('userId not found in url')
            }

            const userId = Number.parseInt(userIdAsString)
            if (Number.isNaN(userId)) {
               throw new Error(`issue id ${userIdAsString} is not a number`)
            }

            setPage({ page: 'users-single', userId: userId })
         })
         .on(pages.issues, () => {
            setPage({ page: 'issues-all' })
         })
         .on(pages.issue(':id'), (match) => {
            const issueIdAsString = match?.data?.id
            if (issueIdAsString === undefined) {
               throw new Error('issueId not found in url')
            }

            const issueId = Number.parseInt(issueIdAsString)
            if (Number.isNaN(issueId)) {
               throw new Error(`issue id ${issueIdAsString} is not a number`)
            }
            setPage({ page: 'issues-single', issueId })
            
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
