import DOMPurify from 'dompurify';

import { createResource } from 'solid-js';
import { ResourceState } from '../Loader.tsx';
import styles from './Main.module.css'

async function loadFileAsString(url: string) {
   try {
      const response = await fetch(url);
      return await response.text();
   } catch (error) {
      console.error('Error fetching the file:', error);
      return null
   }
}

export function MainPage() {

   const [svgAsStrResource] = createResource(() => loadFileAsString('/static/park-desk-svg-export.svg'))

   return (
      <ResourceState resource={svgAsStrResource}>
         {svgAsString => svgAsString === null
            ? <div>Failed to load SVG</div>
            : <SanitiseSvg svgAsString={svgAsString} />
         }
      </ResourceState>
   )
}


const PREFIX = 'parked-';

function SanitiseSvg(props: { svgAsString: string }) {
   const cleanSVG = DOMPurify.sanitize(props.svgAsString, {
      USE_PROFILES: { svg: true },
      ADD_TAGS: ['use'],
      ADD_ATTR: ['href', 'xlink:href'],
   });

   return (
      <div class={styles.map}
         innerHTML={cleanSVG}
         onClick={(e) => {
            for (const candidate of ancestorIterator(e.target)) {
               if (candidate.id.startsWith(PREFIX)) {
                  window.alert('selected ' + candidate.id.substring(PREFIX.length))
                  return
               }
            }
         }}
      />
   )
}

function* ancestorIterator(target: Element) {
   let currentElement = target.parentElement;
   while (currentElement !== null) {
      yield currentElement
      currentElement = currentElement.parentElement;
   }
}
