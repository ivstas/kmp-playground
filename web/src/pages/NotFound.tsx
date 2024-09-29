import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';

export function NotFound() {
   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb]}>
         <div className="mx-6 my-3">
            <h1>No such page</h1>
         </div>
      </PageLayout>
   )
}