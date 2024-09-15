import type { Config } from 'tailwindcss'
import daisyui from 'daisyui' // to configure: https://daisyui.com/docs/config

export default {
   content: [
      './index.html',
      './src/**/*.{ts,tsx}',
   ],
   theme: {
      extend: {},
   },
   plugins: [
      daisyui,
   ],
} satisfies Config

