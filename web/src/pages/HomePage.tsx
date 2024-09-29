import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages, useNavigateToHref } from '../Router.tsx';

export function HomePage() {
   const navigateToHref = useNavigateToHref()
   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb]}>
         <div className="mx-6 my-3">
            <a href={pages.issues} onClick={navigateToHref} className="link">All Issues</a>
         </div>
      </PageLayout>
   )
}