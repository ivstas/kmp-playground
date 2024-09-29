import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';

export function NotFound() {
   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb]}>
         <h1>No such page</h1>
      </PageLayout>
   )
}