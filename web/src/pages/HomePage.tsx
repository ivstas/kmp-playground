import { homePageBreadcrumb, PageLayout } from './PageLayout.tsx';
import { pages, useNavigateToHref } from '../Router.tsx';

export function HomePage() {
   const navigateToHref = useNavigateToHref()
   return (
      <PageLayout breadcrumbs={[homePageBreadcrumb]}>
         <h1>Welcome</h1>
         <a href={pages.issues} onClick={navigateToHref}>Issues</a>
      </PageLayout>
   )
}