module.exports = {
  parser: '@typescript-eslint/parser',
  plugins: ['@typescript-eslint'],
  extends: ['airbnb-base'],
  parserOptions: {
    ecmaVersion: 11,
    sourceType: 'module',
  },
  env: {
    es6: true,
    node: true,
  },
  rules: {
    semi: ['error', 'never'],
    quotes: ['error', 'single'],
    'object-curly-spacing': ['error', 'always'],
    'no-trailing-spaces': 'error',
    'space-before-function-paren': ['error', {
      anonymous: 'never',
      named: 'never',
      asyncArrow: 'always',
    }],
    'comma-dangle': ['error', 'always-multiline'],
    'no-mixed-spaces-and-tabs': 'error',
    'no-underscore-dangle': 'off',
    'no-plusplus': 'off',
    'no-mixed-operators': 'off',
    'camelcase': 'off', // todo
    'max-len': 'off',
    'no-unused-vars': 'warn',
    'class-methods-use-this': 'off',
    'no-param-reassign': 'off',
    'import/prefer-default-export': 'off',
    'no-console': 'off',
    'max-classes-per-file': 'off',
    'no-restricted-syntax': 'warn',
    'no-eval': 'warn',
    'no-shadow': 'warn',
    'lines-between-class-members': ['error', 'always', {
      exceptAfterSingleLine: true,
    }],
    'func-names': ['warn', 'never'],
    'import/no-nodejs-modules': 'warn',
    'import/extensions': ['error', 'ignorePackages'],
    'import/newline-after-import': ['error', { count: 2 }],
    'import/order': ['error', {
      groups: ['builtin', 'internal', 'external', ['sibling', 'parent']],
      'newlines-between': 'always',
      alphabetize: {
        order: 'ignore',
        caseInsensitive: true,
      }
    }],
  },
  overrides: [
    {
      files: ['*.test.js', 'webpack.config.js'],
      rules: {
        'import/no-nodejs-modules': 'off',
        'import/no-unresolved': 'off',
      },
    },
  ],
  globals: {
    $: 'readonly',
    var_dump: 'readonly',
    describe: 'readonly',
    it: 'readonly',
  },
}
