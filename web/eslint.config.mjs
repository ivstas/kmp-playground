import pluginJs from '@eslint/js';
import typescriptEslint from 'typescript-eslint';
import stylistic from '@stylistic/eslint-plugin'
import react from 'eslint-plugin-react'

export default typescriptEslint.config({
   files: ['src/**/*.{js,mjs,cjs,ts,tsx}'],
   extends: [
      pluginJs.configs.recommended,
      ...typescriptEslint.configs.recommended,
   ],
   languageOptions: {
      parserOptions: {
         project: true,
      },
   },
   plugins: {
      '@stylistic': stylistic,
      'react': react,
   },
   rules: {
      'indent': ['warn', 3, {
         'SwitchCase': 1,
      }],
      'object-curly-spacing': ['warn', 'always'],
      '@typescript-eslint/no-unused-vars': 'off',
      '@typescript-eslint/switch-exhaustiveness-check': 'error',
      '@stylistic/comma-dangle': ['warn', 'always-multiline'],
      'quotes': ['warn', 'single'],
   },
})
