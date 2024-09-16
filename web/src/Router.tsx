import { createSignal } from 'solid-js';

import Navigo from 'navigo'; // When using ES modules.


export type Page = {
    page: 'issues-all',
} | {
    page: 'issues-single',
    issueId: string
} | {
    page: 'main',
}

const router = new Navigo('/');
const [signal, setSignal] = createSignal<Page | undefined>(undefined)

router
   .on('/issues', (match) => {
      setSignal({ page: 'issues-all' })
   })
   .on('/issues/:id', (match) => {
      const issueId = match?.data?.id
      if (issueId === undefined) {
         throw new Error('issueId not found')
      } else {
         setSignal({ page: 'issues-single', issueId: issueId })
      }
   })
   .on('/', () => {
      setSignal({ page: 'main' })
   })

router.resolve()

export function onClickNavigateTo(href: string) {
   return (e: Event) => {
      e.preventDefault(); // otherwise browser will reload the page
      router.navigate(href)
   }
}


export const routerSignal = signal