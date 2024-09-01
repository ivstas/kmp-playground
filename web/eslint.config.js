import globals from 'globals';
import pluginJs from '@eslint/js';
import typescriptEslint from 'typescript-eslint';
import stylistic from '@stylistic/eslint-plugin'

export default [
   { files: ['**/*.{js,mjs,cjs,ts}'] },
   { languageOptions: { globals: globals.browser } },
   pluginJs.configs.recommended,
   ...typescriptEslint.configs.recommended,
   {
      plugins: {
         '@stylistic': stylistic,
      },
      rules: {
         'indent': ['warn', 3],
         'object-curly-spacing': ['warn', 'always'],
         'no-unused-vars': 'warn',
         '@stylistic/comma-dangle': ['warn', 'always-multiline'],
         'quotes': ['warn', 'single'],
      },
   },
];